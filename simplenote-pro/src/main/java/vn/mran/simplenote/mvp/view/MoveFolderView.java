package vn.mran.simplenote.mvp.view;

import vn.mran.simplenote.model.Folder;

/**
 * Created by MrAn on 24-Aug-16.
 */
public interface MoveFolderView {
    void onFolderMove(Folder folder);
    void onCancel();
}
