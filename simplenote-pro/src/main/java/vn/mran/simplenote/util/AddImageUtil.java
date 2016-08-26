package vn.mran.simplenote.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrAn on 25-Aug-16.
 */
public class AddImageUtil {
    public static final String NODE_IMAGE_START = "a!G@G#1";
    public static final String NODE_IMAGE_END = "a!G@G#-1";
    private static StringBuilder imageData;

    public static Bitmap createBitmapFromURI(Context context, Uri imageUri, float width) {
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

    public static List<Object> createImageEditText(Context context, Bitmap bitmap, EditText editText) {
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

    public static void createImageEditText(Context context, List<String> listDataNormal
            , List<String> listDataImage, List<Uri> listUri, EditText editText,float width,String fullString) {
        editText.setText("");
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int selectionCursor;
        for (int i = 0 ; i <listDataNormal.size();i++){
            StringBuilder totalData = new StringBuilder();
            Bitmap bitmap = createBitmapFromURI(context,listUri.get(i),width);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            if(listDataNormal.get(i).equals("BEGIN")){
                totalData.append(listDataImage.get(i));
            }else {
                totalData.append(listDataNormal.get(i));
                totalData.append(listDataImage.get(i));
            }
            selectionCursor = totalData.length();
            Log.d(DataUtil.TAG_ADD_IMAGE_UTIL, "select : " + selectionCursor);
            builder = new SpannableStringBuilder(totalData.toString());
            builder.setSpan(new ImageSpan(drawable), selectionCursor - listDataImage.get(i).length(), selectionCursor,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            editText.append(builder);
        }
        int check = StringUtil.getLastIndex(fullString);
        if(check!=fullString.length()){
            editText.append(fullString.substring(check,fullString.length()));
        }
    }

    public static void createImageTextView(Context context, Bitmap bitmap, TextView textView) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        textView.setText(textView.getText() + imageData.toString());
        int selectionCursor = textView.getText().length() - 1;
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        builder.setSpan(new ImageSpan(drawable), selectionCursor - imageData.toString().length(), selectionCursor,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }

    public static Uri getUriFromString(String data) {
        Uri uri = Uri.parse(data);
        return uri;
    }

    public static Uri getUriFormData(String data) {
        return Uri.parse(data.substring(AddImageUtil.NODE_IMAGE_START.length(), data.length() - AddImageUtil.NODE_IMAGE_END.length()));
    }
}
