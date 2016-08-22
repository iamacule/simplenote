package vn.mran.simplenote.mvp.view;

import android.text.SpannableStringBuilder;

/**
 * Created by Mr An on 20/08/2016.
 */
public interface AddNotesView {
    void onSaveFinish(boolean bacPress);
    void onSaveFail(byte messageId);
    void addImage(SpannableStringBuilder data);
    void addSetTxtContentSelection(int selection);
}
