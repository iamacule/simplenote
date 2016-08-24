package vn.mran.simplenote.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import io.realm.RealmResults;
import io.realm.Sort;
import vn.mran.simplenote.R;
import vn.mran.simplenote.adapter.NotesAdapter;
import vn.mran.simplenote.dialog.DialogSelectFolder;
import vn.mran.simplenote.dialog.DialogSort;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.presenter.AddFolderPresenter;
import vn.mran.simplenote.mvp.view.AddFolderView;
import vn.mran.simplenote.mvp.view.DialogSelectFolderView;
import vn.mran.simplenote.mvp.view.DialogSortView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.status.SortStatus;
import vn.mran.simplenote.util.AnimationUtil;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.DividerItemDecoration;
import vn.mran.simplenote.view.ContentMain;
import vn.mran.simplenote.view.Filter;
import vn.mran.simplenote.view.FloatingAdd;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.toast.Boast;

public class MainActivity extends BaseActivity implements AddFolderView,
        DialogSelectFolderView,DialogSortView {
    private FloatingAdd floatingAdd;
    private Header header;
    private Filter filter;
    private ContentMain contentMain;
    private AddFolderPresenter addFolderPresenter;
    private NotesAdapter notesAdapter;
    private SortStatus sortStatus;
    private RealmResults<Notes> realmResults;
    private DialogSelectFolder.Build dialogSelectFolder;
    private DialogSort.Build dialogSort;

    @Override
    protected void onResume() {
        super.onResume();
        updateList(currentFolder);
    }

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
        contentMain.recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
    }

    @Override
    public void initValue() {
        addFolderPresenter = new AddFolderPresenter(this);
        currentFolder = RealmController.with().getFolderByName(Constant.FOLDER_ALL);
        Log.d(DataUtil.TAG_MAIN_ACTIVITY, "Current folder : " + currentFolder.getId());
        sortStatus = new SortStatus();
        setAdapter();
    }

    private void setAdapter() {
        realmResults = RealmController.with().getNotesInFolder(currentFolder.getId());
        notesAdapter = new NotesAdapter(this,realmResults);
        checkEmptyData();
        contentMain.recyclerView.setAdapter(notesAdapter);
    }

    private void checkEmptyData() {
        if (realmResults.size() > 0) {
            contentMain.txtNoData.setVisibility(View.GONE);
        } else {
            contentMain.txtNoData.setVisibility(View.VISIBLE);
        }
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

                    case R.id.btnSort:
                        showDialogSort();
                        break;

                    case R.id.btnFolder:
                        showDialogSelectFolder();
                        break;
                }
            }
        };
        floatingAdd.btnAddNote.setOnClickListener(click);
        floatingAdd.btnAddFolder.setOnClickListener(click);
        filter.btnSort.setOnClickListener(click);
        filter.btnFolder.setOnClickListener(click);
    }

    private void showDialogSelectFolder() {
        dialogSelectFolder = new DialogSelectFolder.Build(this);
        dialogSelectFolder.show();
    }

    private void showDialogSort() {
        dialogSort = new DialogSort.Build(this, sortStatus);
        dialogSort.show();
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
                            floatingAdd.toggleBtnAdd(false);
                            floatingAdd.hideAll(false);
                        }
                    }
                } else {
                    if (dy < -10) {
                        // Scrolling down
                        if (filter.parent.getVisibility() == View.VISIBLE) {
                            filter.parent.setVisibility(View.GONE);
                            floatingAdd.toggleBtnAdd(true);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSaveFinish(Folder folder) {
        Boast.makeText(this, getString(R.string.add_folder_success)).show();
        currentFolder = folder;
        updateList(folder);
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

    void updateList(Folder folder) {
        filter.txtFolderName.setText(folder.getName());
        realmResults = RealmController.with().getNotesInFolder(folder.getId());
        filter.parent.setVisibility(View.VISIBLE);
        floatingAdd.toggleBtnAdd(true);
        notesAdapter.changeFolder(realmResults);
        updateSort();
        checkEmptyData();
    }

    private void updateSort() {
        if (sortStatus.NEWEST == sortStatus.status) {
            notesAdapter.sort("id", Sort.DESCENDING);
            filter.txtSortStatus.setText(getString(R.string.newest));
        } else if (sortStatus.OLDEST == sortStatus.status) {
            notesAdapter.sort("id", Sort.ASCENDING);
            filter.txtSortStatus.setText(getString(R.string.oldest));
        } else if (sortStatus.AZ == sortStatus.status) {
            notesAdapter.sort("title", Sort.ASCENDING);
            filter.txtSortStatus.setText(getString(R.string.az));
        } else {
            notesAdapter.sort("title", Sort.DESCENDING);
            filter.txtSortStatus.setText(getString(R.string.za));
        }
    }

    @Override
    public void onSelectItem(Folder folder) {
        currentFolder = folder;
        updateList(folder);
        dialogSelectFolder.dismiss();
    }

    @Override
    public void onItemSelect() {
        sortStatus.status = dialogSort.getSortStatus();
        updateSort();
    }
}
