package vn.mran.simplenote.application;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class SimpleNoteApplication extends Application {
    private final String DB_NAME = "SIMPLE_NOTE";
    private static SimpleNoteApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(DB_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static SimpleNoteApplication get() {
        return instance;
    }
}
