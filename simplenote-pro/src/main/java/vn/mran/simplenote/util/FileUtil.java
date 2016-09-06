package vn.mran.simplenote.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by MrAn on 12-Aug-16.
 */
public class FileUtil {
    private static final String SIMPLE_NOTE_FOLDER_NAME = "/SimpleNotes/";
    private static final String EXPORT_FOLDER_NAME = "/Export";
    public static String INTERNAL_STORAGE_PATH;
    public static final String PUBLIC_STORAGE_PATH = Environment.getExternalStorageDirectory().toString() + SIMPLE_NOTE_FOLDER_NAME;
    private File file = null;
    private File childFolder;

    public FileUtil() {
    }

    public File getChildFolder() {
        return childFolder;
    }

    public void createFolder(String folderName, String path) {
        if (null == path)
            path = INTERNAL_STORAGE_PATH + SIMPLE_NOTE_FOLDER_NAME;
        ;
        File utrFolder = new File(path);
        childFolder = null;
        if (!utrFolder.exists()) {
            utrFolder.mkdir();
            Log.d(DataUtil.TAG_FILE_UTIL, "Create SimpleNotes folder success");
            childFolder = new File(utrFolder, folderName);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + folderName);
            }
        } else {
            childFolder = new File(path, folderName);
            if (!childFolder.exists()) {
                childFolder.mkdir();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + folderName);
            }
        }
    }

    public FileUtil(String fileName, String folderName, String path) {
        createFolder(folderName, path);
        file = new File(childFolder, File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.d(DataUtil.TAG_FILE_UTIL, "Create new file success : " + fileName);
            } catch (Exception e) {
                Log.d(DataUtil.TAG_FILE_UTIL, "Create new file fail");
            }
        }
    }

    public boolean areFilePresent(String fileName, String folderName) {
        File file = new File(Environment.getExternalStorageDirectory() + SIMPLE_NOTE_FOLDER_NAME + folderName + File.separator + fileName);
        return file.exists();
    }

    public void writeString(String data) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(data.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            Log.d(DataUtil.TAG_FILE_UTIL, "Error message : " + e.getMessage());
            Log.d(DataUtil.TAG_FILE_UTIL, "File not found , please create file first");
        }
    }

    public void clear() {
        if (file.delete()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String readString() {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public File get() {
        return file;
    }
}
