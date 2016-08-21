package vn.mran.simplenote.util;

import android.util.DisplayMetrics;
import android.view.*;

/**
 * Created by MrAn on 14/04/16.
 */
public class ScreenUtil {
    private static DisplayMetrics dm = new DisplayMetrics();

    /*
     * Get screen Width
     */
    public static float getScreenWidth(WindowManager windowManager){
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /*
     * Get Screen Height
     */
    public static float getScreenHeight(WindowManager windowManager){
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
