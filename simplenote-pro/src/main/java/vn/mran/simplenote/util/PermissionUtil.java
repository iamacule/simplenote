package vn.mran.simplenote.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Created by MrAn on 29-Apr-16.
 */
public class PermissionUtil {
    public static final int READ_EXTERNAL_STORAGE = 0;
    public static boolean permissionReadExternalStorage = false;

    public static void checkPermission(Activity activity, String permission, int idCallBack) {
        Preferences preferences = new Preferences(activity);
        boolean firstTimeAccount = preferences.getBooleanValue(permission);

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // 2. Asked before, and the user said "no"
            ActivityCompat.requestPermissions(activity, new String[]{permission}, idCallBack);
        } else {
            if (firstTimeAccount) {
                // 1. first time, never asked
                preferences.storeValue(permission, false);
                // Account permission has not been granted, request it directly.
                ActivityCompat.requestPermissions(activity, new String[]{permission}, idCallBack);
            } else {
                // 3. If you asked a couple of times before, and the user has said "no, and stop asking"
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        }
    }
}
