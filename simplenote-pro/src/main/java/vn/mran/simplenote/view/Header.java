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
    private View view;

    public Header(View view) {
        this.view = view;
        title = (TextView) view.findViewById(R.id.txtTitle);
        btnBack = (LinearLayout) view.findViewById(R.id.btnBack);
        TouchEffect.addAlpha(btnBack);
        btnMenu = (LinearLayout) view.findViewById(R.id.btnMenu);
        TouchEffect.addAlpha(btnMenu);
        title.setText(view.getContext().getString(R.string.app_name));
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
