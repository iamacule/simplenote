package vn.mran.simplenote.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.NotesAdapter;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.presenter.AddFolderPresenter;
import vn.mran.simplenote.mvp.view.AddFolderView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.view.ContentMain;
import vn.mran.simplenote.view.Filter;
import vn.mran.simplenote.view.FloatingAdd;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.toast.Boast;

public class MainActivity extends BaseActivity implements AddFolderView {
    private FloatingAdd floatingAdd;
    private Header header;
    private Filter filter;
    private ContentMain contentMain;
    private AddFolderPresenter addFolderPresenter;

    @Override
    public int getView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        header = new Header(getWindow().getDecorView().getRootView());
        filter = new Filter(getWindow().getDecorView().getRootView());
        contentMain = new ContentMain(getWindow().getDecorView().getRootView());
        floatingAdd = new FloatingAdd(getWindow().getDecorView().getRootView());
        header.btnBack.setVisibility(View.GONE);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        contentMain.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void initValue() {
        RealmResults<Notes> realmResults = RealmController.with().getAllNotes();
        if (realmResults.size() > 0) {
            contentMain.txtNoData.setVisibility(View.GONE);
        }
        contentMain.recyclerView.setAdapter(new NotesAdapter(RealmController.with().getAllNotes()));
        addFolderPresenter = new AddFolderPresenter(this);
        currentFolder = RealmController.with().getFolderByName(Constant.FOLDER_ALL);
        Log.d(DataUtil.TAG_MAIN_ACTIVITY,"Current folder : "+currentFolder.getId());
    }

    @Override
    public void initAction() {
        setOnScrollListener();
        final View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnAddFolder:
                        showDialogAddFolder();
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

    private void showDialogAddFolder() {
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
        contentMain.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        Boast.makeText(this, getString(R.string.add_folder_success)).show();
    }

    @Override
    public void onSaveFail(byte messageId) {
        switch (messageId) {
            case AddFolderPresenter.EMPTY_CONTENT:
                Boast.makeText(this, getString(R.string.add_folder_empty)).show();
                break;
            case AddFolderPresenter.FOLDER_EXITS:
                Boast.makeText(this, getString(R.string.add_folder_exits)).show();
                break;
            case AddFolderPresenter.SAVE_EXCEPTION:
                Boast.makeText(this, getString(R.string.add_folder_fail)).show();
                break;
        }
    }
}
