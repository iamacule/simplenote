package vn.mran.simplenote.mvp.presenter;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.mvp.view.AddFolderView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DataUtil;

/**
 * Created by Mr An on 20/08/2016.
 */
public class AddFolderPresenter {
    public static final byte SAVE_EXCEPTION = 0;
    public static final byte EMPTY_CONTENT = 1;
    public static final byte FOLDER_EXITS = 2;
    private AddFolderView addFolderView;
    private Realm realm;

    public AddFolderPresenter(Context context) {
        this.addFolderView = (AddFolderView) context;
        init();
    }

    private void init() {
        realm = RealmController.with().getRealm();
    }

    public void save(String folderName) {
        if (!DataUtil.checkStringEmpty(folderName)) {
            addFolderView.onSaveFail(EMPTY_CONTENT);
        } else {
            try {
                RealmResults<Folder> listResult = RealmController.with().checkFolderNameExits(folderName);
                if (listResult.isEmpty()) {
                    long id = System.currentTimeMillis();
                    Folder folder = new Folder();
                    folder.setId(id);
                    folder.setName(folderName);
                    realm.beginTransaction();
                    realm.copyToRealm(folder);
                    realm.commitTransaction();
                    Folder result = RealmController.with().getFolderById(id);
                    if (null != result) {
                        Log.d(DataUtil.TAG_ADD_FOLDER_ACTIVITY, "Save success as folder id : " + folder.getId());
                        Log.d(DataUtil.TAG_ADD_FOLDER_ACTIVITY, "Save success as folder name : " + folder.getName());
                        addFolderView.onSaveFinish(folder);
                    } else {
                        addFolderView.onSaveFail(SAVE_EXCEPTION);
                    }
                }else {
                    addFolderView.onSaveFail(FOLDER_EXITS);
                }
            } catch (Exception e) {
                Log.d(DataUtil.TAG_ADD_NOTES_PRESENTER, "Save error : " + e.getMessage());
                addFolderView.onSaveFail(SAVE_EXCEPTION);
            }
        }

    }
}
