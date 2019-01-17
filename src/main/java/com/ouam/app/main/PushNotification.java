package com.ouam.app.main;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;


public class PushNotification extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        System.out.println("notification---" + notification.toString());
        JSONObject type = notification.payload.additionalData;
        System.out.println("MyFirebaseMessagingService---" + type.toString());
        return true;
    }
}



