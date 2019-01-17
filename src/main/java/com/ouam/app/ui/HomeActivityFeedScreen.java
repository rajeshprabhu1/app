package com.ouam.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;
import com.onesignal.OneSignal;
import com.ouam.app.R;
import com.ouam.app.adapter.FeedActivityAdapter;
import com.ouam.app.adapter.PlaceFollowRecommendListAdapter;
import com.ouam.app.chat.ConnectionManager;
import com.ouam.app.chat.SendBirdUtils;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceWithTwoArgumentCallBack;
import com.ouam.app.commonInterfaces.RefreshPinListInterface;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.entity.ClusterMarkerItem;
import com.ouam.app.entity.HiddenGemEntity;
import com.ouam.app.entity.OptimizeEntity;
import com.ouam.app.entity.PlacePinsEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.entity.UserProfileEntity;
import com.ouam.app.fragments.PinDialogFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.ActivityFeedResponse;
import com.ouam.app.model.AddressResponse;
import com.ouam.app.model.HiddenGemResponse;
import com.ouam.app.model.PlacePinsResponse;
import com.ouam.app.model.RecommendationFollowResponse;
import com.ouam.app.model.UserProfileResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AddressUtil;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.ClusterMarkerRender;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageDownloaderTask;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PathUtils;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.Utils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class HomeActivityFeedScreen extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerClickListener, InterfaceBtnCallBack,
        FeedActivityAdapter.OnItemClickListener, RefreshPinListInterface, DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    @BindView(R.id.home_parent_lay)
    RelativeLayout mHomeParentLay;

    @BindView(R.id.home_header_bg_lay)
    RelativeLayout mHomeHeaderBgLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.header_left_img)
    RoundedImageView mHeaderLeftUserImg;

    @BindView(R.id.header_right_txt)
    TextView mHeaderRightTxt;

    @BindView(R.id.feed_discrete_scroll_view)
    DiscreteScrollView mFeedDiscreteScrollView;

    @BindView(R.id.place_sug_discrete_scroll_view)
    DiscreteScrollView mPlcFollowSugDiscreteScrollView;

    @BindView(R.id.radial_menu_lay)
    RelativeLayout mRadialMenuLay;

    @BindView(R.id.dis_covert_floating_img)
    ImageView mDisCovertFloatingImg;

    @BindView(R.id.un_dis_cover_floating_img)
    ImageView mUnDisCoverFloatingImg;

    @BindView(R.id.hidden_gems_floating_img)
    ImageView mHiddenGemsFloatingImg;

    private UserDetailsEntity mUserDetailsRes = new UserDetailsEntity();

    private Animation mFabOpenAnimation, mFabCloseAnimation, mFabRotateForwardAnimation, mFeedBottomToTopAnimation, mFeedTopToBottomAnimation;

    private boolean mIsPhotoLatLonBool = false, mIsCameraPermissionBool = false, mIsFabOpenBool = false, mIsMapMoveBool = false;


    private ArrayList<ActivityFeedEntity> mActivityFeedArrList = new ArrayList<>();
    private ArrayList<HiddenGemEntity> mHiddenGemArrList = new ArrayList<>();

    private InfiniteScrollAdapter mInfiniteScrollAdapter;
    private FeedActivityAdapter mFeedAdapter;
    private RecyclerView.ItemDecoration mRecyclerItemDecorator = new RecyclerItemDecorator();

    private ClusterManager<ClusterMarkerItem> mClusterManager;
    private ClusterMarkerItem mClusterMarkerItem;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;

    private double mRadiusDouble = 15000, mSavedLatitudeDouble = 0.0, mSavedLongitudeDouble = 0.0;
    private final int REQUEST_CHECK_SETTINGS_INT = 300, REQUEST_CAMERA_INT = 999, REQUEST_GALLERY_INT = 888;
    private Uri mPictureFileUri;

    private final int CHANNEL_LIST_LIMIT_INT = 15;
    private final String CONNECTION_HANDLER_ID_STR = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";

    private Handler mMapMoveHandler, mHandler;
    private Runnable mMapMoveRunnable, mRunnable;

    private PinDialogFragment mPinDialogFragment;
    private String mPlaceIdStr = "", mPlaceTypeIdStr = "";
    private boolean mIsFirstPinRecommendBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_feed_screen);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initView();
        if (mIsPhotoLatLonBool) {
            DialogManager.getInstance().showOptionPopup(this, "Photo location not available", "Use current \n location", "Don't use", new InterfaceTwoBtnCallBack() {
                @Override
                public void onNegativeClick() {
                    mIsPhotoLatLonBool = false;
                }

                @Override
                public void onPositiveClick() {
                    mIsPhotoLatLonBool = false;
                    openPinDialog();
                }
            });
        }


        if (AppConstants.IS_CALLED_ALREADY) {
            openPinDialog();
        }

        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID_STR, AppConstants.PROFILE_USER_ID,
                reconnect -> refresh());
    }


    /*InitViews*/
    private void initView() {

        mUserDetailsRes = PreferenceUtil.getUserDetailsRes(this);
        mHandler = new Handler(getMainLooper());
        ButterKnife.bind(this);

        /*Keypad to be hidden when a touch made outside the edit text*/
        setupUI(mHomeParentLay);
        setHeaderView();
        setUserDetails(mUserDetailsRes);

        if (permissionsAccessLocation(true)) {

            initGoogleAPIClient();

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (fragment != null)
                fragment.getMapAsync(this);

            mFabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            mFabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
            mFabRotateForwardAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);

            mFeedBottomToTopAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_in_bottom);
            mFeedTopToBottomAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_in_top);

            /*One Signal setup*/
//            JSONObject tags = new JSONObject();
//            try {
//                tags.put("UserType", "Android");
//                tags.put("UserName", "Gowtham");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            OneSignal.sendTags(tags);

            //TODO Need to remove the block
            OneSignal.idsAvailable((userId, registrationId) -> {
                Log.d("debug", "User:" + userId);
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);

            });


            if (mUserDetailsRes != null) {
                AppConstants.ACCESS_TOKEN = mUserDetailsRes.getAuthorizationToken();
                AppConstants.PROFILE_USER_ID = mUserDetailsRes.getUserId();
                sysOut("ACCESS-TOKEN " + AppConstants.ACCESS_TOKEN);
                sysOut("PROFILE-USER_ID " + AppConstants.PROFILE_USER_ID);
                callUserProfileApi();
            }

            /*from notification navigation*/
            String channelUrl = getIntent().getStringExtra("groupChannelUrl");
            if (channelUrl != null && AppConstants.FROM_NOTIFICATION.equals(AppConstants.KEY_TRUE)) {
                Class<?> clazz = HomeActivityFeedScreen.class;
                if (AppConstants.PREVIOUS_SCREEN.size() == 0) {
                    AppConstants.PREVIOUS_SCREEN.add(clazz.getCanonicalName());
                }
                AppConstants.SEND_BIRD_CHANNER_URL = channelUrl;
                nextScreen(ChatListActivityScreen.class, true);
            }
            trackScreenName(getString(R.string.home_activity_screen));
        }
    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHomeHeaderBgLay.post(() -> mHomeHeaderBgLay.setPadding(0, getStatusBarHeight(HomeActivityFeedScreen.this), 0, 0));
        }
    }


    @OnClick({R.id.header_left_img_lay, R.id.header_search_card_view, R.id.search_img,
            R.id.header_txt, R.id.current_loc_img, R.id.hidden_gems_floating_img,
            R.id.camera_img, R.id.header_right_img_lay, R.id.dis_covert_floating_img,
            R.id.un_dis_cover_floating_img, R.id.feed_img, R.id.main_floating_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                AppConstants.PROFILE_USER_ID = PreferenceUtil.getUserId(this);
                nextScreen(UserProfileActivityScreen.class, true);
                break;

            case R.id.header_search_card_view:
            case R.id.search_img:
            case R.id.header_txt:
                nextScreen(SearchActivityScreen.class, true);
                break;

            case R.id.current_loc_img:
                setCurrentLocation();
                break;
            case R.id.camera_img:
                if (checkPermission()) {
                    mIsCameraPermissionBool = false;
                    uploadImage();
                    AppConstants.IS_CALLED_ALREADY = false;
                    mIsPhotoLatLonBool = false;
                }
                break;
            case R.id.dis_covert_floating_img:
                mIsCameraPermissionBool = true;
                FragmentManager fm = getSupportFragmentManager();
                mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_been_there), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String placeIdStr, String placeTypeIdStr) {
                        if (!placeIdStr.isEmpty()) {
                            mPlaceIdStr = placeIdStr;
                            mPlaceTypeIdStr = mPlaceTypeIdStr;
                            getUserPinListApiCall();
                        }
                    }
                });
                mPinDialogFragment.show(fm, "fragment_edit_name");
                break;
            case R.id.un_dis_cover_floating_img:
                mIsCameraPermissionBool = true;
                FragmentManager fm1 = getSupportFragmentManager();
                mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_explore), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String placeIdStr, String placeTypeIdStr) {
                        if (!placeIdStr.isEmpty()) {
                            mPlaceIdStr = placeIdStr;
                            mPlaceTypeIdStr = mPlaceTypeIdStr;
                            getUserPinListApiCall();
                        }
                    }
                });
                mPinDialogFragment.show(fm1, "fragment_edit_name");
                break;
            case R.id.hidden_gems_floating_img:
                mIsCameraPermissionBool = true;
                FragmentManager fm2 = getSupportFragmentManager();
                mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_hidden_gem), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String placeIdStr, String placeTypeIdStr) {
                        if (!placeIdStr.isEmpty()) {
                            mPlaceIdStr = placeIdStr;
                            mPlaceTypeIdStr = mPlaceTypeIdStr;
                            getUserPinListApiCall();
                        }
                    }
                });
                mPinDialogFragment.show(fm2, "fragment_edit_name");
                break;
            case R.id.header_right_img_lay:
                nextScreen(ChatListActivityScreen.class, true);
                break;
            case R.id.feed_img:
                if (mActivityFeedArrList.size() > 0) {
                    feedListAndRadialAnim(true);
                } else {
                    DialogManager.getInstance().showToast(this, getString(R.string.no_feed_list));
                }
                break;
            case R.id.main_floating_img:
                mIsFabOpenBool = !mIsFabOpenBool;
                animateFab(mIsFabOpenBool);
                break;
        }
    }


    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsAccessLocation(false);
            return;
        }
        if (NetworkUtil.isNetworkAvailable(this)) {
            if (mGoogleMap != null) {

                FusedLocationProviderClient mLastLocation = LocationServices.getFusedLocationProviderClient(this);

                mLastLocation.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {

                        if (ActivityCompat.checkSelfPermission(HomeActivityFeedScreen.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(HomeActivityFeedScreen.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            /*Ask for permission on locatio access
                             * Set flag for call back to continue this process*/
                            permissionsAccessLocation(false);
                        }
                        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                        AppConstants.CURRENT_LAT = location.getLatitude();
                        AppConstants.CURRENT_LONG = location.getLongitude();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 0));

                        try {
                            ArrayList<String> addressList = AddressUtil.getAddressFromLatLng(HomeActivityFeedScreen.this, location.getLatitude(), location.getLongitude());
                            if (addressList.size() > 0) {
                                mHeaderTxt.setText("");
                                mHeaderTxt.setText(addressList.get(0));

                                AppConstants.MAP_MOVE_LOCATION_NAME = addressList.get(0);
                                AppConstants.MAP_MOVE_LAT = location.getLatitude();
                                AppConstants.MAP_MOVE_LONG = location.getLongitude();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        /*Activity Feed API Call*/
                        activityFeedAPICall(location.getLatitude(), location.getLongitude());
                        hiddengemAPICall();


                    }

                });
            }
        } else {
            /*Alert message will be appeared if there is no internet connection*/
            DialogManager.getInstance().showNetworkErrorPopup(this, this::setCurrentLocation);
        }

    }

    /*Set current loc custom marker*/
    private void setCurrentLocMarker() {
        if (mGoogleMap != null) {
            runOnUiThread(() -> {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.current_loc);
                Bitmap bitmap = bitmapDrawable.getBitmap();

                int sizeInt = getResources().getDimensionPixelSize(R.dimen.size30);
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, sizeInt, sizeInt, false);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(AppConstants.CURRENT_LAT, AppConstants.CURRENT_LONG)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                mGoogleMap.addMarker(marker);
            });

        }
    }


    private void setUserDetails(UserDetailsEntity userProfileEntity) {
        /*set user image*/
        if (userProfileEntity.getImage().isEmpty()) {
            mHeaderLeftUserImg.setImageResource(R.drawable.profile_bg);
        } else {
            try {
                Glide.with(this)
                        .load(userProfileEntity.getImage())
                        .apply(new RequestOptions().placeholder(R.drawable.profile_bg).error(R.drawable.profile_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(mHeaderLeftUserImg);
                AppConstants.COMMENT_USER_IMG = mUserDetailsRes.getImage();

            } catch (Exception ex) {
                mHeaderLeftUserImg.setImageResource(R.drawable.profile_bg);
            }
        }
    }


    /*Feed list and radial menu visible and gone process*/
    private void feedListAndRadialAnim(boolean isFeedVisibleBool) {

        runOnUiThread(() -> {
            if (isFeedVisibleBool) {
                mRadialMenuLay.setVisibility(View.GONE);
                mPlcFollowSugDiscreteScrollView.setVisibility(View.GONE);
                mFeedDiscreteScrollView.setVisibility(View.VISIBLE);
                mFeedDiscreteScrollView.startAnimation(mFeedBottomToTopAnimation);

                final RecyclerView.Adapter adapter = mFeedDiscreteScrollView.getAdapter();
                if (mInfiniteScrollAdapter.getRealItemCount() > 0 && adapter != null) {
                    int realPosInt = ((InfiniteScrollAdapter) adapter).getRealCurrentPosition();
                    animZoomCameraToMarker(realPosInt);
                }


            } else {
                mFeedDiscreteScrollView.startAnimation(mFeedTopToBottomAnimation);
                mFeedDiscreteScrollView.setVisibility(View.GONE);
                mRadialMenuLay.setVisibility(View.VISIBLE);
            }
        });

    }

    /*Set follow recommendation list adapter into animated discrete scroll view*/
    public void setRecommendationFollowAdapter(ArrayList<UserProfileEntity> followRecommendArrList) {
        mPlcFollowSugDiscreteScrollView.setVisibility(followRecommendArrList.size() > 0 ? View.VISIBLE : View.GONE);
        mPlcFollowSugDiscreteScrollView.removeItemDecoration(mRecyclerItemDecorator);
        int feedListWidthInt = getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.size60);
        runOnUiThread(() -> {
            InterfaceBtnCallBack userClickedFollowInterfaceBtn = this::recommendationFollowAPI;

            PlaceFollowRecommendListAdapter followRecommendListAdapter = new PlaceFollowRecommendListAdapter(this, followRecommendArrList, mPlcFollowSugDiscreteScrollView, feedListWidthInt, mIsFirstPinRecommendBool, userClickedFollowInterfaceBtn);

            mPlcFollowSugDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
            InfiniteScrollAdapter mInfiniteScrollAdapter = InfiniteScrollAdapter.wrap(followRecommendListAdapter);
            mPlcFollowSugDiscreteScrollView.setAdapter(mInfiniteScrollAdapter);
            mPlcFollowSugDiscreteScrollView.setItemTransitionTimeMillis(150);
            mPlcFollowSugDiscreteScrollView.setNestedScrollingEnabled(true);
            mPlcFollowSugDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
            mPlcFollowSugDiscreteScrollView.addItemDecoration(mRecyclerItemDecorator);

        });
    }


    /*Set feed list adapter into animated discrete scroll view*/
    public void setFeedListAdapter() {
        mFeedDiscreteScrollView.removeItemDecoration(mRecyclerItemDecorator);
        int feedListWidthInt = getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.size60);
        if (mActivityFeedArrList.size() > 0) {
            runOnUiThread(() -> {

                if (mFeedAdapter == null) {

                    mFeedAdapter = new FeedActivityAdapter(this, mActivityFeedArrList, this, mFeedDiscreteScrollView, "", feedListWidthInt);

                    mFeedDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
                    mFeedDiscreteScrollView.addOnItemChangedListener(this);
                    mInfiniteScrollAdapter = InfiniteScrollAdapter.wrap(mFeedAdapter);
                    mFeedDiscreteScrollView.setAdapter(mInfiniteScrollAdapter);
                    mFeedDiscreteScrollView.setItemTransitionTimeMillis(150);
                    mFeedDiscreteScrollView.setNestedScrollingEnabled(true);
                    mFeedDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                            .setMinScale(0.8f)
                            .build());
                    mFeedDiscreteScrollView.addItemDecoration(mRecyclerItemDecorator);

                } else {
                    mFeedAdapter.notifyDataSetChanged();
                }

                final RecyclerView.Adapter adapter = mFeedDiscreteScrollView.getAdapter();
                if (mInfiniteScrollAdapter.getRealItemCount() > 0 && adapter != null) {
                    mFeedDiscreteScrollView.post(() -> {
                        int destinationNewPosInt = ((InfiniteScrollAdapter) adapter).getClosestPosition(0);
                        mFeedDiscreteScrollView.smoothScrollToPosition(destinationNewPosInt);

                    });
                }
            });

        } else {
            ArrayList<ActivityFeedEntity> activityFeedList = new ArrayList<>();
            mFeedAdapter = new FeedActivityAdapter(this, activityFeedList, this, mFeedDiscreteScrollView, "", feedListWidthInt);

            mFeedDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
            mFeedDiscreteScrollView.addOnItemChangedListener(this);
            mInfiniteScrollAdapter = InfiniteScrollAdapter.wrap(mFeedAdapter);
            mFeedDiscreteScrollView.setAdapter(mInfiniteScrollAdapter);
            mFeedDiscreteScrollView.setItemTransitionTimeMillis(150);
            mFeedDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
            mFeedDiscreteScrollView.setNestedScrollingEnabled(true);
            mFeedDiscreteScrollView.addItemDecoration(mRecyclerItemDecorator);
        }

        mFeedDiscreteScrollView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    /*FeedDiscreteScrollView runtime we are setting the item space width*/
    public class RecyclerItemDecorator extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int spaceSizeInt = getResources().getDimensionPixelOffset(R.dimen.size10);
            outRect.left = -spaceSizeInt;
            outRect.right = -spaceSizeInt;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }

    /*FeedDiscreteScrollView current item change listener*/
    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (mFeedDiscreteScrollView.getVisibility() == View.VISIBLE && mActivityFeedArrList.size() > 0 && mGoogleMap != null) {
            animZoomCameraToMarker(mInfiniteScrollAdapter.getRealPosition(adapterPosition));
        }
    }

    /*FeedDiscreteScrollView current item with map marker zoom  process*/
    private void animZoomCameraToMarker(int listPosInt) {
        runOnUiThread(() -> {
            Double[] latLngDoubleArray = mActivityFeedArrList.get(listPosInt).getGeopoint().getCoordinates();
            LatLng coordinate = new LatLng(latLngDoubleArray[0], latLngDoubleArray[1]);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 20f), 500, null);

        });
    }


    /*Fab animation*/
    private void animateFab(boolean isFabOpenBool) {
        mHiddenGemsFloatingImg.startAnimation(isFabOpenBool ? mFabOpenAnimation : mFabCloseAnimation);
        mDisCovertFloatingImg.startAnimation(isFabOpenBool ? mFabOpenAnimation : mFabCloseAnimation);
        mUnDisCoverFloatingImg.startAnimation(isFabOpenBool ? mFabOpenAnimation : mFabCloseAnimation);

        if (isFabOpenBool) {
            mHiddenGemsFloatingImg.startAnimation(mFabRotateForwardAnimation);
            mDisCovertFloatingImg.startAnimation(mFabRotateForwardAnimation);
            mUnDisCoverFloatingImg.startAnimation(mFabRotateForwardAnimation);
        }

        mHiddenGemsFloatingImg.setClickable(isFabOpenBool);
        mDisCovertFloatingImg.setClickable(isFabOpenBool);
        mUnDisCoverFloatingImg.setClickable(isFabOpenBool);

    }

//    public void setAdapter(double lat, double lng) {
//        for (int i = 0; i < mActivityFeedArrList.size(); i++) {
//
//            Double[] latLngDoubleArray = mActivityFeedArrList.get(i).getGeopoint().getCoordinates();
//            if (lat == latLngDoubleArray[0] && lng == latLngDoubleArray[1]) {
//                mActivityFeedArrList.set(0, mActivityFeedArrList.get(i));
//                mFeedAdapter.notifyDataSetChanged();
//                break;
//            }
//        }
//
//    }

    /*Init Google API clients*/
    private void initGoogleAPIClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationManager mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocManager != null && mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                initGoogleAPIClient();
            } else {
                setCurrentLocation();
                setCurrentLocMarker();
            }
        } else {
            LocationSettingsRequest.Builder locSettingsReqBuilder = new LocationSettingsRequest.Builder().
                    addLocationRequest(AddressUtil.createLocationRequest());
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locSettingsReqBuilder.build());
            result.setResultCallback(locationSettingsResult -> {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                            initGoogleAPIClient();
                        } else {
                            setCurrentLocation();
                            setCurrentLocMarker();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            status.startResolutionForResult(HomeActivityFeedScreen.this, REQUEST_CHECK_SETTINGS_INT);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.e(AppConstants.TAG, e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            });
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsAccessLocation(false);
            return;
        }
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(latLng -> runOnUiThread(() -> {
            if (mFeedDiscreteScrollView.getVisibility() == View.VISIBLE) {
                feedListAndRadialAnim(false);
            }
        }));
    }


    private void openPinDialog() {
        if (!AppConstants.HOME_SCREEN_PHOTO.trim().isEmpty()) {
            FragmentManager fm4 = getSupportFragmentManager();
            mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_been_there), null, new InterfaceWithTwoArgumentCallBack() {
                @Override
                public void withParams(String edtTxt, String type) {

                }
            });
            mPinDialogFragment.show(fm4, "fragment_edit_name");

        }
    }

    private void mapMoveStopped() {
        mMapMoveRunnable = () -> runOnUiThread(() -> {

            removeMapHandler();

            VisibleRegion vr = mGoogleMap.getProjection().getVisibleRegion();
            double latDouble = vr.latLngBounds.getCenter().latitude;
            double longitudeDouble = vr.latLngBounds.getCenter().longitude;


            if (Utils.calculationByDistance(mSavedLatitudeDouble, mSavedLongitudeDouble,
                    latDouble, longitudeDouble) > 5) {

                mSavedLatitudeDouble = vr.latLngBounds.getCenter().latitude;
                mSavedLongitudeDouble = vr.latLngBounds.getCenter().longitude;
                try {
                    ArrayList<String> addressList = AddressUtil.getAddressFromLatLng(HomeActivityFeedScreen.this, latDouble, longitudeDouble);
                    if (addressList.size() > 0) {
                        mHeaderTxt.setText("");

                        String addressStr = addressList.get(0).replace("Unnamed Road,", "");
                        if (addressList.size() > 2) {
                            addressStr = addressList.get(1) + " " + addressList.get(3) + ", " + addressList.get(2);
                        }

                        AppConstants.MAP_MOVE_LOCATION_NAME = addressStr;
                        mHeaderTxt.setText(addressStr);
                        AppConstants.MAP_MOVE_LAT = latDouble;
                        AppConstants.MAP_MOVE_LONG = longitudeDouble;

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


//                            getUserAddressApi(latDouble, longitudeDouble);

                if (mIsMapMoveBool && mFeedDiscreteScrollView.getVisibility() == View.GONE) {
                    activityFeedAPICall(latDouble, longitudeDouble);
//                                hiddengemAPICall(latDouble, longitudeDouble);
                    hiddengemAPICall();

                }
            }
        });

        /*set handler to wait for 1 seconds */
        mMapMoveHandler = new Handler();
        mMapMoveHandler.postDelayed(mMapMoveRunnable, 500);
    }


    private void removeMapHandler() {
        if (mMapMoveHandler != null) {
            mMapMoveHandler.removeCallbacks(mMapMoveRunnable);
        }
    }


    private void callUserProfileApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID;
            APIRequestHandler.getInstance().userProfileApi(url, HomeActivityFeedScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::callUserProfileApi);
        }
    }

    private void recommendationFollowAPI() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            APIRequestHandler.getInstance().recommendationPlaceFollowAPI(mPlaceIdStr, mPlaceTypeIdStr, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::recommendationFollowAPI);
        }
    }

    private void getUserPinListApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + PreferenceUtil.getUserId(this) + AppConstants.PLACES_URL;
            APIRequestHandler.getInstance().getUserPinListApi(url, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::getUserPinListApiCall);
        }
    }

    /*ClusterManager for the Showing Marker in Map*/
    private void setUpClusterManager(ArrayList<ActivityFeedEntity> activityFeedList) {

        runOnUiThread(() -> {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
                setCurrentLocMarker();
                mClusterManager = new ClusterManager<>(this, mGoogleMap);

                mGoogleMap.setOnCameraIdleListener(mClusterManager);
                mGoogleMap.setOnMarkerClickListener(mClusterManager);
                mClusterManager.setOnClusterClickListener(cluster -> {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(), (float) Math.floor(mGoogleMap
                                    .getCameraPosition().zoom + 10)), 1500,
                            null);
                    return true;
                });


                ArrayList<OptimizeEntity> merGedList = new ArrayList<>();

                for (int i = 0; i < activityFeedList.size(); i++) {
                    OptimizeEntity optimizeEntity = new OptimizeEntity();
                    optimizeEntity.setType(activityFeedList.get(i).getActivity().getActivityType());
                    optimizeEntity.setPinType(activityFeedList.get(i).getActivity().getSubtype());

                    String url = activityFeedList.get(i).getActivity().getUser().getPhoto().isEmpty() ? "https://s3-us-west-2.amazonaws.com/ouam-placeholders/marge.jpg" : activityFeedList.get(i).getActivity().getUser().getPhoto();


                    optimizeEntity.setImageUrl(url);

                    Double[] latLngDoubleArray = activityFeedList.get(i).getGeopoint().getCoordinates();

                    optimizeEntity.setLat(latLngDoubleArray[0]);
                    optimizeEntity.setLon(latLngDoubleArray[1]);
                    merGedList.add(optimizeEntity);

                }
                /*Add Marker list to show*/

                for (int i = 0; i < merGedList.size(); i++) {

                    LatLng coordinate = new LatLng(merGedList.get(0).getLat(),
                            merGedList.get(0).getLon());

                    mClusterMarkerItem = new ClusterMarkerItem(merGedList.get(i).getLat(),
                            merGedList.get(i).getLon(), merGedList.get(i).getPinType(), merGedList.get(i).getImageUrl(), "", i);
                    mClusterManager.addItem(mClusterMarkerItem);

                    if (!mIsMapMoveBool) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 0));
                    }
                }

                /*Set the Item Marker Using Cluster*/
                ClusterMarkerRender markerRender = new ClusterMarkerRender(HomeActivityFeedScreen.this, mGoogleMap, mClusterManager, itemPosInt -> {
                    if (mActivityFeedArrList.size() > 0) {
                        runOnUiThread(() -> {
                            if (mFeedDiscreteScrollView.getVisibility() == View.GONE)
                                feedListAndRadialAnim(true);

                            final RecyclerView.Adapter adapter = mFeedDiscreteScrollView.getAdapter();
                            if (mInfiniteScrollAdapter.getRealItemCount() > 0 && adapter != null) {
                                int realPosInt = ((InfiniteScrollAdapter) adapter).getRealCurrentPosition();
                                if (realPosInt != itemPosInt) {
                                    mFeedDiscreteScrollView.post(() -> {
                                        int destinationNewPosInt = ((InfiniteScrollAdapter) adapter).getClosestPosition(itemPosInt);
                                        mFeedDiscreteScrollView.smoothScrollToPosition(destinationNewPosInt);

                                    });
                                }
                            }
                        });
                    }
                });

                mClusterManager.setRenderer(markerRender);
                mClusterManager.setAnimation(true);

                mGoogleMap.setOnCameraIdleListener(() -> {
                    float zoomLevel = mGoogleMap.getCameraPosition().zoom;
                    if (zoomLevel <= 5.0) {
                        mRadiusDouble = 15000;
                    } else if (zoomLevel <= 9.0 && zoomLevel >= 5.0) {
                        mRadiusDouble = 3000;
                    } else {
                        mRadiusDouble = 500;
                    }
                    mClusterManager.cluster();
                    mapMoveStopped();

                });

                mGoogleMap.setOnCameraMoveStartedListener(reason -> {
                    if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        mIsMapMoveBool = true;
                        runOnUiThread(() -> {
                            if (mFeedDiscreteScrollView.getVisibility() == View.VISIBLE) {
                                feedListAndRadialAnim(false);
                            }
                        });

                    } else if (reason == GoogleMap.OnCameraMoveStartedListener
                            .REASON_DEVELOPER_ANIMATION) {
                        mIsMapMoveBool = false;
                    }

                });


            }
        });

    }


    /*Ask permission on Location access*/
    private boolean permissionsAccessLocation(boolean initFlowBool) {

        boolean addPermission = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

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
                public void onNegativeClick() {
                    if (initFlowBool) {
                        ActivityCompat.finishAffinity(HomeActivityFeedScreen.this);
                    }
                }

                @Override
                public void onPositiveClick() {
                    if (initFlowBool) {
                        initView();
                    } else {
                        setCurrentLocation();
                    }

                }
            });
        }
        return addPermission;
    }


    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /* Ask for permission on Camera access*/
    private boolean checkPermission() {

        boolean addPermission = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            addPermission = askAccessPermission(listPermissionsNeeded, 1, new InterfaceTwoBtnCallBack() {
                @Override
                public void onNegativeClick() {

                }


                @Override
                public void onPositiveClick() {
                    if (!mIsCameraPermissionBool)
                        uploadImage();
                }

            });
        }

        return addPermission;

    }

    /*open camera*/
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureFileUri = (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", Objects.requireNonNull(getOutputMediaFile(MEDIA_TYPE_IMAGE))) : Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureFileUri);
        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA_INT);
    }

    private File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(this.getClass().getSimpleName(), "failed directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + getString(R.string.app_name) + "-" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    /*open gallery*/
    private void galleryImage() {
        Intent j = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(j, REQUEST_GALLERY_INT);
    }


    private void uploadImage() {
        DialogManager.getInstance().showImageUploadPopup(this,
                getString(R.string.select_photo), getString(R.string.take_photo), getString(R.string.choose_existing_photo), new InterfaceTwoBtnCallBack() {
                    @Override
                    public void onNegativeClick() {
                        captureImage();
                    }

                    @Override
                    public void onPositiveClick() {
                        galleryImage();
                    }
                });
    }

    /*Activity Feed API Call*/
    private void activityFeedAPICall(final double lat, final double lon) {
        if (NetworkUtil.isNetworkAvailable(HomeActivityFeedScreen.this)) {
            APIRequestHandler.getInstance().activityFeedAPICall(lat, lon, mRadiusDouble, HomeActivityFeedScreen.this);

        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, () -> activityFeedAPICall(lat, lon));
        }
    }


    /*Activity Feed API Call*/
    private void hiddengemAPICall() {
//        if (NetworkUtil.isNetworkAvailable(HomeActivityFeedScreen.this)) {
//            APIRequestHandler.getInstance().hiddengemAPICall(41.8864, -87.6667, mRadiusDouble, HomeActivityFeedScreen.this);
//
//        } else {
//            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
//                @Override
//                public void onPositiveClick() {
//                    hiddengemAPICall();
//                }
//            });
//        }
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);

        if (resObj instanceof ActivityFeedResponse) {
            ActivityFeedResponse activityFeedResponse = (ActivityFeedResponse) resObj;
            mActivityFeedArrList = new ArrayList<>();
            mActivityFeedArrList = activityFeedResponse.getWith();

            mFeedAdapter = null;
            setFeedListAdapter();

            /*ClusterManager for the Showing Marker in Map*/
            setUpClusterManager(mActivityFeedArrList);

        }
        if (resObj instanceof HiddenGemResponse) {
            HiddenGemResponse hiddenGemResponse = (HiddenGemResponse) resObj;
            mHiddenGemArrList = new ArrayList<>();
            mHiddenGemArrList = hiddenGemResponse.getWith();

            //  ArrayList<HiddenGemEntity> mHiddenGemArrList = new ArrayList<>();
//
            for (int i = 0; i < mHiddenGemArrList.size(); i++) {
                Double lat = mHiddenGemArrList.get(i).getLat();
                Double lon = mHiddenGemArrList.get(i).getLon();


                int height = 72;
                int width = 85;
                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.gem_icon);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {

                        return null;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public View getInfoContents(Marker marker) {
                        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.mapinfowindow, null);
                        TextView info1 = v.findViewById(R.id.infotitle);
                        for (int i = 0; i < mHiddenGemArrList.size(); i++) {

                            info1.setText("You have found a hidden gem" + " " + "'" + mHiddenGemArrList.get(i).getName() + " " + mHiddenGemArrList.get(i).getCity().getName() + " " + mHiddenGemArrList.get(i).getCity().getLocality() + " " + mHiddenGemArrList.get(i).getCity().getCountry() + "'" + " " + " click here to see the details");

                            mGoogleMap.setOnInfoWindowClickListener(marker1 -> {
                                for (int i1 = 0; i1 < mHiddenGemArrList.size(); i1++) {
                                    if (!mHiddenGemArrList.get(i1).getProviderIDs().isEmpty() && !mHiddenGemArrList.get(i1).getId().isEmpty()) {
                                        String[] providerIdArr = mHiddenGemArrList.get(i1).getProviderIDs().split("\"");
                                        AppConstants.PLACE_NAME = mHiddenGemArrList.get(i1).getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2];
                                        AppConstants.pinType = mHiddenGemArrList.get(i1).getPinnedAs();
                                        nextScreen(PlaceDetailsActivityScreen.class, true);
                                    }
                                }

                            });
                        }

                        return v;
                    }
                });

                mGoogleMap.addMarker(marker);
            }


        }

        if (resObj instanceof UserProfileResponse) {
            UserProfileResponse mProfileResponse = (UserProfileResponse) resObj;
            UserProfileEntity userProfileList = mProfileResponse.getWith();

            UserDetailsEntity userDetailsEntity = PreferenceUtil.getUserDetailsRes(this);
            userDetailsEntity.setEmail(userProfileList.getUser().getEmail());
            userDetailsEntity.setImage(userProfileList.getUser().getPhoto());

            PreferenceUtil.storeUserDetails(HomeActivityFeedScreen.this, userDetailsEntity);

            setUserDetails(userDetailsEntity);

            /*connect to sendBird*/
            connectToSendBird(userProfileList.getUser().getUsername(), userProfileList.getUser().getPhoto());

            /*Tutorial Popup*/
            if (!PreferenceUtil.getBoolPreferenceValue(this, AppConstants.POPUP_TUTORIAL_SEEN_STATUS)) {
                DialogManager.getInstance().showWelcomePopup(this, userProfileList.getUser().getPhoto());
                PreferenceUtil.storeBoolPreferenceValue(this, AppConstants.POPUP_TUTORIAL_SEEN_STATUS, true);
            }
        }

        if (resObj instanceof AddressResponse) {
            AddressResponse userAddressRes = (AddressResponse) resObj;
            if (userAddressRes.getResults().size() > 0)
                AppConstants.MAP_MOVE_LOCATION_NAME = userAddressRes.getResults().get(0).getFormatted_address();
            mHeaderTxt.setText(AppConstants.MAP_MOVE_LOCATION_NAME);
        }
        if (resObj instanceof PlacePinsResponse) {
            PlacePinsResponse placePinsResponse = (PlacePinsResponse) resObj;
            PlacePinsEntity placePinsEntityRes = placePinsResponse.getWith();
            mIsFirstPinRecommendBool = placePinsEntityRes.getBeenThere().size() + placePinsEntityRes.getHiddenGems().size() + placePinsEntityRes.getToBeExplored().size() == 0;
            recommendationFollowAPI();
        }
        if (resObj instanceof RecommendationFollowResponse) {
            RecommendationFollowResponse recommendationFollowResponse = (RecommendationFollowResponse) resObj;
            if (recommendationFollowResponse.getStatus().equals(getString(R.string.succeeded))) {
                if (recommendationFollowResponse.getWith().size() > 0)
                    setRecommendationFollowAdapter(recommendationFollowResponse.getWith());
            }
        }
    }

    @Override
    public void onBackPressed() {
        exitFromApp();
    }

    /*App exit popup*/
    private void exitFromApp() {
        DialogManager.getInstance().showOptionPopup(this, getString(R.string.exit_msg),
                getString(R.string.yes), getString(R.string.no), new InterfaceTwoBtnCallBack() {
                    @Override
                    public void onPositiveClick() {
                        ActivityCompat.finishAffinity(HomeActivityFeedScreen.this);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

    }


//    private void getUserAddressApi(double lat, double lang) {
//        if (NetworkUtil.isNetworkAvailable(this)) {
//            String latLngStr = lat + "," + lang;
//            String addressURLStr = String.format(AppConstants.GET_ADDRESS_URL, latLngStr);
//            APIRequestHandler.getInstance().callGetUserAddressAPI(addressURLStr, this);
//        } else {
//            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
//                @Override
//                public void onPositiveClick() {
//                    getUserAddressApi(lat, lang);
//                }
//            });
//        }
//
//    }


    @Override
    public void onPositiveClick() {
        nextScreen(CommentsLikesActivityScreen.class, true);
    }

    @Override
    public void onItemClicked(ActivityFeedEntity activityFeedEntity) {
//        if (activityFeedEntity.getGeopoint().getCoordinates() != null) {
////            Double[] latLngDoubleArray = activityFeedEntity.getGeopoint().getCoordinates();
////            LatLng coordinate = new LatLng(latLngDoubleArray[0], latLngDoubleArray[1]);
////            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 0));
////
////        }
        if (!activityFeedEntity.getActivity().getUser().getId().trim().isEmpty()) {
            AppConstants.PROFILE_USER_ID = activityFeedEntity.getActivity().getUser().getId();
            nextScreen(UserProfileActivityScreen.class, true);
        }

    }


    private void refresh() {

        mRunnable = () -> {
            refreshChannelList(CHANNEL_LIST_LIMIT_INT);
            mHandler.postDelayed(mRunnable, 5000);
        };
        mHandler.postDelayed(mRunnable, 0);
    }


    /**
     * Creates a new query to get the list of the user's Group Channels,
     * then replaces the existing dataset.
     *
     * @param numChannels The number of channels to load.
     */


    private void refreshChannelList(int numChannels) {
        GroupChannelListQuery mChannelListQuery = GroupChannel.createMyGroupChannelListQuery();
        mChannelListQuery.setLimit(numChannels);

        mChannelListQuery.next((list, e) -> {
            if (e != null) {
                // Error!
                e.printStackTrace();
                return;
            }

            int count = 0;
            /*check the unreadmsg count*/
            for (int i = 0; i < list.size(); i++) {
                count = count + list.get(i).getUnreadMessageCount();
            }
            PreferenceUtil.storeStringValue(HomeActivityFeedScreen.this,
                    AppConstants.UNREAD_MESSAGE_COUNT, String.valueOf(count));
            setBadgeCount();


        });

    }


    /**
     * Attempts to connect a user to SendBird.
     *
     * @param userNickname The user's nickname, which will be displayed in chats.
     */
    private void connectToSendBird(final String userNickname, String userPhoto) {
        // Show the loading indicator
        ConnectionManager.login(AppConstants.PROFILE_USER_ID, (user, e) -> {
            // Callback received; hide the progress bar.
//                showProgressBar(false);

            if (e != null) {
                // Error!
                DialogManager.getInstance().showToast(HomeActivityFeedScreen.this, "" + e.getCode() + ": " + e.getMessage());

                return;
            }


            // Update the user's nickname
            SendBirdUtils.updateCurrentUserInfo(HomeActivityFeedScreen.this, userNickname);
            AppConstants.USER_NAME = userNickname;
            updateCurrentUserProfileImage(userPhoto);

            SendBirdUtils.updateCurrentUserPushToken(HomeActivityFeedScreen.this);

        });
    }


    private void updateCurrentUserProfileImage(String userPhoto) {
        ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask();
        imageDownloaderTask.execute(userPhoto);
    }


    @Override
    public void refreshUserPinListApi() {
        onResume();
    }

    private void setBadgeCount() {
        mHeaderRightTxt.setText(PreferenceUtil.getStringValue(this, AppConstants.UNREAD_MESSAGE_COUNT));

        if (!PreferenceUtil.getStringValue(this, AppConstants.UNREAD_MESSAGE_COUNT).trim().isEmpty()) {
            if (!PreferenceUtil.getStringValue(this, AppConstants.UNREAD_MESSAGE_COUNT).equalsIgnoreCase("0")) {
                mHeaderRightTxt.setVisibility(View.VISIBLE);
            } else {
                mHeaderRightTxt.setVisibility(View.GONE);
            }
        } else {
            mHeaderRightTxt.setVisibility(View.GONE);

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }

        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID_STR);
        String CHANNEL_HANDLER_ID_Str = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID_Str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AppConstants.PIN_DIALOG_ACTIVITY_RESULT_FLAG) {
            mPinDialogFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS_INT:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                                initGoogleAPIClient();
                            } else {
                                setCurrentLocation();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            // user does not want to update setting. Handle it in a way that it will to affect your app functionality
                            DialogManager.getInstance().showToast(this, getString(R.string.user_not_update));
                            break;
                    }
                    break;
                case REQUEST_CAMERA_INT:
                    if (resultCode == RESULT_OK) {

                        try {
                            AppConstants.IS_CALLED_ALREADY = true;

                            AppConstants.HOME_SCREEN_PHOTO = mPictureFileUri.getPath();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
//                    else {
//                        if (resultCode == RESULT_CANCELED) {
//                            /*Cancelling the image capture process by the user*/
//                        } else {
//                            /*image capture getting failed due to certail technical issues*/
//                        }
//                    }
                    break;
                case REQUEST_GALLERY_INT:
                    if (resultCode == RESULT_OK) {
                        // successfully captured the image
                        // display it in image view
                        Uri selectedImagePath = data.getData();
                        AppConstants.IS_CALLED_ALREADY = false;
                        try {

                            AppConstants.HOME_SCREEN_PHOTO = PathUtils.getPath(this, selectedImagePath);

                            ExifInterface exif = new ExifInterface(AppConstants.HOME_SCREEN_PHOTO);
                            float[] latLong = new float[2];
                            boolean hasLatLong = exif.getLatLong(latLong);
                            if (hasLatLong) {
                                AppConstants.PHOTO_LATITUDE = latLong[0];
                                AppConstants.PHOTO_LONGITUDE = latLong[1];
                                AppConstants.IS_CALLED_ALREADY = true;

                            } else {
                                mIsPhotoLatLonBool = true;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
//                    else {
//                        if (resultCode == RESULT_CANCELED) {
//                            /*Cancelling the image capture process by the user*/
//
//                        } else {
//                            /*image capture getting failed due to certail technical issues*/
//                        }
//                    }
                    break;
            }
        }

    }


}
