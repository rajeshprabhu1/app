package com.ouam.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ouam.app.R;
import com.ouam.app.chat.ConnectionManager;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.PreferenceUtil;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivityScreen extends BaseActivity {

    @BindView(R.id.setting_par_lay)
    RelativeLayout mSettingParLay;

    @BindView(R.id.setting_header_bg_lay)
    RelativeLayout mSettingHeaderBgLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_settings_screen1);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        setHeaderView();

        setupUI(mSettingParLay);

        trackScreenName(getString(R.string.setting_screen));

    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSettingHeaderBgLay.post(new Runnable() {
                public void run() {
                    mSettingHeaderBgLay.setPadding(0, getStatusBarHeight(SettingsActivityScreen.this), 0, 0);

                }
            });
        }
        mHeaderTxt.setText(getResources().getText(R.string.setting));

    }

    @OnClick({R.id.header_left_img_lay, R.id.edit_profile_lay, R.id.notifications_lay, R.id.logout_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                onBackPressed();
                break;
            case R.id.edit_profile_lay:
                nextScreen(GetStartedScreen.class, false);
                break;
            case R.id.notifications_lay:
                nextScreen(NotificationActivityScreen.class, false);
                break;
            case R.id.logout_txt:
                DialogManager.getInstance().showOptionPopup(this, getString(R.string.sure_want_to_logout),
                        getString(R.string.yes), getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                PreferenceUtil.storeBoolPreferenceValue(SettingsActivityScreen.this,
                                        AppConstants.LOGIN_STATUS, false);
                                PreferenceUtil.storeUserDetails(SettingsActivityScreen.this, null);
                                disconnect();
                                previousScreen(TutorialScreen.class, false);
                            }
                        });
                break;
        }
    }


    /**
     * Unregisters all push tokens for the current user so that they do not receive any notifications,
     * then disconnects from SendBird.
     */
    private void disconnect() {

        SendBird.unregisterPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {

            }
        });


        ConnectionManager.logout(new SendBird.DisconnectHandler() {
            @Override
            public void onDisconnected() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        backScreen(true);
    }

}
