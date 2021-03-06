package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.Spanned;
import android.util.Log;
import android.view.View;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.DataUtil;

/**
 * Created by MrAn on 14-Jul-16.
 */
public class DialogEvent {
    private ProgressDialog progressDialog;
    private DialogAsk.Build dialogAsk;
    private DialogInfo.Build dialogInfo;

    public DialogEvent(Activity context) {
        dialogAsk = new DialogAsk.Build(context);
        dialogInfo = DialogInfo.Build.getInstance(context);
        progressDialog = new ProgressDialog(context, R.style.MyDialogThemes);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public DialogAsk.Build getDialogAsk() {
        return dialogAsk;
    }

    public void showDialogAsk(Object message, String positiveButtonMessage,
                              String negativeButtonMessage, final Thread functionPositive,
                              final Thread functionNegative, final int isShowMessage) {
        dialogAsk.setMessage(message);
        dialogAsk.getTvMessage().setVisibility(isShowMessage);
        if (positiveButtonMessage != null) {
            dialogAsk.getPositiveButton().setText(positiveButtonMessage);
        } else {
            dialogAsk.setDefaultPositiveButton();
        }
        if (negativeButtonMessage != null) {
            dialogAsk.getNegativeButton().setText(negativeButtonMessage);
        } else {
            dialogAsk.setDefaultNegativeButton();
        }
        if (functionNegative != null) {
            dialogAsk.getNegativeButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAsk.dismiss();
                    functionNegative.start();
                }
            });
        } else {
            dialogAsk.setNegativeButtonDefaultClick();
        }
        if (functionPositive != null) {
            dialogAsk.getPositiveButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAsk.dismiss();
                    functionPositive.start();
                }
            });
        } else {
            dialogAsk.setPositiveButtonDefaultClick();
        }
        dialogAsk.show();
    }

    public void showDialogInfo(Object message, Thread function) {
        dialogInfo.setMessage(message);
        if (null == function) {
            dialogInfo.setDefaultButtonClick();
        } else {
            dialogInfo.setButtonClick(function);
        }
        dialogInfo.show();
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

    public void updateDialogMessage(String message) {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        }
    }

    public void dismissAll() {
        dismissProgressDialog();
        dialogAsk.dismiss();
    }
}
