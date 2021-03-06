package vn.mran.simplenote.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.task.ExportTask;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.util.PermissionUtil;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by MrAn on 18-Aug-16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Header header;
    public DialogEvent dialogEvent;
    private Intent intent;
    protected static Folder currentFolder;
    public static Notes currentNotes;
    protected static int currentColorId = -1;
    protected final int ACTION_REQUEST_GALLERY = 0;
    protected final int SPEECH_REQUEST_CODE = 1;
    protected final int TAKE_PICTURE_REQUEST_CODE = 2;
    private ExportTask exportTask;
    private RealmResults<Notes> realmResults;

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
        FileUtil.INTERNAL_STORAGE_PATH = getFilesDir().getPath();
        dialogEvent = new DialogEvent(this);
        initHeader();
    }

    private void initHeader() {
        try {
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
                                        case R.id.btnImport:
                                            Boast.makeText(BaseActivity.this, "Import click").show();
                                            break;
                                        case R.id.btnExport:
                                            PermissionUtil.checkAppPermission(BaseActivity.this);
                                            if (!PermissionUtil.permissionWriteExternalStorage) {
                                                showDialogAskPermission(getString(R.string.permission_storage),
                                                        Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtil.READ_EXTERNAL_STORAGE);
                                            } else {
                                                dialogEvent.showDialogAsk(Html.fromHtml(getString(R.string.guide_export)), null, null, new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                onExport();
                                                            }
                                                        });
                                                    }
                                                }), null, View.VISIBLE);
                                            }
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
        } catch (Exception e) {
            Log.d(DataUtil.TAG_BASE, "This view can not include Header");
        }
    }

    private void onExport() {
        realmResults = RealmController.with().getAllNotes();
        if (realmResults.size() > 0) {
            startExport();
        } else {
            Boast.makeText(this, getString(R.string.empty_notes)).show();
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

    public void goToActivityWithResult(Class activity, int requesCode) {
        intent = new Intent(this, activity);
        startActivityForResult(intent, requesCode);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void goToActivityWithIntData(Class activity, String key, String value) {
        intent = new Intent(this, activity);
        intent.putExtra(key, value);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    protected void goToIntentAction(int requestCode, String actionType) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(actionType);
        startActivityForResult(intent, requestCode);
    }

    protected void requestVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        FileUtil fileUtil = new FileUtil(imageFileName, Constant.IMAGE_FOLDER, null);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtil.CAMERA:
            case PermissionUtil.WRITE_EXTERNAL_STORAGE:
            case PermissionUtil.READ_EXTERNAL_STORAGE: {
                PermissionUtil.checkAppPermission(this);
                break;
            }
        }
    }

    private void startExport() {
        if (null != exportTask && !exportTask.isCancelled()) {
            exportTask.cancel(true);
            exportTask = null;
        }
        exportTask = new ExportTask(this);
        exportTask.execute(new Void[]{});
    }
}
