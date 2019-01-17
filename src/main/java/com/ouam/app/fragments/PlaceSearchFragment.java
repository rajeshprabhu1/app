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
import com.ouam.app.adapter.PlaceSearchAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.SearchPlaceInterface;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.PlacesSearchResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceSearchFragment extends BaseFragment implements SearchPlaceInterface {


    @BindView(R.id.recycler_view)
    RecyclerView mSearchRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mSearchPeopleParLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private String mQueryNamStr = "";

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
        AppConstants.SEARCH_PLACE_CALLBACK = this;

        setupUI(mSearchPeopleParLay);
        callApi("",true);

    }


    /*api call*/
    private void callApi(final String query,boolean isShow) {
            if (NetworkUtil.isNetworkAvailable(getContext())) {

                String url = AppConstants.BASE_URL + AppConstants.PLACE_SEARCH_URL + AppConstants.LAT_URL +
                        AppConstants.CURRENT_LAT + AppConstants.LON_URL +
                        AppConstants.CURRENT_LONG + AppConstants.RADIUS_URL + 25000 + AppConstants.QUERY + query;

                APIRequestHandler.getInstance().placesSearchApi(url, isShow,this);
            } else {
                DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                    @Override
                    public void onPositiveClick() {
                        callApi(query,true);
                    }
                });
            }

    }


    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        PlacesSearchResponse placesSearchResponse = (PlacesSearchResponse) resObj;
        setAdapter(placesSearchResponse.getWith());

    }

    private void setAdapter(ArrayList<WithEntity> placeList) {

        mSearchRecyclerView.setVisibility(placeList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(placeList.size() > 0 ? View.GONE : View.VISIBLE);
        PlaceSearchAdapter mAdapter = new PlaceSearchAdapter(getActivity(),this, placeList);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
        setAdapter(new ArrayList<WithEntity>());
    }

    @Override
    public void searchPlace(String query) {
        callApi(query,false);
        mQueryNamStr = query;
    }

    @Override
    public void onResume() {
        super.onResume();
            callApi(mQueryNamStr,false);
    }
}
