package com.ouam.app.ui;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.google.maps.android.clustering.ClusterManager;
import com.ouam.app.R;
import com.ouam.app.adapter.FollowRecommendListAdapter;
import com.ouam.app.chat.ConnectionManager;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceWithTwoArgumentCallBack;
import com.ouam.app.commonInterfaces.RefreshPinListInterface;
import com.ouam.app.entity.ClusterMarkerItem;
import com.ouam.app.entity.PlacePinsEntity;
import com.ouam.app.entity.UserEntity;
import com.ouam.app.entity.UserProfileEntity;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.fragments.BeenThereListFragment;
import com.ouam.app.fragments.HiddenGemListFragment;
import com.ouam.app.fragments.PinDialogFragment;
import com.ouam.app.fragments.ToBeExploredListFragment;
import com.ouam.app.fragments.UserRecentFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.FollowResponse;
import com.ouam.app.model.PlacePinsResponse;
import com.ouam.app.model.RecommendationFollowResponse;
import com.ouam.app.model.UnFollowResponse;
import com.ouam.app.model.UserFollowerResponse;
import com.ouam.app.model.UserFollowingResponse;
import com.ouam.app.model.UserProfileResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AddressUtil;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.ProfileClusterMarkerRender;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.TextUtils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import biz.laenger.android.vpbs.BottomSheetUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfileActivityScreen extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, RefreshPinListInterface {

    @BindView(R.id.profile_parent_lay)
    CoordinatorLayout mProfileParentLay;

    @BindView(R.id.profile_header_lay)
    RelativeLayout mProfileHeaderLay;

    @BindView(R.id.user_bottom_sheet_lay)
    CardView mBottomSheetLay;

    @BindView(R.id.profile_header_right_bottom_img_lay)
    RelativeLayout mProfileHeaderRightBottomImgLay;

    @BindView(R.id.profile_header_right_top_txt)
    TextView mProfileHeaderRightTopTxt;

    @BindView(R.id.user_img)
    RoundedImageView mUserImg;

    @BindView(R.id.user_name_txt)
    TextView mUserNameTxt;

    @BindView(R.id.pin_type_img)
    ImageView mPinTypeImg;

    @BindView(R.id.place_name_txt)
    TextView mPlaceNameTxt;

    @BindView(R.id.drop_down_img)
    ImageView mDropDownImg;

    @BindView(R.id.pin_count_txt)
    TextView mPinCountTxt;

    @BindView(R.id.followers_count_txt)
    TextView mFollowersCountTxt;

    @BindView(R.id.following_count_txt)
    TextView mFollowingCountTxt;

    @BindView(R.id.expandable_lay)
    LinearLayout mExpandableLay;

    @BindView(R.id.bio_txt)
    TextView mBioTxt;

    @BindView(R.id.website_url_txt)
    TextView mWebsiteURLTxt;

    @BindView(R.id.instagram_txt)
    TextView mInstagramTxt;

    @BindView(R.id.user_profile_bottom_screen_tab_lay)
    TabLayout mTabLayout;

    @BindView(R.id.user_status_btn_card_view)
    CardView mUserStatusBtnCardView;

    @BindView(R.id.user_status_btn)
    Button mUserStatusBtn;

    @BindView(R.id.dis_covert_floating_img)
    ImageView mDisCovertFloatingImg;

    @BindView(R.id.un_dis_cover_floating_img)
    ImageView mUnDisCoverFloatingImg;

    @BindView(R.id.hidden_gems_floating_img)
    ImageView mHiddenGemsFloatingImg;

    @BindView(R.id.camera_img)
    ImageView mInstaImg;

    @BindView(R.id.feed_img)
    ImageView mShareImg;

    @BindView(R.id.radial_menu_lay)
    RelativeLayout mRadialMenuLay;

    @BindView(R.id.follow_sug_discrete_scroll_view)
    DiscreteScrollView mFollowSugDiscreteScrollView;

    @BindView(R.id.ask_me_ques_lay)
    RelativeLayout mAskMeQuesLay;

    @BindView(R.id.user_profile_bottom_search_screen_pager)
    ViewPager mViewPager;


    private Animation mFabOpenAnimation, mFabCloseAnimation, mFabRotateForwardAnimation;

    private ImageView mRecentImg, mToBeImg, mBeenThereImg, mHiddenGemImg;
    private TextView mRecentTxt, mToBeTxt, mBeenThereTxt, mHiddenGemTxt;

    /*ClusterManager for the Showing Marker in Map*/
    public ClusterManager<ClusterMarkerItem> mClusterManager;
    /*Cluster Item Entity*/
    public ClusterMarkerItem offSetItem;

    private boolean mIsFabOpenBool = false;

    private String mUserIDStr = "", mTravelerUserIDStr = "";
    private static final int CHANNEL_LIST_LIMIT = 15;
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";


    private RecyclerView.ItemDecoration mRecyclerItemDecorator = new UserProfileActivityScreen.RecyclerItemDecorator();
    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mGoogleMap;

    private final int REQUEST_CHECK_SETTINGS = 300;

    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_user_profile_screen);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUserIDStr.equalsIgnoreCase(mTravelerUserIDStr)) {
            ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, mTravelerUserIDStr,
                    reconnect -> refresh());
        }
    }

    /*InitViews*/
    private void initView() {

        mUserIDStr = PreferenceUtil.getUserId(this);
        mTravelerUserIDStr = AppConstants.PROFILE_USER_ID;

        mHandler = new Handler(getMainLooper());
        ButterKnife.bind(this);

        setHeaderView();

        AppConstants.REFRESH_USER_PIN_LIST_INTERFACE = this;

        getUserPinListApiCall();

        initGoogleAPIClient();

        /*Keypad to be hidden when a touch made outside the edit text*/
        setupUI(mProfileParentLay);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.profile_screen_map);
        if (fragment != null) {
            fragment.getMapAsync(this);
        }

        mFabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        mFabRotateForwardAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);

        mInstaImg.setImageResource(R.drawable.instagram);
        mShareImg.setImageResource(R.drawable.share_map);


        if (!mUserIDStr.isEmpty()) {

            mProfileHeaderRightBottomImgLay.setVisibility(mTravelerUserIDStr.equalsIgnoreCase(mUserIDStr) ? View.VISIBLE : View.GONE);
            mRadialMenuLay.setVisibility(mTravelerUserIDStr.equalsIgnoreCase(mUserIDStr) ? View.VISIBLE : View.GONE);

            setupViewPager(mViewPager);

            mTabLayout.setupWithViewPager(mViewPager);

            initTabHeaderView();

        }

        trackScreenName(getString(R.string.traveler_profile_screen));

    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mProfileHeaderLay.post(() -> mProfileHeaderLay.setPadding(0, getStatusBarHeight(UserProfileActivityScreen.this), 0, 0));
        }
    }

    private void initTabHeaderView() {
        /*set custom tab*/
        View headerView = LayoutInflater.from(this).inflate(R.layout.tab_user_profile_view1, null, false);

        LinearLayout recentParentTabLay, beenThereParentTabLay, toBeParentTabLay, hiddenGemParentTabLay;

        recentParentTabLay = headerView.findViewById(R.id.recent_lay);
        beenThereParentTabLay = headerView.findViewById(R.id.been_there_lay);
        toBeParentTabLay = headerView.findViewById(R.id.to_been_lay);
        hiddenGemParentTabLay = headerView.findViewById(R.id.hidden_gem_lay);

        mRecentTxt = headerView.findViewById(R.id.recent_txt);
        mBeenThereTxt = headerView.findViewById(R.id.been_there_txt);
        mToBeTxt = headerView.findViewById(R.id.to_been_txt);
        mHiddenGemTxt = headerView.findViewById(R.id.hidden_gem_txt);

        mRecentImg = headerView.findViewById(R.id.recent_img);
        mBeenThereImg = headerView.findViewById(R.id.been_there_img);
        mToBeImg = headerView.findViewById(R.id.to_been_img);
        mHiddenGemImg = headerView.findViewById(R.id.hidden_gem_img);

        Objects.requireNonNull(mTabLayout.getTabAt(0)).setCustomView(recentParentTabLay);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setCustomView(beenThereParentTabLay);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setCustomView(toBeParentTabLay);
        Objects.requireNonNull(mTabLayout.getTabAt(3)).setCustomView(hiddenGemParentTabLay);

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

    private void getUserPinListApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + mTravelerUserIDStr + AppConstants.PLACES_URL;
            APIRequestHandler.getInstance().getUserPinListApi(url, UserProfileActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::getUserPinListApiCall);
        }
    }


    private void userFollowerApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + mTravelerUserIDStr + AppConstants.USER_FOLLOWER_URL;
            APIRequestHandler.getInstance().userFollowerApi(url, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::userFollowerApiCall);
        }
    }

    private void userFollowingApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + mTravelerUserIDStr + AppConstants.USER_FOLLOWING_URL;
            APIRequestHandler.getInstance().userFollowingApi(url, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::userFollowingApiCall);
        }
    }

    private void callFollowApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            APIRequestHandler.getInstance().followApi(mTravelerUserIDStr, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::callFollowApi);
        }
    }

    private void unFollowApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            APIRequestHandler.getInstance().unFollowApi(mTravelerUserIDStr, this, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    UnFollowResponse mResponse = (UnFollowResponse) obj;
                    if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                        mAskMeQuesLay.setVisibility(View.GONE);
                        userFollowerApiCall();
                    }
                }

                @Override
                public void onRequestFailure(Throwable r) {
                }
            });
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::unFollowApi);
        }
    }

    private void callUserProfileApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.USER_URL + mTravelerUserIDStr;
            APIRequestHandler.getInstance().userProfileApi(url, UserProfileActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::callUserProfileApi);
        }
    }

    private void recommendationFollowAPI() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            APIRequestHandler.getInstance().recommendationFollowAPI(mUserIDStr, UserProfileActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, this::recommendationFollowAPI);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new UserRecentFragment());
        adapter.addFragment(new BeenThereListFragment());
        adapter.addFragment(new ToBeExploredListFragment());
        adapter.addFragment(new HiddenGemListFragment());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        BottomSheetUtils.setupViewPager(viewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mRecentTxt.setTextColor(position == 0 ? getResources().getColor(R.color.icon_back) : getResources().getColor(R.color.gray));
                mBeenThereTxt.setTextColor(position == 1 ? getResources().getColor(R.color.icon_back) : getResources().getColor(R.color.gray));
                mToBeTxt.setTextColor(position == 2 ? getResources().getColor(R.color.icon_back) : getResources().getColor(R.color.gray));
                mHiddenGemTxt.setTextColor(position == 3 ? getResources().getColor(R.color.icon_back) : getResources().getColor(R.color.gray));

                mRecentImg.setImageResource(position == 0 ? R.drawable.recent_blue : R.drawable.recent_gray);
                mBeenThereImg.setImageResource(position == 1 ? R.drawable.discover_blue : R.drawable.discover_gray);
                mToBeImg.setImageResource(position == 2 ? R.drawable.undiscover_blue : R.drawable.undiscover_gray);
                mHiddenGemImg.setImageResource(position == 3 ? R.drawable.hidden_blue : R.drawable.hidden_gray);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void refreshUserPinListApi() {
        getUserPinListApiCall();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }


    private void setUserDetails(UserProfileEntity userProfileEntity) {
        UserEntity userDetails = userProfileEntity.getUser();

        if (userDetails.getPhoto().isEmpty()) {
            mUserImg.setImageResource(R.color.dark_blue);
        } else {
            try {
                Glide.with(this)
                        .load(userDetails.getPhoto())
                        .into(mUserImg);
            } catch (Exception ex) {
                mUserImg.setImageResource(R.color.dark_blue);
            }
        }

        mUserNameTxt.setText(userDetails.getUsername());
        mPinTypeImg.setImageResource(ImageUtils.userBatchLevelImage(userDetails.getLevel()));
        String userLocation = (userDetails.getCity().getName().isEmpty() ? "" : userDetails.getCity().getName() + ", ") + (userDetails.getCity().getLocality().isEmpty() ? "" : userDetails.getCity().getLocality() + ", ") + (userDetails.getCity().getCountry().isEmpty() ? "" : userDetails.getCity().getCountry());
        mPlaceNameTxt.setText(userLocation);
        mBioTxt.setText(userProfileEntity.getBio());
        mWebsiteURLTxt.setText(userProfileEntity.getUrl());
        mInstagramTxt.setText(userProfileEntity.getInstagram());

        if (mUserIDStr.equalsIgnoreCase(userDetails.getId())) {
            mUserStatusBtn.setText(TextUtils.userBatchLevelStr(this, userDetails.getLevel()));
            mUserStatusBtn.setBackground(getResources().getDrawable(ImageUtils.userBatchLevelBtn(userDetails.getLevel())));
            mUserStatusBtn.setTextColor(getResources().getColor(R.color.white));
        } else {
            mUserStatusBtn.setText(getString(userProfileEntity.isYouFollow() ? R.string.following : R.string.follow).toUpperCase(Locale.US));

            mAskMeQuesLay.setVisibility(userProfileEntity.isYouFollow() ? View.VISIBLE : View.GONE);
            mUserStatusBtn.setBackground(getResources().getDrawable(userProfileEntity.isYouFollow() ? R.drawable.following_btn : R.drawable.follow_btn));
            mUserStatusBtn.setTextColor(getResources().getColor(userProfileEntity.isYouFollow() ? R.color.white : R.color.followed_bg_end_color));
        }

        mUserStatusBtnCardView.setVisibility(View.VISIBLE);
    }


    /*Init Google API clients*/
    private void initGoogleAPIClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @OnClick({R.id.profile_header_left_img_lay, R.id.profile_header_right_bottom_img_lay, R.id.profile_header_right_top_img,
            R.id.ask_me_ques_child_lay, R.id.drop_down_img, R.id.user_status_btn, R.id.dis_covert_floating_img,
            R.id.un_dis_cover_floating_img, R.id.hidden_gems_floating_img, R.id.main_floating_img,
            R.id.feed_img, R.id.close_img, R.id.followers_lay, R.id.following_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_header_left_img_lay:
                onBackPressed();
                break;
            case R.id.profile_header_right_bottom_img_lay:
                if (mTravelerUserIDStr.equalsIgnoreCase(mUserIDStr)) {
                    nextScreen(NotificationActivityScreen.class, true);
                }
                break;
            case R.id.profile_header_right_top_img:
            case R.id.ask_me_ques_child_lay:
                if (mTravelerUserIDStr.equalsIgnoreCase(mUserIDStr)) {
                    nextScreen(ChatListActivityScreen.class, true);
                } else {
                    ArrayList<String> mUserIDList = new ArrayList<>();
//                    mUserIDList.add(mUserIDStr);
                    mUserIDList.add(mTravelerUserIDStr);
                    createGroupChannel(mUserIDList);
                }
                break;
            case R.id.drop_down_img:
                mExpandableLay.setVisibility(mExpandableLay.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                mDropDownImg.setRotation(mExpandableLay.getVisibility() == View.VISIBLE ? 180 : 0);
                break;
            case R.id.user_status_btn:
                if (!mTravelerUserIDStr.equalsIgnoreCase(mUserIDStr)) {
                    if (mUserStatusBtn.getText().toString().equalsIgnoreCase(getString(R.string.follow))) {
                        mUserStatusBtn.setText(getString(R.string.following).toUpperCase(Locale.US));
                        mUserStatusBtn.setBackground(getResources().getDrawable(R.drawable.following_btn));
                        mUserStatusBtn.setTextColor(getResources().getColor(R.color.white));
                        callFollowApi();
                    } else {
                        mUserStatusBtn.setText(getString(R.string.follow).toUpperCase(Locale.US));
                        mUserStatusBtn.setBackground(getResources().getDrawable(R.drawable.follow_btn));
                        mUserStatusBtn.setTextColor(getResources().getColor(R.color.followed_bg_end_color));
                        unFollowApi();
                    }
                }
                break;
            case R.id.dis_covert_floating_img:
                FragmentManager fm = getSupportFragmentManager();
                PinDialogFragment mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_been_there), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String edtTxt, String type) {

                    }
                });
                mPinDialogFragment.show(fm, "fragment_edit_name");
                break;
            case R.id.un_dis_cover_floating_img:
                FragmentManager fm1 = getSupportFragmentManager();
                mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_explore), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String edtTxt, String type) {

                    }
                });
                mPinDialogFragment.show(fm1, "fragment_edit_name");
                break;
            case R.id.hidden_gems_floating_img:
                FragmentManager fm2 = getSupportFragmentManager();
                mPinDialogFragment = PinDialogFragment.newInstance(this, getString(R.string.type_hidden_gem), null, new InterfaceWithTwoArgumentCallBack() {
                    @Override
                    public void withParams(String edtTxt, String type) {

                    }
                });
                mPinDialogFragment.show(fm2, "fragment_edit_name");
                break;
            case R.id.main_floating_img:
                mIsFabOpenBool = !mIsFabOpenBool;
                animateFab(mIsFabOpenBool);
                break;
            case R.id.feed_img:
                nextScreen(ShareMapActivityScreen.class, true);
                break;
            case R.id.followers_lay:
                nextScreen(UserFollowerScreen.class, true);
                break;
            case R.id.following_lay:
                nextScreen(UserFollowingScreen.class, true);
                break;
            case R.id.close_img:
                mAskMeQuesLay.setVisibility(View.GONE);
                break;
        }
    }


    /*Set follow recommendation list adapter into animated discrete scroll view*/
    public void setRecommendationFollowAdapter(ArrayList<UserProfileEntity> followRecommendArrList) {
        mAskMeQuesLay.setVisibility(View.GONE);
        mFollowSugDiscreteScrollView.setVisibility(followRecommendArrList.size() > 0 ? View.VISIBLE : View.GONE);
        mFollowSugDiscreteScrollView.removeItemDecoration(mRecyclerItemDecorator);
        int feedListWidthInt = getScreenWidth() - getResources().getDimensionPixelSize(R.dimen.size60);
        runOnUiThread(() -> {
            InterfaceBtnCallBack userClickedFollowInterfaceBtn = this::recommendationFollowAPI;

            FollowRecommendListAdapter followRecommendListAdapter = new FollowRecommendListAdapter(this, followRecommendArrList, mFollowSugDiscreteScrollView, feedListWidthInt, userClickedFollowInterfaceBtn);

            mFollowSugDiscreteScrollView.setOrientation(DSVOrientation.HORIZONTAL);
            InfiniteScrollAdapter mInfiniteScrollAdapter = InfiniteScrollAdapter.wrap(followRecommendListAdapter);
            mFollowSugDiscreteScrollView.setAdapter(mInfiniteScrollAdapter);
            mFollowSugDiscreteScrollView.setItemTransitionTimeMillis(150);
            mFollowSugDiscreteScrollView.setNestedScrollingEnabled(true);
            mFollowSugDiscreteScrollView.setItemTransformer(new ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build());
            mFollowSugDiscreteScrollView.addItemDecoration(mRecyclerItemDecorator);

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


    private void setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsAccessLocation();
            return;
        }
        if (NetworkUtil.isNetworkAvailable(this)) {
            if (mGoogleMap != null) {

                FusedLocationProviderClient mLastLocation = LocationServices.getFusedLocationProviderClient(this);

                mLastLocation.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        if (ActivityCompat.checkSelfPermission(UserProfileActivityScreen.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(UserProfileActivityScreen.this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            /*Ask for permission on locatio access
                             * Set flag for call back to continue this process*/
                            permissionsAccessLocation();
                        }

                        LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                        AppConstants.CURRENT_LAT = location.getLatitude();
                        AppConstants.CURRENT_LONG = location.getLongitude();

                        mGoogleMap.setMyLocationEnabled(false);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mGoogleMap.getUiSettings().setCompassEnabled(false);
                        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 0));


                    }

                });


            }
        } else {
            /*Alert message will be appeared if there is no internet connection*/
            DialogManager.getInstance().showNetworkErrorPopup(this,
                    this::setCurrentLocation);
        }

    }

    /*Ask permission on Location access*/
    private void permissionsAccessLocation() {
//        boolean addPermission = false;

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
            askAccessPermission(listPermissionsNeeded, 1, new InterfaceTwoBtnCallBack() {
                @Override
                public void onNegativeClick() {
                    if (ActivityCompat.checkSelfPermission(UserProfileActivityScreen.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.checkSelfPermission(UserProfileActivityScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    }
                }

                @Override
                public void onPositiveClick() {
                    setCurrentLocation();

                }
            });
        }
    }

    private void setPinList(PlacePinsEntity response) {

        if (response.getHiddenGems().size() > 0) {
            for (int i = 0; i < response.getHiddenGems().size(); i++) {
                WhoDetailEntity entity = response.getHiddenGems().get(i);
                entity.setPinType(getString(R.string.type_hidden_gem));
            }
        }
        if (response.getToBeExplored().size() > 0) {
            for (int i = 0; i < response.getToBeExplored().size(); i++) {
                WhoDetailEntity entity = response.getToBeExplored().get(i);
                entity.setPinType(getString(R.string.type_explore));
            }
        }
        if (response.getBeenThere().size() > 0) {
            for (int i = 0; i < response.getBeenThere().size(); i++) {
                WhoDetailEntity entity = response.getBeenThere().get(i);
                entity.setPinType(getString(R.string.type_been_there));
            }
        }
        AppConstants.HIDDEN_GEM_PIN_LIST = response.getHiddenGems();
        AppConstants.TO_BE_EXPLORED_PIN_LIST = response.getToBeExplored();
        AppConstants.BEEN_THERE_PIN_LIST = response.getBeenThere();

        setUpClusterManager(response);


        AppConstants.BEEN_THERE_UPDATE_FRAGMENT_INTERFACE.update(response.getBeenThere());
        AppConstants.TO_BE_EXPLORED_UPDATE_FRAGMENT_INTERFACE.update(response.getToBeExplored());
        AppConstants.HIDDEN_GEM_UPDATE_FRAGMENT_INTERFACE.update(response.getHiddenGems());

        mToBeTxt.setText(String.valueOf(AppConstants.TO_BE_EXPLORED_PIN_LIST.size()));
        mBeenThereTxt.setText(String.valueOf(AppConstants.BEEN_THERE_PIN_LIST.size()));
        mHiddenGemTxt.setText(String.valueOf(AppConstants.HIDDEN_GEM_PIN_LIST.size()));

//        mBeenThereImg.setImageResource(AppConstants.BEEN_THERE_PIN_LIST.size() > 0 ? R.drawable.check_icon : R.drawable.check_grey_icon);
//        mToBeImg.setImageResource(AppConstants.TO_BE_EXPLORED_PIN_LIST.size() > 0 ? R.drawable.plane_icon : R.drawable.plane_icon_gray);
//        mHiddenGemImg.setImageResource(AppConstants.HIDDEN_GEM_PIN_LIST.size() > 0 ? R.drawable.gem_icon : R.drawable.gem_icon_gray);

        AppConstants.BEEN_THERE_MAP_STR = String.valueOf(AppConstants.BEEN_THERE_PIN_LIST.size());
        AppConstants.TO_BE_EXPLORE_MAP_STR = String.valueOf(AppConstants.TO_BE_EXPLORED_PIN_LIST.size());
        AppConstants.HIDDEN_GEM_MAP_STR = String.valueOf(AppConstants.HIDDEN_GEM_PIN_LIST.size());
    }


    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof PlacePinsResponse) {
            callUserProfileApi();
            PlacePinsResponse placePinsResponse = (PlacePinsResponse) resObj;
            PlacePinsEntity placePinsEntityRes = placePinsResponse.getWith();
            setPinList(placePinsEntityRes);
            mPinCountTxt.setText(String.valueOf(placePinsEntityRes.getBeenThere().size() + placePinsEntityRes.getHiddenGems().size() + placePinsEntityRes.getToBeExplored().size()));

        } else if (resObj instanceof UserProfileResponse) {
//            hiddengemAPICall(41.8864, -87.6667);
            userFollowerApiCall();
            UserProfileResponse mProfileResponse = (UserProfileResponse) resObj;
            setUserDetails(mProfileResponse.getWith());
        } else if (resObj instanceof UserFollowerResponse) {
            userFollowingApiCall();
            UserFollowerResponse userFollowerResponse = (UserFollowerResponse) resObj;
            mFollowersCountTxt.setText(String.valueOf(userFollowerResponse.getWith().size()));
        } else if (resObj instanceof UserFollowingResponse) {
            UserFollowingResponse userFollowerResponse = (UserFollowingResponse) resObj;
            mFollowingCountTxt.setText(String.valueOf(userFollowerResponse.getWith().size()));
        } else if (resObj instanceof FollowResponse) {
            FollowResponse mResponse = (FollowResponse) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                userFollowerApiCall();
                recommendationFollowAPI();
            }
        } else if (resObj instanceof RecommendationFollowResponse) {
            RecommendationFollowResponse recommendationFollowResponse = (RecommendationFollowResponse) resObj;
            if (recommendationFollowResponse.getStatus().equals(getString(R.string.succeeded))) {
                if (recommendationFollowResponse.getWith().size() > 0)
                    setRecommendationFollowAdapter(recommendationFollowResponse.getWith());
            }
        }

    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsAccessLocation();
            return;
        }

        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
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
        }
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
                        // API call.

                        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                            initGoogleAPIClient();
                        } else {
                            setCurrentLocation();
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(UserProfileActivityScreen.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
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
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }


    /*ClusterManager for the Showing Marker in Map*/
    private void setUpClusterManager(PlacePinsEntity withEntity) {

        if (mGoogleMap != null) {
            mClusterManager = new ClusterManager<>(this, mGoogleMap);

            mGoogleMap.setOnCameraIdleListener(mClusterManager);


            for (int i = 0; i < withEntity.getBeenThere().size(); i++) {
//                LatLng coordinate = new LatLng(withEntity.getBeenThere().get(0).getLat(),
//                        withEntity.getBeenThere().get(0).getLon());

                offSetItem = new ClusterMarkerItem(withEntity.getBeenThere().get(i).getLat(),
                        withEntity.getBeenThere().get(i).getLon(), getString(R.string.type_been_there), withEntity.getBeenThere().get(i).getName(), i);
                mClusterManager.addItem(offSetItem);
//                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15));

            }


            for (int i = 0; i < withEntity.getToBeExplored().size(); i++) {

                offSetItem = new ClusterMarkerItem(withEntity.getToBeExplored().get(i).getLat(),
                        withEntity.getToBeExplored().get(i).getLon(), getString(R.string.type_explore), withEntity.getToBeExplored().get(i).getName(), i);
                mClusterManager.addItem(offSetItem);

            }


            for (int i = 0; i < withEntity.getHiddenGems().size(); i++) {
                offSetItem = new ClusterMarkerItem(withEntity.getHiddenGems().get(i).getLat(),
                        withEntity.getHiddenGems().get(i).getLon(), getString(R.string.type_hidden_gem), withEntity.getHiddenGems().get(i).getName(), i);
                mClusterManager.addItem(offSetItem);

            }

            /*Set the Item Marker Using Cluster*/
            ProfileClusterMarkerRender markerRender = new ProfileClusterMarkerRender(this, mGoogleMap, mClusterManager);
            mClusterManager.setRenderer(markerRender);
            mClusterManager.setAnimation(true);

            mGoogleMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(cluster -> {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        cluster.getPosition(), (float) Math.floor(mGoogleMap
                                .getCameraPosition().zoom + 10)), 1500,
                        null);
                return true;
            });
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker1) {
        Intent intent = new Intent(this, HomeActivityFeedScreen.class);
        startActivity(intent);
        return false;
    }


    @Override
    public void onBackPressed() {
        backScreen(true);
    }


    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds The users to be members of the new channel.
     */
    private void createGroupChannel(List<String> userIds) {
        GroupChannel.createChannelWithUserIds(userIds, true, (groupChannel, e) -> {
            if (e != null) {
                // Error!
                return;
            }
            AppConstants.SEND_BIRD_CHANNER_URL = groupChannel.getUrl();

            for (int i = 0; i < groupChannel.getMembers().size(); i++) {
                if (!mUserIDStr.equalsIgnoreCase(groupChannel.getMembers().get(i).getUserId())) {
                    AppConstants.SEND_BIRD_USER_NAME = groupChannel.getMembers().get(i).getNickname();
                    AppConstants.SEND_BIRD_USER_IMAGE = groupChannel.getMembers().get(i).getProfileUrl();
                    break;
                }
            }

            nextScreen(ChatConversationActivityScreen.class, true);
        });
    }


    private void refresh() {
        mRunnable = () -> {
            refreshChannelList(CHANNEL_LIST_LIMIT);
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
            /*check the unread msg count*/
            for (int i = 0; i < list.size(); i++) {
                count = count + list.get(i).getUnreadMessageCount();
            }
            PreferenceUtil.storeStringValue(UserProfileActivityScreen.this,
                    AppConstants.UNREAD_MESSAGE_COUNT, String.valueOf(count));


            if (!PreferenceUtil.getStringValue(UserProfileActivityScreen.this,
                    AppConstants.UNREAD_MESSAGE_COUNT).trim().isEmpty() && mUserIDStr.equalsIgnoreCase(mTravelerUserIDStr)) {
                if (!PreferenceUtil.getStringValue(UserProfileActivityScreen.this, AppConstants.UNREAD_MESSAGE_COUNT).equalsIgnoreCase("0") && mUserIDStr.equalsIgnoreCase(mTravelerUserIDStr)) {
                    mProfileHeaderRightTopTxt.setVisibility(View.VISIBLE);

                } else {
                    mProfileHeaderRightTopTxt.setVisibility(View.GONE);

                }
            } else {
                mProfileHeaderRightTopTxt.setVisibility(View.GONE);

            }
            mProfileHeaderRightTopTxt.setText(PreferenceUtil.getStringValue(UserProfileActivityScreen.this,
                    AppConstants.UNREAD_MESSAGE_COUNT));


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

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }

        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
    }
}
