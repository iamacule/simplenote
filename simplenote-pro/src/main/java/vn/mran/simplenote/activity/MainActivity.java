package vn.mran.simplenote.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.MoviesAdapter;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.view.Filter;
import vn.mran.simplenote.view.FloatingAdd;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.toast.Boast;

public class MainActivity extends BaseActivity {
    private FloatingAdd floatingAdd;
    private Header header;
    private Filter filter;
    private RecyclerView recyclerView;
    private List<String> list = new ArrayList<>();

    @Override
    public int getView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.listData);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        header = new Header(getWindow().getDecorView().getRootView());
        filter = new Filter(getWindow().getDecorView().getRootView());
        floatingAdd = new FloatingAdd(getWindow().getDecorView().getRootView());
        filter.parent.setVisibility(View.GONE);
        header.btnBack.setVisibility(View.GONE);
    }

    @Override
    public void initValue() {
        for (int i = 0; i < 100; i++) {
            list.add("Item " + i);
        }
        recyclerView.setAdapter(new MoviesAdapter(list));
    }

    @Override
    public void initAction() {
        setOnScrollListener();
        final View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnAddFolder:
                        Boast.makeText(MainActivity.this, "btnAddFolder").show();
                        break;

                    case R.id.btnAddNote:
                        floatingAdd.hideAll(false);
                        goToActivity(AddNoteActivity.class);
                        break;
                }
            }
        };
        floatingAdd.btnAddNote.setOnClickListener(click);
        floatingAdd.btnAddFolder.setOnClickListener(click);
    }

    private void setOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (dy > 10) {
                        // Scrolling up
                        if (filter.parent.getVisibility() == View.GONE) {
                            filter.parent.startAnimation(AnimationUtil.slideInTop(MainActivity.this));
                            filter.parent.setVisibility(View.VISIBLE);
                            floatingAdd.toggleBtnAdd();
                            floatingAdd.hideAll(false);
                        }
                    }
                } else {
                    if (dy < -10) {
                        // Scrolling down
                        if (filter.parent.getVisibility() == View.VISIBLE) {
                            filter.parent.setVisibility(View.GONE);
                            floatingAdd.toggleBtnAdd();
                        }
                    }
                }
            }
        });
    }
}
