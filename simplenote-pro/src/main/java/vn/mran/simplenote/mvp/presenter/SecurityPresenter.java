package vn.mran.simplenote.mvp.presenter;

import android.content.Context;

import vn.mran.simplenote.mvp.view.SecurityView;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.Preferences;

/**
 * Created by Mr An on 05/09/2016.
 */
public class SecurityPresenter {
    private SecurityView securityView;
    private Context context;
    private Preferences preferences;

    public SecurityPresenter(Context context) {
        this.context = context;
        this.securityView = (SecurityView) context;
        preferences = new Preferences(context);
    }

    public void checkSecurityType() {

        String data = preferences.getStringValue(Preferences.SECURITY_TYPE);
        if (!DataUtil.checkStringEmpty(data)) {
            preferences.storeValue(Preferences.SECURITY_TYPE,data);
            data = Constant.SECURITY_NONE;
        }
        securityView.onReturnLockType(data);
    }

    public void updateSecurity(String type) {
        preferences.storeValue(Preferences.SECURITY_TYPE,type);
    }
}
