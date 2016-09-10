package vn.mran.simplenote.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
            if (null != folderName) {
                childFolder = new File(utrFolder, folderName);
                if (!childFolder.exists()) {
                    childFolder.mkdir();
                    Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + folderName);
                }
            } else {
                childFolder = utrFolder;
            }
        } else {
            if (null != folderName) {
                childFolder = new File(path, folderName);
                if (!childFolder.exists()) {
                    childFolder.mkdir();
                    Log.d(DataUtil.TAG_FILE_UTIL, "Create folder success : " + folderName);
                }
            } else {
                childFolder = utrFolder;
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
                e.printStackTrace();
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

    public static File copyFile(Context context, Uri sourceUri, String folder, String path) {
        try {
            File source = new File(getPath(context, sourceUri));
            FileUtil destination = new FileUtil(source.getName(), folder, path);
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(destination.get()).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            return destination.get();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void copyFile(File file, String folder, String path) {
        try {
            FileUtil destination = new FileUtil(file.getName(), folder, path);
            FileChannel src = new FileInputStream(file).getChannel();
            FileChannel dst = new FileOutputStream(destination.get()).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void delFile(File dir) {
        Log.d(DataUtil.TAG_FILE_UTIL, "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory()) {
                    Log.d(DataUtil.TAG_FILE_UTIL, "Recursive Call" + temp.getPath());
                    delFile(temp);
                } else {
                    Log.d(DataUtil.TAG_FILE_UTIL, "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false) {
                        Log.d(DataUtil.TAG_FILE_UTIL, "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }
}
