package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.CitiesTrendingAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.CityWithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.CitiesTrendingPlaceResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesTrendingPlaceFragment extends BaseFragment {

    @BindView(R.id.parent_lay)
    RelativeLayout mCitiesTrendingPlaceParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mCitiesTrendingPlaceRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    ArrayList<CityWithEntity> mCitiesTrendingList;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view,container,false);

        ButterKnife.bind(this,rootView);

        initView();

        return rootView;
    }

    //init views
    private void initView() {

        setupUI(mCitiesTrendingPlaceParLay);

        citiesTrendingAPICall();
    }

    public void citiesTrendingAPICall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME + AppConstants.TRENDING_URL;
            APIRequestHandler.getInstance().citiesTrendingApi(url, CitiesTrendingPlaceFragment.this);
        }else {
            DialogManager.getInstance().showNetworkErrorPopup(getActivity(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    citiesTrendingAPICall();
                }
            });
        }

    }


    private void setAdapter(ArrayList<CityWithEntity> cityTrendingList) {

        mCitiesTrendingPlaceRecyclerView.setVisibility(cityTrendingList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(cityTrendingList.size() > 0 ? View.GONE : View.VISIBLE);
        CitiesTrendingAdapter mAdapter = new CitiesTrendingAdapter(getActivity(),this, cityTrendingList);
        mCitiesTrendingPlaceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCitiesTrendingPlaceRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        CitiesTrendingPlaceResponse cityTrendingResponse = (CitiesTrendingPlaceResponse) resObj;
        mCitiesTrendingList = cityTrendingResponse.getWith();
        setAdapter(mCitiesTrendingList);
    }

    @Override
    public void onResume() {
        super.onResume();
        citiesTrendingAPICall();
    }
}
