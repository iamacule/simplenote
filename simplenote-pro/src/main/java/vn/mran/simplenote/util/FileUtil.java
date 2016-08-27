package vn.mran.simplenote.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by MrAn on 12-Aug-16.
 */
public class FileUtil {
    public final String SIMPLE_NOTE_FOLDER_NAME = "/SimpleNotes/";
    public final String IMAGE_FOLDER = "/Image";
    private File file = null;

    public FileUtil(String fileName) {
        File utrFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME);
        File childFolder = null;
        if (!utrFolder.exists()) {
            utrFolder.mkdir();
            Log.d(DataUtil.TAG_FILE_UTIL, "Create SimpleNotes folder success");
            childFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME + IMAGE_FOLDER);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + IMAGE_FOLDER);
            }
        } else {
            childFolder = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME + IMAGE_FOLDER);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + IMAGE_FOLDER);
            }
        }
        file = new File(childFolder, File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create new file success : " + fileName);
            } catch (Exception e) {
                Log.d(DataUtil.TAG_FILE_UTIL, "Create new file fail");
            }
        } else {
            Log.d(DataUtil.TAG_FILE_UTIL, "Founded : " + fileName);
        }
    }

    public File get() {
        return file;
    }
}
