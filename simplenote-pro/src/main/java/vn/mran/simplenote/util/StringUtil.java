package vn.mran.simplenote.util;

import android.net.Uri;

/**
 * Created by MrAn on 25-Aug-16.
 */
public class StringUtil {
    public static final String IMAGE_STRING = "{Image}";
    public static final String BEGIN_STRING = "d@!dv89)&(*wdef";

    public static int getPostTitleToCut(String data) {
        return data.indexOf(AddImageUtil.NODE_IMAGE_START);
    }

    public static int getPostPathEnd(String data) {
        return data.indexOf(AddImageUtil.NODE_IMAGE_END) + AddImageUtil.NODE_IMAGE_END.length();
    }

    public static int getLastIndex(String data){
        return data.lastIndexOf(AddImageUtil.NODE_IMAGE_END)+AddImageUtil.NODE_IMAGE_END.length();
    }

    public static String filterTitle(String data) {
        int posImageNode = StringUtil.getPostTitleToCut(data);
        if (-1 != posImageNode) {
            if (0 == posImageNode) {
                return IMAGE_STRING;
            } else {
                return data.substring(0, posImageNode) + " " + IMAGE_STRING;
            }
        }
        return data;
    }

    public static Uri getUriFormData(String data) {
        return Uri.parse(data.substring(AddImageUtil.NODE_IMAGE_START.length(), data.length() - AddImageUtil.NODE_IMAGE_END.length()));
    }
}
