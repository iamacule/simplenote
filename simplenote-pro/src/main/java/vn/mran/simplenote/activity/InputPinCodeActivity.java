package vn.mran.simplenote.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.InputPinCodePresenter;
import vn.mran.simplenote.mvp.view.InputPinCodeView;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.effect.TouchEffect;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by Mr An on 05/09/2016.
 */
public class InputPinCodeActivity extends BaseActivity implements InputPinCodeView {
    private CustomEditText inputPin;
    private TextView btnConfirm;
    private TextView btnCancel;
    private TextView txtMessage;
    private LinearLayout btnShowPin;
    private CheckBox checkBox;
    private boolean isShowPin = false;
    private InputPinCodePresenter inputPinCodePresenter;
    private String flag = null;

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flag = extras.getString(Constant.SECURITY_FLAG);
        }
    }

    @Override
    public int getView() {
        return R.layout.activity_input_pin;
    }

    @Override
    public void initView() {
        header.btnMenu.setVisibility(View.GONE);
        header.title.setText(getString(R.string.input_pin_code));
        header.setDefaultBtnBack();

        View view = getWindow().getDecorView().getRootView();
        inputPin = new CustomEditText(view, R.id.inputPin);
        inputPin.txtMain.setHint("");
        inputPin.editText.setHint("");
        inputPin.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
        inputPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        inputPin.editText.setTextSize(getResources().getDimension(R.dimen.app_text_size_big));
        inputPin.editText.setGravity(Gravity.CENTER);
        inputPin.editText.requestFocus();
        inputPin.editText.setFilters(limitCharacter());

        btnShowPin = (LinearLayout) findViewById(R.id.btnShowPin);
        TouchEffect.addAlpha(btnShowPin);
        checkBox = (CheckBox) findViewById(R.id.check);
        checkBox.setClickable(false);

        btnConfirm = (TextView) findViewById(R.id.btnConfirm);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        btnShowPin = (LinearLayout) findViewById(R.id.btnShowPin);
    }

    public InputFilter[] limitCharacter() {
        InputFilter[] filterArraym = new InputFilter[1];
        filterArraym[0] = new InputFilter.LengthFilter(6);
        return filterArraym;
    }

    @Override
    public void initValue() {
        inputPinCodePresenter = new InputPinCodePresenter(this);
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
                    case R.id.btnConfirm:
                        inputPinCodePresenter.check(inputPin.editText.getText().toString());
                        break;
                    case R.id.btnCancel:
                        InputPinCodeActivity.super.onBackPressed();
                        InputPinCodeActivity.this.finish();
                        break;
                }
            }

            private void showHidePin() {
                if (!isShowPin) {
                    inputPin.editText.setTransformationMethod(null);
                    inputPin.editText.invalidate();
                } else {
                    inputPin.editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    inputPin.editText.invalidate();
                }
                inputPin.editText.setSelection(inputPin.editText.length());
                isShowPin = !isShowPin;
                checkBox.setChecked(isShowPin);
            }
        };
        btnShowPin.setOnClickListener(onClickListener);
        btnConfirm.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    @Override
    public void onSuccess() {
        switch (flag) {
            case Constant.SECURITY_NONE:
                inputPinCodePresenter.updateToNone();
                InputPinCodeActivity.super.onBackPressed();
                break;
            case Constant.SECURITY_PIN:
                goToActivity(CreatePinCodeActivity.class);
                break;
        }
        InputPinCodeActivity.this.finish();
    }

    @Override
    public void onError(int messageId) {
        switch (messageId) {
            case InputPinCodePresenter.LENGTH_ERROR:
                Boast.makeText(InputPinCodeActivity.this, getString(R.string.error_length_pin)).show();
                break;

            case InputPinCodePresenter.WRONG_PIN:
                Boast.makeText(InputPinCodeActivity.this, getString(R.string.wrong_pin)).show();
                break;
        }
    }
}
