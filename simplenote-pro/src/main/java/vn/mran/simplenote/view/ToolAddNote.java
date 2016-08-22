package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class ToolAddNote {
    public LinearLayout btnClear;
    public LinearLayout btnSave;
    public LinearLayout btnAddPhoto;
    public TextView txtDate;
    public TextView txtFolder;

    public ToolAddNote(View view) {
        btnClear = (LinearLayout) view.findViewById(R.id.btnClear);
        btnSave = (LinearLayout) view.findViewById(R.id.btnSave);
        btnAddPhoto = (LinearLayout) view.findViewById(R.id.btnAddPhoto);
        txtDate = (TextView) view.findViewById(R.id.txtDate);
        txtFolder = (TextView) view.findViewById(R.id.txtFolder);
        TouchEffect.addAlpha(btnClear);
        TouchEffect.addAlpha(btnSave);
        TouchEffect.addAlpha(btnAddPhoto);
    }
}
