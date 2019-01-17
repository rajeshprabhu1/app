package com.ouam.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.database.DatabaseUtil;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends BaseActivity {

    @BindView(R.id.splash_par_lay)
    ViewGroup mSplashViewGroup;


    private Handler mHandler;
    private Runnable mRunnable;

    private DatabaseUtil mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.ui_splash_screen);

        initView();
        facebookHashKey(this);

    }

    /*View initialization*/
    private void initView() {
        /*ButterKnife for variable initialization*/
        ButterKnife.bind(this);

        /*Keypad to be hidden when a Click/touch made outside the edit text*/
        setupUI(mSplashViewGroup);

        mDatabase = new DatabaseUtil(this);

        /*Ask permission for */
        if (askPermissions()) {
            checkNextScreenFlow();
        }

        trackScreenName(getString(R.string.splash_screen));

    }


    private void checkNextScreenFlow() {

        mRunnable = () -> {
            removeHandler();
            Class<?> nextScreenClass =
                    PreferenceUtil.getBoolPreferenceValue(SplashScreen.this, AppConstants.LOGIN_STATUS) &&
                            NetworkUtil.isNetworkAvailable(SplashScreen.this) && mDatabase.getAllPins().size() > 0 ? OfflinePiningModeActivityScreen.class
                            : PreferenceUtil.getBoolPreferenceValue(SplashScreen.this, AppConstants.LOGIN_STATUS) &&
                            !NetworkUtil.isNetworkAvailable(SplashScreen.this) ? OfflineActivityScreen.class : PreferenceUtil.getBoolPreferenceValue(SplashScreen.this, AppConstants.LOGIN_STATUS) ?
                            HomeActivityFeedScreen.class : TutorialScreen.class;

            nextScreen(nextScreenClass, false);
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 3000);

    }

    private void removeHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler();
    }


    /*To get permission for access image camera and storage*/
    private boolean askPermissions() {
        boolean addPermission = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);


            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }

        }
        if (!listPermissionsNeeded.isEmpty()) {
            addPermission = askAccessPermission(listPermissionsNeeded, 1, new InterfaceTwoBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    checkNextScreenFlow();
                }

                public void onNegativeClick() {
                    checkNextScreenFlow();
                }
            });
        }

        return addPermission;
    }


    /*getting facebook hash key*/
    @SuppressLint("PackageManagerGetSignatures")
    private void facebookHashKey(BaseActivity context) {
        PackageInfo packageInfo;
        String key;
        try {
            String packageName = context.getApplicationContext().getPackageName();

            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                System.out.println("Hash Key-----" + key);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
