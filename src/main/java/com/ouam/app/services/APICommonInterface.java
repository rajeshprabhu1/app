package com.ouam.app.services;


import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.CommentInputEntity;
import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.ImageUploadResultEntity;
import com.ouam.app.entity.LoginInputEntity;
import com.ouam.app.entity.SignUpEntity;
import com.ouam.app.entity.TinyBodyEntity;
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

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface APICommonInterface {


    /*SignUpAPI*/
    @POST("users/me/signup")
    Call<CommonResultEntity> signUpAPI(@Body SignUpEntity signUpEntity);

    /*ProfileUpdate*/
    @POST("http://ouam-development-env-2.us-east-2.elasticbeanstalk.com/v1/users/me/profile")
    Call<ProfileUpdateResponse> updateProfileAPI(@Header("Authorization") String headerAccessToken, @Body SignUpEntity signUpEntity);

    /*ProfileImageUpload*/
    @Multipart
    @POST("http://ouam-development-env-2.us-east-2.elasticbeanstalk.com/v1/users/me/profile/image")
    Call<ImageUploadResultEntity> profileUploadImage(@Header("Authorization") String headerAccessToken, @Part MultipartBody.Part image);

    /*Place search api call*/
    @GET
    Call<ActivityFeedResponse> activityFeedAPI(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Hidden Gem api call*/
    @GET
    Call<HiddenGemResponse> hiddengemAPI(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Place search api call*/
    @GET
    Call<PlacesSearchResponse> placesSearchApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    @GET
    Call<PeopleSearchResponse> peopleSearchAPI(@Header("Authorization") String headerAccessToken, @Url String quertyUrl);

    /*Cities Search Api Call*/
    @GET
    Call<CitySearchResponse> citiesSearchAPI(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*User fav api call*/
    @POST("users/user/{username}/follow")
    Call<FollowResponse> followApi(@Header("Authorization") String headerAccessToken, @Path("username") String userName);

    /*User unFav api call*/
    @DELETE("users/user/{username}/follow")
    Call<UnFollowResponse> unFollowApi(@Header("Authorization") String headerAccessToken, @Path("username") String userName);


    /*User fav api call*/
    @GET("users/user/{userId}/recommendations")
    Call<RecommendationFollowResponse> recommendationFollowAPI (@Header("Authorization") String headerAccessToken, @Path("userId") String userIdStr);

    /*User fav api call*/
    @GET("/places/place/{placeID}/userRecommendations?pinType={placeTypeID}")
    Call<RecommendationFollowResponse> recommendationPlaceFollowAPI (@Header("Authorization") String headerAccessToken, @Path("placeID") String placeIDStr, @Path("placeTypeID") String placeTypeIDStr);

    /*Add pin api call*/
    @POST
    Call<AddPinResponse> addPinApi(@Header("Authorization") String headerAccessToken,
                                   @Body AddPinInputEntity addPinInputEntity, @Url String url);

    /*Add post api call*/
    @Multipart
    @POST
    Call<PinPostResponse> addPostApi(@Header("Authorization") String headerAccessToken,
                                     @Url String url,
                                     @Part MultipartBody.Part comments,
                                     @Part MultipartBody.Part imageOne,
                                     @Part MultipartBody.Part imageTwo

    );

    /*Delete pin api call*/
    @DELETE
    Call<RemovePinResponse> deletePinApi(@Header("Authorization") String headerAccessToken,
                                         @Url String url);


    /*Get all pines list of User*/
    @GET
    Call<PlacePinsResponse> getUserPinListApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Get lat and long from address*/
    @GET
    Call<AddressResponse> getUserAddressAPICall(@Url String urlStr);

    /*User Follower api call*/
    @GET
    Call<UserFollowerResponse> userFollowerApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*User Following api Call*/
    @GET
    Call<UserFollowingResponse> userFollowingApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*User Recent api Call*/
    @GET
    Call<ActivityFeedResponse> userRecentApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*User Profile api Call*/

    @GET
    Call<UserProfileResponse> userProfileApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Place Activity api Call*/
    @GET
    Call<ActivityFeedResponse> placeActivityApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Place Pins api Call*/
    @GET
    Call<PlacePinsResponse> placePinsApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Place Details Api Call*/
    @GET
    Call<PlaceDetailsResponse> placeDetailsApi(@Header("Authorization") String headAccessToken, @Url String queryUrl);

    /*User Name exist api call*/
    @GET
    Call<UserNameExistResponse> userNameExistApi(@Url String queryUrl);


    /*Cities Trending Place api Call*/
    @GET
    Call<CitiesTrendingPlaceResponse> citiesTrendingPlaceApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Cities Empty Pins api Call*/
    @GET
    Call<CitiesPinsResponse> citiesPinsApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*City Details api Call*/
    @GET
    Call<CitiesDetailsResponse> citiesDetailsApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    @GET
    Call<CityRecentResponse> citiesRecentApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Login api Call*/
    @POST("http://ouam-development-env-2.us-east-2.elasticbeanstalk.com/v1/users/me/login")
    Call<LoginResponse> loginApi(@Body LoginInputEntity loginInputEntity);

    /*User Chat Details Api Calls*/
    @GET
    Call<UserChatResponse> userChatApi(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Get Comments Api Calls*/
    @GET
    Call<GetCommentsResponse> getCommentOrLikesApiCalls(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Post Comments Api Calls*/
    @POST
    Call<SendCommentResponse> postCommentApiCalls(@Header("Authorization") String headerAccessToken, @Url String queryUrl
            , @Body CommentInputEntity commentInputEntity);

    @DELETE
    Call<CommentDeleteResponse> deleteCommentApiCalls(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Comment Like Api Calls*/
    @POST
    Call<CommentLikeResponse> commentLikeApiCalls(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Comment Unlike Api Calls*/
    @DELETE
    Call<CommentUnlikeResponse> commentUnlikeApiCalls(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    /*Share Map Like*/
    @GET
    Call<ShareMapResponse> shareMapApiCall(@Header("Authorization") String headerAccessToken, @Url String queryUrl);


    /*Add pin api call*/
    @POST("http://ouam-development-env-2.us-east-2.elasticbeanstalk.com/v1/places/createCustomPin")
    Call<CommonResultEntity> createCustomPin(@Header("Authorization") String headerAccessToken,
                                             @Body AddPinInputEntity addPinInputEntity);
    /*Notification Api Call*/
    @GET
    Call<NotificationResponse> NotificationApiCall(@Header("Authorization") String headerAccessToken, @Url String queryUrl);

    @POST
    Call<TinyUrlResponse> TinyApiCall(@Header("Content-Type") String content_type, @Body TinyBodyEntity tinyBodyEntity, @Url String queryUrl);
}
