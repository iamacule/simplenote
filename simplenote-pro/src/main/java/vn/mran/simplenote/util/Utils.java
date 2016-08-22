package vn.mran.simplenote.util;

import java.text.SimpleDateFormat;

/**
 * Created by MrAn on 22-Aug-16.
 */
public class Utils {
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        return formatter.format(milliSeconds);
    }
}
