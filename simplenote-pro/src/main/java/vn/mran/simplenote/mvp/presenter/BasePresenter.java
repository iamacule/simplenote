package vn.mran.simplenote.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vn.mran.simplenote.mvp.view.BaseView;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.FileUtil;

/**
 * Created by Mr An on 06/09/2016.
 */
public class BasePresenter {
    private Activity activity;
    private BaseView baseView;
    private Uri mCurrentPhotoUri;

    public BasePresenter(Activity activity) {
        this.activity = activity;
        this.baseView = (BaseView) activity;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        FileUtil fileUtil = new FileUtil(imageFileName, Constant.IMAGE_FOLDER, null);
        return fileUtil.get();
    }

    public Uri getmCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }
}
