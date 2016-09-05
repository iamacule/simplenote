package vn.mran.simplenote.activity;

import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.SecurityPresenter;
import vn.mran.simplenote.mvp.view.SecurityView;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.view.ButtonSecurity;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by Mr An on 05/09/2016.
 */
public class SecurityActivity extends BaseActivity implements SecurityView {
    private ButtonSecurity btnNone;
    private ButtonSecurity btnPin;
    private SecurityPresenter securityPresenter;
    public static String currentSecurityType = "";

    @Override
    protected void onResume() {
        super.onResume();
        securityPresenter.checkSecurityType();
    }

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

        btnNone.txtName.setText(getString(R.string.none));
        btnNone.txtDetail.setText(getString(R.string.no_lock));
        btnPin.txtName.setText(getString(R.string.pin));
        btnPin.txtDetail.setText(getString(R.string.pin_detail));
    }

    @Override
    public void initValue() {
        securityPresenter = new SecurityPresenter(this);
    }

    @Override
    public void initAction() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnNone:
                        securityPresenter.onClickNone();
                        break;
                    case R.id.btnPin:
                        securityPresenter.onClickPinCode();
                        break;
                }
            }
        };
        btnNone.lnMain.setOnClickListener(clickListener);
        btnPin.lnMain.setOnClickListener(clickListener);
    }

    @Override
    public void onReturnLockType(String type) {
        currentSecurityType = type;
        Log.d(DataUtil.TAG_SECURITY, "Current lock type : " + type);
        switch (type) {
            case Constant.SECURITY_NONE:
                btnNone.checkBox.setChecked(true);
                btnPin.checkBox.setChecked(false);
                break;
            case Constant.SECURITY_PIN:
                btnNone.checkBox.setChecked(false);
                btnPin.checkBox.setChecked(true);
                break;
        }
    }

    @Override
    public void onCreateNewPinCode() {
        goToActivity(CreatePinCodeActivity.class);
    }

    @Override
    public void onInputPinCode(String flag) {
        goToActivityWithIntData(InputPinCodeActivity.class, Constant.SECURITY_FLAG, flag);
    }

    @Override
    public void onNone() {
        btnNone.checkBox.setChecked(true);
        btnPin.checkBox.setChecked(false);
    }
}
