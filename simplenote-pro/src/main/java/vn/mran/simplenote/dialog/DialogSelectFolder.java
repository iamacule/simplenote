package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.FolderAdapter;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DividerItemDecoration;
import vn.mran.simplenote.util.EventUtil;

/**
 * Created by MrAn on 23-Aug-16.
 */
public class DialogSelectFolder {
    public static class Build {
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private RecyclerView recFolder;
        private FolderAdapter folderAdapter;
        private RealmResults<Folder> realmResults;

        public Build(Activity activity) {
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.select_folder_dialog, null);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            recFolder = (RecyclerView) view.findViewById(R.id.recFolder);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            recFolder.setLayoutManager(layoutManager);
            recFolder.addItemDecoration(new DividerItemDecoration(activity.getResources().getDrawable(R.drawable.divider)));
            init(activity);
            final EventUtil.KeyBoard board = new EventUtil.KeyBoard(activity);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    board.hide(recFolder);
                }
            });
        }

        private void init(Activity activity) {
            realmResults = RealmController.with().getAllFolder();
            folderAdapter = new FolderAdapter(activity, realmResults);
            recFolder.setAdapter(folderAdapter);
        }

        public void show() {
            if (dialog != null & !dialog.isShowing()) {
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }

        public void dismiss() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
}
