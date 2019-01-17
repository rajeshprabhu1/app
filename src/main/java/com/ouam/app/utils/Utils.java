package com.ouam.app.utils;


import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.ouam.app.R;

public class Utils {

    public static TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    private void showError(Context context, View view) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(shake);
    }


    public static float calculationByDistance(double firstPointLatDouble, double firstPointLngDouble,
                                              double secondPointLatDouble, double secondPointLngDouble) {

        Location firstPointLocation = new Location(AppConstants.FACEBOOK);
        firstPointLocation.setLatitude(firstPointLatDouble);
        firstPointLocation.setLongitude(firstPointLngDouble);

        Location secondPointLocation = new Location(AppConstants.GOOGLE);
        secondPointLocation.setLatitude(secondPointLatDouble);
        secondPointLocation.setLongitude(secondPointLngDouble);

        // distance = locationA.distanceTo(locationB);   // in meters
        return firstPointLocation.distanceTo(secondPointLocation) / 1000;   // in km
    }





}
