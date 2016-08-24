package vn.mran.simplenote.activity;

import android.view.View;

import vn.mran.simplenote.R;
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

        txtTitle.editText.setText(currentNotes.getTitle());
        txtContent.editText.setText(currentNotes.getContent());
    }

    void showEdit(){
        toolAddNote.btnSave.setVisibility(View.VISIBLE);
        toolAddNote.btnClear.setVisibility(View.VISIBLE);
        toolAddNote.btnAddPhoto.setVisibility(View.VISIBLE);
        toolAddNote.btnEdit.setVisibility(View.GONE);
    }

    void hideEdit(){
        toolAddNote.btnSave.setVisibility(View.GONE);
        toolAddNote.btnClear.setVisibility(View.GONE);
        toolAddNote.btnAddPhoto.setVisibility(View.GONE);
        toolAddNote.btnEdit.setVisibility(View.VISIBLE);
    }

    @Override
    public void initValue() {
        toolAddNote.txtDate.setText(Utils.getDate(currentNotes.getId(), "yyyy-MM-dd:HH:mm"));
        toolAddNote.txtFolder.setText(currentFolder.getName());
    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnEdit:
                        showEdit();
                        break;
                }
            }
        };
        toolAddNote.btnEdit.setOnClickListener(click);
    }
}
