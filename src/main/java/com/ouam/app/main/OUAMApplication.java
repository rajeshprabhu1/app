package com.ouam.app.main;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.bumptech.glide.request.target.ViewTarget;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.onesignal.OneSignal;
import com.ouam.app.R;
import com.ouam.app.database.DatabaseHelper;
import com.ouam.app.database.DatabaseManager;
import com.ouam.app.database.DatabaseUtil;
import com.ouam.app.ui.HomeActivityFeedScreen;
import com.ouam.app.ui.OfflineActivityScreen;
import com.ouam.app.ui.OfflinePiningModeActivityScreen;
import com.ouam.app.ui.TutorialScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.sendbird.android.SendBird;

import io.fabric.sdk.android.Fabric;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OUAMApplication extends Application {
    private static boolean activityVisible;
    private static OUAMApplication mInstance;

    public static synchronized OUAMApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context context) {
        MultiDex.install(this);
        super.attachBaseContext(context);
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityStopped() {
        activityVisible = false;
    }

    public static void activityFinished() {
        activityVisible = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        mInstance = this;
        ViewTarget.setTagId(R.id.glide_tag);

        /*initialize the database*/
        DatabaseManager.initialize(getApplicationContext(), new DatabaseHelper(
                this));



        /*One signal push initialisation*/
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        AppEventsLogger.activateApp(this);


        /*SendBird Initialization*/
        SendBird.init(AppConstants.SEND_BIRD_LIVE_APP_ID, mInstance);

        /*init UncaughtException*/
        Thread.setDefaultUncaughtExceptionHandler(new unCaughtException());

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    /*unCaughtException*/
    private class unCaughtException implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Crashlytics.logException(ex);
            Class<?> nextScreenClass =
                    PreferenceUtil.getBoolPreferenceValue(mInstance, AppConstants.LOGIN_STATUS) &&
                            NetworkUtil.isNetworkAvailable(mInstance) && (new DatabaseUtil(mInstance)).getAllPins().size() > 0 ? OfflinePiningModeActivityScreen.class
                            : PreferenceUtil.getBoolPreferenceValue(mInstance, AppConstants.LOGIN_STATUS) &&
                            !NetworkUtil.isNetworkAvailable(mInstance) ? OfflineActivityScreen.class : PreferenceUtil.getBoolPreferenceValue(mInstance, AppConstants.LOGIN_STATUS) ?
                            HomeActivityFeedScreen.class : TutorialScreen.class;

            /*Restart application*/
            if (activityVisible) {
                Intent intent = new Intent(mInstance, nextScreenClass);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }

                Runtime.getRuntime().exit(0);
            }


        }
    }


    @Override
    public void registerActivityLifecycleCallbacks(
            ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
    }
}
