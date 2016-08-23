package vn.mran.simplenote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.mvp.view.DialogSelectFolderView;
import vn.mran.simplenote.view.effect.TouchEffect;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

    private RealmResults<Folder> realmResult;
    private DialogSelectFolderView dialogSelectFolderView;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItem;
        public LinearLayout row;

        public MyViewHolder(View view) {
            super(view);
            txtItem = (TextView) view.findViewById(R.id.txtItem);
            row = (LinearLayout) view.findViewById(R.id.row);
        }
    }

    public FolderAdapter(Context context,RealmResults<Folder> realmResult) {
        this.realmResult = realmResult;
        dialogSelectFolderView = (DialogSelectFolderView)context;
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
        setOnClick(holder,folder);
    }

    private void setOnClick(MyViewHolder myViewHolder, final Folder folder) {
        myViewHolder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelectFolderView.onSelectItem(folder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmResult.size();
    }
}