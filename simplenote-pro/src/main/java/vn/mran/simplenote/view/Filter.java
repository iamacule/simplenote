package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 18-Aug-16.
 */
public class Filter {
    public LinearLayout parent;
    public LinearLayout btnSort;
    public LinearLayout btnFolder;
    public TextView txtSortStatus;
    public TextView txtFolderName;

    public Filter(View view) {
        parent = (LinearLayout) view.findViewById(R.id.parent);
        btnSort = (LinearLayout) view.findViewById(R.id.btnSort);
        btnFolder = (LinearLayout) view.findViewById(R.id.btnFolder);
        txtSortStatus = (TextView) view.findViewById(R.id.txtSortStatus);
        txtFolderName = (TextView) view.findViewById(R.id.txtFolderName);
        TouchEffect.addAlpha(btnSort);
        TouchEffect.addAlpha(btnFolder);
    }
}
