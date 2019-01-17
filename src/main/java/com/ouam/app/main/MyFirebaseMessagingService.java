package com.ouam.app.main;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ouam.app.R;
import com.ouam.app.ui.ChatConversationActivityScreen;
import com.ouam.app.ui.HomeActivityFeedScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.PreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FROM", "From: " + remoteMessage.getFrom());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.

        if (PreferenceUtil.getBoolPreferenceValue(this, AppConstants.LOGIN_STATUS)
                && !OUAMApplication.isActivityVisible()) {
            if (remoteMessage.getData().size() > 0) {
                Log.d("DATA", "Message data payload: " + remoteMessage.getData());
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d("NotificationData", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            String channelUrl = null, message = "";
            try {
                JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                JSONObject channel = (JSONObject) sendBird.get("channel");
                channelUrl = (String) channel.get("channel_url");
                message = sendBird.getString("message");
                JSONObject senderObj = sendBird.getJSONObject("sender");
                AppConstants.SEND_BIRD_USER_NAME = senderObj.getString("name");
                AppConstants.SEND_BIRD_USER_IMAGE = senderObj.getString("profile_url");
                String count = sendBird.getString("unread_message_count");


                PreferenceUtil.storeStringValue(this,
                        AppConstants.UNREAD_MESSAGE_COUNT, count);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.

            sendNotification(this, message, channelUrl);
//        } else {
//            AppConstants.UPDATE_BADGE_INTERFACE.updateBadge();
//        }
        }

    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public void sendNotification(Context context, String messageBody, String channelUrl) {
        Intent intent = new Intent(context, HomeActivityFeedScreen.class);
        intent.putExtra("groupChannelUrl", channelUrl);
        AppConstants.FROM_NOTIFICATION = AppConstants.KEY_TRUE;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        /*above oreo*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(getColor(R.color.fb_blue));
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notificationBuilder.setContentText(messageBody);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}