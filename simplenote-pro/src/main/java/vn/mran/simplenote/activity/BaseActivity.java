package vn.mran.simplenote.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.util.PermissionUtil;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 18-Aug-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Header header;
    protected DialogEvent dialogEvent;
    private Intent intent;
    protected static Folder currentFolder;
    public static Notes currentNotes;
    protected static int currentColorId = -1;
    protected final int ACTION_REQUEST_GALLERY = 0;
    protected final int SPEECH_REQUEST_CODE = 1;
    protected final int TAKE_PICTURE_REQUEST_CODE = 2;
    protected Uri mCurrentPhotoUri;

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
        initHeader();
    }

    private void initHeader() {
        try{
            header = new Header(getWindow().getDecorView().getRootView());
            header.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(BaseActivity.this, header.btnMenu);
                    DroppyMenuPopup droppyMenu = droppyBuilder.fromMenu(R.menu.app_popup)
                            .triggerOnAnchorClick(false)
                            .setOnClick(new DroppyClickCallbackInterface() {
                                @Override
                                public void call(View v, int id) {
                                    switch (id) {
                                        case R.id.btnSecurity:
                                            goToActivity(SecurityActivity.class);
                                            break;
                                    }
                                }
                            })
                            .setPopupAnimation(new DroppyFadeInAnimation())
                            .setYOffset(5)
                            .build();
                    droppyMenu.show();
                }
            });
        }catch (Exception e){
            Log.d(DataUtil.TAG_BASE,"This view can not include Header");
        }
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                mCurrentPhotoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        FileUtil fileUtil = new FileUtil(imageFileName,DataUtil.IMAGE_FOLDER);
        return fileUtil.get();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    protected void showDialogAskPermission(String message, final String permission, final int requestCode) {
        dialogEvent.showDialogAsk(message, null, null,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PermissionUtil.checkPermission(BaseActivity.this,
                                        permission, requestCode);
                            }
                        });
                    }
                }), null,
                View.VISIBLE);
    }
}
