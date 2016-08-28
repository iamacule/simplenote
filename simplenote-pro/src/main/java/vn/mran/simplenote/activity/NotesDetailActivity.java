package vn.mran.simplenote.activity;

import android.Manifest;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.mvp.presenter.NotesDetailPresenter;
import vn.mran.simplenote.mvp.view.NotesDetailView;
import vn.mran.simplenote.mvp.view.ToolAddNotesView;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.EventUtil;
import vn.mran.simplenote.util.PermissionUtil;
import vn.mran.simplenote.util.ScreenUtil;
import vn.mran.simplenote.util.StringUtil;
import vn.mran.simplenote.util.Utils;
import vn.mran.simplenote.view.CustomEditText;
import vn.mran.simplenote.view.Header;
import vn.mran.simplenote.view.ToolAddNote;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by MrAn on 24-Aug-16.
 */
public class NotesDetailActivity extends BaseActivity implements NotesDetailView, ToolAddNotesView {
    private Header header;
    private ToolAddNote toolAddNote;
    private ProgressBar progressBar;
    private CustomEditText txtTitle;
    private CustomEditText txtContent;
    private LinearLayout lnEdit;
    private final int ACTION_REQUEST_GALLERY = 0;
    private boolean isUpdated = false;
    private NotesDetailPresenter notesDetailPresenter;
    private EventUtil.KeyBoard keyBoard;

    @Override
    public int getView() {
        return R.layout.activity_notes_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteColorId = currentColorId;
        lnEdit.setBackgroundResource(noteColorId);
        txtContent.txtMain.setBackgroundResource(noteColorId);
        txtTitle.txtMain.setBackgroundResource(noteColorId);
    }

    @Override
    public void initView() {
        View v = getWindow().getDecorView().getRootView();
        header = new Header(v);
        header.title.setText(getString(R.string.notes_detail));
        header.setDefaultBtnBack();

        toolAddNote = new ToolAddNote(v, this);

        txtTitle = new CustomEditText(v, R.id.lnTitle);
        txtContent = new CustomEditText(v, R.id.lnContent);
        progressBar = (ProgressBar) findViewById(R.id.pbBar);
        lnEdit = (LinearLayout) findViewById(R.id.lnEdit);
        txtContent.editText.setFocusable(false);
        txtContent.editText.setFocusableInTouchMode(false);
        txtTitle.editText.setFocusable(false);
        txtTitle.editText.setFocusableInTouchMode(false);
        hideEdit();

        txtTitle.editText.setText(StringUtil.filterTitle(currentNotes.getTitle()));
        txtContent.txtMain.setBackgroundResource(currentNotes.getColorId());
        txtTitle.txtMain.setBackgroundResource(currentNotes.getColorId());
        lnEdit.setBackgroundResource(currentNotes.getColorId());
    }

    void showEdit() {
        toolAddNote.btnSave.setVisibility(View.VISIBLE);
        toolAddNote.btnClear.setVisibility(View.VISIBLE);
        toolAddNote.btnMore.setVisibility(View.VISIBLE);
        toolAddNote.btnEdit.setVisibility(View.GONE);
        txtContent.editText.setFocusable(true);
        txtContent.editText.setFocusableInTouchMode(true);
        txtTitle.editText.setFocusable(true);
        txtTitle.editText.setFocusableInTouchMode(true);
    }

    void hideEdit() {
        toolAddNote.btnSave.setVisibility(View.GONE);
        toolAddNote.btnClear.setVisibility(View.GONE);
        toolAddNote.btnMore.setVisibility(View.GONE);
        toolAddNote.btnEdit.setVisibility(View.VISIBLE);
    }

    @Override
    public void initValue() {
        notesDetailPresenter = new NotesDetailPresenter(this);
        toolAddNote.txtDate.setText(Utils.getDate(currentNotes.getId(), "yyyy-MM-dd:HH:mm"));
        keyBoard = new EventUtil.KeyBoard(this);
        toolAddNote.txtFolder.setText(currentFolder.getName());
        notesDetailPresenter.loadData(currentNotes.getContent());
    }

    @Override
    public void initAction() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnEdit:
                        showEdit();
                        break;
                    case R.id.btnClear:
                        clearText();
                        break;
                    case R.id.btnSave:
                        isUpdated = true;
                        update();
                        break;
                }
            }
        };
        toolAddNote.btnEdit.setOnClickListener(click);
        toolAddNote.btnClear.setOnClickListener(click);
        toolAddNote.btnSave.setOnClickListener(click);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY:
                    notesDetailPresenter.addImage
                            (notesDetailPresenter.createBitmapFromURINew(this, intent.getData(),
                                    ScreenUtil.getScreenWidth(getWindowManager()) / 3), txtContent.editText);
                    break;
                case TAKE_PICTURE_REQUEST_CODE:
                    notesDetailPresenter.addImage
                            (notesDetailPresenter.createBitmapFromURINew(this, mCurrentPhotoUri,
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

    @Override
    public void onWaiting() {
        progressBar.setVisibility(View.VISIBLE);
        txtContent.editText.setClickable(false);
    }

    @Override
    public void onLoadContent(SpannableStringBuilder builder) {
        txtContent.editText.setClickable(true);
        progressBar.setVisibility(View.GONE);
        txtContent.editText.setText(builder);
    }

    @Override
    public void onUpdateFinish() {
        Boast.makeText(this, getString(R.string.update_success)).show();
        keyBoard.hide(txtContent.editText);
    }

    @Override
    public void onUpdateFail(byte messageId) {
        Log.d(DataUtil.TAG_NOTES_DETAIL, "Message fail : " + messageId);
        switch (messageId) {
            case NotesDetailPresenter.EMPTY_CONTENT:
                Boast.makeText(this, getString(R.string.save_fail_empty)).show();
                break;

            case NotesDetailPresenter.UPDATE_EXCEPTION:
                Boast.makeText(this, getString(R.string.update_fail)).show();
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
            if (!isUpdated) {
                if (checkChanged()) {
                    dialogEvent.showDialogAsk(getString(R.string.update_your_note), null, null,
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            update();
                                            NotesDetailActivity.super.onBackPressed();
                                        }
                                    });
                                }
                            }), new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NotesDetailActivity.super.onBackPressed();
                                        }
                                    });
                                }
                            }), View.VISIBLE);
                } else {
                    NotesDetailActivity.super.onBackPressed();
                }
            } else {
                NotesDetailActivity.super.onBackPressed();
            }
        } else {
            Realm realm = RealmController.with().getRealm();
            realm.beginTransaction();
            RealmController.with().deleteNotesById(currentNotes.getId());
            realm.commitTransaction();
            super.onBackPressed();
        }
    }

    private boolean checkChanged() {
        if (((txtTitle.editText.getText().toString().equals(currentNotes.getTitle()) ||
                txtTitle.editText.getText().equals(StringUtil.IMAGE_STRING))) &&
                txtContent.editText.getText().toString().equals(currentNotes.getContent())) {
            return false;
        }
        return true;
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

    private void update() {
        notesDetailPresenter.update(currentNotes, txtTitle.editText.getText().toString().trim(),
                txtContent.editText.getText().toString().trim(), currentFolder.getId(), noteColorId);
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
}
