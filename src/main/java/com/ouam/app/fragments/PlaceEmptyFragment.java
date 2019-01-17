package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.PlacePinsAdapter;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.PlacePinsResponse;
import com.ouam.app.ui.PlaceDetailsActivityScreen;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceEmptyFragment extends BaseFragment {

    @BindView(R.id.parent_lay)
    RelativeLayout mPlaceEmptyParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mPlaceEmptyRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private ArrayList<WhoDetailEntity> mWhoDetailsList;

    private PlacePinsAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    private void initView() {
        setupUI(mPlaceEmptyParLay);

//        placeEmptyApiCall();
    }

//    private void placeEmptyApiCall() {
//        if (NetworkUtil.isNetworkAvailable(getActivity())) {
//            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME + AppConstants.PINS_URL;
//
//            APIRequestHandler.getInstance().placePinsApi(url, PlaceEmptyFragment.this);
//        }else {
//            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
//                @Override
//                public void onPositiveClick() {
//                    placeEmptyApiCall();
//                }
//            });
//        }
//    }

    private void setAdapter(ArrayList<WhoDetailEntity> whoDetailList) {

        mPlaceEmptyRecyclerView.setVisibility(whoDetailList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(whoDetailList.size() > 0 ? View.GONE : View.VISIBLE);
        mAdapter = new PlacePinsAdapter(getActivity(), whoDetailList);
        mPlaceEmptyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlaceEmptyRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        PlacePinsResponse placePinsResponse = (PlacePinsResponse) resObj;

        mWhoDetailsList = new ArrayList<>();

        for (int i = 0; i < placePinsResponse.getWith().getBeenThere().size(); i++) {
            placePinsResponse.getWith().getBeenThere().get(i).setPinType(getString(R.string.type_been_there));
            mWhoDetailsList.add(placePinsResponse.getWith().getBeenThere().get(i));
        }

        for (int i = 0; i < placePinsResponse.getWith().getToBeExplored().size(); i++) {
            placePinsResponse.getWith().getToBeExplored().get(i).setPinType(getString(R.string.type_explore));
            mWhoDetailsList.add(placePinsResponse.getWith().getToBeExplored().get(i));
        }

        for (int i = 0; i < placePinsResponse.getWith().getHiddenGems().size(); i++) {
            placePinsResponse.getWith().getHiddenGems().get(i).setPinType(getString(R.string.type_hidden_gem));
            mWhoDetailsList.add(placePinsResponse.getWith().getHiddenGems().get(i));
        }

        ((PlaceDetailsActivityScreen) getActivity()).mToBeTxt.setText(String.valueOf(placePinsResponse.getWith().getToBeExplored().size()));
        ((PlaceDetailsActivityScreen) getActivity()).mBeenThereTxt.setText(String.valueOf(placePinsResponse.getWith().getBeenThere().size()));
        ((PlaceDetailsActivityScreen) getActivity()).mHiddenGemTxt.setText(String.valueOf(placePinsResponse.getWith().getHiddenGems().size()));
        setAdapter(mWhoDetailsList);
    }
}
