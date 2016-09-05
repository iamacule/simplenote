package vn.mran.simplenote.mvp.presenter;

import android.content.Context;

import vn.mran.simplenote.mvp.view.CreatePinCodeView;
import vn.mran.simplenote.util.Preferences;

/**
 * Created by Mr An on 05/09/2016.
 */
public class CreatePinCodePresenter {
    private Context context;
    private Preferences preferences;
    private CreatePinCodeView createPinCodeView;

    public CreatePinCodePresenter(Context context) {
        this.context = context;
        this.createPinCodeView = (CreatePinCodeView) context;
        this.preferences = new Preferences(context);
    }

    public void createPinCode(int pinCode){
        preferences.storeValue(Preferences.PIN_CODE,pinCode);
        createPinCodeView.onCreatePinCodeSuccess();
    }
}
