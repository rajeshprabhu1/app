package com.ouam.app.main;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.ui.HomeActivityFeedScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements InterfaceTwoBtnCallBack {

    private AppCompatActivity mActivity;
    private List<String> mAppPermissionsStrArrList;
    private InterfaceTwoBtnCallBack mPermissionCallback = null;
    private int mAskPermissionCountInt = 0;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Default Init*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;

        /*Init default font*/
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/Dosis-Medium.otf").build());

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    /*Apply font plugin default class*/
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    protected int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels;
    }

    /*This method is used to check, if the current view can be focused in the edit text */
    protected void setupUI(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(mActivity);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View mInnerView = ((ViewGroup) view).getChildAt(i);
                setupUI(mInnerView);
            }
        }
    }

    /*Keypad to be hidden when a touch made outside the edit text*/
    public static void hideSoftKeyboard(Activity activity) {
        try {
            if (activity != null && !activity.isFinishing()) {
                InputMethodManager mInputMethodManager = (InputMethodManager) activity
                        .getSystemService(INPUT_METHOD_SERVICE);

                if (mInputMethodManager != null && activity.getCurrentFocus() != null
                        && activity.getCurrentFocus().getWindowToken() != null) {
                    mInputMethodManager.hideSoftInputFromWindow(activity
                            .getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            Log.e(activity.getClass().getSimpleName(), e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        OUAMApplication.activityResumed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        OUAMApplication.activityStopped();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /*Application control*/
    protected OUAMApplication app() {
        return ((OUAMApplication) mActivity.getApplication());
    }


    /*Direct to next activity*/
    public void nextScreen(Class<?> clazz, boolean animType) {
        runOnUiThread(() -> {
            Intent nextScreenIntent = new Intent(getApplicationContext(), clazz);
            mActivity.startActivity(nextScreenIntent);

            mActivity.overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);


            if (clearBackGroundScreen(clazz)) {
                AppConstants.PREVIOUS_SCREEN = new ArrayList<>();
            }

            AppConstants.PREVIOUS_SCREEN.add(clazz.getCanonicalName());

            if (!animType) {
                mActivity.finish();
            }
        });


    }


    public void previousScreen(Class<?> clazz, boolean animType) {

        runOnUiThread(() -> {
            Intent previousScreenIntent = new Intent(getApplicationContext(), clazz);
            previousScreenIntent = clearBackGroundActivity(clazz, previousScreenIntent);
            mActivity.startActivity(previousScreenIntent);

//            if (animType) {
////            mActivity.overridePendingTransition(R.anim.slide_up,
////                    R.anim.slide_down);
//
//                mActivity.overridePendingTransition(R.anim.no_change_anim,
//                        R.anim.slide_down);
//
//
//            } else {
            mActivity.overridePendingTransition(R.anim.slide_out_right,
                    R.anim.slide_in_left);
//            }
            if (clearBackGroundScreen(clazz)) {
                AppConstants.PREVIOUS_SCREEN = new ArrayList<>();
            }
            AppConstants.PREVIOUS_SCREEN.add(clazz.getCanonicalName());
            if (!animType) {
                mActivity.finish();
            }
        });

    }


    /*Clear the all background activity*/
    private boolean clearBackGroundScreen(Class<?> clazz) {
        String classStr = clazz.getSimpleName();
        return classStr.equalsIgnoreCase(AppConstants.HOME_ACTIVITY_FEED_SCREEN);
    }

    public void backScreen(boolean animType) {
//        if (AppConstants.PREVIOUS_SCREEN != null && AppConstants.PREVIOUS_SCREEN.size() > 1) {
//            AppConstants.PREVIOUS_SCREEN.remove(AppConstants.PREVIOUS_SCREEN.size() - 1);
//            Class<?> clazz = null;
//            try {
//                clazz = Class.forName(AppConstants.PREVIOUS_SCREEN.get(AppConstants.PREVIOUS_SCREEN.size() - 1));
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            if (clazz != null) {
//
//                Intent previousScreenIntent = new Intent(getApplicationContext(), clazz);
//                previousScreenIntent = clearBackGroundActivity(clazz, previousScreenIntent);
//                mActivity.startActivity(previousScreenIntent);
////            if (animType) {
//////                mActivity.overridePendingTransition(R.anim.slide_up,
//////                        R.anim.slide_down);
////
////                mActivity.overridePendingTransition(R.anim.no_change_anim,
////                        R.anim.slide_down);
////
////
////
////            } else {
//                mActivity.overridePendingTransition(R.anim.slide_out_right,
//                        R.anim.slide_in_left);
////            }
//                hideSoftKeyboard(mActivity);
//
//                mActivity.finish();
//            }
//
//        }

        if (AppConstants.PREVIOUS_SCREEN != null && AppConstants.PREVIOUS_SCREEN.size() > 1) {
            AppConstants.PREVIOUS_SCREEN.remove(AppConstants.PREVIOUS_SCREEN.size() - 1);
            Class<?> clazz = null;
            try {
                clazz = Class.forName(AppConstants.PREVIOUS_SCREEN.get(AppConstants.PREVIOUS_SCREEN.size() - 1));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Intent previousScreenIntent = new Intent(getApplicationContext(), clazz);
            mActivity.startActivity(previousScreenIntent);
            mActivity.overridePendingTransition(R.anim.slide_out_right,
                    R.anim.slide_in_left);
            hideSoftKeyboard(mActivity);
            mActivity.finish();
        } else {
            nextScreen(HomeActivityFeedScreen.class,true);
        }
    }


    /*Clear the all background activity*/
    private Intent clearBackGroundActivity(Class<?> clazz, Intent screenIntent) {
        String classStr = clazz.getSimpleName();
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        return screenIntent;

    }

    /*Finish current activity*/
    public void finishScreen() {
        mActivity.finish();
        mActivity.overridePendingTransition(R.anim.slide_out_right,
                R.anim.slide_in_left);

    }


    /*Check screen orientation*/
    protected boolean IsScreenModePortrait() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /*API call back success*/
    public void onRequestSuccess(Object resObj) {

    }


    /*API call back failure*/
    public void onRequestFailure(Throwable t) {

        if (t != null && t.getMessage() != null && !t.getMessage().isEmpty()) {
            if (t.getCause() instanceof java.net.SocketTimeoutException) {

                DialogManager.getInstance().showAlertPopup(mActivity, getString(R.string.connect_time_out), new InterfaceBtnCallBack() {
                    @Override
                    public void onPositiveClick() {

                    }
                });
            } else if (!(t instanceof IOException)) {
                DialogManager.getInstance().showAlertPopup(mActivity, t.getMessage(), new InterfaceBtnCallBack() {
                    @Override
                    public void onPositiveClick() {

                    }
                });
            }
        }

    }


    /*Ask permission for device access*/
    public boolean askAccessPermission(List<String> permissionStrList, int askPermissionCountInt, InterfaceTwoBtnCallBack permissionCallback) {
        mAppPermissionsStrArrList = new ArrayList<>();
        mAppPermissionsStrArrList.addAll(permissionStrList);
        mAskPermissionCountInt = askPermissionCountInt;
        mPermissionCallback = permissionCallback;

        if (!mAppPermissionsStrArrList.isEmpty()) {
            ActivityCompat.requestPermissions(this, mAppPermissionsStrArrList.toArray(new String[mAppPermissionsStrArrList.size()]), 200);
            return false;
        }

        return true;
    }


    public static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    /*Permission call back*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200: {
                Map<String, Integer> perms = new HashMap<>();
                if (grantResults.length > 0) {
                    boolean isGrantAllPermissionBool = true;
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    for (int j = 0; j < mAppPermissionsStrArrList.size(); j++) {
                        if (perms.get(mAppPermissionsStrArrList.get(j)) == PackageManager.PERMISSION_GRANTED) {
                            if (j == mAppPermissionsStrArrList.size() - 1) {
                                if (isGrantAllPermissionBool)
                                    mPermissionCallback.onPositiveClick();
                                else if (mAskPermissionCountInt == 2)
                                    mPermissionCallback.onNegativeClick();
                                else
                                    askAccessPermission(mAppPermissionsStrArrList, mAskPermissionCountInt + 1, mPermissionCallback);
                            }
                        } else {
                            isGrantAllPermissionBool = false;
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, mAppPermissionsStrArrList.get(j))) {
                                if (perms.get(mAppPermissionsStrArrList.get(j)) == PackageManager.PERMISSION_DENIED) {
                                    DialogManager.getInstance().showOptionPopup(mActivity, mActivity.getString(R.string.go_settings_per),
                                            getString(R.string.yes), getString(R.string.no), new InterfaceTwoBtnCallBack() {
                                                @Override
                                                public void onPositiveClick() {
                                                    Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                                                    myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivityForResult(myAppSettings, 168);
                                                }

                                                @Override
                                                public void onNegativeClick() {
                                                    mPermissionCallback.onNegativeClick();
                                                }
                                            });
                                    break;

                                } else if (j == mAppPermissionsStrArrList.size() - 1) {
                                    if (mAskPermissionCountInt == 2)
                                        mPermissionCallback.onNegativeClick();
                                    else
                                        askAccessPermission(mAppPermissionsStrArrList, mAskPermissionCountInt + 1, mPermissionCallback);
                                }

                            } else {
                                if (j == mAppPermissionsStrArrList.size() - 1) {
                                    if (mAskPermissionCountInt == 2)
                                        mPermissionCallback.onNegativeClick();
                                    else
                                        askAccessPermission(mAppPermissionsStrArrList, mAskPermissionCountInt + 1, mPermissionCallback);
                                }
                            }
                        }
                    }

                } else if (mPermissionCallback != null) {
                    mPermissionCallback.onNegativeClick();
                }
            }
        }
    }


    /*Interface default ok click*/
    @Override
    public void onPositiveClick() {

    }

    @Override
    public void onNegativeClick() {

    }

    public void sysOut(String printStr) {
        System.out.println(printStr);
    }

    /*Track the screen name - Google Analytics */
    public void trackScreenName(String screenNameStr) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, 1);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screenNameStr);
//
//        //Logs an app event.
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//        mFirebaseAnalytics.setCurrentScreen(this, screenNameStr, null);
        mFirebaseAnalytics.setCurrentScreen(this, screenNameStr, null);

    }


}
