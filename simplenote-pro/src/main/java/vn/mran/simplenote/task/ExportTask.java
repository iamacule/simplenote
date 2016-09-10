package vn.mran.simplenote.task;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.realm.RealmResults;
import vn.mran.simplenote.R;
import vn.mran.simplenote.activity.BaseActivity;
import vn.mran.simplenote.model.Folder;
import vn.mran.simplenote.model.Notes;
import vn.mran.simplenote.realm.RealmController;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.view.toast.Boast;

/**
 * Created by Mr An on 08/09/2016.
 */
public class ExportTask extends AsyncTask<Void, String, Boolean> {
    private FileUtil fileUtil;
    private BaseActivity baseActivity;
    private RealmResults<Notes> realmResults;

    public ExportTask(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        realmResults = RealmController.with().getAllNotes();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        baseActivity.dialogEvent.showProgressDialog(baseActivity.getString(R.string.exporting));
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return export();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        String message = values[0];
        baseActivity.dialogEvent.updateDialogMessage(message);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        baseActivity.dialogEvent.dismissProgressDialog();
        if (aBoolean) {
            Boast.makeText(baseActivity, baseActivity.getString(R.string.export_success)).show();
            openFolder();
        } else {
            Boast.makeText(baseActivity, baseActivity.getString(R.string.export_fail)).show();
        }
    }

    private void openFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(fileUtil.get().getParent());
        intent.setDataAndType(uri, "text/csv");
        baseActivity.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    private boolean export() {
        try {
            publishProgress(baseActivity.getString(R.string.clear_export_folder));
            Thread.sleep(500);
            //Clear export folder
            FileUtil exportFolderRoot = new FileUtil();
            exportFolderRoot.createFolder(Constant.EXPORT_FOLDER, FileUtil.PUBLIC_STORAGE_PATH);
            FileUtil.delFile(exportFolderRoot.getChildFolder().getAbsoluteFile());

            //Create Export root folder
            exportFolderRoot = new FileUtil();
            exportFolderRoot.createFolder(Constant.EXPORT_FOLDER, FileUtil.PUBLIC_STORAGE_PATH);

            //Create Export folder
            FileUtil progress = new FileUtil();
            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String exportFolderName = Constant.EXPORT_FOLDER_NAME + timeStamp;
            progress.createFolder(exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
            String externalExportFolder = progress.getChildFolder().getAbsolutePath();

            //Get Image folder
            progress.createFolder(Constant.IMAGE_FOLDER, null);
            File srcFile = new File(progress.getChildFolder().getAbsolutePath());

            //Copy Image
            publishProgress(baseActivity.getString(R.string.checking_image));
            Thread.sleep(500);
            File[] files = srcFile.listFiles();
            if (srcFile.length() > 0) {
                for (int i = 0; i < files.length; i++) {
                    FileUtil.copyFile(files[i], Constant.IMAGE_FOLDER, externalExportFolder);
                    Log.d(DataUtil.TAG_BASE, "Copy file success : " + files[0].getName());
                }
            }

            //Create Excel file
            //New Workbook
            publishProgress(baseActivity.getString(R.string.export_excel));
            Thread.sleep(500);
            Workbook wb = new HSSFWorkbook();
            final CreationHelper createHelper = wb.getCreationHelper();

            Cell c = null;

            //Cell style for header row
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            final CellStyle csNormal = wb.createCellStyle();
            csNormal.setWrapText(true);

            //New Sheet
            final Sheet sheet1 = wb.createSheet(exportFolderName.substring(1, exportFolderName.length()));

            String[] columnTitle = new String[]{
                    Constant.COLUMN_ID,
                    Constant.COLUMN_TITLE,
                    Constant.COLUMN_CONTENT,
                    Constant.COLUMN_COLOR_ID,
                    Constant.COLUMN_FOLDER_NAME
            };

            int[] columnLength = new int[]{
                    3500,
                    6000,
                    8000,
                    3500,
                    3500
            };

            Row row = sheet1.createRow(0);
            for (int i = 0; i < columnTitle.length; i++) {
                c = row.createCell(i);
                c.setCellValue(columnTitle[i]);
                c.setCellStyle(cs);
                sheet1.setColumnWidth(i, columnLength[i]);
            }
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < realmResults.size(); i++) {
                        Row row = sheet1.createRow(i + 1);
                        //Cell id
                        Cell id = row.createCell(0);
                        id.setCellStyle(csNormal);
                        String idString = realmResults.get(i).getId() + "";
                        id.setCellValue(idString);

                        //Cell title
                        Cell title = row.createCell(1);
                        title.setCellStyle(csNormal);
                        title.setCellValue(realmResults.get(i).getTitle());

                        //Cell Content
                        Cell content = row.createCell(2);
                        content.setCellStyle(csNormal);
                        content.setCellValue(realmResults.get(i).getContent());
                        content.setCellValue(createHelper.createRichTextString(realmResults.get(i).getContent()));

                        //Cell color id
                        Cell colorId = row.createCell(3);
                        colorId.setCellStyle(csNormal);
                        colorId.setCellValue(realmResults.get(i).getColorId());

                        //Cell folder name
                        Cell folderName = row.createCell(4);
                        folderName.setCellStyle(csNormal);
                        Folder folder = RealmController.with().getFolderById(realmResults.get(i).getFolderId());
                        folderName.setCellValue(folder.getName());

                    }
                }
            });

            FileUtil excelFile = new FileUtil(Constant.FILE_NAME_EXCEL_EXPORT, exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
            boolean isWriteSuccess = false;
            while (!isWriteSuccess){
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(excelFile.get());
                    wb.write(os);
                    isWriteSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    isWriteSuccess = false;
                    try{
                        Thread.sleep(500);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                finally {
                    try {
                        if (null != os)
                            os.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }
            }

            publishProgress(baseActivity.getString(R.string.create_guide));
            Thread.sleep(500);
            //Create Guide file
            FileUtil guideFile = new FileUtil(Constant.FILE_NAME_GUIDE_EXPORT, exportFolderName, exportFolderRoot.getChildFolder().getAbsolutePath());
            guideFile.writeString(baseActivity.getString(R.string.guide_export));

            //Zip image
            publishProgress(baseActivity.getString(R.string.zip_data));
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
            publishProgress(baseActivity.getString(R.string.finish_export));
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
