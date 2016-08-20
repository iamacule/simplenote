package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 18-Aug-16.
 */
public class Filter {
    public LinearLayout parent;
    public LinearLayout btnSort;
    public LinearLayout btnFilter;

    public Filter(View view) {
        parent = (LinearLayout) view.findViewById(R.id.parent);
        btnSort = (LinearLayout) view.findViewById(R.id.btnSort);
        btnFilter = (LinearLayout) view.findViewById(R.id.btnFilter);
        TouchEffect.addAlpha(btnSort);
        TouchEffect.addAlpha(btnFilter);
    }
}
