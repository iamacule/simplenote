package vn.mran.simplenote.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.EditText;

import java.util.List;

import io.realm.Realm;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.InitPresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.DataUtil;

/**
 * Created by Mr An on 20/08/2016.
 */
public class AddNotePresenter implements InitPresenter {
    public static final byte SAVE_EXCEPTION = 0;
    public static final byte EMPTY_CONTENT = 1;
    private Context context;
    private AddNotesView addNotesView;
    private Realm realm;
    private EditText editText;

    public AddNotePresenter(Context context) {
        this.context = context;
        this.addNotesView = (AddNotesView) context;
        init();
    }

    public void save(String title, String content, long folderId, boolean back) {
        if (!DataUtil.checkStringEmpty(title)) {
            if (DataUtil.checkStringEmpty(content))
                title = DataUtil.createTitle(content);
            else {
                addNotesView.onSaveFail(EMPTY_CONTENT);
                return;
            }
        }
        if (DataUtil.checkStringEmpty(content)) {
            try {
                Notes notes = new Notes();
                long id = System.currentTimeMillis();
                notes.setId(id);
                notes.setTitle(title);
                notes.setContent(content);
                notes.setFolderId(folderId);

                realm.beginTransaction();
                realm.copyToRealm(notes);
                realm.commitTransaction();

                Notes result = RealmController.with().getNotesById(id);
                if (null != result) {
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Save success as title : " + notes.getTitle());
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Save success as content : " + notes.getContent());
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Save success as folderId : " + notes.getFolderId());
                    addNotesView.onSaveFinish(back);
                } else {
                    addNotesView.onSaveFail(SAVE_EXCEPTION);
                }
            } catch (Exception e) {
                Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Save error : " + e.getMessage());
                addNotesView.onSaveFail(SAVE_EXCEPTION);
            }
        } else {
            addNotesView.onSaveFail(EMPTY_CONTENT);
        }
    }

    public Bitmap createBitmap(Uri imageUri, EditText editText) {
        this.editText = editText;
        return AddImageUtil.createBitmap(context, imageUri, editText.getWidth() / 2);
    }

    public void addImage(Bitmap bitmap) {
        List<Object> list = AddImageUtil.createImageEditText(context, bitmap, editText);
        addNotesView.addImage((SpannableStringBuilder)list.get(0));
        addNotesView.addSetTxtContentSelection((int)list.get(1));
    }

    @Override
    public void init() {
        realm = RealmController.with().getRealm();
    }
}
