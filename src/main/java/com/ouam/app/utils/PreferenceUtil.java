package com.ouam.app.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ouam.app.entity.UserDetailsEntity;

public class PreferenceUtil {
    private static int STRING_PREFERENCE = 1;
    private static int INT_PREFERENCE = 2;
    private static int BOOLEAN_PREFERENCE = 3;

    /*store preference*/
    private static void storeValueToPreference(Context context, int preference, String key, Object value) {
        if (context != null) {
            SharedPreferences sharedPreference = context.getSharedPreferences(
                    AppConstants.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreference.edit();

            if (preference == STRING_PREFERENCE) {
                edit.putString(key, (String) value);
            }
            if (preference == INT_PREFERENCE) {
                edit.putInt(key, (int) value);
            }
            if (preference == BOOLEAN_PREFERENCE) {
                edit.putBoolean(key, (boolean) value);
            }

            if (android.os.Build.VERSION.SDK_INT >= 23) {
                edit.apply();
            } else {
                edit.commit();
            }

        }
    }

    /*get preference value*/
    private static Object getValueFromPreference(Context context, int preference, String key) {
        if (context != null) {
            SharedPreferences sharedPreference = context.getSharedPreferences(
                    AppConstants.SHARE_PREFERENCE, Context.MODE_PRIVATE);

            if (preference == STRING_PREFERENCE) {
                return sharedPreference.getString(key, "");
            }
            if (preference == INT_PREFERENCE) {
                return sharedPreference.getInt(key, 0);
            }
            if (preference == BOOLEAN_PREFERENCE) {
                return sharedPreference.getBoolean(key, false);
            }
        }

        return null;
    }

    /*Store bool value to preference*/
    public static void storeBoolPreferenceValue(Context context, String appConstantsStr, boolean keyBool) {
        if (context != null) {
            storeValueToPreference(context, BOOLEAN_PREFERENCE, appConstantsStr, keyBool);
        }
    }

    /*Get bool value from preference*/
    public static boolean getBoolPreferenceValue(Context context, String appConstantsStr) {
        return (boolean) getValueFromPreference(context,
                BOOLEAN_PREFERENCE, appConstantsStr);
    }


    /*Store string value to preference*/
    public static void storeStringValue(Context context, String appConstantsVal, String key) {
        if (context != null) {
            storeValueToPreference(context, STRING_PREFERENCE, appConstantsVal, key);
        }
    }


    /*Get string value from preference*/
    public static String getStringValue(Context context, String key) {
        return (String) getValueFromPreference(context, STRING_PREFERENCE, key);
    }

    /*Get user Id value from preference*/
    public static String getUserId(Context context) {
        return (String) getValueFromPreference(context, STRING_PREFERENCE, AppConstants.USER_ID);
    }


    /*Store user details to preference*/
    public static void storeUserDetails(Context context, UserDetailsEntity userDetailsEntity) {
        String userIdStr = "", userDetailStr = "";

        Gson gson = new Gson();
        userIdStr = userDetailsEntity.getUserId();
        userDetailStr = gson.toJson(userDetailsEntity);

        PreferenceUtil.storeValueToPreference(context, PreferenceUtil.STRING_PREFERENCE,
                AppConstants.USER_DETAILS, userDetailStr);
        PreferenceUtil.storeValueToPreference(context, PreferenceUtil.STRING_PREFERENCE,
                AppConstants.USER_ID, userIdStr);
    }

    /*Get user details from preference*/
    public static UserDetailsEntity getUserDetailsRes(Context context) {
        UserDetailsEntity userDetailsEntityRes = new UserDetailsEntity();

        String userDetailsStr = getStringValue(context, AppConstants.USER_DETAILS);
        if (userDetailsStr != null && !userDetailsStr.isEmpty()) {
            userDetailsEntityRes = new Gson().fromJson(userDetailsStr, UserDetailsEntity.class);
        }
        return userDetailsEntityRes;
    }

}
