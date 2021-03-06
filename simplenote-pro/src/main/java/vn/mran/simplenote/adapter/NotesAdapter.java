package vn.mran.simplenote.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import vn.mran.simplenote.R;
import vn.mran.simplenote.activity.BaseActivity;
import vn.mran.simplenote.activity.MainActivity;
import vn.mran.simplenote.activity.NotesDetailActivity;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.StringUtil;
import vn.mran.simplenote.util.Utils;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private RealmResults<Notes> realmResult;
    private MainActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtContent;
        public TextView txtTime;
        public TextView txtDate;
        public LinearLayout row;
        SwipeLayout swipeLayout;
        Button buttonDelete;

        public MyViewHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtContent = (TextView) view.findViewById(R.id.txtContent);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            row = (LinearLayout) view.findViewById(R.id.row);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            buttonDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }

    public NotesAdapter(Activity activity, RealmResults<Notes> realmResult) {
        this.activity = (MainActivity) activity;
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
        holder.txtTitle.setText(StringUtil.filterTitle(notes.getTitle()));
        holder.txtContent.setText(StringUtil.filterTitle(notes.getContent()));
        holder.txtDate.setText(Utils.getDate(notes.getId(), "yyyy-MM-dd"));
        holder.txtTime.setText(Utils.getDate(notes.getId(), "HH:mm"));
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                holder.row.setBackgroundColor(activity.getResources().getColor(R.color.white));
                holder.row.setBackgroundResource(R.drawable.item_selector);
            }
        });
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
                BaseActivity.currentNotes = notes;
                activity.goToActivity(NotesDetailActivity.class);
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

    public void filter(RealmResults<Notes> realmResult) {
        this.realmResult = realmResult;
        notifyDataSetChanged();
    }
}