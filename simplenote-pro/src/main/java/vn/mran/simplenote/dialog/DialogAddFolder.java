package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by Mr An on 20/08/2016.
 */
public class DialogAddFolder {
    public static class Build {
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private TextView btnConfirm;
        private TextView btnCancel;
        private CustomEditText txtName;

        public Build(Activity activity) {
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_folder, null);
            builder.setView(view);
            dialog = builder.create();
            btnConfirm = (TextView) view.findViewById(R.id.btnConfirm);
            btnCancel = (TextView) view.findViewById(R.id.btnCancel);
            txtName = new CustomEditText(view, R.id.lnMain);

            txtName.editText.setHint(activity.getString(R.string.add_folder_hint));
            TouchEffect.addAlpha(btnCancel);
            TouchEffect.addAlpha(btnConfirm);
            setNegativeButtonDefaultClick();
        }

        private Build setNegativeButtonDefaultClick() {
            this.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return this;
        }

        public Build setBtnConfirmDefaultClick() {
            this.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return this;
        }

        public TextView getBtnConfirm() {
            return this.btnConfirm;
        }

        public TextView getBtnCencel() {
            return this.btnCancel;
        }

        public TextView getTxtName() {
            return txtName.editText;
        }

        public void show() {
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
