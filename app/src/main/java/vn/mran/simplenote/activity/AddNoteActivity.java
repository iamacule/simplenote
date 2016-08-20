package vn.mran.simplenote.activity;

import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.AddNotePresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;
import vn.mran.simplenote.view.toast.Boast;
import vn.mran.simplenote.view.toast.CustomEditText;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class AddNoteActivity extends BaseActivity implements AddNotesView {
    private AddNotePresenter addNotePresenter;
    private Header header;
    private ToolAddNote toolAddNote;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;

    @Override
    public int getView() {
        return R.layout.activity_add_note;
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
    }

    @Override
    public void initValue() {
        addNotePresenter = new AddNotePresenter(this);
    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnClear:
                        clearText();
                        break;
                    case R.id.btnSave:
                        addNotePresenter.save(txtTitle.editText.getText().toString(),
                                txtContent.editText.getText().toString(), 0l);
                        break;

                }
            }
        };
        toolAddNote.btnClear.setOnClickListener(click);
        toolAddNote.btnSave.setOnClickListener(click);
    }

    private void clearText() {
        boolean clear = false;
        if (!txtTitle.isEmpty()) {
            txtTitle.clearText();
            clear = true;
        }
        if (!txtContent.isEmpty()) {
            txtContent.clearText();
            clear = true;
        }
        if (clear)
            Boast.makeText(this, getString(R.string.clear_success)).show();
    }

    @Override
    public void onSaveFinish() {
        Boast.makeText(this, getString(R.string.save_success)).show();
        onBackPressed();
    }

    @Override
    public void onSaveFail(byte messageId) {
        Log.d(DataUtil.TAG_ADD_NOTES_ACTIVITY,"Message fail : "+messageId);
        switch (messageId) {
            case AddNotePresenter.EMPTY_CONTENT:
                Boast.makeText(this, getString(R.string.save_fail_empty)).show();
                break;

            case AddNotePresenter.SAVE_EXCEPTION:
                Boast.makeText(this, getString(R.string.save_fail)).show();
                break;
        }
    }
}
