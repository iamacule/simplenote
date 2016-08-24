package vn.mran.simplenote.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.InputStream;

import io.realm.Realm;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.InitPresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.ResizeBitmap;

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
        try {
            this.editText = editText;
            InputStream stream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            if (bitmap.getWidth() > editText.getWidth() - 20)
                bitmap = ResizeBitmap.resize(bitmap, editText.getWidth() - 20);
            stream.close();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public void addImage(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        int selectionCursor = editText.getSelectionStart();
        editText.getText().insert(selectionCursor, ".");
        selectionCursor = editText.getSelectionStart();
        SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
        builder.setSpan(new ImageSpan(drawable), selectionCursor - ".".length(), selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        addNotesView.addImage(builder);
        addNotesView.addSetTxtContentSelection(selectionCursor);
    }

    @Override
    public void init() {
        realm = RealmController.with().getRealm();
    }
}
