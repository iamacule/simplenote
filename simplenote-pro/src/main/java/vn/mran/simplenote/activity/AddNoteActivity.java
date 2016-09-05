package vn.mran.simplenote.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.mvp.presenter.AddNotePresenter;
import vn.mran.simplenote.mvp.view.AddNotesView;
import vn.mran.simplenote.mvp.view.ToolAddNotesView;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.PermissionUtil;
import vn.mran.simplenote.util.ScreenUtil;
import vn.mran.simplenote.util.Utils;
import vn.mran.simplenote.view.ToolAddNote;
import vn.mran.simplenote.view.toast.Boast;
import vn.mran.simplenote.view.CustomEditText;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class AddNoteActivity extends BaseActivity implements AddNotesView, ToolAddNotesView {
    private AddNotePresenter addNotePresenter;
    private ToolAddNote toolAddNote;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;
    private LinearLayout lnEdit;
    private boolean isSaved = false;

    @Override
    public int getView() {
        return R.layout.activity_add_note;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(-1!=currentColorId){
            lnEdit.setBackgroundResource(currentColorId);
            txtContent.txtMain.setBackgroundResource(currentColorId);
            txtTitle.txtMain.setBackgroundResource(currentColorId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentColorId = -1;
    }

    @Override
    public void initView() {
        View v = getWindow().getDecorView().getRootView();
        header.title.setText(getString(R.string.add_note));
        header.btnBack.setVisibility(View.VISIBLE);
        header.setDefaultBtnBack();

        toolAddNote = new ToolAddNote(v, this);

        txtTitle = new CustomEditText(v, R.id.lnTitle);
        txtContent = new CustomEditText(v, R.id.lnContent);
        lnEdit = (LinearLayout) findViewById(R.id.lnEdit);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        txtContent = new CustomEditText(getWindow().getDecorView().getRootView(), R.id.lnContent);
        txtTitle = new CustomEditText(getWindow().getDecorView().getRootView(), R.id.lnTitle);
        CharSequence contentText = txtContent.editText.getText();
        CharSequence titleText = txtTitle.editText.getText();
        outState.putCharSequence("contentText", contentText);
        outState.putCharSequence("titleText", titleText);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        txtContent = new CustomEditText(getWindow().getDecorView().getRootView(), R.id.lnContent);
        txtTitle = new CustomEditText(getWindow().getDecorView().getRootView(), R.id.lnTitle);

        CharSequence contentText = savedState.getCharSequence("contentText");
        CharSequence titleText = savedState.getCharSequence("titleText");

        txtContent.editText.setText(contentText);
        txtTitle.editText.setText(titleText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY:
                    addNotePresenter.addImage
                            (addNotePresenter.createBitmapFromURI(this, intent.getData(),
                                    ScreenUtil.getScreenWidth(getWindowManager()) / 3), txtContent.editText);
                    break;
                case TAKE_PICTURE_REQUEST_CODE:
                    addNotePresenter.addImage
                            (addNotePresenter.createBitmapFromURI(this, mCurrentPhotoUri,
                                    ScreenUtil.getScreenWidth(getWindowManager()) / 3), txtContent.editText);
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
            case PermissionUtil.CAMERA:
            case PermissionUtil.WRITE_EXTERNAL_STORAGE:
            case PermissionUtil.READ_EXTERNAL_STORAGE: {
                PermissionUtil.checkAppPermission(this);
                break;
            }
        }
    }

    private void save(boolean back) {
        addNotePresenter.save(txtTitle.editText.getText().toString().trim(),
                txtContent.editText.getText().toString().trim(), currentFolder.getId(), currentColorId, back);
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
            showDialogAskPermission(getString(R.string.permission_storage), Manifest.permission.READ_EXTERNAL_STORAGE, PermissionUtil.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onVoice() {
        requestVoice();
    }

    @Override
    public void onCamera() {
        PermissionUtil.checkAppPermission(this);
        if (!PermissionUtil.permissionCamera) {
            showDialogAskPermission(getString(R.string.permission_camera), Manifest.permission.CAMERA, PermissionUtil.CAMERA);
        } else if (!PermissionUtil.permissionWriteExternalStorage) {
            showDialogAskPermission(getString(R.string.permission_storage), Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtil.WRITE_EXTERNAL_STORAGE);
        } else {
            requestTakePhoTo();
        }

    }

    @Override
    public void onStyle() {
        goToActivity(StyleColorActivity.class);
    }
}
