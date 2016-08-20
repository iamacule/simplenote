package vn.mran.simplenote.realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import io.realm.Realm;
import vn.mran.simplenote.application.SimpleNoteApplication;
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
}
