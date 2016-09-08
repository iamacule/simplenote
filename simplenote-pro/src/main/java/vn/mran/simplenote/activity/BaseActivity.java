package vn.mran.simplenote.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyFadeInAnimation;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.dialog.DialogEvent;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
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
    protected DialogEvent dialogEvent;
    private Intent intent;
    protected static Folder currentFolder;
    public static Notes currentNotes;
    protected static int currentColorId = -1;
    protected final int ACTION_REQUEST_GALLERY = 0;
    protected final int SPEECH_REQUEST_CODE = 1;
    protected final int TAKE_PICTURE_REQUEST_CODE = 2;
    protected Uri mCurrentPhotoUri;
    private ExportAsync exportAsync;
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
                                                FileUtil.INTERNAL_STORAGE_PATH = getFilesDir().getPath();
                                                dialogEvent.showDialogInfo(Html.fromHtml(getString(R.string.guide_export)), new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                onExport();
                                                            }
                                                        });
                                                    }
                                                }));
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

    public void goToActivityWithIntData(Class activity, String key, String value) {
        intent = new Intent(this, activity);
        intent.putExtra(key, value);
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

    private class ExportAsync extends AsyncTask<Void, String, Boolean> {
        private FileUtil fileUtil;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogEvent.showProgressDialog(getString(R.string.exporting));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return export();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            dialogEvent.updateDialogMessage(message);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialogEvent.dismissProgressDialog();
            if (aBoolean) {
                Boast.makeText(BaseActivity.this, getString(R.string.export_success)).show();
                openFolder();
            } else {
                Boast.makeText(BaseActivity.this, getString(R.string.export_fail)).show();
            }
        }

        private void openFolder() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(fileUtil.get().getParent());
            intent.setDataAndType(uri, "text/csv");
            startActivity(Intent.createChooser(intent, "Open folder"));
        }

        private boolean export() {
            try {
                publishProgress(getString(R.string.clear_export_folder));
                Thread.sleep(500);
                //Clear export folder
                FileUtil exportFolderRoot = new FileUtil();
                exportFolderRoot.createFolder(Constant.EXPORT_FOLDER,FileUtil.PUBLIC_STORAGE_PATH);
                FileUtil.delFile(exportFolderRoot.getChildFolder().getAbsoluteFile());

                //Create Export root folder
                exportFolderRoot = new FileUtil();
                exportFolderRoot.createFolder(Constant.EXPORT_FOLDER,FileUtil.PUBLIC_STORAGE_PATH);

                //Create Export folder
                FileUtil progress = new FileUtil();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String exportFolderName = Constant.EXPORT_FOLDER_NAME + timeStamp;
                progress.createFolder(exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
                String externalExportFolder = progress.getChildFolder().getAbsolutePath();

                //Get Image folder
                progress.createFolder(Constant.IMAGE_FOLDER, null);
                File srcFile = new File(progress.getChildFolder().getAbsolutePath());

                //Copy Image
                publishProgress(getString(R.string.checking_image));
                Thread.sleep(500);
                File[] files = srcFile.listFiles();
                if (srcFile.length() > 0) {
                    for (int i = 0; i < files.length; i++) {
                        FileUtil.copyFile(files[i], Constant.IMAGE_FOLDER, externalExportFolder);
                        Log.d(DataUtil.TAG_BASE, "Copy file success : " + files[0].getName());
                    }
                }

                //Create Excel file
                //Create excel file
                //New Workbook
                publishProgress(getString(R.string.export_excel));
                Thread.sleep(500);
                Workbook wb = new HSSFWorkbook();

                Cell c = null;

                //Cell style for header row
                CellStyle cs = wb.createCellStyle();
                cs.setFillForegroundColor(HSSFColor.LIME.index);
                cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                //New Sheet
                final Sheet sheet1 = wb.createSheet(exportFolderName.substring(1, exportFolderName.length()));

                String[] columnTitle = new String[]{
                        Constant.COLUMN_TITLE,
                        Constant.COLUMN_CONTENT,
                        Constant.COLUMN_COLOR_ID,
                        Constant.COLUMN_FOLDER_NAME
                };

                Row row = sheet1.createRow(0);
                for (int i = 0; i < columnTitle.length; i++) {
                    c = row.createCell(i);
                    c.setCellValue(columnTitle[i]);
                    c.setCellStyle(cs);
                    sheet1.setColumnWidth(i, (15 * 500));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < realmResults.size(); i++) {
                            Row row = sheet1.createRow(i + 1);
                            Cell title = row.createCell(0);
                            title.setCellValue(realmResults.get(i).getTitle());
                            Cell content = row.createCell(1);
                            content.setCellValue(realmResults.get(i).getContent());
                            Cell colorId = row.createCell(2);
                            colorId.setCellValue(realmResults.get(i).getColorId());
                            Cell folderName = row.createCell(3);
                            Folder folder = RealmController.with().getFolderById(realmResults.get(i).getFolderId());
                            folderName.setCellValue(folder.getName());

                        }
                    }
                });

                FileUtil excelFile = new FileUtil(Constant.FILE_NAME_EXCEL_EXPORT, exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
                FileOutputStream os = null;

                try {
                    os = new FileOutputStream(excelFile.get());
                    wb.write(os);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        if (null != os)
                            os.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }

                publishProgress(getString(R.string.create_guide));
                Thread.sleep(500);
                //Create Guide file
                FileUtil guideFile = new FileUtil(Constant.FILE_NAME_GUIDE_EXPORT, exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
                guideFile.writeString(getString(R.string.guide_export));

                //Zip image
                publishProgress(getString(R.string.zip_data));
                Thread.sleep(500);
                srcFile = new File(externalExportFolder);
                files = srcFile.listFiles();
                fileUtil = new FileUtil(exportFolderName + Constant.ZIP_EXTENSION, Constant.EXPORT_FOLDER, FileUtil.PUBLIC_STORAGE_PATH);
                FileOutputStream fos = new FileOutputStream(fileUtil.get());
                ZipOutputStream zos = new ZipOutputStream(fos);
                for (int i = 0; i < files.length; i++) {
                    addDirToZipArchive(zos, files[i], null);
                }
                zos.flush();
                fos.flush();
                zos.close();
                fos.close();

                //Delete folder , keep zip file
                publishProgress(getString(R.string.finish_export));
                Thread.sleep(500);
                FileUtil.delFile(new File(externalExportFolder));

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
            if (fileToZip == null || !fileToZip.exists()) {
                return;
            }

            String zipEntryName = fileToZip.getName();
            if (parrentDirectoryName != null && !parrentDirectoryName.isEmpty()) {
                zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
            }

            if (fileToZip.isDirectory()) {
                for (File file : fileToZip.listFiles()) {
                    addDirToZipArchive(zos, file, zipEntryName);
                }
            } else {
                System.out.println("   " + zipEntryName);
                byte[] buffer = new byte[1024];
                FileInputStream fis = new FileInputStream(fileToZip);
                zos.putNextEntry(new ZipEntry(zipEntryName));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
        }
    }

    private void startExport() {
        if (null != exportAsync && !exportAsync.isCancelled()) {
            exportAsync.cancel(true);
            exportAsync = null;
        }
        exportAsync = new ExportAsync();
        exportAsync.execute(new Void[]{});
    }
}
