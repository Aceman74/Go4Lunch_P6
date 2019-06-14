package com.aceman.go4lunch.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.aceman.go4lunch.R;

/**
 * Created by Lionel JOFFRAY - on 12/06/2019.
 */
public class AnimationClass {

    static Animation mAnimation;

    public static Animation animClick(Context context) {

        mAnimation = AnimationUtils.loadAnimation(context, R.anim.click_anim);

        return mAnimation;
    }

    public static Animation animClickFadeOut(Context context) {

        mAnimation = AnimationUtils.loadAnimation(context, R.anim.click_fade_out);

        return mAnimation;
    }

    public static Animation refreshClick(Context context) {

        mAnimation = AnimationUtils.loadAnimation(context, R.anim.refresh_btn_anim);

        return mAnimation;
    }

    public static Animation pulseClick(Context context) {

        mAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse_effect);

        return mAnimation;
    }


    public static void setFadeAnimation(View view, Context context) {
        mAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        view.startAnimation(mAnimation);
    }

}
