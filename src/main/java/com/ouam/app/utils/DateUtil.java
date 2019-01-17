package com.ouam.app.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateUtil {


    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                AppConstants.DEFAULT_DATE_FORMAT, Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static String getDateTimeDiff(String serverTimeStr, boolean isTimeStamp) {

        Date mServerDate = null, mCurrentDate = null;

        String diffTime = "";
        try {

            String mCurrentDate1 = AppConstants.SERVER_DATE_FORMAT.format(new Date());

            mCurrentDate = AppConstants.SERVER_DATE_FORMAT.parse(mCurrentDate1);

            if (isTimeStamp) {
                SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DEFAULT_DATE_FORMAT, Locale.US);
                Date netDate = (new Date(Long.valueOf(serverTimeStr) * 1000L));
                String serverStr = sdf.format(netDate);

                mServerDate = AppConstants.SERVER_DATE_FORMAT.parse(serverStr);
            } else {
                mServerDate = AppConstants.SERVER_DATE_FORMAT.parse(serverTimeStr);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mServerDate != null && mCurrentDate != null) {
            //milliseconds
            long diff = mCurrentDate.getTime() - mServerDate.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            int diffInDays = (int) (diff / (60 * 60 * 1000 * 24));
            int diffInMonths = diffInDays / 30;
            int diffInYear = diffInMonths / 12;

            if (diffInYear > 0) {

                if (diffInYear == 1) {
                    diffTime = diffInYear + " Year ago";
                } else {
                    diffTime = diffInYear + " Years ago";
                }

            } else if (diffInMonths > 0) {
                if (diffInMonths == 1) {
                    diffTime = diffInMonths + " Month ago";
                } else {
                    diffTime = diffInMonths + " Months ago";
                }
            } else if (diffInDays > 0) {
                if (diffInDays == 1) {
                    diffTime = diffInDays + " Day ago";
                } else if (diffInDays >= 7) {
                    if (diffInDays / 7 == 1) {
                        diffTime = diffInDays / 7 + " Week ago";
                    } else {
                        diffTime = diffInDays / 7 + " Weeks ago";
                    }
                } else {
                    diffTime = diffInDays + " Days ago";
                }
            } else if (diffHours > 0) {
                if (diffHours == 1) {
                    diffTime = diffHours + " Hour ago";
                } else {
                    diffTime = diffHours + " Hours ago";
                }
            } else if (diffMinutes > 0) {
                if (diffMinutes == 1) {
                    diffTime = diffMinutes + " Min ago";
                } else {
                    diffTime = diffMinutes + " Mins ago";
                }
            } else {
                diffTime = "Just Now";
            }
            return diffTime;


        }
        return diffTime;
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }


    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }

}
