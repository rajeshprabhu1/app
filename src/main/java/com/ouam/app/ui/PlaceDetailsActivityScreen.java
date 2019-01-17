package com.ouam.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.DetailsScreenImageViewPagerAdapter;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.RefreshPinListInterface;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.fragments.BeenTherePinFragment;
import com.ouam.app.fragments.PlacePinDialogFragment;
import com.ouam.app.fragments.ToBeenExploredPinFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.AddPinResponse;
import com.ouam.app.model.PlaceDetailsResponse;
import com.ouam.app.model.PlacePinsResponse;
import com.ouam.app.model.RemovePinResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RoundedImageView;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PlaceDetailsActivityScreen extends BaseActivity implements RefreshPinListInterface {

    @BindView(R.id.city_detials_par_lay)
    LinearLayout mCityDetailsParLay;

    @BindView(R.id.place_name_txt)
    TextView mPlaceNameTxt;

    @BindView(R.id.place_location_txt)
    TextView mPlaceLocationTxt;

    @BindView(R.id.place_img)
    ImageView mPlaceImg;

    @BindView(R.id.city_details_pager)
    ViewPager mViewPager;

    @BindView(R.id.city_details_tab_lay)
    TabLayout mTabLayout;

//    @BindView(R.id.pin_img_lay)
//    RelativeLayout mPinImgLay;
//
//    @BindView(R.id.post_img_lay)
//    RelativeLayout mPostImgLay;

//    @BindView(R.id.pin_type_img)
//    ImageView mPinTypeImg;

    @BindView(R.id.user_img)
    RoundedImageView mUserImg;

    @BindView(R.id.user_img1)
    RoundedImageView mUserImg1;

    @BindView(R.id.user_img2)
    RoundedImageView mUserImg2;

    @BindView(R.id.user_count_txt)
    TextView mUserCountTxt;


    @BindView(R.id.top_pin_type_img)
    ImageView mTopPinTypeImg;

    @BindView(R.id.details_header_lay)
    RelativeLayout mHeaderLay;

    @BindView(R.id.details_screen_view_pager_lay)
    RelativeLayout mViewPagerLay;

    @BindView(R.id.user_img_lay)
    RelativeLayout mUserImgLay;

    @BindView(R.id.user_img1_lay)
    RelativeLayout mUserImg1Lay;

    @BindView(R.id.user_img2_lay)
    RelativeLayout mUserImg2Lay;

    @BindView(R.id.user_img3_lay)
    RelativeLayout mUserImg3Lay;

    @BindView(R.id.city_place_details_view_pager)
    ViewPager mImageViewPager;

    @BindView(R.id.page_indicator_view)
    PageIndicatorView mPageIndicatorView;

    @BindView(R.id.dis_covert_floating_img)
    ImageView mDisCovertFloatingImg;

    @BindView(R.id.un_dis_cover_floating_img)
    ImageView mUnDisCoverFloatingImg;

    @BindView(R.id.hidden_gems_floating_img)
    ImageView mHiddenGemsFloatingImg;

    @BindView(R.id.main_floating_img)
    ImageView mMainFloatingImg;

    private WithEntity mWithEntity = new WithEntity();

    DetailsScreenImageViewPagerAdapter mImageViewPagerAdapter;

    LinearLayout mEmptyInnerLay;

    public TextView mActivityTxt, mDetailsTxt, mEmptyTxt, mToBeTxt, mBeenThereTxt, mHiddenGemTxt;
    private ImageView mToBeImg, mBeenThereImg, mHiddenGemImg;
    RelativeLayout mActivityMarkerLay, mDetailsMarkerLay, mEmptyMarkerLay, mToBeLay, mBeenThereLay, mHiddenGemLay, mActivityLay, mDetailsLay, mEmptyLay, mActivityInnerLay, mDetailsInnerLay, mToBeMarkerLay, mBeenThereMarkerLay, mHiddenGemMarkerLay;
    LinearLayout mToBeenBgLay, mBeenThereBgLay, mHiddenGemBgLay;
    private Animation mFabOpenAnimation, mFabCloseAnimation, mFabRotateForwardAnimation, mFeedBottomToTopAnimation, mFeedTopToBottomAnimation;
    private boolean mIsFabOpenBool = false;

    private PlacePinDialogFragment mPinDialogFragment;
    private int mDiscoverListSizeInt = 0, mUnDiscoverListSizeInt = 0;
    private ArrayList<String> mDiscoverImgList = new ArrayList<>(), mUnDiscoverImgList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_place_detail_screen);
        initView();
    }


    /*InitViews*/
    private void initView() {
        ButterKnife.bind(this);

        setupUI(mCityDetailsParLay);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        /*set user image*/
        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(this, AppConstants.USER_DETAILS);
        UserDetailsEntity userDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);

        if (userDetailsEntityRes.getImage().isEmpty()) {
//            mUserImg.setImageResource(R.drawable.user_demo_icon);
        } else {
            try {

//                Glide.with(this)
//                        .load(userDetailsEntityRes.getImage())
//                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
//                        .into(mUserImg);
            } catch (Exception ex) {
//                mUserImg.setImageResource(R.drawable.user_demo_icon);
            }
        }

//        mHeaderLay.setBackgroundColor(getColorWithAlpha(Color.WHITE, 0.8f));
        if (!AppConstants.pinType.equalsIgnoreCase("none")) {
            setAddPinPlace(AppConstants.pinType.equalsIgnoreCase("BeenThere") ? getString(R.string.type_been_there) : AppConstants.pinType.equalsIgnoreCase("ToBeExplored") ? getString(R.string.type_explore) : getString(R.string.type_hidden_gem));
        }

        mViewPagerLay.setVisibility(View.VISIBLE);
        mPlaceImg.setVisibility(View.GONE);


        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicatorView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initTabHeaderview();

        callPlaceDetailsAPI();

        placeEmptyApiCall();

        mFabOpenAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabCloseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        mFabRotateForwardAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);

        mFeedBottomToTopAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_in_bottom);
        mFeedTopToBottomAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_in_top);

        trackScreenName(getString(R.string.place_details_screen));

    }


    /*initialise tab views*/
    private void initTabHeaderview() {

        /*set custom tab*/
        View headerView = LayoutInflater.from(this).inflate(R.layout.tab_details_view, null, false);

        mActivityLay = headerView.findViewById(R.id.activity_par_lay);
        mDetailsLay = headerView.findViewById(R.id.details_par_lay);

        mActivityInnerLay = headerView.findViewById(R.id.activity_inner_lay);
        mDetailsInnerLay = headerView.findViewById(R.id.details_inner_lay);

        mActivityMarkerLay = headerView.findViewById(R.id.activity_marker_lay);
        mDetailsMarkerLay = headerView.findViewById(R.id.details_marker_lay);

        mActivityTxt = headerView.findViewById(R.id.activity_txt);
        mDetailsTxt = headerView.findViewById(R.id.details_txt);

        mBeenThereLay = headerView.findViewById(R.id.been_there_lay);
        mToBeLay = headerView.findViewById(R.id.to_been_lay);
        mHiddenGemLay = headerView.findViewById(R.id.hidden_gem_lay);


        mBeenThereTxt = headerView.findViewById(R.id.been_there_txt);
        mToBeTxt = headerView.findViewById(R.id.to_been_txt);

        mBeenThereBgLay = headerView.findViewById(R.id.been_there_inner_lay);
        mToBeenBgLay = headerView.findViewById(R.id.to_been_inner_lay);
        mHiddenGemBgLay = headerView.findViewById(R.id.hidden_gem_inner_lay);


        mActivityTxt.setText(getString(R.string.activity));
        mDetailsTxt.setText(getString(R.string.details));

        Objects.requireNonNull(mTabLayout.getTabAt(0)).setCustomView(mBeenThereLay);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setCustomView(mToBeLay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHeaderLay.post(new Runnable() {
                public void run() {
                    mHeaderLay.setPadding(0, getStatusBarHeight(PlaceDetailsActivityScreen.this), 0, 0);

                }
            });
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new BeenTherePinFragment());
        adapter.addFragment(new ToBeenExploredPinFragment());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mBeenThereBgLay.setBackgroundResource(position == 0 ? R.color.discovered_bg_start_color : R.color.white);
                mToBeenBgLay.setBackgroundResource(position == 1 ? R.color.un_discovered_bg_start_color : R.color.white);

                mBeenThereTxt.setTextColor(position == 0 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.discovered_bg_start_color));
                mToBeTxt.setTextColor(position == 1 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.un_discovered_bg_start_color));

                mMainFloatingImg.setImageResource(position == 1 ? R.drawable.undiscovered_map_pin : R.drawable.master_pin);
                mMainFloatingImg.setClickable(position != 1);
                mActivityMarkerLay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                mDetailsMarkerLay.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                setImageList(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void refreshUserPinListApi() {
//        AppConstants.UPDATE_ACTIVITY_INTERFACE.onPositiveClick();
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

    @OnClick({R.id.down_img, R.id.top_pin_type_img, R.id.dis_covert_floating_img, R.id.un_dis_cover_floating_img, R.id.hidden_gems_floating_img, R.id.main_floating_img})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.down_img:
                onBackPressed();
                break;
            case R.id.top_pin_type_img:
                WithEntity mwithEntity= null;
                DialogManager.getInstance().showPlaceDetailsPopup(this, mWithEntity);
                break;
            case R.id.dis_covert_floating_img:
                placePinApiCall(getString(R.string.type_been_there));
                FragmentManager fm = getSupportFragmentManager();
                mPinDialogFragment = PlacePinDialogFragment.newInstance(this, getString(R.string.type_been_there), null);
                mPinDialogFragment.show(fm, "fragment_edit_name");
                break;
            case R.id.un_dis_cover_floating_img:
                placePinApiCall(getString(R.string.type_explore));
                FragmentManager fm1 = getSupportFragmentManager();
                mPinDialogFragment = PlacePinDialogFragment.newInstance(this, getString(R.string.type_explore), null);
                mPinDialogFragment.show(fm1, "fragment_edit_name");
                break;
            case R.id.hidden_gems_floating_img:
                placePinApiCall(getString(R.string.type_hidden_gem));
                FragmentManager fm2 = getSupportFragmentManager();
                mPinDialogFragment = PlacePinDialogFragment.newInstance(this, getString(R.string.type_hidden_gem), null);
                mPinDialogFragment.show(fm2, "fragment_edit_name");
                break;
            case R.id.main_floating_img:
                mIsFabOpenBool = !mIsFabOpenBool;
                animateFab(mIsFabOpenBool);
                break;
        }

    }

    /*Call Place Details Api*/
    private void callPlaceDetailsAPI() {
        if (NetworkUtil.isNetworkAvailable(this)) {
//            AppConstants.PLACE_NAME = "6c87e1d1171dbe776e6861cdf6ca4aa3e31634be23900f644b7ee4d96d8891b7%7Cfs%3A4b6f20dcf964a520f3de2ce3";
            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME;
            APIRequestHandler.getInstance().placeDetailsApiCall(url, PlaceDetailsActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    callPlaceDetailsAPI();
                }
            });
        }
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof PlaceDetailsResponse) {
            PlaceDetailsResponse mResponse = (PlaceDetailsResponse) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                mWithEntity = mResponse.getWith();
                mWithEntity.setPhotoUrl(mResponse.getWith().getPhotoUrl());
                mWithEntity.setCategory(mResponse.getWith().getCategory());

                mImageViewPagerAdapter = new DetailsScreenImageViewPagerAdapter(PlaceDetailsActivityScreen.this, mWithEntity.getPhotos());
                mImageViewPager.setAdapter(mImageViewPagerAdapter);
                mPageIndicatorView.setViewPager(mImageViewPager);

                if (mResponse.getWith().getBestPhoto().isEmpty()) {
                    mPlaceImg.setImageResource(R.drawable.profile_bg);
                } else {
                    try {

                        Glide.with(this)
                                .load(mResponse.getWith().getBestPhoto())
                                .apply(new RequestOptions().error(R.drawable.profile_bg))
                                .into(mPlaceImg);
                    } catch (Exception ex) {
                        mUserImg.setImageResource(R.drawable.profile_bg);
                    }
                }

                mPlaceNameTxt.setText(mResponse.getWith().getName());
                mPlaceLocationTxt.setText(mResponse.getWith().getCity().getName().isEmpty() ? "" : mResponse.getWith().getCity().getName() + (mResponse.getWith().getCity().getLocality().isEmpty() ? "" : mResponse.getWith().getCity().getLocality() + " , ") + (mResponse.getWith().getCity().getCountry().isEmpty() ? "" : mResponse.getWith().getCity().getCountry() + "."));
            }
        }


        if (resObj instanceof PlacePinsResponse) {
            PlacePinsResponse placePinsResponse = (PlacePinsResponse) resObj;
            mDiscoverListSizeInt = placePinsResponse.getWith().getBeenThere().size();
            mUnDiscoverListSizeInt = placePinsResponse.getWith().getToBeExplored().size();
            mDiscoverImgList = new ArrayList<>();
            mUnDiscoverImgList = new ArrayList<>();

            int discoverListInt = 0, unDiscoverListInt = 0;
            for (int i = 0; i < placePinsResponse.getWith().getBeenThere().size(); i++) {

                if (!placePinsResponse.getWith().getBeenThere().get(i).getPhoto().isEmpty()) {
                    discoverListInt += 1;
                    mDiscoverImgList.add(placePinsResponse.getWith().getBeenThere().get(i).getPhoto());
                }

                if (discoverListInt >= 3)
                    break;
            }

            for (int i = 0; i < placePinsResponse.getWith().getToBeExplored().size(); i++) {
                if (!placePinsResponse.getWith().getToBeExplored().get(i).getPhoto().isEmpty()) {
                    unDiscoverListInt += 1;
                    mUnDiscoverImgList.add(placePinsResponse.getWith().getToBeExplored().get(i).getPhoto());
                }

                if (unDiscoverListInt >= 3)
                    break;
            }

            setImageList(0);
            AppConstants.HIDDEN_GEM_PIN_LIST = placePinsResponse.getWith().getHiddenGems();
            AppConstants.TO_BE_EXPLORED_PIN_LIST = placePinsResponse.getWith().getToBeExplored();
            AppConstants.BEEN_THERE_PIN_LIST = placePinsResponse.getWith().getBeenThere();

            AppConstants.BEEN_THERE_UPDATE_FRAGMENT_INTERFACE.update(placePinsResponse.getWith().getBeenThere());
            AppConstants.TO_BE_EXPLORED_UPDATE_FRAGMENT_INTERFACE.update(placePinsResponse.getWith().getToBeExplored());

        }
    }

    private void setAddPinPlace(final String pinType) {
        mWithEntity.setPinType(pinType);
        mWithEntity.setId(AppConstants.PLACE_NAME);

    }

    private void setImageList(int firstPostInt) {
        ArrayList<String> imgStrArrList = firstPostInt == 0 ? mDiscoverImgList : mUnDiscoverImgList;

        int valInt = (firstPostInt == 0 ? mDiscoverListSizeInt : mUnDiscoverListSizeInt) - imgStrArrList.size();
        mUserCountTxt.setText("+" + String.valueOf(valInt));
        mUserCountTxt.setVisibility(valInt > 0 ? View.VISIBLE : View.GONE);
        mUserImgLay.setVisibility(imgStrArrList.size() > 0 ? View.VISIBLE : View.INVISIBLE);

        if (imgStrArrList.size() > 0) {
            if (imgStrArrList.get(0) != null) {
                mUserImg1Lay.setVisibility(View.VISIBLE);

                try {
                    Glide.with(this)
                            .load(imgStrArrList.get(0))
                            .apply(new RequestOptions().error(R.drawable.profile_bg))
                            .into(mUserImg);
                } catch (Exception ex) {
                    mUserImg.setImageResource(R.drawable.profile_bg);
                }
            } else if(imgStrArrList.get(0) == null){
                mUserImg1Lay.setVisibility(View.GONE);
            }

            if (imgStrArrList.get(1) != null) {
                mUserImg2Lay.setVisibility(View.VISIBLE);

                try {
                    Glide.with(this)
                            .load(imgStrArrList.get(1))
                            .apply(new RequestOptions().error(R.drawable.profile_bg))
                            .into(mUserImg1);
                } catch (Exception ex) {
                    mUserImg1.setImageResource(R.drawable.profile_bg);
                }
            }
            else if(imgStrArrList.get(1) == null){
                mUserImg2Lay.setVisibility(View.GONE);
            }
            if (imgStrArrList.get(2) != null) {
                mUserImg3Lay.setVisibility(View.VISIBLE);

                try {
                    Glide.with(this)
                            .load(imgStrArrList.get(2))
                            .apply(new RequestOptions().error(R.drawable.profile_bg))
                            .into(mUserImg2);
                } catch (Exception ex) {
                    mUserImg2.setImageResource(R.drawable.profile_bg);
                }
            }
            else if(imgStrArrList.get(2) == null){
                mUserImg3Lay.setVisibility(View.GONE);
            }
        }


    }

    /*Place Pin Api Call*/
    private void placePinApiCall(String pinType) {
        if (NetworkUtil.isNetworkAvailable(this)) {

            AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

            addPinInputEntity.setPinType(pinType.equals(getString(R.string.type_been_there)) ? AppConstants.BEEN_THERE_PIN : pinType.equals(getString(R.string.type_explore)) ? AppConstants.TO_BE_EXPLORED_PIN : AppConstants.HIDDEN_GEM_PIN);

            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME + AppConstants.PIN_URL;

            APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, this, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    if (obj instanceof AddPinResponse) {
                        AddPinResponse mResponse = (AddPinResponse) obj;
                        if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                            DialogManager.getInstance().showToast(PlaceDetailsActivityScreen.this, String.valueOf(mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe()));
                            setAddPinPlace(pinType);
                            placeEmptyApiCall();
                        }
                    }
                }

                @Override
                public void onRequestFailure(Throwable r) {

                }
            });

        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    placePinApiCall(pinType);
                }
            });
        }
    }

    /*Delete Pin Api Call*/
    private void removePinApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {

            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME + AppConstants.PIN_URL;

            APIRequestHandler.getInstance().deletePinApi(url, PlaceDetailsActivityScreen.this, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    if (obj instanceof RemovePinResponse) {
                        RemovePinResponse mResponse = (RemovePinResponse) obj;

                        if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                            DialogManager.getInstance().showToast(PlaceDetailsActivityScreen.this, String.valueOf(mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe()));
//                            mTopPinTypeImg.setImageResource(R.color.transparent);
//                            mPostImgLay.setVisibility(View.GONE);
//                            mPinImgLay.setVisibility(View.VISIBLE);
//                            mPinTypeImg.setImageResource(R.color.transparent);
                            placeEmptyApiCall();
                        }
                    }
                }

                @Override
                public void onRequestFailure(Throwable r) {

                }
            });
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    removePinApi();
                }
            });
        }
    }

    private void placeEmptyApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME + AppConstants.PINS_URL;

            APIRequestHandler.getInstance().placePinsApi(url, PlaceDetailsActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    placeEmptyApiCall();
                }
            });
        }
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
    public void onBackPressed() {
        backScreen(true);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPinDialogFragment != null)
            mPinDialogFragment.onActivityResult(requestCode, resultCode, data);
    }

}
