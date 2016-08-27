package vn.mran.simplenote.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by Mr An on 27/08/2016.
 */
public class StyleColor {
    private LinearLayout btnRed;
    private LinearLayout btnBlue;
    private LinearLayout btnGreen;
    private LinearLayout btnYellow;
    private LinearLayout btnOrange;
    private LinearLayout btnPink;
    private LinearLayout btnPurple;
    private LinearLayout btnWhite;
    private LinearLayout btnBlueGrey;
    private LinearLayout btnBrown;
    public TextView btnConfirm;
    public TextView btnCancel;
    private RelativeLayout preview;
    public int colorChooser;

    public StyleColor(View view) {
        btnConfirm = (TextView) view.findViewById(R.id.btnConfirm);
        btnCancel = (TextView) view.findViewById(R.id.btnCancel);
        btnRed = (LinearLayout) view.findViewById(R.id.btnRed);
        btnBlue = (LinearLayout) view.findViewById(R.id.btnBlue);
        btnGreen = (LinearLayout) view.findViewById(R.id.btnGreen);
        btnYellow = (LinearLayout) view.findViewById(R.id.btnYellow);
        btnOrange = (LinearLayout) view.findViewById(R.id.btnOrange);
        btnPink = (LinearLayout) view.findViewById(R.id.btnPink);
        btnPurple = (LinearLayout) view.findViewById(R.id.btnPurple);
        btnWhite = (LinearLayout) view.findViewById(R.id.btnWhite);
        btnBlueGrey = (LinearLayout) view.findViewById(R.id.btnBlueGrey);
        btnBrown = (LinearLayout) view.findViewById(R.id.btnBrown);
        preview = (RelativeLayout) view.findViewById(R.id.preview);
        TouchEffect.addAlpha(btnCancel);
        TouchEffect.addAlpha(btnConfirm);
        TouchEffect.addAlpha(btnRed);
        TouchEffect.addAlpha(btnBlue);
        TouchEffect.addAlpha(btnGreen);
        TouchEffect.addAlpha(btnYellow);
        TouchEffect.addAlpha(btnOrange);
        TouchEffect.addAlpha(btnPink);
        TouchEffect.addAlpha(btnPurple);
        TouchEffect.addAlpha(btnWhite);
        TouchEffect.addAlpha(btnBlueGrey);
        TouchEffect.addAlpha(btnBrown);
        setOnClick();
    }

    private void setOnClick() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRed:
                        setPreview(R.color.red_50);
                        break;
                    case R.id.btnBlue:
                        setPreview(R.color.blue_50);
                        break;
                    case R.id.btnBlueGrey:
                        setPreview(R.color.blue_grey_50);
                        break;
                    case R.id.btnBrown:
                        setPreview(R.color.brown_50);
                        break;
                    case R.id.btnGreen:
                        setPreview(R.color.green_50);
                        break;
                    case R.id.btnPurple:
                        setPreview(R.color.purple_50);
                        break;
                    case R.id.btnPink:
                        setPreview(R.color.pink_50);
                        break;
                    case R.id.btnOrange:
                        setPreview(R.color.orange_50);
                        break;
                    case R.id.btnWhite:
                        setPreview(R.color.white);
                        break;
                    case R.id.btnYellow:
                        setPreview(R.color.yellow_50);
                }
            }
        };
        btnRed.setOnClickListener(click);
        btnBlue.setOnClickListener(click);
        btnBlueGrey.setOnClickListener(click);
        btnBrown.setOnClickListener(click);
        btnGreen.setOnClickListener(click);
        btnWhite.setOnClickListener(click);
        btnOrange.setOnClickListener(click);
        btnPink.setOnClickListener(click);
        btnYellow.setOnClickListener(click);
        btnPurple.setOnClickListener(click);
        btnConfirm.setOnClickListener(click);
        btnCancel.setOnClickListener(click);
    }

    private void setPreview(int color) {
        colorChooser = color;
        preview.setBackgroundResource(color);
    }
}
