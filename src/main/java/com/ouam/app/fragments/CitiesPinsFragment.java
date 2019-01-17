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
import com.ouam.app.adapter.CitiesPinsAdapter;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.CitiesPinsResponse;
import com.ouam.app.ui.CityDetailsActivityScreen;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesPinsFragment extends BaseFragment {

    @BindView(R.id.parent_lay)
    RelativeLayout mCitiesPinsParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mCitiesPinsRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private ArrayList<WhoDetailEntity> mWhoDetailsList;

    private CitiesPinsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {

        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }

    private void initView() {

        setupUI(mCitiesPinsParLay);

//        citiesPinsApiCall();
    }

//    private void citiesPinsApiCall() {
//        if (NetworkUtil.isNetworkAvailable(getActivity())) {
//            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + AppConstants.CITY_NAME + AppConstants.PINS_URL;
//
//            APIRequestHandler.getInstance().citiesPinsApi(url, CitiesPinsFragment.this);
//        } else {
//            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
//                @Override
//                public void onPositiveClick() {
//                    citiesPinsApiCall();
//                }
//            });
//        }
//    }

    private void setAdapter(ArrayList<WhoDetailEntity> whoDetailList) {

        mCitiesPinsRecyclerView.setVisibility(whoDetailList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(whoDetailList.size() > 0 ? View.GONE : View.VISIBLE);

        mAdapter = new CitiesPinsAdapter(getActivity(), whoDetailList);
        mCitiesPinsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCitiesPinsRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        CitiesPinsResponse citiesPinsResponse = (CitiesPinsResponse) resObj;

        mWhoDetailsList = new ArrayList<>();

        for (int i = 0; i < citiesPinsResponse.getWith().getBeenThere().size(); i++) {
            citiesPinsResponse.getWith().getBeenThere().get(i).setPinType(getString(R.string.type_been_there));
            mWhoDetailsList.add(citiesPinsResponse.getWith().getBeenThere().get(i));
        }

        for (int i = 0; i < citiesPinsResponse.getWith().getToBeExplored().size(); i++) {
            citiesPinsResponse.getWith().getToBeExplored().get(i).setPinType(getString(R.string.type_explore));
            mWhoDetailsList.add(citiesPinsResponse.getWith().getToBeExplored().get(i));
        }

        for (int i = 0; i < citiesPinsResponse.getWith().getHiddenGems().size(); i++) {
            citiesPinsResponse.getWith().getHiddenGems().get(i).setPinType(getString(R.string.type_hidden_gem));
            mWhoDetailsList.add(citiesPinsResponse.getWith().getHiddenGems().get(i));
        }

        ((CityDetailsActivityScreen) getActivity()).mBeenThereTxt.setText(String.valueOf(citiesPinsResponse.getWith().getBeenThere().size()));
        ((CityDetailsActivityScreen) getActivity()).mToBeTxt.setText(String.valueOf(citiesPinsResponse.getWith().getToBeExplored().size()));
        ((CityDetailsActivityScreen) getActivity()).mHiddenGemTxt.setText(String.valueOf(citiesPinsResponse.getWith().getHiddenGems().size()));
        setAdapter(mWhoDetailsList);
    }
}
