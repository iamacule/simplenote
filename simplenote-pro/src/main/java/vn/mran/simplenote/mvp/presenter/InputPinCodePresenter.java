package vn.mran.simplenote.mvp.presenter;

import android.content.Context;

import vn.mran.simplenote.mvp.view.InputPinCodeView;
import vn.mran.simplenote.util.Preferences;

/**
 * Created by Mr An on 05/09/2016.
 */
public class InputPinCodePresenter {
    public static final int LENGTH_ERROR = 0;
    public static final int WRONG_PIN = 1;
    private Context context;
    private InputPinCodeView inputPinCodeView;
    private Preferences preferences;

    public InputPinCodePresenter(Context context) {
        this.context = context;
        this.inputPinCodeView = (InputPinCodeView) context;
        preferences = new Preferences(context);
    }

    public void check(String data) {
        if (data.length() < 4) {
            inputPinCodeView.onError(LENGTH_ERROR);
        } else {
            int pinCode = Integer.parseInt(data.toString());
            int currentPinCode = preferences.getIntValue(Preferences.PIN_CODE);
            if (pinCode != currentPinCode) {
                inputPinCodeView.onError(WRONG_PIN);
            } else {
                inputPinCodeView.onSuccess();
            }
        }
    }
}
