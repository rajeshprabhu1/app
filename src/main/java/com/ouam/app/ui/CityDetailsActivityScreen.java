package com.ouam.app.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.CitiesWithEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.fragments.BeenTherePinFragment;
import com.ouam.app.fragments.CitiesRecentFragment;
import com.ouam.app.fragments.CitiesTrendingPlaceFragment;
import com.ouam.app.fragments.HiddenPinFragment;
import com.ouam.app.fragments.PinDialogFragment;
import com.ouam.app.fragments.ToBeenExploredPinFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.AddPinResponse;
import com.ouam.app.model.CitiesDetailsResponse;
import com.ouam.app.model.CitiesPinsResponse;
import com.ouam.app.model.RemovePinResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CityDetailsActivityScreen extends BaseActivity {

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

    @BindView(R.id.pin_type_img)
    ImageView mPinTypeImg;

    @BindView(R.id.user_img)
    RoundedImageView mUserImg;

    @BindView(R.id.top_pin_type_img)
    ImageView mTopPinTypeImg;

    @BindView(R.id.details_header_lay)
    RelativeLayout mHeaderLay;

    @BindView(R.id.details_screen_view_pager_lay)
    RelativeLayout mViewPagerLay;

    @BindView(R.id.city_place_details_view_pager)
    ViewPager mImageViewPager;

    LinearLayout mEmptyInnerLay;

    private CitiesWithEntity mWithEntity = new CitiesWithEntity();

    public TextView mActivityTxt, mDetailsTxt, mEmptyTxt, mToBeTxt, mBeenThereTxt, mHiddenGemTxt;
    private ImageView mToBeImg, mBeenThereImg, mHiddenGemImg;
    private RelativeLayout mActivityMarkerLay, mDetailsMarkerLay, mToBeLay, mBeenThereLay, mHiddenGemLay, mEmptyMarkerLay, mActivityLay, mDetailsLay, mEmptyLay, mActivityInnerLay, mDetailsInnerLay, mToBeMarkerLay, mBeenThereMarkerLay, mHiddenGemMarkerLay;
    private LinearLayout mToBeenBgLay, mBeenThereBgLay, mHiddenGemBgLay;

    private PinDialogFragment mPinDialogFragment;


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
        mHeaderLay.setBackgroundColor(getColorWithAlpha(Color.WHITE, 0.8f));
        if (userDetailsEntityRes.getImage().isEmpty()) {
            mUserImg.setImageResource(R.drawable.user_demo_icon);
        } else {
            try {
                Glide.with(this)
                        .load(userDetailsEntityRes.getImage())
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                        .into(mUserImg);
            } catch (Exception ex) {
                mUserImg.setImageResource(R.drawable.user_demo_icon);
            }
        }

        mViewPagerLay.setVisibility(View.GONE);
        mPlaceImg.setVisibility(View.VISIBLE);

        initTabHeaderview();

        citiesDetailsAPICall();

        citiesPinsApiCall();

        trackScreenName(getString(R.string.city_details_screen));

    }


    /*initialise tab views*/
    private void initTabHeaderview() {

        /*set custom tab*/
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.tab_details_view, null, false);

        mActivityLay = headerView.findViewById(R.id.activity_par_lay);
        mDetailsLay = headerView.findViewById(R.id.details_par_lay);
        //  mEmptyLay = headerView.findViewById(R.id.empty_par_lay);

        mActivityInnerLay = headerView.findViewById(R.id.activity_inner_lay);
        mDetailsInnerLay = headerView.findViewById(R.id.details_inner_lay);

        mActivityMarkerLay = headerView.findViewById(R.id.activity_marker_lay);
        mDetailsMarkerLay = headerView.findViewById(R.id.details_marker_lay);
//        mEmptyMarkerLay = headerView.findViewById(R.id.empty_marker_lay);

        mBeenThereLay = headerView.findViewById(R.id.been_there_lay);
        mToBeLay = headerView.findViewById(R.id.to_been_lay);
        mHiddenGemLay = headerView.findViewById(R.id.hidden_gem_lay);

        mBeenThereImg = headerView.findViewById(R.id.been_there_img);
        mToBeImg = headerView.findViewById(R.id.to_been_img);
        mHiddenGemImg = headerView.findViewById(R.id.hidden_gem_img);

        mBeenThereTxt = headerView.findViewById(R.id.been_there_txt);
        mToBeTxt = headerView.findViewById(R.id.to_been_txt);
        mHiddenGemTxt = headerView.findViewById(R.id.hidden_gem_txt);

        mBeenThereMarkerLay = headerView.findViewById(R.id.been_there_marker_lay);
        mToBeMarkerLay = headerView.findViewById(R.id.to_been_marker_lay);
        mHiddenGemMarkerLay = headerView.findViewById(R.id.hidden_gem_marker_lay);

        mBeenThereBgLay = headerView.findViewById(R.id.been_there_inner_lay);
        mToBeenBgLay = headerView.findViewById(R.id.to_been_inner_lay);
        mHiddenGemBgLay = headerView.findViewById(R.id.hidden_gem_inner_lay);

        mBeenThereImg.setImageResource(R.drawable.check_grey_icon);
        mToBeImg.setImageResource(R.drawable.plane_icon_gray);
        mHiddenGemImg.setImageResource(R.drawable.gem_icon_gray);

        mActivityTxt = headerView.findViewById(R.id.activity_txt);
        mDetailsTxt = headerView.findViewById(R.id.details_txt);
//        mEmptyTxt = headerView.findViewById(R.id.empty_txt);

        mActivityTxt.setText(getString(R.string.activity));
        mDetailsTxt.setText(getString(R.string.trending_places));

//        mEmptyInnerLay = headerView.findViewById(R.id.empty_inner_lay);

        mTabLayout.getTabAt(0).setCustomView(mActivityLay);
        mTabLayout.getTabAt(1).setCustomView(mDetailsLay);
        mTabLayout.getTabAt(2).setCustomView(mBeenThereLay);
        mTabLayout.getTabAt(3).setCustomView(mToBeLay);
        mTabLayout.getTabAt(4).setCustomView(mHiddenGemLay);


    }


    private void setupViewPager(ViewPager viewPager) {
        CityDetailsActivityScreen.ViewPagerAdapter adapter = new CityDetailsActivityScreen.ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new CitiesRecentFragment());
        adapter.addFragment(new CitiesTrendingPlaceFragment());
        adapter.addFragment(new BeenTherePinFragment());
        adapter.addFragment(new ToBeenExploredPinFragment());
        adapter.addFragment(new HiddenPinFragment());


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mActivityInnerLay.setBackgroundResource(position == 0 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                mDetailsInnerLay.setBackgroundResource(position == 1 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                //   mEmptyInnerLay.setImageResource(position == 2 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                mBeenThereBgLay.setBackgroundResource(position == 2 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                mToBeenBgLay.setBackgroundResource(position == 3 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                mHiddenGemBgLay.setBackgroundResource(position == 4 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);

                mActivityTxt.setTextColor(position == 0 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.gray));
                mDetailsTxt.setTextColor(position == 1 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.gray));
                // mEmptyTxt.setTextColor(position == 2 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.gray));
                mBeenThereTxt.setTextColor(position == 2 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));
                mToBeTxt.setTextColor(position == 3 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));
                mHiddenGemTxt.setTextColor(position == 4 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));

                mActivityMarkerLay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                mDetailsMarkerLay.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                //  mEmptyMarkerLay.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                mBeenThereMarkerLay.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
                mToBeMarkerLay.setVisibility(position == 3 ? View.VISIBLE : View.GONE);
                mHiddenGemMarkerLay.setVisibility(position == 4 ? View.VISIBLE : View.GONE);

//                mBeenThereImg.setImageResource(position == 2 ? R.drawable.check_icon : R.drawable.check_grey_icon);
//                mToBeImg.setImageResource(position == 3 ? R.drawable.plane_icon : R.drawable.plane_icon_gray);
//                mHiddenGemImg.setImageResource(position == 4 ? R.drawable.gem_icon : R.drawable.gem_icon_gray);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

    public void citiesDetailsAPICall() {
        if (NetworkUtil.isNetworkAvailable(CityDetailsActivityScreen.this)) {
            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME;
            APIRequestHandler.getInstance().citiesDetailsApi(url, CityDetailsActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    citiesDetailsAPICall();
                }
            });
        }
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof CitiesDetailsResponse) {
            CitiesDetailsResponse cityDetailsResponse = (CitiesDetailsResponse) resObj;
            setData(cityDetailsResponse.getWith());
            mWithEntity = cityDetailsResponse.getWith();
        }


        if (resObj instanceof CitiesPinsResponse) {
            CitiesPinsResponse citiesPinsResponse = (CitiesPinsResponse) resObj;

            if (citiesPinsResponse.getWith().getHiddenGems().size() > 0) {
                for (int i = 0; i < citiesPinsResponse.getWith().getHiddenGems().size(); i++) {
                    WhoDetailEntity entity = citiesPinsResponse.getWith().getHiddenGems().get(i);
                    entity.setPinType(getString(R.string.type_hidden_gem));
                }
            }
            if (citiesPinsResponse.getWith().getToBeExplored().size() > 0) {
                for (int i = 0; i < citiesPinsResponse.getWith().getToBeExplored().size(); i++) {
                    WhoDetailEntity entity = citiesPinsResponse.getWith().getToBeExplored().get(i);
                    entity.setPinType(getString(R.string.type_explore));
                }
            }
            if (citiesPinsResponse.getWith().getBeenThere().size() > 0) {
                for (int i = 0; i < citiesPinsResponse.getWith().getBeenThere().size(); i++) {
                    WhoDetailEntity entity = citiesPinsResponse.getWith().getBeenThere().get(i);
                    entity.setPinType(getString(R.string.type_been_there));
                }
            }
            AppConstants.HIDDEN_GEM_PIN_LIST = citiesPinsResponse.getWith().getHiddenGems();
            AppConstants.TO_BE_EXPLORED_PIN_LIST = citiesPinsResponse.getWith().getToBeExplored();
            AppConstants.BEEN_THERE_PIN_LIST = citiesPinsResponse.getWith().getBeenThere();

            AppConstants.BEEN_THERE_UPDATE_FRAGMENT_INTERFACE.update(citiesPinsResponse.getWith().getBeenThere());
            AppConstants.TO_BE_EXPLORED_UPDATE_FRAGMENT_INTERFACE.update(citiesPinsResponse.getWith().getToBeExplored());
            AppConstants.HIDDEN_GEM_UPDATE_FRAGMENT_INTERFACE.update(citiesPinsResponse.getWith().getHiddenGems());

//            for (int i = 0; i < citiesPinsResponse.getWith().getBeenThere().size(); i++) {
//                citiesPinsResponse.getWith().getBeenThere().get(i).setPinType(getString(R.string.type_been_there));
//                mWhoDetailsList.add(citiesPinsResponse.getWith().getBeenThere().get(i));
//            }
//
//            for (int i = 0; i < citiesPinsResponse.getWith().getToBeExplored().size(); i++) {
//                citiesPinsResponse.getWith().getToBeExplored().get(i).setPinType(getString(R.string.type_explore));
//                mWhoDetailsList.add(citiesPinsResponse.getWith().getToBeExplored().get(i));
//            }
//
//            for (int i = 0; i < citiesPinsResponse.getWith().getHiddenGems().size(); i++) {
//                citiesPinsResponse.getWith().getHiddenGems().get(i).setPinType(getString(R.string.type_hidden_gem));
//                mWhoDetailsList.add(citiesPinsResponse.getWith().getHiddenGems().get(i));
//            }
        }
        mToBeTxt.setText(String.valueOf(AppConstants.TO_BE_EXPLORED_PIN_LIST.size()));
        mBeenThereTxt.setText(String.valueOf(AppConstants.BEEN_THERE_PIN_LIST.size()));
        mHiddenGemTxt.setText(String.valueOf(AppConstants.HIDDEN_GEM_PIN_LIST.size()));

        mBeenThereImg.setImageResource(AppConstants.BEEN_THERE_PIN_LIST.size() > 0 ? R.drawable.check_icon : R.drawable.check_grey_icon);
        mToBeImg.setImageResource(AppConstants.TO_BE_EXPLORED_PIN_LIST.size() > 0 ? R.drawable.plane_icon : R.drawable.plane_icon_gray);
        mHiddenGemImg.setImageResource(AppConstants.HIDDEN_GEM_PIN_LIST.size() > 0 ? R.drawable.gem_icon : R.drawable.gem_icon_gray);

    }

    private void setData(CitiesWithEntity with) {
        if (with != null) {
            mWithEntity.setName(with.getName().isEmpty() ? "" : with.getName());
            mWithEntity.setAddress(with.getLocality().isEmpty() ? "" : with.getLocality() + "," + (with.getCountry().isEmpty() ? "" : with.getCountry()));
            mWithEntity.setPhotoUrl(with.getPhotoUrl() != null ? with.getPhotoUrl() : "");
            mWithEntity.setCategory(with.getCategory() != null ? with.getCategory() : "");

            if (!with.getPinnedAs().equalsIgnoreCase("None")) {
                mWithEntity.setPinType(with.getPinnedAs().equalsIgnoreCase("BeenThere") ? getString(R.string.type_been_there) : with.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? getString(R.string.type_explore) : getString(R.string.type_hidden_gem));
                setAddPinCities(with.getPinnedAs().equalsIgnoreCase("BeenThere") ? getString(R.string.type_been_there) : with.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? getString(R.string.type_explore) : getString(R.string.type_hidden_gem));
            }


            mPlaceNameTxt.setText(with.getName().isEmpty() ? "" : with.getName());
            mPlaceLocationTxt.setText(with.getLocality().isEmpty() ? "" : with.getLocality() + "," + (with.getCountry().isEmpty() ? "" : with.getCountry()));

            if (with.getPhoto().getUrl() != null) {
                if (with.getPhoto().getUrl().isEmpty()) {
                    mPlaceImg.setImageResource(R.color.dark_blue);
                } else {
                    try {
                        Glide.with(CityDetailsActivityScreen.this)
                                .load(with.getPhoto().getUrl())
                                .into(mPlaceImg);
                    } catch (Exception ex) {
                        mPlaceImg.setImageResource(R.color.dark_blue);
                        Log.e(AppConstants.TAG, ex.getMessage());
                    }
                }
            }
        }
    }

    @OnClick({R.id.down_img, R.id.top_pin_type_img})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.down_img:
                onBackPressed();
                break;
//            case R.id.been_There:
//                citiesAddPinApiCall(getString(R.string.type_been_there));
//                break;
//            case R.id.to_Be_Explored:
//                citiesAddPinApiCall(getString(R.string.type_explore));
//                break;
//            case R.id.hidden_Gems:
//                citiesAddPinApiCall(getString(R.string.type_hidden_gem));
//                break;
            case R.id.top_pin_type_img:
                DialogManager.getInstance().showOptionPopup(this, getString(R.string.pin_type_delete_msg), getString(R.string.delete), getString(R.string.no), new InterfaceTwoBtnCallBack() {
                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onPositiveClick() {
                        removePinApi();

                    }
                });
                break;
//            case R.id.post_img_lay:
//                FragmentManager fm = getSupportFragmentManager();
//                if (!mWithEntity.getPinType().isEmpty()) {
//                    mPinDialogFragment = PinDialogFragment.newInstance(mWithEntity.getPinType(), mWithEntity);
//                    editNameDialogFragment.show(fm, "fragment_edit_name");
//                } else {
//                    mPinDialogFragment = PinDialogFragment.newInstance(mWithEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? getString(R.string.type_been_there) : mWithEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? getString(R.string.type_explore) : getString(R.string.type_hidden_gem), mWithEntity);
//                    editNameDialogFragment.show(fm, "fragment_edit_name");
//
//                }
//                break;
        }

    }


    private void setAddPinCities(final String pinType) {
        mWithEntity.setPinType(pinType);
        mWithEntity.setId(AppConstants.CITY_NAME);

//        mPinImgLay.setVisibility(View.GONE);
//        mPostImgLay.setVisibility(View.VISIBLE);

        mPinTypeImg.setImageResource(pinType.equals(getString(R.string.type_been_there)) ? R.drawable.been_there_icon : pinType.equals(getString(R.string.type_explore)) ? R.drawable.to_be_explored_icon : R.drawable.hidden_gem_map_pin);
        mTopPinTypeImg.setImageResource(pinType.equals(getString(R.string.type_been_there)) ? R.drawable.check_icon : pinType.equals(getString(R.string.type_explore)) ? R.drawable.plane_icon : R.drawable.gem_icon);

    }

    /*Delete Pin Api Call*/
    private void removePinApi() {
        if (NetworkUtil.isNetworkAvailable(this)) {

            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME + AppConstants.PIN_URL;

            APIRequestHandler.getInstance().deletePinApi(url, CityDetailsActivityScreen.this, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    if (obj instanceof RemovePinResponse) {
                        RemovePinResponse mResponse = (RemovePinResponse) obj;
                        if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                            DialogManager.getInstance().showToast(CityDetailsActivityScreen.this, String.valueOf(mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe()));
                            mTopPinTypeImg.setImageResource(R.color.transparent);
//                            mPostImgLay.setVisibility(View.GONE);
//                            mPinImgLay.setVisibility(View.VISIBLE);
                            mPinTypeImg.setImageResource(R.color.transparent);
                            citiesPinsApiCall();
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

    /*Add Pin Place*/
    private void citiesAddPinApiCall(String pinType) {
        if (NetworkUtil.isNetworkAvailable(this)) {

            AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

            addPinInputEntity.setPinType(pinType.equals(getString(R.string.type_been_there)) ? AppConstants.BEEN_THERE_PIN : pinType.equals(getString(R.string.type_explore)) ? AppConstants.TO_BE_EXPLORED_PIN : AppConstants.HIDDEN_GEM_PIN);

            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME + AppConstants.PIN_URL;

            APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, this, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    if (obj instanceof AddPinResponse) {
                        AddPinResponse mResponse = (AddPinResponse) obj;
                        if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                            DialogManager.getInstance().showToast(CityDetailsActivityScreen.this, String.valueOf(mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe()));
                            setAddPinCities(pinType);
                            citiesPinsApiCall();
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
                    citiesAddPinApiCall(pinType);
                }
            });
        }
    }


    private void citiesPinsApiCall() {
        if (NetworkUtil.isNetworkAvailable(CityDetailsActivityScreen.this)) {
            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME + AppConstants.PINS_URL;

            APIRequestHandler.getInstance().citiesPinsApi(url, CityDetailsActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    citiesPinsApiCall();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        backScreen(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPinDialogFragment != null)
            mPinDialogFragment.onActivityResult(requestCode, resultCode, data);
    }
}
