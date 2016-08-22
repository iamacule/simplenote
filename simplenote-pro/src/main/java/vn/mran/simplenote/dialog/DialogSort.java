package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.status.SortStatus;
import vn.mran.simplenote.util.DataUtil;

/**
 * Created by MrAn on 22-Aug-16.
 */
public class DialogSort {
    public static class Build implements View.OnClickListener {
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private TextView btnCancel;
        private TextView btnOK;
        private RadioGroup rgSort;
        private RadioButton rdNewest;
        private RadioButton rdOldest;
        private RadioButton rdAZ;
        private RadioButton rdZA;
        private Activity activity;
        private SortStatus sortStatus;

        public Build(Activity activity, SortStatus sortStatus) {
            this.activity = activity;
            this.sortStatus = sortStatus;
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.sort_dialog, null);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            rgSort = (RadioGroup) view.findViewById(R.id.rgSort);
            rdNewest = (RadioButton) view.findViewById(R.id.rdNewest);
            rdOldest = (RadioButton) view.findViewById(R.id.rdOldest);
            rdAZ = (RadioButton) view.findViewById(R.id.rdAZ);
            rdZA = (RadioButton) view.findViewById(R.id.rdZA);
            btnOK = (TextView) view.findViewById(R.id.btnConfirm);
            btnCancel = (TextView) view.findViewById(R.id.btnCancel);
            setChecked();
            setOnRadioClick();
        }

        private void setChecked() {
            if (sortStatus.NEWEST == sortStatus.status) {
                rdNewest.setChecked(true);
            } else if (sortStatus.OLDEST == sortStatus.status) {
                rdOldest.setChecked(true);
            } else if (sortStatus.AZ == sortStatus.status) {
                rdAZ.setChecked(true);
            } else {
                rdZA.setChecked(true);
            }
        }

        public byte getSortStatus() {
            return sortStatus.status;
        }

        private void setOnRadioClick() {
            rdNewest.setOnClickListener(this);
            rdOldest.setOnClickListener(this);
            rdAZ.setOnClickListener(this);
            rdZA.setOnClickListener(this);
        }

        public Build setNegativeButtonDefaultClick() {
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

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rdNewest:
                    sortStatus.status = sortStatus.NEWEST;
                    break;
                case R.id.rdOldest:
                    sortStatus.status = sortStatus.OLDEST;
                    break;
                case R.id.rdAZ:
                    sortStatus.status = sortStatus.AZ;
                    break;
                case R.id.rdZA:
                    sortStatus.status = sortStatus.ZA;
                    break;
            }
            Log.d(DataUtil.TAG_DIALOG_SORT, "Sort status : " + sortStatus.status);
        }
    }
}
