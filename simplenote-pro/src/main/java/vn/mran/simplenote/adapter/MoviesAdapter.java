package vn.mran.simplenote.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.mran.simplenote.R;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<String> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView item;

        public MyViewHolder(View view) {
            super(view);
            item = (TextView) view.findViewById(R.id.item);
        }
    }


    public MoviesAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String data = list.get(position);
        holder.item.setText(data);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}