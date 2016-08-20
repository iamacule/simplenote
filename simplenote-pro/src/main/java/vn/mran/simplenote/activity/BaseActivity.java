package vn.mran.simplenote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import vn.mran.simplenote.R;

/**
 * Created by MrAn on 18-Aug-16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());
        initView();
        initValue();
        initAction();
    }

    public abstract int getView();
    public abstract void initView();
    public abstract void initValue();
    public abstract void initAction();

    protected void goToActivity(Class activity) {
        startActivity(new Intent(this, activity));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
