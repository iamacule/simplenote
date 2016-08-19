package vn.mran.simplenote.activity;

import android.util.Log;
import android.view.View;

import io.realm.Realm;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;
import vn.mran.simplenote.view.toast.Boast;
import vn.mran.simplenote.view.toast.CustomEditText;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class AddNoteActivity extends BaseActivity {
    private final String TAG = "AddNoteActivity";
    private Realm realm;
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
        realm = RealmController.with(this).getRealm();
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
                        save();
                        break;

                }
            }
        };
        toolAddNote.btnClear.setOnClickListener(click);
        toolAddNote.btnSave.setOnClickListener(click);
    }

    private void save() {
        Notes notes = new Notes();
        notes.setId(System.currentTimeMillis());
        notes.setContent(txtContent.editText.getText().toString().trim());
        notes.setTitle(txtTitle.editText.getText().toString().trim());
        notes.setFolderId(1);

        realm.beginTransaction();
        realm.copyToRealm(notes);
        realm.commitTransaction();
        Log.d(TAG,"Save success");
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
}
