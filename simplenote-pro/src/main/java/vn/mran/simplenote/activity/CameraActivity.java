package vn.mran.simplenote.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.ScreenUtil;

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
    private String data;

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
                try {
                    File file = createImageFile();
                    FileOutputStream outStream = new FileOutputStream(file);
                    outStream.write(data);
                    outStream.close();
                    CameraActivity.this.data = file.getPath();
                }catch (Exception e){
                    e.printStackTrace();
                }
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
                        CameraActivity.this.finish();
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

        setParamerter();

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    private void setParamerter() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizes) {
            Log.d("Preview size : ", size.width + " , " + size.height);
        }

        Camera.Size previewSize = previewSizes.get(0);


        if (display.getRotation() == Surface.ROTATION_0) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setDisplayOrientation(180);
        }
        parameters.set("rotation", 90);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
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