package vn.mran.simplenote.activity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.StringUtil;
import vn.mran.simplenote.util.Utils;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;

/**
 * Created by MrAn on 24-Aug-16.
 */
public class NotesDetailActivity extends BaseActivity {
    private Header header;
    private ToolAddNote toolAddNote;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;
    private final int ACTION_REQUEST_GALLERY = 0;
    private boolean isSaved = false;
    private List<Point> pointList;

    @Override
    public int getView() {
        return R.layout.activity_notes_detail;
    }

    @Override
    public void initView() {
        View v = getWindow().getDecorView().getRootView();
        header = new Header(v);
        header.title.setText(getString(R.string.add_note));
        header.setDefaultBtnBack();

        toolAddNote = new ToolAddNote(v);

        txtTitle = new CustomEditText(v, R.id.lnTitle);
        txtContent = new CustomEditText(v, R.id.lnContent);
        hideEdit();

        txtTitle.editText.setText(StringUtil.filterTitle(currentNotes.getTitle()));
    }

    void showEdit() {
        toolAddNote.btnSave.setVisibility(View.VISIBLE);
        toolAddNote.btnClear.setVisibility(View.VISIBLE);
        toolAddNote.btnAddPhoto.setVisibility(View.VISIBLE);
        toolAddNote.btnEdit.setVisibility(View.GONE);
    }

    void hideEdit() {
        toolAddNote.btnSave.setVisibility(View.GONE);
        toolAddNote.btnClear.setVisibility(View.GONE);
        toolAddNote.btnAddPhoto.setVisibility(View.GONE);
        toolAddNote.btnEdit.setVisibility(View.VISIBLE);
    }

    @Override
    public void initValue() {
        toolAddNote.txtDate.setText(Utils.getDate(currentNotes.getId(), "yyyy-MM-dd:HH:mm"));
        toolAddNote.txtFolder.setText(currentFolder.getName());
        pointList = new ArrayList<>();
        pointList = StringUtil.getListImageInContent(currentNotes.getContent());
        List<String> listDataImage = new ArrayList<>();
        List<String> listDataNormal = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            listDataImage.add(currentNotes.getContent().substring(pointList.get(i).x, pointList.get(i).y));
            if (pointList.size() > 1) {
                if (i == 0 && pointList.get(i).x == 0) {
                    listDataNormal.add("BEGIN");
                } else if (i == 0 && pointList.get(i).x != 0) {
                    listDataNormal.add(currentNotes.getContent().substring(0, pointList.get(i).x));
                } else {
                    try {
                        if(pointList.get(i).x-pointList.get(i-1).y>1){
                            listDataNormal.add(currentNotes.getContent().substring(pointList.get(i-1).y,
                                    pointList.get(i).x));
                        } else {
                            listDataNormal.add(currentNotes.getContent().substring(pointList.get(i).y, pointList.get(i + 1).x));
                        }
                    } catch (Exception e) {
                        listDataNormal.add(currentNotes.getContent().substring(pointList.get(i).y, currentNotes.getContent().length()));
                        break;
                    }
                }
            } else {
                if (pointList.get(i).x == 0) {
                    listDataNormal.add("BEGIN");
                } else {
                    listDataNormal.add(currentNotes.getContent().substring(0, pointList.get(i).x));
                }
            }
        }
        String contentShow = currentNotes.getContent();
        for (String s : listDataImage) {
            contentShow = contentShow.replace(s, "");
        }
        txtContent.editText.setText(contentShow);
        List<Uri> listUri = new ArrayList<>();
        for (String s : listDataImage){
            listUri.add(AddImageUtil.getUriFormData(s));
            Log.d(DataUtil.TAG_NOTES_DETAIL,"Uri : "+AddImageUtil.getUriFormData(s));
        }
        addImageToEditText(listDataNormal,listDataImage,listUri,100);
    }

    private void addImageToEditText(List<String> lisDataNormal,List<String> listDataImage, List<Uri> listUri, float width) {
        for (int i = 0; i < lisDataNormal.size(); i++) {
            Bitmap bitmap = AddImageUtil.createBitmapFromURI(this, listUri.get(i), width);
            List<Object> list = AddImageUtil.createImageEditText(this,lisDataNormal.get(i),
                    listDataImage.get(i),bitmap,currentNotes.getContent());
            txtContent.editText.setText((SpannableStringBuilder) list.get(0));
            txtContent.editText.setSelection((int) list.get(1));
        }
    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnEdit:
                        showEdit();
                        break;
                }
            }
        };
        toolAddNote.btnEdit.setOnClickListener(click);
    }
}
