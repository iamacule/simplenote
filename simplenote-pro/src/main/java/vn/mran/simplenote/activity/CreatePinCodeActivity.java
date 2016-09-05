package vn.mran.simplenote.activity;

import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.CreatePinCodePresenter;
import vn.mran.simplenote.mvp.view.CreatePinCodeView;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.effect.TouchEffect;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by Mr An on 05/09/2016.
 */
public class CreatePinCodeActivity extends BaseActivity implements CreatePinCodeView {
    private CustomEditText inputPin;
    private CustomEditText inputConfirmPin;
    private TextView btnOK;
    private TextView btnCancel;
    private TextView txtMessage;
    private LinearLayout btnShowPin;
    private CheckBox checkBox;
    private boolean isShowPin = false;
    private int currentPinCode = -1;
    private CreatePinCodePresenter createPinCodePresenter;

    @Override
    public int getView() {
        return R.layout.activity_create_pin;
    }

    @Override
    public void initView() {
        header.btnMenu.setVisibility(View.GONE);
        header.title.setText(getString(R.string.create_pin_code));
        header.setDefaultBtnBack();

        View view = getWindow().getDecorView().getRootView();
        inputPin = new CustomEditText(view, R.id.inputPin);
        inputConfirmPin = new CustomEditText(view, R.id.inputConfirmPin);
        inputPin.txtMain.setHint("");
        inputPin.editText.setHint("");
        inputConfirmPin.txtMain.setHint("");
        inputConfirmPin.editText.setHint("");
        inputConfirmPin.lnMain.setVisibility(View.GONE);
        inputPin.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        inputConfirmPin.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        inputPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        inputConfirmPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        inputPin.editText.setTextSize(getResources().getDimension(R.dimen.app_text_size_big));
        inputConfirmPin.editText.setTextSize(getResources().getDimension(R.dimen.app_text_size_big));
        inputPin.editText.setGravity(Gravity.CENTER);
        inputConfirmPin.editText.setGravity(Gravity.CENTER);
        inputPin.editText.requestFocus();
        inputPin.editText.setFilters(limitCharacter());
        inputConfirmPin.editText.setFilters(limitCharacter());

        btnShowPin = (LinearLayout) findViewById(R.id.btnShowPin);
        TouchEffect.addAlpha(btnShowPin);
        checkBox = (CheckBox) findViewById(R.id.check);
        checkBox.setClickable(false);

        btnOK = (TextView) findViewById(R.id.btnOK);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        btnShowPin = (LinearLayout) findViewById(R.id.btnShowPin);
    }

    @Override
    public void initValue() {
        createPinCodePresenter = new CreatePinCodePresenter(this);
    }

    @Override
    public void initAction() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnShowPin:
                        showHidePin();
                        break;
                    case R.id.btnOK:
                        next();
                        break;
                    case R.id.btnCancel:
                        back();
                        break;
                }
            }

            private void showHidePin() {
                if (!isShowPin) {
                    inputPin.editText.setTransformationMethod(null);
                    inputConfirmPin.editText.setTransformationMethod(null);
                    inputPin.editText.invalidate();
                    inputConfirmPin.editText.invalidate();
                } else {
                    inputPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    inputConfirmPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    inputPin.editText.invalidate();
                    inputConfirmPin.editText.invalidate();
                }
                inputPin.editText.setSelection(inputPin.editText.length());
                inputConfirmPin.editText.setSelection(inputConfirmPin.editText.length());
                isShowPin = !isShowPin;
                checkBox.setChecked(isShowPin);
            }
        };
        btnShowPin.setOnClickListener(onClickListener);
        btnOK.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void back() {
        if (-1 != currentPinCode) {
            inputPin.lnMain.setVisibility(View.VISIBLE);
            inputConfirmPin.lnMain.setVisibility(View.GONE);
            inputPin.lnMain.startAnimation(AnimationUtil.fadeIn(this));
            inputPin.editText.requestFocus();
            currentPinCode = -1;
            btnOK.setText(getString(R.string.next));
            btnCancel.setText(getString(R.string.cancel));
            txtMessage.setText(getString(R.string.input_pin));
            txtMessage.startAnimation(AnimationUtil.fadeIn(this));
        } else {
            CreatePinCodeActivity.super.onBackPressed();
        }
    }

    private void next() {
        if (-1 == currentPinCode) {
            if (inputPin.editText.getText().length() >= 4) {
                inputPin.lnMain.setVisibility(View.GONE);
                inputConfirmPin.lnMain.setVisibility(View.VISIBLE);
                inputConfirmPin.lnMain.startAnimation(AnimationUtil.fadeIn(this));
                inputConfirmPin.editText.requestFocus();
                currentPinCode = Integer.parseInt(inputPin.editText.getText().toString());
                btnOK.setText(getString(R.string.confirm));
                btnCancel.setText(getString(R.string.back));
                txtMessage.setText(getString(R.string.input_pin_confirm));
                txtMessage.startAnimation(AnimationUtil.fadeIn(this));
            } else {
                Boast.makeText(this, getString(R.string.error_length_pin)).show();
            }
        } else {
            String error = null;
            int pin = Integer.parseInt(inputConfirmPin.editText.getText().toString());
            if (inputConfirmPin.editText.getText().length() < 4)
                error = getString(R.string.error_length_pin);
            else {
                if (pin != currentPinCode) {
                    error = getString(R.string.error_confirm_pin);
                }
            }

            if (null == error) {
                createPinCodePresenter.createPinCode(pin);
            } else {
                Boast.makeText(this, error).show();
            }
        }
    }

    public InputFilter[] limitCharacter() {
        InputFilter[] filterArraym = new InputFilter[1];
        filterArraym[0] = new InputFilter.LengthFilter(6);
        return filterArraym;
    }

    @Override
    public void onCreatePinCodeSuccess() {
        Boast.makeText(this, getString(R.string.create_pin_success)).show();
        super.onBackPressed();
    }
}
