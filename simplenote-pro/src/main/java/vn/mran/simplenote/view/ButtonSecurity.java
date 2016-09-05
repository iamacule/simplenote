package vn.mran.simplenote.view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by Mr An on 05/09/2016.
 */
public class ButtonSecurity {
    public CheckBox checkBox;
    public LinearLayout lnMain;
    public TextView txtName;
    public TextView txtDetail;

    public ButtonSecurity(View view, int id) {
        lnMain = (LinearLayout) view.findViewById(id);
        txtName = (TextView) lnMain.findViewById(R.id.txtName);
        txtDetail = (TextView) lnMain.findViewById(R.id.txtDetail);
        checkBox = (CheckBox) lnMain.findViewById(R.id.check);
        TouchEffect.addAlpha(lnMain);
        checkBox.setClickable(false);
    }
}
