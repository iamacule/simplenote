package vn.mran.simplenote.mvp.view;

import android.text.SpannableStringBuilder;

/**
 * Created by MrAn on 26-Aug-16.
 */
public interface NotesDetailView {
    void onWaiting();
    void onLoadContent(SpannableStringBuilder builder);
    void onUpdateFinish();
    void onUpdateFail(byte messageId);
    void addImage(SpannableStringBuilder data);
    void addSetTxtContentSelection(int selection);
}
