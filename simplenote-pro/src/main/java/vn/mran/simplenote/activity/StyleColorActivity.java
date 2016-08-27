package vn.mran.simplenote.activity;

import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.StyleColor;

/**
 * Created by Mr An on 27/08/2016.
 */
public class StyleColorActivity extends BaseActivity {
    private StyleColor styleColor;
    @Override
    public int getView() {
        return R.layout.activity_style_color;
    }

    @Override
    public void initView() {
        styleColor = new StyleColor(getWindow().getDecorView().getRootView());
    }

    @Override
    public void initValue() {

    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){

                    case R.id.btnConfirm:
                        confirmClick();
                        break;
                    case R.id.btnCancel:
                        cancelClick();
                        break;
                }
            }
        };
        styleColor.btnCancel.setOnClickListener(click);
        styleColor.btnConfirm.setOnClickListener(click);
    }

    private void cancelClick() {
        onBackPressed();
    }

    private void confirmClick() {
        currentColorId = styleColor.colorChooser;
        onBackPressed();
    }
}
