package vn.mran.simplenote.activity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.NotesDetailPresenter;
import vn.mran.simplenote.mvp.view.NotesDetailView;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.ScreenUtil;
import vn.mran.simplenote.util.StringUtil;
import vn.mran.simplenote.util.Utils;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;

/**
 * Created by MrAn on 24-Aug-16.
 */
public class NotesDetailActivity extends BaseActivity implements NotesDetailView {
    private Header header;
    private ToolAddNote toolAddNote;
    private ProgressBar progressBar;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;
    private final int ACTION_REQUEST_GALLERY = 0;
    private boolean isSaved = false;
    private NotesDetailPresenter notesDetailPresenter;

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
        progressBar = (ProgressBar) findViewById(R.id.pbBar);
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
        notesDetailPresenter = new NotesDetailPresenter(this);
        toolAddNote.txtDate.setText(Utils.getDate(currentNotes.getId(), "yyyy-MM-dd:HH:mm"));
        toolAddNote.txtFolder.setText(currentFolder.getName());
        notesDetailPresenter.loadData(currentNotes.getContent());
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

    @Override
    public void onWaiting() {
        progressBar.setVisibility(View.VISIBLE);
        txtContent.editText.setClickable(false);
    }

    @Override
    public void onLoadContent(SpannableStringBuilder builder) {
        txtContent.editText.setClickable(true);
        progressBar.setVisibility(View.GONE);
        txtContent.editText.setText(builder);
    }
}
