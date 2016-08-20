package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.DataUtil;

/**
 * Created by MrAn on 14-Jul-16.
 */
public class DialogEvent {
    private ProgressDialog progressDialog;
    private DialogAddFolder.Build dialogAddFolder;

    public DialogEvent(Activity context) {
        dialogAddFolder = new DialogAddFolder.Build(context);
        progressDialog = new ProgressDialog(context, R.style.TransparentDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public DialogAddFolder.Build getDialogAddFolder() {
        return dialogAddFolder;
    }

    public void showDialogAddFolder(final Thread functionBtnConfirm) {
        if (functionBtnConfirm != null) {
            dialogAddFolder.getBtnConfirm().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddFolder.dismiss();
                    functionBtnConfirm.start();
                }
            });
        } else {
            dialogAddFolder.setBtnConfirmDefaultClick();
        }
        dialogAddFolder.show();
    }

    public void showProgressDialog(String message) {
        try {
            progressDialog.setMessage(message);
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        } catch (Exception e) {
            Log.w(DataUtil.TAG_DIALOG_ADD_FOLDER, "Dialog can not show when activity don't have an instance");
        }
    }

    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.w(DataUtil.TAG_DIALOG_ADD_FOLDER, "Progress dialog can not dismiss when activity don't have an instance");
        }
    }

    public void dismissAll() {
        dismissProgressDialog();
        dialogAddFolder.dismiss();
    }
}
