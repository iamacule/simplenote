package vn.mran.simplenote.util;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MrAn on 25-Aug-16.
 */
public class StringUtil {
    private static final String IMAGE_STRING = "{Image}";

    public static int getPostTitleToCut(String data) {
        return data.indexOf(AddImageUtil.NODE_IMAGE_START);
    }

    public static int getPostPathEnd(String data) {
        return data.indexOf(AddImageUtil.NODE_IMAGE_END) + AddImageUtil.NODE_IMAGE_END.length();
    }

    public static List<Point> getListImageInContent(String content) {
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
}
