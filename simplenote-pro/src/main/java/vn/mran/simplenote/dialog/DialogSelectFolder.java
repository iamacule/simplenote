package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.FolderAdapter;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.realm.RealmController;

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
            recFolder = (RecyclerView) view.findViewById(R.id.recFolder);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            recFolder.setLayoutManager(layoutManager);
            init(activity);
        }

        private void init(Context context) {
            realmResults = RealmController.with().getAllFolder();
            folderAdapter = new FolderAdapter(context,realmResults);
            recFolder.setAdapter(folderAdapter);
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
