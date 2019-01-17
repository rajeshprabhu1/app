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
import com.ouam.app.adapter.CitiesRecentAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.CityRecentResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesRecentFragment extends BaseFragment {

    @BindView(R.id.parent_lay)
    RelativeLayout mCitiesRecentParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mCitiesRecentRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstances) {
        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);
        initView();

        return rootView;
    }

    //init views
    private void initView() {

        setupUI(mCitiesRecentParLay);

        citiesRecentAPICall();
    }

    public void citiesRecentAPICall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME+AppConstants.USER_RECENT_URL;
            APIRequestHandler.getInstance().citiesRecentApi(url, CitiesRecentFragment.this);
        }else {
            DialogManager.getInstance().showNetworkErrorPopup(getActivity(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    citiesRecentAPICall();
                }
            });
        }

    }


    private void setAdapter(ArrayList<WithEntity> cityList) {

        mCitiesRecentRecyclerView.setVisibility(cityList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(cityList.size() > 0 ? View.GONE : View.VISIBLE);

        CitiesRecentAdapter mAdapter = new CitiesRecentAdapter(getActivity(), cityList);
        mCitiesRecentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCitiesRecentRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        CityRecentResponse cityDetailsResponse = (CityRecentResponse) resObj;
        setAdapter(cityDetailsResponse.getWith());
    }
}
