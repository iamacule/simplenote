package vn.mran.simplenote.util;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrAn on 25-Aug-16.
 */
public class StringUtil {
    public static int getPostTitleToCut(String data) {
        return data.indexOf(AddImageUtil.NODE_IMAGE_START);
    }
}
