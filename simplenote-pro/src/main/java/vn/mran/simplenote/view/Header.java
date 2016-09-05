package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.activity.BaseActivity;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 18-Aug-16.
 */
public class Header {
    public TextView title;
    public LinearLayout btnBack;
    public LinearLayout btnMenu;

    public Header(View view) {
        title = (TextView) view.findViewById(R.id.txtTitle);
        btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        btnMenu = (LinearLayout) view.findViewById(R.id.btnMenu);
        TouchEffect.addAlpha(btnMenu);
        TouchEffect.addAlpha(btnBack);
    }

    public void setDefaultBtnBack() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) view.getContext()).onBackPressed();
            }
        });
    }
}
