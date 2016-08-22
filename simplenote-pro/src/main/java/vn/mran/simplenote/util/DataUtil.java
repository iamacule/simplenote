package vn.mran.simplenote.util;

import android.util.Log;

/**
 * Created by Mr An on 20/08/2016.
 */
public class DataUtil {
    private static final String APP_TAG = "SimpleNote:";
    public static final String TAG_DATA_UTIL = APP_TAG+"DataUtil";
    public static final String TAG_MAIN_ACTIVITY = APP_TAG+"MainActivity";
    public static final String TAG_ADD_NOTES_PRESENTER = APP_TAG+"AddNotesPresenter";
    public static final String TAG_ADD_NOTES_ACTIVITY = APP_TAG+"AddNoteActivity";
    public static final String TAG_ADD_FOLDER_ACTIVITY = APP_TAG+"AddFolderActivity";
    public static final String TAG_DIALOG_ADD_FOLDER = APP_TAG+"DialogAddFolder";
    public static final String TAG_REALM_CONTROLLER = APP_TAG+"RealmController";
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

    public static String createTitle(String content){
        if(30>=content.length()){
            Log.d(TAG_DATA_UTIL,"Create title : "+content);
            return content;
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0 ; i< content.length() ; i++){
                stringBuilder.append(content.charAt(i));
                if(30<=i && 32==content.charAt(i)){
                    break;
                } else if(40<=i){
                    break;
                }
            }
            Log.d(TAG_DATA_UTIL,"Create title : "+stringBuilder.toString());
            return stringBuilder.toString();
        }
    }
}
