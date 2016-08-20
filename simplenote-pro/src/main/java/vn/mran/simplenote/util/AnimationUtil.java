package vn.mran.simplenote.util;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import vn.mran.simplenote.R;


/**
 * Created by MrAn on 13-May-16.
 */
public class AnimationUtil {
    public static Animation slideInTop(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_in_top);
    }

    public static Animation slideOutTop(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_out_top);
    }

    public static Animation slideInBottom(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slice_in_bottom);
    }

    public static Animation slideOutBottom(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slice_out_bottom);
    }

    public static Animation fadeIn(Context context) {
        return AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
    }

    public static Animation fadeOut(Context context) {
        return AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
    }
}
