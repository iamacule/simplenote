package vn.mran.simplenote.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.dialog.DialogMoveFolder;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.view.DialogSelectFolderView;
import vn.mran.simplenote.mvp.view.MoveFolderView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.EventUtil;
import vn.mran.simplenote.view.effect.TouchEffect;
import vn.mran.simplenote.view.toast.Boast;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> implements MoveFolderView {

    private RealmResults<Folder> realmResult;
    private DialogSelectFolderView dialogSelectFolderView;
    private Activity activity;
    private EventUtil.KeyBoard keyBoard;
    private DialogEvent dialogEvent;
    private Realm realm;
    private DialogMoveFolder.Build dialogMoveFolder;
    private Folder delFolder;
    private MyViewHolder myViewHolders;
    private int pos;

    @Override
    public void onFolderMove(Folder folder) {
        Log.d(DataUtil.TAG_DIALOG_SELECT_FOLDER, "" + folder.getName());
        RealmResults<Notes> listNotesInFolder = RealmController.with().getNotesInFolder(delFolder.getId());
        realm.beginTransaction();
        for (Notes notes : listNotesInFolder) {
            notes.setFolderId(folder.getId());
            realm.copyToRealm(notes);
        }
        realm.commitTransaction();
        del(myViewHolders,pos);
        dialogSelectFolderView.onSelectItem(folder);
    }

    @Override
    public void onCancel() {
        dialogMoveFolder.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;
        LinearLayout row;
        SwipeLayout swipeLayout;
        Button btnDelete;
        Button btnRename;
        LinearLayout lnEdit;
        EditText etItem;
        LinearLayout btnConfirm;
        LinearLayout btnCancel;

        public MyViewHolder(View view) {
            super(view);
            txtItem = (TextView) view.findViewById(R.id.txtItem);
            row = (LinearLayout) view.findViewById(R.id.row);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            btnRename = (Button) itemView.findViewById(R.id.btnRename);
            btnConfirm = (LinearLayout) itemView.findViewById(R.id.btnConfirm);
            btnCancel = (LinearLayout) itemView.findViewById(R.id.btnCancel);
            etItem = (EditText) itemView.findViewById(R.id.etItem);
            lnEdit = (LinearLayout) itemView.findViewById(R.id.lnEdit);
        }
    }

    public FolderAdapter(Activity activity, RealmResults<Folder> realmResult) {
        this.activity = activity;
        this.realmResult = realmResult;
        dialogSelectFolderView = (DialogSelectFolderView) activity;
        keyBoard = new EventUtil.KeyBoard(activity);
        dialogEvent = new DialogEvent(activity);
        realm = RealmController.with().getRealm();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_folder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Folder folder = realmResult.get(position);
        TouchEffect.addAlpha(holder.btnConfirm);
        TouchEffect.addAlpha(holder.btnCancel);
        holder.txtItem.setText(folder.getName());
        if(folder.getName().equals(Constant.FOLDER_ALL)){
            holder.txtItem.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                holder.row.setBackgroundColor(activity.getResources().getColor(R.color.white));
                holder.row.setBackgroundResource(R.drawable.item_selector);
            }
        });
        setOnClick(holder, folder, position);
    }

    private void setOnClick(final MyViewHolder myViewHolder, final Folder folder, final int position) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.row:
                        dialogSelectFolderView.onSelectItem(folder);
                        break;
                    case R.id.btnRename:
                        if(folder.getName().equals(Constant.FOLDER_ALL)){
                            Boast.makeText(activity,activity.getString(R.string.not_rename_folder)).show();
                        }else {
                            rename(myViewHolder);
                        }
                        break;
                    case R.id.btnConfirm:
                        confirmRename(myViewHolder, folder, position);
                        break;
                    case R.id.btnDelete:
                        if(folder.getName().equals(Constant.FOLDER_ALL)){
                            Boast.makeText(activity,activity.getString(R.string.not_delete_folder)).show();
                        }else {
                            delFolder = folder;
                            FolderAdapter.this.myViewHolders = myViewHolder;
                            pos = position;
                            delete(myViewHolder, position);
                        }
                        break;
                    case R.id.btnCancel:
                        cancelEdit(myViewHolder);
                        break;
                }
            }
        };
        myViewHolder.row.setOnClickListener(onClickListener);
        myViewHolder.btnRename.setOnClickListener(onClickListener);
        myViewHolder.btnDelete.setOnClickListener(onClickListener);
        myViewHolder.btnCancel.setOnClickListener(onClickListener);
        myViewHolder.btnConfirm.setOnClickListener(onClickListener);
    }

    private void confirmRename(MyViewHolder myViewHolder, Folder folder, int pos) {
        if (DataUtil.checkStringEmpty(myViewHolder.etItem.getText().toString())) {
            realm.beginTransaction();
            folder.setName(myViewHolder.etItem.getText().toString().trim());
            realm.copyToRealm(folder);
            realm.commitTransaction();
            notifyItemChanged(pos);
            myViewHolder.txtItem.setVisibility(View.VISIBLE);
            myViewHolder.lnEdit.setVisibility(View.GONE);
            keyBoard.hide(myViewHolder.etItem);
        } else {
            Boast.makeText(activity, activity.getString(R.string.add_folder_empty)).show();
        }
    }

    private void cancelEdit(MyViewHolder myViewHolder) {
        myViewHolder.txtItem.setVisibility(View.VISIBLE);
        myViewHolder.lnEdit.setVisibility(View.GONE);
        myViewHolder.swipeLayout.setSwipeEnabled(true);
        keyBoard.hide(myViewHolder.etItem);
    }

    private void delete(final MyViewHolder myViewHolder, final int position) {
        final RealmResults<Notes> realmResults = RealmController.with().getNotesInFolder(delFolder.getId());
        if (realmResults.size() > 0) {
            dialogEvent.showDialogAsk(activity.getString(R.string.del_folder_note), activity.getString(R.string.move),
                    activity.getString(R.string.delete_all), new Thread(new Runnable() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showDialogMoveNotes(delFolder);
                                }
                            });
                        }
                    }), new Thread(new Runnable() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deleteAllNotesInFolder(realmResults);
                                    del(myViewHolder, position);
                                }
                            });
                        }
                    }), View.VISIBLE);
        } else {
            del(myViewHolder, position);
        }
    }

    private void showDialogMoveNotes(Folder folder) {
        dialogMoveFolder = new DialogMoveFolder.Build(activity, folder, this);
        dialogMoveFolder.show();
    }

    private void deleteAllNotesInFolder(RealmResults<Notes> realmResults) {
        realm.beginTransaction();
        for (int i = 0; i < realmResults.size(); i++) {
            realmResults.deleteFromRealm(i);
        }
        realm.commitTransaction();
    }

    private void del(MyViewHolder myViewHolder, int position) {
        myViewHolder.swipeLayout.close();
        realm.beginTransaction();
        realmResult.deleteFromRealm(position);
        realm.commitTransaction();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, realmResult.size());
        Boast.makeText(activity, activity.getString(R.string.del_folder_success));
    }

    private void rename(MyViewHolder myViewHolder) {
        myViewHolder.swipeLayout.close();
        myViewHolder.swipeLayout.setSwipeEnabled(false);
        myViewHolder.txtItem.setVisibility(View.GONE);
        myViewHolder.lnEdit.setVisibility(View.VISIBLE);
        myViewHolder.lnEdit.startAnimation(AnimationUtil.fadeIn(activity));
        myViewHolder.etItem.setText(myViewHolder.txtItem.getText());
        myViewHolder.etItem.requestFocus();
        myViewHolder.etItem.selectAll();
        keyBoard.show(myViewHolder.etItem);
    }

    @Override
    public int getItemCount() {
        return realmResult.size();
    }
}