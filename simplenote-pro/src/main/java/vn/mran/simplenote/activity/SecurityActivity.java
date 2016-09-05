package vn.mran.simplenote.activity;

import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.SecurityPresenter;
import vn.mran.simplenote.mvp.view.SecurityView;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.view.ButtonSecurity;

/**
 * Created by Mr An on 05/09/2016.
 */
public class SecurityActivity extends BaseActivity implements SecurityView {
    private ButtonSecurity btnNone;
    private ButtonSecurity btnPin;
    private ButtonSecurity btnFinger;
    private SecurityPresenter securityPresenter;

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
        securityPresenter = new SecurityPresenter(this);
        securityPresenter.checkSecurityType();
    }

    @Override
    public void initAction() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnNone:
                        securityPresenter.updateSecurity(Constant.SECURITY_NONE);
                        btnNone.checkBox.setChecked(true);
                        btnPin.checkBox.setChecked(false);
                        btnFinger.checkBox.setChecked(false);
                        break;
                    case R.id.btnPin:
                        securityPresenter.updateSecurity(Constant.SECURITY_PIN);
                        btnNone.checkBox.setChecked(false);
                        btnPin.checkBox.setChecked(true);
                        btnFinger.checkBox.setChecked(false);
                        break;
                    case R.id.btnFinger:
                        securityPresenter.updateSecurity(Constant.SECURITY_FINGER);
                        btnNone.checkBox.setChecked(false);
                        btnPin.checkBox.setChecked(false);
                        btnFinger.checkBox.setChecked(true);
                        break;
                }
            }
        };
        btnNone.lnMain.setOnClickListener(clickListener);
        btnPin.lnMain.setOnClickListener(clickListener);
        btnFinger.lnMain.setOnClickListener(clickListener);
    }

    @Override
    public void onReturnLockType(String type) {
        Log.d(DataUtil.TAG_SECURITY, "Current lock type : " + type);
        switch (type) {
            case Constant.SECURITY_NONE:
                btnNone.checkBox.setChecked(true);
                btnPin.checkBox.setChecked(false);
                btnFinger.checkBox.setChecked(false);
                break;
            case Constant.SECURITY_PIN:
                btnNone.checkBox.setChecked(false);
                btnPin.checkBox.setChecked(true);
                btnFinger.checkBox.setChecked(false);
                break;
            case Constant.SECURITY_FINGER:
                btnNone.checkBox.setChecked(false);
                btnPin.checkBox.setChecked(false);
                btnFinger.checkBox.setChecked(true);
                break;
        }
    }
}
