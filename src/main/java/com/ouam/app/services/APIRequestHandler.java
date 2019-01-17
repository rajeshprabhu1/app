package com.ouam.app.services;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.CommentInputEntity;
import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.ImageUploadResultEntity;
import com.ouam.app.entity.LoginInputEntity;
import com.ouam.app.entity.SignUpEntity;
import com.ouam.app.entity.TinyBodyEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.ActivityFeedResponse;
import com.ouam.app.model.AddPinResponse;
import com.ouam.app.model.AddressResponse;
import com.ouam.app.model.CitiesDetailsResponse;
import com.ouam.app.model.CitiesPinsResponse;
import com.ouam.app.model.CitiesTrendingPlaceResponse;
import com.ouam.app.model.CityRecentResponse;
import com.ouam.app.model.CitySearchResponse;
import com.ouam.app.model.CommentDeleteResponse;
import com.ouam.app.model.CommentLikeResponse;
import com.ouam.app.model.CommentUnlikeResponse;
import com.ouam.app.model.FollowResponse;
import com.ouam.app.model.GetCommentsResponse;
import com.ouam.app.model.HiddenGemResponse;
import com.ouam.app.model.LoginResponse;
import com.ouam.app.model.NotificationResponse;
import com.ouam.app.model.PeopleSearchResponse;
import com.ouam.app.model.PinPostResponse;
import com.ouam.app.model.PlaceDetailsResponse;
import com.ouam.app.model.PlacePinsResponse;
import com.ouam.app.model.PlacesSearchResponse;
import com.ouam.app.model.ProfileUpdateResponse;
import com.ouam.app.model.RecommendationFollowResponse;
import com.ouam.app.model.RemovePinResponse;
import com.ouam.app.model.SendCommentResponse;
import com.ouam.app.model.ShareMapResponse;
import com.ouam.app.model.TinyUrlResponse;
import com.ouam.app.model.UnFollowResponse;
import com.ouam.app.model.UserChatResponse;
import com.ouam.app.model.UserFollowerResponse;
import com.ouam.app.model.UserFollowingResponse;
import com.ouam.app.model.UserNameExistResponse;
import com.ouam.app.model.UserProfileResponse;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRequestHandler {

    private static APIRequestHandler sInstance = new APIRequestHandler();
    private static Retrofit mRetrofitDialog;

    /*Init retrofit for API call*/
    private Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    /*Init retrofit for API call*/
    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private Retrofit retrofit = new Retrofit.Builder().baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private APICommonInterface mServiceInterface = retrofit.create(APICommonInterface.class);

    public static APIRequestHandler getInstance() {
        return sInstance;
    }


    public static Retrofit getClient() {
        if (mRetrofitDialog == null) {
            mRetrofitDialog = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofitDialog;
    }


    /*SignUpAPICall*/
    public void signUpAPICall(SignUpEntity signUpEntity, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.signUpAPI(signUpEntity).enqueue(new Callback<CommonResultEntity>() {
            @Override
            public void onResponse(@NonNull Call<CommonResultEntity> call, @NonNull Response<CommonResultEntity> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResultEntity> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*Profile Image Upload Api Call*/
    public void profileImageUploadApiCall(String imageString, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        File file = new File(imageString);
        MultipartBody.Part imageFile = null;

        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), file);

        imageFile = MultipartBody.Part.createFormData("image", file.getName(), imageBody);

        mServiceInterface.profileUploadImage(AppConstants.ACCESS_TOKEN, imageFile).enqueue(new Callback<ImageUploadResultEntity>() {
            @Override
            public void onResponse(Call<ImageUploadResultEntity> call, Response<ImageUploadResultEntity> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResultEntity> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*ProfileUpdateAPICall*/
    public void profileUpdateAPICall(SignUpEntity signUpEntity, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.updateProfileAPI(AppConstants.ACCESS_TOKEN, signUpEntity).enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileUpdateResponse> call, @NonNull Response<ProfileUpdateResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileUpdateResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }


    /*Place search api call*/
    public void placesSearchApi(String url, boolean isShow, final BaseFragment baseActivity) {

        if (isShow)
            DialogManager.getInstance().showProgress(baseActivity.getContext());
        mServiceInterface.placesSearchApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<PlacesSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesSearchResponse> call, @NonNull Response<PlacesSearchResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacesSearchResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Place search api call with Interface*/
    public void placesSearchApi(String url, final Context context, final InterfaceApiResponseCallBack mCallback) {


        mServiceInterface.placesSearchApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<PlacesSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesSearchResponse> call, @NonNull Response<PlacesSearchResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacesSearchResponse> call, @NonNull Throwable t) {

                mCallback.onRequestFailure(t);
            }
        });
    }


    /*Activity Feed api call*/
    public void activityFeedAPICall(double lat, double lon, double radius, final BaseActivity baseActivity) {
//        String queryUrl = AppConstants.BASE_URL + AppConstants.ACTIVITY_FEED_URL + AppConstants.LAT_URL+ lat + AppConstants.LON_URL + lon + AppConstants.RADIUS_URL +radius;
        String queryUrl = AppConstants.BASE_URL + AppConstants.ACTIVITY_FEED_URL + AppConstants.LAT_URL + lat + AppConstants.LON_URL + lon + AppConstants.RADIUS_URL + radius;
        mServiceInterface.activityFeedAPI(AppConstants.ACCESS_TOKEN, queryUrl).enqueue(new Callback<ActivityFeedResponse>() {
            @Override
            public void onResponse(@NonNull Call<ActivityFeedResponse> call, @NonNull Response<ActivityFeedResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActivityFeedResponse> call, @NonNull Throwable t) {
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*Follow Api*/
    public void followApi(String userName, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.followApi(AppConstants.ACCESS_TOKEN, userName).enqueue(new Callback<FollowResponse>() {
            @Override
            public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    System.out.println("onFailure---followApi");
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                System.out.println("onFailure---followApi");
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Follow Api with Callback*/
    public void followApi(String userIdStr, final Context context, final InterfaceApiResponseCallBack mCallback) {
        DialogManager.getInstance().showProgress(context);
        mServiceInterface.followApi(AppConstants.ACCESS_TOKEN, userIdStr).enqueue(new Callback<FollowResponse>() {
            @Override
            public void onResponse(@NonNull Call<FollowResponse> call, @NonNull Response<FollowResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<FollowResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                mCallback.onRequestFailure(t);
            }
        });
    }


    /*People Search api call*/
    public void peopleSearchAPICall(final String queryUrl, boolean isShow, final BaseFragment baseFragment) {
        if (isShow)
            DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.peopleSearchAPI(AppConstants.ACCESS_TOKEN, queryUrl).enqueue(new Callback<PeopleSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<PeopleSearchResponse> call, @NonNull Response<PeopleSearchResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PeopleSearchResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Cities Search api Call*/
    public void citiesSearchAPICall(final String queryUrl, boolean isShow, final BaseFragment baseFragment) {
        if (isShow)
            DialogManager.getInstance().showProgress(baseFragment.getContext());

        mServiceInterface.citiesSearchAPI(AppConstants.ACCESS_TOKEN, queryUrl).enqueue(new Callback<CitySearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<CitySearchResponse> call, @NonNull Response<CitySearchResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CitySearchResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Follow Api Call*/
    public void unFollowApi(final String userName, final Context context, final InterfaceApiResponseCallBack mCallback) {
        DialogManager.getInstance().showProgress(context);
        mServiceInterface.unFollowApi(AppConstants.ACCESS_TOKEN, userName).enqueue(new Callback<UnFollowResponse>() {
            @Override
            public void onResponse(@NonNull Call<UnFollowResponse> call, @NonNull Response<UnFollowResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    System.out.println("onFailure---unFollowApi");
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UnFollowResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                System.out.println("onFailure---unFollowApi");
                mCallback.onRequestFailure(t);
            }
        });
    }

    /*Recommendation API Call*/
    public void recommendationFollowAPI(final String userIdStr, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.recommendationFollowAPI(AppConstants.ACCESS_TOKEN, userIdStr).enqueue(new Callback<RecommendationFollowResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecommendationFollowResponse> call, @NonNull Response<RecommendationFollowResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecommendationFollowResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Recommendation API Call*/
    public void recommendationPlaceFollowAPI(final String placeIdStr, final String placeTypeIdStr, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.recommendationPlaceFollowAPI(AppConstants.ACCESS_TOKEN, placeIdStr, placeTypeIdStr).enqueue(new Callback<RecommendationFollowResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecommendationFollowResponse> call, @NonNull Response<RecommendationFollowResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecommendationFollowResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*User Follower Api Call*/
    public void userFollowerApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.userFollowerApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserFollowerResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFollowerResponse> call, @NonNull Response<UserFollowerResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFollowerResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*User Follower Api Call*/
    public void userFollowerApi(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.userFollowerApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserFollowerResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFollowerResponse> call, @NonNull Response<UserFollowerResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFollowerResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*User Following Api Call*/
    public void userFollowingApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.userFollowingApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserFollowingResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFollowingResponse> call, @NonNull Response<UserFollowingResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFollowingResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*User Following Api Call*/
    public void userFollowingApi(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.userFollowingApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserFollowingResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserFollowingResponse> call, @NonNull Response<UserFollowingResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFollowingResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*User Recent Api Call*/
    public void userRecentApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.userRecentApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<ActivityFeedResponse>() {
            @Override
            public void onResponse(@NonNull Call<ActivityFeedResponse> call, @NonNull Response<ActivityFeedResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }

            }

            @Override
            public void onFailure(@NonNull Call<ActivityFeedResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }


    /*get user current loc address*/
    public void callGetUserAddressAPI(String urlStr, final BaseActivity baseActivity, boolean isShown) {
        if (isShown) {
            DialogManager.getInstance().showProgress(baseActivity);
        }
        mServiceInterface.getUserAddressAPICall(urlStr).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddressResponse> call, @NonNull Response<AddressResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    System.out.println("onFailure---callGetUserAddressAPI");
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddressResponse> call, @NonNull Throwable t) {
                System.out.println("onFailure---callGetUserAddressAPI");
                System.out.println("onFailure---callGetUserAddressAPI");
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*User Profile Api Call*/
    public void userProfileApi(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.userProfileApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Add Pin Api*/
    public void addPinApi(final AddPinInputEntity addPinInputEntity, String url, final Context context,
                          final InterfaceApiResponseCallBack mCallback) {


        mServiceInterface.addPinApi(AppConstants.ACCESS_TOKEN, addPinInputEntity, url).enqueue(new Callback<AddPinResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddPinResponse> call, @NonNull Response<AddPinResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddPinResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                mCallback.onRequestFailure(t);
            }
        });
    }


    public void addPostApi(String urlStr, String commentStr, String imagePathStrOne, String imagePathStrTwo,
                           Context context, final InterfaceApiResponseCallBack mCallback) {
        DialogManager.getInstance().showProgress(context);
        MultipartBody.Part imageOne = null;
        MultipartBody.Part imageTwo = null;
        MultipartBody.Part imageThree = null;
        MultipartBody.Part imageFour = null;
        if (!imagePathStrOne.isEmpty()) {
            File file1 = new File(ImageUtils.compressImage(context, imagePathStrOne));
            RequestBody imageBody1 = RequestBody.create(MediaType.parse("image/*"), file1);
            imageOne = MultipartBody.Part.createFormData("image", file1.getName(), imageBody1);
        }
        if (!imagePathStrTwo.isEmpty()) {
            File file2 = new File(ImageUtils.compressImage(context, imagePathStrTwo));
            RequestBody imageBody2 = RequestBody.create(MediaType.parse("image/*"), file2);
            imageTwo = MultipartBody.Part.createFormData("image2", file2.getName(), imageBody2);
        }
//        if (!imagePathStrThree.isEmpty()) {
//            File file3 = new File(imagePathStrThree);
//            RequestBody imageBody3 = RequestBody.create(MediaType.parse("image/*"), file3);
//            imageThree = MultipartBody.Part.createFormData("image3", file3.getName(), imageBody3);
//        }
//        if (!imagePathStrFour.isEmpty()) {
//            File file4 = new File(imagePathStrFour);
//            RequestBody imageBody4 = RequestBody.create(MediaType.parse("image/*"), file4);
//            imageFour = MultipartBody.Part.createFormData("image4", file4.getName(), imageBody4);
//        }

        MultipartBody.Part comment = MultipartBody.Part.createFormData("comment",
                commentStr);

//, imageTwo, imageThree, imageFour
        mServiceInterface.addPostApi(AppConstants.ACCESS_TOKEN, urlStr, comment, imageOne, imageTwo).enqueue(new Callback<PinPostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PinPostResponse> call, @NonNull Response<PinPostResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PinPostResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                mCallback.onRequestFailure(t);
            }
        });


    }

    /*Delete pin APi*/
    public void deletePinApi(String urlStr, final Context context,
                             final InterfaceApiResponseCallBack mCallback) {
        // DialogManager.getInstance().showProgress(context);

        mServiceInterface.deletePinApi(AppConstants.ACCESS_TOKEN, urlStr).enqueue(new Callback<RemovePinResponse>() {
            @Override
            public void onResponse(@NonNull Call<RemovePinResponse> call, @NonNull Response<RemovePinResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RemovePinResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                mCallback.onRequestFailure(t);
            }
        });

    }


    /*Delete pin APi*/
    public void deletePinApiWithCallback(String urlStr, final InterfaceApiResponseCallBack mCallback) {

        mServiceInterface.deletePinApi(AppConstants.ACCESS_TOKEN, urlStr).enqueue(new Callback<RemovePinResponse>() {
            @Override
            public void onResponse(@NonNull Call<RemovePinResponse> call, @NonNull Response<RemovePinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RemovePinResponse> call, @NonNull Throwable t) {
                mCallback.onRequestFailure(t);
            }
        });

    }


    /*User Pin List Api*/
    public void getUserPinListApi(String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.getUserPinListApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<PlacePinsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacePinsResponse> call, @NonNull Response<PlacePinsResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    System.out.println("onFailure---getUserPinListApi");
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacePinsResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                System.out.println("onFailure---getUserPinListApi");
                baseActivity.onRequestFailure(t);
            }
        });

    }


    /*Place Activity Api Call*/
    public void placeActivityApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.placeActivityApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<ActivityFeedResponse>() {
            @Override
            public void onResponse(@NonNull Call<ActivityFeedResponse> call, @NonNull Response<ActivityFeedResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActivityFeedResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Place Pins Api Call*/
    public void placePinsApi(final String url, final BaseActivity baseActivity) {
        //  DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.placePinsApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<PlacePinsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacePinsResponse> call, @NonNull Response<PlacePinsResponse> response) {
//                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacePinsResponse> call, @NonNull Throwable t) {
//                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }


    /*User Name exist Api Call*/
    public void userNameExistApi(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.userNameExistApi(url).enqueue(new Callback<UserNameExistResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserNameExistResponse> call, @NonNull Response<UserNameExistResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {

                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserNameExistResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }


    /*Cities Trending Place Api Call*/
    public void citiesTrendingApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.citiesTrendingPlaceApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CitiesTrendingPlaceResponse>() {
            @Override
            public void onResponse(Call<CitiesTrendingPlaceResponse> call, Response<CitiesTrendingPlaceResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CitiesTrendingPlaceResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Cities Empty Pins Api Call*/
    public void citiesPinsApi(final String url, final BaseActivity baseActivity) {
//        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.citiesPinsApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CitiesPinsResponse>() {
            @Override
            public void onResponse(Call<CitiesPinsResponse> call, Response<CitiesPinsResponse> response) {
//                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CitiesPinsResponse> call, Throwable t) {
//                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Cities Details Api Call*/
    public void citiesRecentApi(final String url, final BaseFragment baseFragment) {
        DialogManager.getInstance().showProgress(baseFragment.getContext());
        mServiceInterface.citiesRecentApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CityRecentResponse>() {
            @Override
            public void onResponse(Call<CityRecentResponse> call, Response<CityRecentResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestSuccess(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CityRecentResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseFragment.onRequestSuccess(t);
            }
        });
    }


    /*Cities Details Api Call*/
    public void citiesDetailsApi(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.citiesDetailsApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CitiesDetailsResponse>() {
            @Override
            public void onResponse(Call<CitiesDetailsResponse> call, Response<CitiesDetailsResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestSuccess(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CitiesDetailsResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestSuccess(t);
            }
        });
    }

    /*Login Api Call*/
    public void loginApiCall(final BaseActivity baseActivity, LoginInputEntity loginInputEntity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.loginApi(loginInputEntity).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*Place Details Api Call*/
    public void placeDetailsApiCall(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.placeDetailsApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<PlaceDetailsResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailsResponse> call, Response<PlaceDetailsResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailsResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*User Chat*/
    public void userChatApiCall(final String url, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.userChatApi(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<UserChatResponse>() {
            @Override
            public void onResponse(Call<UserChatResponse> call, Response<UserChatResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<UserChatResponse> call, Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });
    }

    /*Post Comments*/
    public void postCommentsApiCall(final String url, final CommentInputEntity commentInputEntity, final BaseFragment baseFragment) {
        mServiceInterface.postCommentApiCalls(AppConstants.ACCESS_TOKEN, url, commentInputEntity).enqueue(new Callback<SendCommentResponse>() {
            @Override
            public void onResponse(Call<SendCommentResponse> call, Response<SendCommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<SendCommentResponse> call, Throwable t) {
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Delete Comments*/
    public void deleteCommentsApiCall(final String url, final Context context, final InterfaceApiResponseCallBack mCallBack) {

        mServiceInterface.deleteCommentApiCalls(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CommentDeleteResponse>() {
            @Override
            public void onResponse(Call<CommentDeleteResponse> call, Response<CommentDeleteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mCallBack.onRequestSuccess(response.body());
                } else {
                    mCallBack.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CommentDeleteResponse> call, Throwable t) {
                mCallBack.onRequestFailure(t);
            }
        });

    }

    /*Comment Like Api call*/
    public void commentlikeApiCall(final String url, final Context context, final InterfaceApiResponseCallBack mCallback) {

        mServiceInterface.commentLikeApiCalls(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CommentLikeResponse>() {
            @Override
            public void onResponse(Call<CommentLikeResponse> call, Response<CommentLikeResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    mCallback.onRequestSuccess(response.body());
                } else {
                    mCallback.onRequestFailure(new Throwable(response.raw().message()));
                }

            }

            @Override
            public void onFailure(Call<CommentLikeResponse> call, Throwable t) {

                mCallback.onRequestFailure(t);
            }
        });
    }

    /*Comment Unlike Api Call*/
    public void commentUnLikeApiCall(final String url, final Context context, final InterfaceApiResponseCallBack mCallBack) {

        mServiceInterface.commentUnlikeApiCalls(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<CommentUnlikeResponse>() {
            @Override
            public void onResponse(Call<CommentUnlikeResponse> call, Response<CommentUnlikeResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    mCallBack.onRequestSuccess(response.body());
                } else {
                    mCallBack.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<CommentUnlikeResponse> call, Throwable t) {

                mCallBack.onRequestFailure(t);
            }
        });
    }

    /*Get Comment Api Call*/
    public void getCommentsOrLikesApiCall(final String url, final BaseFragment baseFragment) {
        mServiceInterface.getCommentOrLikesApiCalls(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<GetCommentsResponse>() {
            @Override
            public void onResponse(Call<GetCommentsResponse> call, Response<GetCommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    baseFragment.onRequestSuccess(response.body());
                } else {
                    baseFragment.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<GetCommentsResponse> call, Throwable t) {
                baseFragment.onRequestFailure(t);
            }
        });
    }

    /*Share Map Api Call*/
    public void shareMapApiCall(final String url, final BaseActivity baseActivity) {
        mServiceInterface.shareMapApiCall(AppConstants.ACCESS_TOKEN, url).enqueue(new Callback<ShareMapResponse>() {
            @Override
            public void onResponse(Call<ShareMapResponse> call, Response<ShareMapResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestSuccess(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(Call<ShareMapResponse> call, Throwable t) {
                baseActivity.onRequestFailure(t);
            }
        });
    }


    /*custom pin Api Call*/
    public void createCustomPin(AddPinInputEntity addPinInputEntity, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.createCustomPin(AppConstants.ACCESS_TOKEN, addPinInputEntity).enqueue(new Callback<CommonResultEntity>() {
            @Override
            public void onResponse(@NonNull Call<CommonResultEntity> call, @NonNull Response<CommonResultEntity> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonResultEntity> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*custom pin Api Call*/
    public void NofiticationApiCall(String queryUrl, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.NotificationApiCall(AppConstants.ACCESS_TOKEN, queryUrl).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*tiny Url  Call*/
    public void TinyUrlApiCall(TinyBodyEntity tinyBodyEntity, String queryUrl, final BaseActivity baseActivity) {
        DialogManager.getInstance().showProgress(baseActivity);
        mServiceInterface.TinyApiCall("application/json", tinyBodyEntity, queryUrl).enqueue(new Callback<TinyUrlResponse>() {
            @Override
            public void onResponse(@NonNull Call<TinyUrlResponse> call, @NonNull Response<TinyUrlResponse> response) {
                DialogManager.getInstance().hideProgress();
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<TinyUrlResponse> call, @NonNull Throwable t) {
                DialogManager.getInstance().hideProgress();
                baseActivity.onRequestFailure(t);
            }
        });

    }

    /*Hidden gem api call*/
    public void hiddengemAPICall(double lat, double lon, double radius, final BaseActivity baseActivity) {
//        String queryUrl = AppConstants.BASE_URL + AppConstants.ACTIVITY_FEED_URL + AppConstants.LAT_URL+ lat + AppConstants.LON_URL + lon + AppConstants.RADIUS_URL +radius;
        String queryUrl = AppConstants.BASE_URL + AppConstants.HIDDEN_GEM_URL + AppConstants.LAT_URL + lat + AppConstants.LON_URL + lon + AppConstants.RADIUS_URL + radius;
        Log.e("sddsd", queryUrl);
        mServiceInterface.hiddengemAPI(AppConstants.ACCESS_TOKEN, queryUrl).enqueue(new Callback<HiddenGemResponse>() {
            @Override
            public void onResponse(@NonNull Call<HiddenGemResponse> call, @NonNull Response<HiddenGemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    baseActivity.onRequestSuccess(response.body());
                } else {
//                    baseActivity.onRequestFailure(new Throwable(response.raw().message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<HiddenGemResponse> call, @NonNull Throwable t) {
//                baseActivity.onRequestFailure(t);
            }
        });

    }
}


