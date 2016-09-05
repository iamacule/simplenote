package vn.mran.simplenote.activity;

import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.view.ButtonSecurity;

/**
 * Created by Mr An on 05/09/2016.
 */
public class SecurityActivity extends BaseActivity {
    private ButtonSecurity btnNone;
    private ButtonSecurity btnPin;
    private ButtonSecurity btnFinger;

    @Override
    public int getView() {
        return R.layout.activity_security;
    }

    @Override
    public void initView() {
        header.title.setText(getString(R.string.security));
        header.btnMenu.setVisibility(View.GONE);
        header.setDefaultBtnBack();

        View v = getWindow().getDecorView().getRootView();
        btnNone = new ButtonSecurity(v, R.id.btnNone);
        btnPin = new ButtonSecurity(v, R.id.btnPin);
        btnFinger = new ButtonSecurity(v, R.id.btnFinger);

        btnNone.txtName.setText(getString(R.string.none));
        btnNone.txtDetail.setText(getString(R.string.no_lock));
        btnPin.txtName.setText(getString(R.string.pin));
        btnPin.txtDetail.setText(getString(R.string.pin_detail));
        btnFinger.txtName.setText(getString(R.string.finger_print));
        btnFinger.txtDetail.setText(getString(R.string.finger_print_detail));
    }

    @Override
    public void initValue() {
        FileUtil fileUtil = new FileUtil(DataUtil.LOCK_TYPE_FILE, DataUtil.DATA_FOLDER);
        String type = fileUtil.readString();
        Log.d(DataUtil.TAG_SECURITY, "Current lock type : " + type);
    }

    @Override
    public void initAction() {

    }
}
