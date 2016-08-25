package vn.mran.simplenote.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by MrAn on 12-Aug-16.
 */
public class FileUtil {
    public final String TAG = "FileUtil";
    public final String SIMPLE_NOTE_FOLDER_NAME = "/SimpleNote/";
    public final String IMAGE_FOLDER = "/Image";
    private File file = null;

    public FileUtil(String fileName) {
        File utrFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME);
        File childFolder = null;
        if (!utrFolder.exists()) {
            utrFolder.mkdir();
            Log.d(TAG, "Create AUDIUTR folder success");
            childFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME + IMAGE_FOLDER);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(TAG, "Create folder success : " + IMAGE_FOLDER);
            }
        } else {
            childFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME + IMAGE_FOLDER);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(TAG, "Create folder success : " + IMAGE_FOLDER);
            }
        }
        file = new File(childFolder, File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.d(TAG, "Create new file success : " + fileName);
            } catch (Exception e) {
                Log.d(TAG, "Create new file fail");
            }
        } else {
            Log.d(TAG, "Founded : " + fileName);
        }
    }

    public void write(String data) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(data.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error message : " + e.getMessage());
            Log.d(TAG, "File not found , please create file first");
        }
    }

    public File get() {
        return file;
    }
}
