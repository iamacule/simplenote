package vn.mran.simplenote.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.mran.simplenote.R;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback {
    public static final String DATA_CAMERA = "DATA_CAMERA";
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private PictureCallback jpegCallback;

    private LinearLayout lnCaptune;
    private LinearLayout lnNavigation;
    private TextView btnCancel;
    private TextView btnConfirm;
    private byte[] data;

    @Override
    public int getView() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        lnCaptune = (LinearLayout) findViewById(R.id.lnCaptune);
        lnNavigation = (LinearLayout) findViewById(R.id.lnNavigation);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        btnConfirm = (TextView) findViewById(R.id.btnConfirm);
    }

    @Override
    public void initValue() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void initAction() {
        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraActivity.this.data = data;
                lnCaptune.setVisibility(View.GONE);
                lnNavigation.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnConfirm:
                        Intent intent = new Intent();
                        intent.putExtra(DATA_CAMERA, data);
                        setResult(CameraActivity.RESULT_OK, intent);
                        finish();
                        break;

                    case R.id.btnCancel:
                        lnNavigation.setVisibility(View.GONE);
                        lnCaptune.setVisibility(View.VISIBLE);
                        refreshCamera();
                        break;

                    case R.id.lnCaptune:
                        captureImage();
                        break;
                }
            }
        };
        lnCaptune.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
        btnConfirm.setOnClickListener(onClickListener);
    }

    public void captureImage() {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

}