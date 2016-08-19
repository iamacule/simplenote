package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class ToolAddNote {
    public LinearLayout btnClear;
    public LinearLayout btnSave;

    public ToolAddNote(View view) {
        btnClear = (LinearLayout) view.findViewById(R.id.btnClear);
        btnSave = (LinearLayout) view.findViewById(R.id.btnSave);
        TouchEffect.addAlpha(btnClear);
        TouchEffect.addAlpha(btnSave);
    }
}
