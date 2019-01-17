package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.CitySearchAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.SearchPlaceInterface;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.AddPinResponse;
import com.ouam.app.model.CitySearchResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CitySearchFragment extends BaseFragment implements SearchPlaceInterface {


    @BindView(R.id.recycler_view)
    RecyclerView mSearchRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mSearchPeopleParLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    ArrayList<WithEntity> mCitiesList;

    private String mCityNameStr = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }

    //init views
    private void initView() {
        AppConstants.SEARCH_CITY_CALLBACK = this;

        setupUI(mSearchPeopleParLay);

       // citiesAPICall("", true);
        mNoValueTxt.setVisibility(View.VISIBLE);
    }


    public void citiesAPICall(final String city, boolean isShow) {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            String url = AppConstants.BASE_URL + AppConstants.CITIES_SEARCH_URL + city;
            APIRequestHandler.getInstance().citiesSearchAPICall(url, isShow, CitySearchFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    citiesAPICall(city, true);
                }
            });
        }


    }


    private void setAdapter(ArrayList<WithEntity> cityList) {

        mSearchRecyclerView.setVisibility(cityList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(cityList.size() > 0 ? View.GONE : View.VISIBLE);

        CitySearchAdapter mAdapter = new CitySearchAdapter(getActivity(),this ,cityList);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof CitySearchResponse){
            CitySearchResponse citySearchResponse = (CitySearchResponse) resObj;
            mCitiesList = citySearchResponse.getWith();
            setAdapter(mCitiesList);
        }
        if (resObj instanceof AddPinResponse){
            AddPinResponse mResponse = (AddPinResponse)resObj;
            if (mResponse.getStatus().equalsIgnoreCase(getString(R.string.succeeded))){
                if (!mCityNameStr.isEmpty()) {
                    citiesAPICall(mCityNameStr, false);
                } else {
                    setAdapter(new ArrayList<>());
                }
            }
        }
    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
        setAdapter(new ArrayList<WithEntity>());
    }

    @Override
    public void searchPlace(String city) {
        mCityNameStr = city;
        if (!city.isEmpty()) {
            citiesAPICall(city, false);
        } else {
            setAdapter(new ArrayList<>());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mCityNameStr.isEmpty()) {
            citiesAPICall(mCityNameStr, false);
        } else {
            setAdapter(new ArrayList<>());
        }
    }
}
