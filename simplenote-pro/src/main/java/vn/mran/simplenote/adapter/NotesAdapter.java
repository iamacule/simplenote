package vn.mran.simplenote.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmResults;
import io.realm.Sort;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Notes;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private RealmResults<Notes> realmResult;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        public MyViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.item);
        }
    }

    public NotesAdapter(RealmResults<Notes> realmResult) {
        this.realmResult = realmResult;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Notes notes = realmResult.get(position);
        holder.item.setText(notes.getTitle());
    }

    @Override
    public int getItemCount() {
        return realmResult.size();
    }

    public void sort(String field , Sort sort){
        this.realmResult = this.realmResult.sort(field,sort);
        notifyDataSetChanged();
    }

    public void changeFolder(RealmResults<Notes> realmResult){
        this.realmResult = realmResult;
        notifyDataSetChanged();
    }
}