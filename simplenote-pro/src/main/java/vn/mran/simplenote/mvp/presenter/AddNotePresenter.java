package vn.mran.simplenote.mvp.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import vn.mran.simplenote.R;
import vn.mran.simplenote.activity.AddNoteActivity;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.InitPresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.util.ResizeBitmap;
import vn.mran.simplenote.util.ScreenUtil;

/**
 * Created by Mr An on 20/08/2016.
 */
public class AddNotePresenter implements InitPresenter {
    public static final byte SAVE_EXCEPTION = 0;
    public static final byte EMPTY_CONTENT = 1;
    private int MAX_WIDTH;
    private StringBuilder imageData;
    private Context context;
    private AddNotesView addNotesView;
    private Realm realm;
    private FileUtil destination;

    public AddNotePresenter(Context context) {
        this.context = context;
        this.addNotesView = (AddNotesView) context;
        init();
    }

    public void save(String title, String content, long folderId, int colorId, boolean back) {
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
                if (-1 == colorId)
                    colorId = R.color.white;
                notes.setColorId(colorId);

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

    public void addImage(Bitmap bitmap, EditText editText) {
        List<Object> list = createImageEditText(context, bitmap, editText);
        addNotesView.addImage((SpannableStringBuilder) list.get(0));
        addNotesView.addSetTxtContentSelection((int) list.get(1));
    }

    public Bitmap createBitmapFromFile(File file) {
        try {
            imageData = new StringBuilder();
            imageData.append(AddImageUtil.NODE_IMAGE_START);
            imageData.append(Uri.fromFile(file).toString());
            imageData.append(AddImageUtil.NODE_IMAGE_END);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

            if (bitmap.getWidth() > MAX_WIDTH)
                bitmap = ResizeBitmap.resize(bitmap, MAX_WIDTH);
            return bitmap;
        } catch (Exception e) {
            Log.d(DataUtil.TAG_ADD_IMAGE_UTIL, e.getMessage());
            return null;
        }
    }

    public List<Object> createImageEditText(Context context, Bitmap bitmap, EditText editText) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        int selectionCursor = editText.getSelectionStart();
        editText.getText().insert(selectionCursor, imageData.toString());
        selectionCursor = editText.getSelectionStart();
        SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
        builder.setSpan(new ImageSpan(drawable), selectionCursor - imageData.toString().length(), selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        List<Object> list = new ArrayList<>();
        list.add(builder);
        list.add(selectionCursor);
        return list;
    }

    public FileUtil getDestination() {
        return destination;
    }

    @Override
    public void init() {
        realm = RealmController.with().getRealm();
        MAX_WIDTH = (int) ScreenUtil.getScreenWidth(((AddNoteActivity) context).getWindowManager()) / 3;
    }
}
