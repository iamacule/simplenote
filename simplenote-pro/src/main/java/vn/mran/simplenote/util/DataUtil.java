package vn.mran.simplenote.util;

import android.util.Log;

/**
 * Created by Mr An on 20/08/2016.
 */
public class DataUtil {
    private static final String APP_TAG = "SimpleNote:";
    public static final String TAG_DATA_UTIL = APP_TAG + "DataUtil";
    public static final String TAG_MAIN_ACTIVITY = APP_TAG + "MainActivity";
    public static final String TAG_ADD_NOTES_PRESENTER = APP_TAG + "AddNotesPresenter";
    public static final String TAG_ADD_NOTES_ACTIVITY = APP_TAG + "AddNoteActivity";
    public static final String TAG_ADD_FOLDER_ACTIVITY = APP_TAG + "AddFolderActivity";
    public static final String TAG_DIALOG_ADD_FOLDER = APP_TAG + "DialogAddFolder";
    public static final String TAG_REALM_CONTROLLER = APP_TAG + "RealmController";
    public static final String TAG_DIALOG_SORT = APP_TAG + "DialogSort";
    public static final String TAG_DIALOG_SELECT_FOLDER = APP_TAG + "DialogSelectFolder";
    public static final String TAD_NOTES_ADAPTER = APP_TAG + "NotesAdapter";
    public static final String TAG = "DataUtil";

    public static boolean checkStringEmpty(String data) {
        if (data.isEmpty())
            return false;
        if (data.equals(""))
            return false;
        if (null == data)
            return false;
        return true;
    }

    public static String createTitle(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String dataCheck = content.substring(0, 40);
            int checkContainImage = StringUtil.getPostTitleToCut(dataCheck);
            if (-1 != checkContainImage) {
                if (0 == checkContainImage) {
                    stringBuilder.append(content.substring(0, StringUtil.getPostPathEnd(content)));
                    stringBuilder.append(getTextTitle(content.substring(StringUtil.getPostPathEnd(content), content.length())));
                } else {
                    stringBuilder.append(content.substring(0, StringUtil.getPostPathEnd(content)));
                }
            } else {
                stringBuilder.append(getTextTitle(content));
            }
            Log.d(TAG_DATA_UTIL, "Create title : " + stringBuilder.toString());
            return stringBuilder.toString();
        }catch (Exception e){
            Log.d(TAG_DATA_UTIL, "Create title : " + content);
            return content;
        }
    }

    private static String getTextTitle(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        if (30 >= content.length()) {
            return content;
        } else {
            for (int i = 0; i < content.length(); i++) {
                stringBuilder.append(content.charAt(i));
                if (30 <= i && 32 == content.charAt(i)) {
                    break;
                } else if (35 <= i) {
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }
}
