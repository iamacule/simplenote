package vn.mran.simplenote.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.AddNotePresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.mvp.view.ToolAddNotesView;
import vn.mran.simplenote.util.AddImageUtil;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.PermissionUtil;
import vn.mran.simplenote.util.ResizeBitmap;
import vn.mran.simplenote.util.ScreenUtil;
import vn.mran.simplenote.util.Utils;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;
import vn.mran.simplenote.view.toast.Boast;
import vn.mran.simplenote.view.CustomEditText;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class AddNoteActivity extends BaseActivity implements AddNotesView,ToolAddNotesView {
    private AddNotePresenter addNotePresenter;
    private Header header;
    private ToolAddNote toolAddNote;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;
    private final int ACTION_REQUEST_GALLERY = 0;
    private boolean isSaved = false;

    @Override
    public int getView() {
        return R.layout.activity_add_note;
    }

    @Override
    public void initView() {
        View v = getWindow().getDecorView().getRootView();
        header = new Header(v);
        header.title.setText(getString(R.string.add_note));
        header.setDefaultBtnBack();

        toolAddNote = new ToolAddNote(v,this);

        txtTitle = new CustomEditText(v, R.id.lnTitle);
        txtContent = new CustomEditText(v, R.id.lnContent);
    }

    @Override
    public void initValue() {
        addNotePresenter = new AddNotePresenter(this);
        toolAddNote.txtDate.setText(Utils.getDate(System.currentTimeMillis(), "yyyy-MM-dd"));
        toolAddNote.txtFolder.setText(currentFolder.getName());
    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnClear:
                        clearText();
                        break;
                    case R.id.btnSave:
                        isSaved = true;
                        save(true);
                        break;
                }
            }
        };
        toolAddNote.btnClear.setOnClickListener(click);
        toolAddNote.btnSave.setOnClickListener(click);
    }

    private void showDialogAskPermission() {
        dialogEvent.showDialogAsk(getString(R.string.permission_storage), null, null,
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PermissionUtil.checkPermission(AddNoteActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtil.READ_EXTERNAL_STORAGE);
                            }
                        });
                    }
                }), null,
                View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY:
                    TAKE_PICTURE_REQUEST_CODE:
                    addNotePresenter.addImage
                            (addNotePresenter.createBitmapFromURI(this, intent.getData(),
                                    ScreenUtil.getScreenWidth(getWindowManager())/3), txtContent.editText);
                    break;
                case SPEECH_REQUEST_CODE:
                    List<String> results = intent.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    txtContent.editText.append(spokenText);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.READ_EXTERNAL_STORAGE: {
                PermissionUtil.checkAppPermission(this);
                return;
            }
        }
    }

    private void save(boolean back) {
        addNotePresenter.save(txtTitle.editText.getText().toString().trim(),
                txtContent.editText.getText().toString().trim(), currentFolder.getId(), back);
    }

    private void clearText() {
        boolean clear = false;
        if (!txtTitle.isEmpty()) {
            txtTitle.clearText();
            clear = true;
        }
        if (!txtContent.isEmpty()) {
            txtContent.clearText();
            clear = true;
        }
        if (clear)
            Boast.makeText(this, getString(R.string.clear_success)).show();
    }

    @Override
    public void onSaveFinish(boolean back) {
        Boast.makeText(this, getString(R.string.save_success)).show();
        if (back)
            super.onBackPressed();
    }

    @Override
    public void onSaveFail(byte messageId) {
        Log.d(DataUtil.TAG_ADD_NOTES_ACTIVITY, "Message fail : " + messageId);
        switch (messageId) {
            case AddNotePresenter.EMPTY_CONTENT:
                Boast.makeText(this, getString(R.string.save_fail_empty)).show();
                break;

            case AddNotePresenter.SAVE_EXCEPTION:
                Boast.makeText(this, getString(R.string.save_fail)).show();
                break;
        }
    }

    @Override
    public void addImage(SpannableStringBuilder data) {
        txtContent.editText.setText(data);
    }

    @Override
    public void addSetTxtContentSelection(int selection) {
        txtContent.editText.setSelection(selection);
    }

    @Override
    public void onBackPressed() {
        if (DataUtil.checkStringEmpty(txtContent.editText.getText().toString())) {
            if (!isSaved) {
                dialogEvent.showDialogAsk(getString(R.string.save_your_note), null, null,
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        save(false);
                                        AddNoteActivity.super.onBackPressed();
                                    }
                                });
                            }
                        }), new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AddNoteActivity.super.onBackPressed();
                                    }
                                });
                            }
                        }), View.VISIBLE);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPhoto() {
        PermissionUtil.checkAppPermission(this);
        if (PermissionUtil.permissionReadExternalStorage) {
            goToIntentAction(ACTION_REQUEST_GALLERY, "image/*");
        } else {
            showDialogAskPermission();
        }
    }

    @Override
    public void onVoice() {
        requestVoice();
    }

    @Override
    public void onCamera() {
        requestTakePhoTo();
    }
}
