package vn.mran.simplenote.mvp.view;

import vn.mran.simplenote.model.Folder;

/**
 * Created by Mr An on 20/08/2016.
 */
public interface AddFolderView {
    void onSaveFinish(Folder folder);
    void onSaveFail(byte messageId);
}
