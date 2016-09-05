package vn.mran.simplenote.mvp.view;

/**
 * Created by Mr An on 05/09/2016.
 */
public interface SecurityView {
    void onReturnLockType(String type);
    void onCreateNewPinCode();
    void onInputPinCode();
}
