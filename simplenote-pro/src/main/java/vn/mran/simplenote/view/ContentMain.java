package vn.mran.simplenote.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import vn.mran.simplenote.R;

/**
 * Created by Mr An on 20/08/2016.
 */
public class ContentMain {
    public TextView txtNoData;
    public RecyclerView recyclerView;

    public ContentMain(View view) {
        txtNoData = (TextView) view.findViewById(R.id.txtNodata);
        recyclerView = (RecyclerView) view.findViewById(R.id.listData);
    }
}
