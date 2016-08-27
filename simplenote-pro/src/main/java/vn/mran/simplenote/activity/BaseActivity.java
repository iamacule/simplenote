package vn.mran.simplenote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;

import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;

/**
 * Created by MrAn on 18-Aug-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected DialogEvent dialogEvent;
    private Intent intent;
    protected static Folder currentFolder;
    public static Notes currentNotes;
    protected static int currentColorId;
    protected final int SPEECH_REQUEST_CODE = 1;
    protected final int TAKE_PICTURE_REQUEST_CODE = 2;

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

    public void goToActivity(Class activity) {
        intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void goToIntentAction(int requestCode, String actionType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(actionType);
        Intent chooser = Intent.createChooser(intent, "Choose a Picture");
        startActivityForResult(chooser, requestCode);
    }

    protected void requestVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public void requestTakePhoTo() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
