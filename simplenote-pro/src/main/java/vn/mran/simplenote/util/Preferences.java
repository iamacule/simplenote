package vn.mran.simplenote.util;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by Covisoft on 24/12/2015.
 */
public final class Preferences {
    private SharedPreferences pref;
    private Editor editor;

    public static final String SIMPLE_NOTE = "SIMPLE_NOTE";
    public static final String PERMISSION_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    public Preferences(Context context) {
        pref = context.getSharedPreferences(SIMPLE_NOTE, context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void storeValue(String key, Object value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        }
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        }
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        if (value instanceof Long) {
            editor.putLong(key, (long) value);
        }
        editor.commit();
        Log.d("Preferences store", "Key(" + key + ") , Value(" + value + ")");
    }

    public boolean getBooleanValue(String key) {
        if (key.equals(PERMISSION_STORAGE)) {
            return pref.getBoolean(key, true);
        } else {
            return pref.getBoolean(key, false);
        }
    }
}
