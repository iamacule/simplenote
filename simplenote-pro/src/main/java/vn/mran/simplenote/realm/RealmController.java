package vn.mran.simplenote.realm;

import android.app.Application;

import java.lang.reflect.Field;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import vn.mran.simplenote.application.SimpleNoteApplication;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with() {

        if (instance == null) {
            instance = new RealmController(SimpleNoteApplication.get());
        }
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public Notes getNotesById(long id) {
        return realm.where(Notes.class).equalTo("id", id).findFirst();
    }

    public Folder getFolderById(long id) {
        return realm.where(Folder.class).equalTo("id", id).findFirst();
    }

    public RealmResults<Folder> getAllFolder() {
        return realm.where(Folder.class).findAll();
    }

    public RealmResults<Notes> getAllNotes() {
        return realm.where(Notes.class).findAll();
    }

    public RealmResults<Folder> getNotesInFolder(long folderId) {
        RealmQuery<Folder> query = realm.where(Folder.class).beginGroup();
        Field[] fields = Folder.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            query.equalTo(fieldName, Integer.parseInt("" + folderId));
        }
        return query.endGroup().findAll();
    }

    public RealmResults<Folder> checkFolderNameExits(String folderName) {
        return realm.where(Folder.class)
                .contains("name", folderName)
                .findAll();
    }
}
