package vn.mran.simplenote.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.mvp.view.MoveFolderView;
import vn.mran.simplenote.view.effect.TouchEffect;

public class MoveNotesAdapter extends RecyclerView.Adapter<MoveNotesAdapter.MyViewHolder> {

    private MoveFolderView moveFolderView;
    private List<Folder> listFolder;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtItem;
        LinearLayout row;

        public MyViewHolder(View view) {
            super(view);
            txtItem = (TextView) view.findViewById(R.id.txtTitle);
            row = (LinearLayout) view.findViewById(R.id.row);
        }
    }

    public MoveNotesAdapter(MoveFolderView moveFolderView, List<Folder> listFolder) {
        this.listFolder = listFolder;
        this.moveFolderView = moveFolderView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_move_note, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Folder folder = listFolder.get(position);
        TouchEffect.addAlpha(holder.row);
        holder.txtItem.setText(folder.getName());
        setOnClick(holder, folder, position);
    }

    private void setOnClick(final MyViewHolder myViewHolder, final Folder folder, final int position) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.row:
                        moveFolderView.onFolderMove(folder);
                        break;
                }
            }
        };
        myViewHolder.row.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }
}