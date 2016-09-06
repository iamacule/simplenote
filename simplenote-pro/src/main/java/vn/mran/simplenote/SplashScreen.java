package vn.mran.simplenote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import vn.mran.simplenote.activity.InputPinCodeActivity;
import vn.mran.simplenote.activity.MainActivity;
import vn.mran.simplenote.util.Constant;
import vn.mran.simplenote.util.DataUtil;
import vn.mran.simplenote.util.FileUtil;
import vn.mran.simplenote.util.Preferences;

public class SplashScreen extends AppCompatActivity {
    private Intent intent;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initValue();
        initActivity();
    }

    private void initValue() {
        preferences = new Preferences(this);
    }

    private void initActivity() {
        String securityType = preferences.getStringValue(Preferences.SECURITY_TYPE);
        Log.d(DataUtil.TAG, "Security type : " + securityType);
        switch (securityType) {
            case "":
                intent = new Intent(this, MainActivity.class);
                break;
            case Constant.SECURITY_NONE:
                intent = new Intent(this, MainActivity.class);
                break;
            case Constant.SECURITY_PIN:
                intent = new Intent(this, InputPinCodeActivity.class);
                intent.putExtra(Constant.SECURITY_FLAG, Constant.SECURITY_MAIN);
                break;
        }
        startActivity(intent);
        finish();
    }
}
