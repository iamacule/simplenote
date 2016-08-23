package vn.mran.simplenote.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.mvp.view.DialogSelectFolderView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.view.effect.TouchEffect;
import vn.mran.simplenote.view.toast.Boast;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

    private RealmResults<Folder> realmResult;
    private DialogSelectFolderView dialogSelectFolderView;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;
        LinearLayout row;
        SwipeLayout swipeLayout;
        Button btnDelete;
        Button btnRename;
        LinearLayout lnEdit;
        EditText etItem;
        Button btnConfirm;
        Button btnCancel;

        public MyViewHolder(View view) {
            super(view);
            txtItem = (TextView) view.findViewById(R.id.txtItem);
            row = (LinearLayout) view.findViewById(R.id.row);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            btnRename = (Button) itemView.findViewById(R.id.btnRename);
            btnConfirm = (Button) itemView.findViewById(R.id.btnConfirm);
            btnCancel = (Button) itemView.findViewById(R.id.btnCancel);
            etItem = (EditText) itemView.findViewById(R.id.etItem);
            lnEdit = (LinearLayout) itemView.findViewById(R.id.lnEdit);
        }
    }

    public FolderAdapter(Activity activity, RealmResults<Folder> realmResult) {
        this.activity = activity;
        this.realmResult = realmResult;
        dialogSelectFolderView = (DialogSelectFolderView) activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_folder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Folder folder = realmResult.get(position);
        TouchEffect.addAlpha(holder.row);
        holder.txtItem.setText(folder.getName());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
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
                        rename(myViewHolder);
                        break;
                    case R.id.btnConfirm:
                        confirmRename(myViewHolder,folder,position);
                        break;
                    case R.id.btnDelete:
                        delete(myViewHolder, position);
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

    private void confirmRename(MyViewHolder myViewHolder,Folder folder,int pos) {
        if(DataUtil.checkStringEmpty(myViewHolder.etItem.getText().toString())){
            Realm realm = RealmController.with().getRealm();
            realm.beginTransaction();
            folder.setName(myViewHolder.etItem.getText().toString().trim());
            realm.copyToRealm(folder);
            realm.commitTransaction();
            notifyItemChanged(pos);
        }else {
            Boast.makeText(activity,activity.getString(R.string.add_folder_empty)).show();
        }
    }

    private void cancelEdit(MyViewHolder myViewHolder) {
        myViewHolder.txtItem.setVisibility(View.VISIBLE);
        myViewHolder.lnEdit.setVisibility(View.GONE);
        myViewHolder.swipeLayout.setSwipeEnabled(true);
    }

    private void delete(MyViewHolder myViewHolder, int position) {
        myViewHolder.swipeLayout.close();
        Realm realm = RealmController.with().getRealm();
        realm.beginTransaction();
        realmResult.deleteFromRealm(position);
        realm.commitTransaction();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, realmResult.size());
    }

    private void rename(MyViewHolder myViewHolder) {
        myViewHolder.swipeLayout.close();
        myViewHolder.swipeLayout.setSwipeEnabled(false);
        myViewHolder.txtItem.setVisibility(View.GONE);
        myViewHolder.lnEdit.setVisibility(View.VISIBLE);
        myViewHolder.etItem.setText(myViewHolder.txtItem.getText());
        myViewHolder.etItem.requestFocus();
        myViewHolder.etItem.selectAll();
    }

    @Override
    public int getItemCount() {
        return realmResult.size();
    }
}