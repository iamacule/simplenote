package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import vn.mran.simplenote.R;

/**
 * Created by Covisoft on 07/01/2016.
 */
public class DialogInfo {
    public static class Build {
        private static Build instance;
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private TextView btnConfirm;
        private TextView txtMessage;
        private Activity activity;
        private Thread functionButton;
        private final String TAG = "DialogInfo";


        public static Build getInstance(Activity activity) {
            if (null == instance)
                instance = new Build(activity);
            return instance;
        }

        private Build(Activity activity) {
            this.activity = activity;
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.info_dialog, null);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);
            btnConfirm = (TextView) view.findViewById(R.id.btnConfirm);
        }

        public Build setMessage(Object message) {

            if(message instanceof String)
                txtMessage.setText((String)message);
            if(message instanceof Spanned)
                txtMessage.setText((Spanned)message);
            return this;
        }

        public Build setBtnConfirm(String text) {
            this.btnConfirm.setText(text);
            return this;
        }

        public Build setDefaultButtonClick() {
            Build.this.functionButton = null;
            this.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return this;
        }

        public void setButtonClick(Thread functionButton) {
            this.functionButton = functionButton;
            getBtnConfirm().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (null != Build.this.functionButton) {
                        Build.this.functionButton.start();
                    }
                }
            });
        }

        private TextView getBtnConfirm() {
            return this.btnConfirm;
        }

        public void show() {
            dismiss();
            if (dialog != null & !dialog.isShowing()) {
                dialog.show();
            }
        }

        public void dismiss() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
