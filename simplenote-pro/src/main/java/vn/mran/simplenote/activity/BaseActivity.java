package vn.mran.simplenote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.model.Folder;

/**
 * Created by MrAn on 18-Aug-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected DialogEvent dialogEvent;
    private Intent intent;
    protected static Folder currentFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());
        initBaseValue();
        initView();
        initValue();
        initAction();
    }

    private void initBaseValue() {
        dialogEvent = new DialogEvent(this);
    }

    public abstract int getView();

    public abstract void initView();

    public abstract void initValue();

    public abstract void initAction();

    protected void goToActivity(Class activity) {
        intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void goToAddNote(Class activity, String key, long value) {
        intent = new Intent(this, activity);
        intent.putExtra(key, value);
        startActivity(new Intent(this, activity));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void goToIntentAction(int requestCode, String actionType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(actionType);
        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, requestCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
