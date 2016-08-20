package vn.mran.simplenote.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.MoviesAdapter;
import vn.mran.simplenote.mvp.presenter.AddFolderPresenter;
import vn.mran.simplenote.mvp.view.AddFolderView;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.view.Filter;
import vn.mran.simplenote.view.FloatingAdd;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.toast.Boast;

public class MainActivity extends BaseActivity implements AddFolderView{
    private FloatingAdd floatingAdd;
    private Header header;
    private Filter filter;
    private RecyclerView recyclerView;
    private List<String> list = new ArrayList<>();
    private AddFolderPresenter addFolderPresenter;

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
        addFolderPresenter = new AddFolderPresenter(this);
    }

    @Override
    public void initAction() {
        setOnScrollListener();
        final View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnAddFolder:
                        shoeDialogAddFolder();
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

    private void shoeDialogAddFolder() {
        dialogEvent.showDialogAddFolder(new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addFolderPresenter.save(dialogEvent.getDialogAddFolder().getTxtName().getText().toString().trim());
                        dialogEvent.getDialogAddFolder().getTxtName().setText("");
                    }
                });
            }
        }));
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

    @Override
    public void onSaveFinish() {
        Boast.makeText(this,getString(R.string.add_folder_success)).show();
    }

    @Override
    public void onSaveFail(byte messageId) {
        switch (messageId){
            case AddFolderPresenter.EMPTY_CONTENT:
                Boast.makeText(this,getString(R.string.add_folder_empty)).show();
                break;
            case AddFolderPresenter.FOLDER_EXITS:
                Boast.makeText(this,getString(R.string.add_folder_exits)).show();
                break;
            case AddFolderPresenter.SAVE_EXCEPTION:
                Boast.makeText(this,getString(R.string.add_folder_fail)).show();
                break;
        }
    }
}
