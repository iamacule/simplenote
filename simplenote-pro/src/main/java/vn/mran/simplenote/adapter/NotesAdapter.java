package vn.mran.simplenote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.view.effect.TouchEffect;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private RealmResults<Notes> realmResult;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item;
        public LinearLayout row;
        SwipeLayout swipeLayout;
        Button buttonDelete;

        public MyViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.txtItem);
            row = (LinearLayout) view.findViewById(R.id.row);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            buttonDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }

    public NotesAdapter(Context context,RealmResults<Notes> realmResult) {
        this.context = context;
        this.realmResult = realmResult;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Notes notes = realmResult.get(position);
        TouchEffect.addAlpha(holder.row);
        holder.item.setText(notes.getTitle());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = RealmController.with().getRealm();
                realm.beginTransaction();
                realmResult.deleteFromRealm(position);
                realm.commitTransaction();
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, realmResult.size());
            }
        });
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return realmResult.size();
    }

    public void sort(String field, Sort sort) {
        this.realmResult = this.realmResult.sort(field, sort);
        notifyDataSetChanged();
    }

    public void changeFolder(RealmResults<Notes> realmResult) {
        this.realmResult = realmResult;
        notifyDataSetChanged();
    }
}