package vn.mran.simplenote.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.InitPresenter;
import vn.mran.simplenote.mvp.view.NotesDetailView;
import vn.mran.simplenote.performance.CacheBitmap;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.ResizeBitmap;
import vn.mran.simplenote.util.ScreenUtil;
import vn.mran.simplenote.util.StringUtil;

/**
 * Created by MrAn on 26-Aug-16.
 */
public class NotesDetailPresenter implements InitPresenter {
    private NotesDetailView notesDetailView;
    private Activity activity;
    private LoadContentAsync loadContentAsync;
    private float imageWidth;
    public static final byte UPDATE_EXCEPTION = 0;
    public static final byte EMPTY_CONTENT = 1;
    private StringBuilder imageData;
    private Realm realm;

    public NotesDetailPresenter(Activity activity) {
        this.activity = activity;
        this.notesDetailView = (NotesDetailView) activity;
        imageWidth = ScreenUtil.getScreenWidth(activity.getWindowManager()) / 3;
        init();
    }

    public void loadData(String content) {
        List<Point> pointList = getListImageInContent(content);
        if (pointList.size() > 0) {
            startLoad(pointList, content);
        } else {
            notesDetailView.onLoadContent(new SpannableStringBuilder(content));
        }
    }

    private List<Point> getListImageInContent(String content) {
        List<Point> pointList = new ArrayList<>();

        for (int i = -1; (i = content.indexOf(AddImageUtil.NODE_IMAGE_START, i + 1)) != -1; ) {
            Point point = new Point();
            point.x = i;
            for (int j = i - 1; (j = content.indexOf(AddImageUtil.NODE_IMAGE_END, j + 1)) != -1; ) {
                point.y = j + AddImageUtil.NODE_IMAGE_END.length();
                break;
            }
            pointList.add(point);
        }
        return pointList;
    }

    private class LoadContentAsync extends AsyncTask<Void, Void, SpannableStringBuilder> {
        private List<Point> pointList;
        private String content;

        public LoadContentAsync(List<Point> pointList, String content) {
            this.pointList = pointList;
            this.content = content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notesDetailView.onWaiting();
        }

        @Override
        protected SpannableStringBuilder doInBackground(Void... voids) {
            List<String> listDataImage = new ArrayList<>();
            List<String> listDataNormal = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                listDataImage.add(content.substring(pointList.get(i).x, pointList.get(i).y));
                if (pointList.size() > 1) {
                    if (i == 0 && pointList.get(i).x == 0) {
                        listDataNormal.add(StringUtil.BEGIN_STRING);
                    } else if (i == 0 && pointList.get(i).x != 0) {
                        listDataNormal.add(content.substring(0, pointList.get(i).x));
                    } else {
                        try {
                            if (pointList.get(i).x - pointList.get(i - 1).y > 1) {
                                listDataNormal.add(content.substring(pointList.get(i - 1).y,
                                        pointList.get(i).x));
                            } else {
                                listDataNormal.add(content.substring(pointList.get(i).y, pointList.get(i + 1).x));
                            }
                        } catch (Exception e) {
                            listDataNormal.add(content.substring(pointList.get(i).y, content.length()));
                            break;
                        }
                    }
                } else {
                    if (pointList.get(i).x == 0) {
                        listDataNormal.add(StringUtil.BEGIN_STRING);
                    } else {
                        listDataNormal.add(content.substring(0, pointList.get(i).x));
                    }
                }
            }
            String contentShow = content;
            for (String s : listDataImage) {
                contentShow = contentShow.replace(s, "");
            }
            List<Uri> listUri = new ArrayList<>();
            for (String s : listDataImage) {
                listUri.add(StringUtil.getUriFormData(s));
            }

            SpannableStringBuilder builder = loadData(listDataNormal, listDataImage, listUri,
                    imageWidth, content);
            return builder;
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder builder) {
            super.onPostExecute(builder);
            notesDetailView.onLoadContent(builder);
        }
    }

    private void startLoad(List<Point> pointsList, String content) {
        stopLoad();
        loadContentAsync = new LoadContentAsync(pointsList, content);
        loadContentAsync.execute();
    }

    private void stopLoad() {
        if (loadContentAsync != null && !loadContentAsync.isCancelled()) {
            loadContentAsync.cancel(true);
            loadContentAsync = null;
        }
    }

    private SpannableStringBuilder loadData(List<String> listDataNormal
            , List<String> listDataImage, List<Uri> listUri, float width, String fullString) {
        SpannableStringBuilder builderParent = new SpannableStringBuilder();
        int selectionCursor;
        for (int i = 0; i < listDataNormal.size(); i++) {
            Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER,"Data : "+listDataImage.get(i));
            StringBuilder totalData = new StringBuilder();
            Bitmap bitmap = createBitmapFromURINew(activity, listUri.get(i), width);
            BitmapDrawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            if (listDataNormal.get(i).equals(StringUtil.BEGIN_STRING)) {
                totalData.append(listDataImage.get(i));
            } else {
                totalData.append(listDataNormal.get(i));
                totalData.append(listDataImage.get(i));
            }
            selectionCursor = totalData.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(totalData.toString());
            builder.setSpan(new ImageSpan(drawable), selectionCursor - listDataImage.get(i).length(), selectionCursor,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builderParent.append(builder);
        }
        int check = StringUtil.getLastIndex(fullString);
        if (check != fullString.length()) {
            builderParent.append(fullString.substring(check, fullString.length()));
        }
        return builderParent;
    }

    public Bitmap createBitmapFromURINew(Context context, Uri imageUri, float width) {
        Bitmap bitmap;
        bitmap = CacheBitmap.getInstance().getBitmapFromMemCache(imageUri.toString());
        if (bitmap != null) {
            Log.d(DataUtil.TAG_NOTES_DETAIL_PRESENTER, "Get from cache success");
            return bitmap;
        } else {
            try {
                InputStream stream = context.getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(stream);
                if (bitmap.getWidth() > width)
                    bitmap = ResizeBitmap.resize(bitmap, width);
                stream.close();
                CacheBitmap.getInstance().addBitmapToMemoryCache(imageUri.toString(), bitmap);
                Log.d(DataUtil.TAG_NOTES_DETAIL_PRESENTER, "Add to cache success");
                return bitmap;
            } catch (Exception e) {
                Log.d(DataUtil.TAG_ADD_IMAGE_UTIL, e.getMessage());
                return null;
            }
        }
    }

    public void update(Notes notes,String title, String content, long folderId,int colorId) {
        if (!DataUtil.checkStringEmpty(title)) {
            if (DataUtil.checkStringEmpty(content))
                title = DataUtil.createTitle(content);
            else {
                notesDetailView.onUpdateFail(EMPTY_CONTENT);
                return;
            }
        }
        if (DataUtil.checkStringEmpty(content)) {
            try {

                realm.beginTransaction();
                notes.setTitle(title);
                notes.setContent(content);
                notes.setFolderId(folderId);
                if(-1==colorId)
                    colorId = R.color.white;
                notes.setColorId(colorId);
                realm.copyToRealm(notes);
                realm.commitTransaction();

                Notes result = RealmController.with().getNotesById(notes.getId());
                if (null != result) {
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Update success as title : " + notes.getTitle());
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Update success as content : " + notes.getContent());
                    Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Update success as folderId : " + notes.getFolderId());
                    notesDetailView.onUpdateFinish();
                } else {
                    notesDetailView.onUpdateFail(UPDATE_EXCEPTION);
                }
            } catch (Exception e) {
                Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Update error : " + e.getMessage());
                notesDetailView.onUpdateFail(UPDATE_EXCEPTION);
            }
        } else {
            notesDetailView.onUpdateFail(EMPTY_CONTENT);
        }
    }

    public void addImage(Bitmap bitmap, EditText editText) {
        List<Object> list = createImageEditText(activity, bitmap, editText);
        notesDetailView.addImage((SpannableStringBuilder) list.get(0));
        notesDetailView.addSetTxtContentSelection((int) list.get(1));
    }

    public Bitmap createBitmapFromURI(Context context, Uri imageUri, float width) {
        try {
            imageData = new StringBuilder();
            imageData.append(AddImageUtil.NODE_IMAGE_START);
            imageData.append(imageUri.toString());
            imageData.append(AddImageUtil.NODE_IMAGE_END);
            InputStream stream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            if (bitmap.getWidth() > width)
                bitmap = ResizeBitmap.resize(bitmap, width);
            stream.close();
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

    @Override
    public void init() {
        realm = RealmController.with().getRealm();
    }
}
