package com.ouam.app.utils;


import com.ouam.app.commonInterfaces.DetailsUpdateFragmentInterface;
import com.ouam.app.commonInterfaces.FeedUpdateInterFace;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.RefreshPinListInterface;
import com.ouam.app.commonInterfaces.SearchPlaceInterface;
import com.ouam.app.commonInterfaces.UpdatePagerFragmentInterface;
import com.ouam.app.entity.PostEntity;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.ui.NewMessageScreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AppConstants {

    static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    public static String TAG = "TAG";

    /*PREVIOUS_SCREEN*/
    public static ArrayList<String> PREVIOUS_SCREEN = new ArrayList<>();

    /*BASE URL*/
//    public static final String OLD_BASE_URL = "http://ouam-development-env.us-east-2.elasticbeanstalk.com/v1/";
    public static final String BASE_URL = "http://ouam-development-env-2.us-east-2.elasticbeanstalk.com/v1/";

    /*ACTIVITY FEED URL*/
    public static final String ACTIVITY_FEED_URL = "activity/feed?";

    /*USER SEARCH URL*/
    public static final String USER_SEARCH_URL = "users/search?username=";

    public static final String HOME_ACTIVITY_FEED_SCREEN = "HomeActivityFeedScreen";

    /*COMMON LAT FOR URL CALL*/
    public static final String LAT_URL = "lat=";

    /*COMMON LON FOR URL CALL*/
    public static final String LON_URL = "&lon=";

    /*COMMON RADIUS FOR URL CALL*/
    public static final String RADIUS_URL = "&radius=";

    /*COMMON SEARCH QUERY*/
    public static final String QUERY = "&query=";

    /*PLACE SEARCH URL*/
    public static final String PLACE_SEARCH_URL = "places/search?";

    /* GET USER PIN LIST  */
    public static String PLACES_URL = "/places";

    /*USER NAME EXIST URL*/
    public static String USER_NAME_EXIST_URL = "/exists";

    /*GOOGLE PLACE SUGGESTION API URL*/
    public static final String GET_ADDRESS_URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$s&sensor=true";

    public static final String GET_DETAILS_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?key=%1$s&address=%2$s&sensor=true";

    /*CITIES SEARCH URL*/
    public static final String CITIES_SEARCH_URL = "cities/search?cityName=";

    /*User URL*/
    public static final String USER_URL = "users/user/";

    /*User Follower URL*/
    public static final String USER_FOLLOWER_URL = "/followers";

    /*User Following URL*/
    public static final String USER_FOLLOWING_URL = "/following";

    /*User Recent URL*/
    public static final String USER_RECENT_URL = "/activity";

    /*Place URL*/
    public static final String PLACE_URL = "places/place/";

    /*User Cities URL*/
    public static final String CITIES_URL = "cities/city/";

    /*User Trending URL*/
    public static final String TRENDING_URL = "/trendingPlaces";

    /*Get Pins URL*/
    public static final String PINS_URL = "/pins";

    /*Add Pin URL*/
    public static final String PIN_URL = "/pin";

    /*Add Post URL*/
    public static final String POST_URL = "/post";

    /*Post Comments*/
    public static final String COMMENT_URL = "/comment";

    /*Get Comments*/
    public static final String GET_COMMENTS_URL = "/comments";

    /*Get Likes*/
    public static final String GET_LIKES_URL = "/likes";

    /*Post Comment Url*/
    public static final String POST_COMMENT_URL = "posts/post/";

    /*Chat List Url*/
    public static final String CHAT_LIST_URL = "users/me/chats";

    /*Like Url*/
    public static final String LIKE_URL = "/like";

    /*Map Share Img*/
    public static final String MAP_SHARE_URL = "/mapImage";

    /*Notification Url*/
    public static final String NOTIFICATION_URL = "users/me/notifications?searchDate=";

    /*Hidden Gems Url*/
    public static final String HIDDEN_GEM_URL = "hiddengems/nearby?";

    /*Login*/
    public final static String FACEBOOK = "fb";
    public final static String GOOGLE = "google";
    public final static String EMAIL = "email";

    /*User Details*/
    public static String USER_NAME = "";
    public static String CITY_NAME = "";
    public static String PLACE_NAME = "";
    public static String USER_FIRST_NAME = "";
    public static String USER_IMAGE = "";
    public static String USER_EMAIL = "";
    public static String PLATFORM_ID = "";
    public static String PLATFORM = "";
    public static String pinType = "";

    public static String USER_DETAILS = "USER_DETAILS";
    public static String USER_ID = "USER_ID";
    public static String LOGIN_STATUS = "LOGIN_STATUS";
    public static String POPUP_TUTORIAL_SEEN_STATUS = "POPUP_TUTORIAL_SEEN_STATUS";

    /*Temp*/
//    public static final String SEND_BIRD_LIVE_APP_ID = "8FFAB4F4-51C6-40DF-B538-8DFB742CD2FC";

    public static final String SEND_BIRD_LIVE_APP_ID = "0B1945F5-ED4C-428C-9CF5-842182E19780";


    /*Date Format*/
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);

    /*CURRENT LAT LONG*/
    public static double CURRENT_LAT = 0.0;
    public static double CURRENT_LONG = 0.0;
    public static String MAP_MOVE_LOCATION_NAME = "";
    public static double MAP_MOVE_LAT = 0.0;
    public static double MAP_MOVE_LONG = 0.0;

    public static String RADIUS = "250";

    /* Add pin constants*/
    public static String BEEN_THERE_PIN = "beenthere";
    public static String TO_BE_EXPLORED_PIN = "tobeexplored";
    public static String HIDDEN_GEM_PIN = "hiddengem";

    /* User Pinned List*/
    public static ArrayList<WhoDetailEntity> BEEN_THERE_PIN_LIST = new ArrayList<>();

    public static ArrayList<WhoDetailEntity> TO_BE_EXPLORED_PIN_LIST = new ArrayList<>();

    public static ArrayList<WhoDetailEntity> HIDDEN_GEM_PIN_LIST = new ArrayList<>();

    public static PostEntity POST_ENTITY = new PostEntity();

    public static Long POST_CREATED_TIME;

    public static DetailsUpdateFragmentInterface WITH_DETAILS_INTERFACE = null;

    /*interface declaration*/
    public static SearchPlaceInterface SEARCH_PLACE_CALLBACK = null;
    public static SearchPlaceInterface SEARCH_PEOPLE_CALLBACK = null;
    public static SearchPlaceInterface SEARCH_CITY_CALLBACK = null;
    public static SearchPlaceInterface SEARCH_FOLLOW_CALLBACK = null;




    public static UpdatePagerFragmentInterface BEEN_THERE_UPDATE_FRAGMENT_INTERFACE = null;
    public static UpdatePagerFragmentInterface TO_BE_EXPLORED_UPDATE_FRAGMENT_INTERFACE = null;
    public static UpdatePagerFragmentInterface HIDDEN_GEM_UPDATE_FRAGMENT_INTERFACE = null;
    public static InterfaceBtnCallBack UPDATE_ACTIVITY_INTERFACE = null;
    public static FeedUpdateInterFace UPDATE_FEED_INTERFACE = null;


    public static RefreshPinListInterface REFRESH_USER_PIN_LIST_INTERFACE = null;
    public static NewMessageScreen REFRESH_FOLLOW_LIST = null;

    /*static access token*/
    public static String ACCESS_TOKEN = "";
    public static String PROFILE_USER_ID = "";

    /*Access Token*/
    public static String SOCIAL_ACCESS_TOKEN = "";

    /*Store Picked Pics from Home Screen*/
    public static String HOME_SCREEN_PHOTO = "";

    /*Comment Like Screen*/
    public static String COMMENT_USER_IMG = "";

    /*Send bird channel url*/
    public static String SEND_BIRD_CHANNER_URL = "";
    public static String SEND_BIRD_USER_NAME = "";
    public static String SEND_BIRD_USER_IMAGE = "";
    public static String UNREAD_MESSAGE_COUNT = "0";


    public static boolean PIN_DIALOG_ACTIVITY_RESULT_FLAG = false;
    public static String FROM_NOTIFICATION = "";
    public static String KEY_TRUE = "true";
    public static String KEY_FALSE = "false";

    public static boolean IS_CALLED_ALREADY = false;
    public static double PHOTO_LATITUDE = 0.0;
    public static double PHOTO_LONGITUDE = 0.0;

    public static String BEEN_THERE_MAP_STR = "";
    public static String TO_BE_EXPLORE_MAP_STR = "";
    public static String HIDDEN_GEM_MAP_STR = "";

}
