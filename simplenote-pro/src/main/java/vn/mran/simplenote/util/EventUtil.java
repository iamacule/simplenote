package vn.mran.simplenote.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by MrAn on 24-Aug-16.
 */
public class EventUtil {
    public static class KeyBoard {
        private InputMethodManager imm;

        public KeyBoard(Context context) {
            imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        public void show(View v) {
            if (imm != null) {
                imm.showSoftInput(v,InputMethodManager.SHOW_IMPLICIT);
            }
        }

        public void hide(View view) {
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
