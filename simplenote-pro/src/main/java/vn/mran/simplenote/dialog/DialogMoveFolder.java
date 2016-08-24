package vn.mran.simplenote.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.MoveNotesAdapter;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.mvp.view.MoveFolderView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DividerItemDecoration;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 24-Aug-16.
 */
public class DialogMoveFolder {

    public static class Build implements MoveFolderView {
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private RecyclerView recFolder;
        private TextView btnCancel;
        private MoveNotesAdapter moveNotesAdapter;
        private RealmResults<Folder> realmResults;
        private MoveFolderView moveFolderView;

        private Folder delFolder;

        public Build(Activity activity, Folder delFolder, MoveFolderView moveFolderView) {
            this.moveFolderView = moveFolderView;
            this.delFolder = delFolder;
            builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.select_folder_move_dialog, null);
            builder.setView(view);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            recFolder = (RecyclerView) view.findViewById(R.id.recFolder);
            btnCancel = (TextView) view.findViewById(R.id.btnCancel);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            recFolder.setLayoutManager(layoutManager);
            recFolder.addItemDecoration(new DividerItemDecoration(activity.getResources().getDrawable(R.drawable.divider)));
            TouchEffect.addAlpha(btnCancel);
            init();
        }

        private void init() {
            realmResults = RealmController.with().getAllFolder();
            List<Folder> listData = new ArrayList<>(realmResults);
            listData.remove(delFolder);

            moveNotesAdapter = new MoveNotesAdapter(this, listData);
            recFolder.setAdapter(moveNotesAdapter);

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        public void show() {
            if (dialog != null & !dialog.isShowing()) {
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }

        public void dismiss() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        public void onFolderMove(Folder folder) {
            moveFolderView.onFolderMove(folder);
            dismiss();
        }

        @Override
        public void onCancel() {
            dismiss();
        }
    }
}
