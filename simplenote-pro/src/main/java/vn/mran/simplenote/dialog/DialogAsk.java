package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import vn.mran.simplenote.R;

/**
 * Created by Covisoft on 07/01/2016.
 */
public class DialogAsk {
    public static class Build {
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private TextView btnCancel;
        private TextView btnOK;
        private TextView tvMessage;
        private Activity activity;

        public Build(Activity activity) {
            this.activity = activity;
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_dialog, null);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            tvMessage = (TextView) view.findViewById(R.id.txtMessage);
            btnOK = (TextView) view.findViewById(R.id.btnConfirm);
            btnCancel = (TextView) view.findViewById(R.id.btnCancel);
        }

        public Build setMessage(String message) {
            tvMessage.setText(message);
            return this;
        }

        public Build setPositiveButton(String text){
            this.btnOK.setText(text);
            return this;
        }

        public Build setDefaultPositiveButton(){
            this.btnOK.setText(activity.getString(R.string.confirm));
            return this;
        }

        public Build setDefaultNegativeButton(){
            this.btnCancel.setText(activity.getString(R.string.cancel));
            return this;
        }

        public Build setNegativeButton(String text){
            this.btnCancel.setText(text);
            return this;
        }

        public Build setNegativeButtonDefaultClick(){
            this.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return this;
        }

        public Build setPositiveButtonDefaultClick() {
            this.btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return this;
        }

        public TextView getPositiveButton() {
            return this.btnOK;
        }

        public TextView getNegativeButton() {
            return this.btnCancel;
        }

        public TextView getTvMessage(){
            return tvMessage;
        }

        public void show() {
            if(dialog!=null&!dialog.isShowing()){
                dialog.show();
            }
        }

        public void dismiss() {
            if(dialog!=null&&dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
}
