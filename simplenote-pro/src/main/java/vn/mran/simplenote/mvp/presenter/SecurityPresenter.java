package vn.mran.simplenote.mvp.presenter;

import android.content.Context;

import vn.mran.simplenote.mvp.view.SecurityView;
import vn.mran.simplenote.rsa.EncryptionUtil;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;

/**
 * Created by Mr An on 05/09/2016.
 */
public class SecurityPresenter {
    private SecurityView securityView;
    private Context context;
    private EncryptionUtil encryptionUtil;

    public SecurityPresenter(Context context) {
        this.context = context;
        this.securityView = (SecurityView) context;
    }

    public void checkSecurityType() {
        encryptionUtil = new EncryptionUtil();
        FileUtil fileUtil = new FileUtil(DataUtil.LOCK_TYPE_FILE, Constant.DATA_FOLDER);
        String data = fileUtil.readString();
        String type = null;
        type = encryptionUtil.decrypt(data);
        if (!DataUtil.checkStringEmpty(type)) {
            data = encryptionUtil.encrypt(Constant.SECURITY_NONE);
            fileUtil.writeString(data);
            type = Constant.SECURITY_NONE;
        }
        securityView.onReturnLockType(type);
    }

    public void updateSecurity(String type) {
        FileUtil fileUtil = new FileUtil(DataUtil.LOCK_TYPE_FILE, Constant.DATA_FOLDER);
        fileUtil.clear();
        String data = encryptionUtil.encrypt(type);
        fileUtil.writeString(data);
    }
}
