package com.ouam.app.chat;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ouam.app.R;
import com.ouam.app.ui.GetStartedScreen;
import com.ouam.app.ui.HomeActivityFeedScreen;
import com.ouam.app.utils.DialogManager;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.io.File;

public class SendBirdUtils {


    public static void updateCurrentUserPushToken(Context context) {
        // TODO: Implement this method to send token to your app server.
        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            DialogManager.getInstance().showToast(context, e.getCode() + ":" + e.getMessage());
                            return;
                        }

                        if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                            DialogManager.getInstance().showToast(context, context.getString(R.string.connection_required));
                        }
                    }
                });
    }


    /**
     * Updates the user's nickname.
     *
     * @param userNickname The new nickname of the user.
     */
    public static void updateCurrentUserInfo(Context context, final String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
                    DialogManager.getInstance().showToast(context,
                            e.getCode() + ":" + e.getMessage());


                    return;
                }

            }
        });
    }


    /**
     * Updates the user's nickname.
     *
     * @param nickname The new nickname of the user.
     */
    public static void updateCurrentUserProfileImage(final File profileImage, String nickname) {
//        final String nickname = PreferenceUtils.getNickname();
        SendBird.updateCurrentUserInfoWithProfileImage(nickname, profileImage, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!

                    // Show update failed snackbar
//                    showSnackbar("Update user info failed");
                    return;
                }

            }
        });
    }


}
