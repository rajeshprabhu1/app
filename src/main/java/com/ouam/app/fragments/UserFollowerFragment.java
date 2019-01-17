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
import com.ouam.app.adapter.UserFollowerAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.RefreshFollowInterface;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.UserFollowerResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFollowerFragment extends BaseFragment implements RefreshFollowInterface {

    @BindView(R.id.parent_lay)
    RelativeLayout mUserFollowerPayLay;

    @BindView(R.id.recycler_view)
    RecyclerView mUserFollowerRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private ArrayList<WhoDetailEntity> mWhoDetailsList = new ArrayList<>();

    private UserFollowerAdapter mUserFollowerAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

//        AppConstants.REFRESH_FOLLOW_LIST = UserFollowerFragment.this;

        initView();

        return rootView;
    }

    private void initView() {

        setupUI(mUserFollowerPayLay);

        userFollowerApiCall();
    }

    private void userFollowerApiCall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {

            String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID + AppConstants.USER_FOLLOWER_URL;

            APIRequestHandler.getInstance().userFollowerApi(url, UserFollowerFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    userFollowerApiCall();
                }
            });
        }
    }

    private void setAdapter(ArrayList<WhoDetailEntity> whoDetailList) {

        mUserFollowerRecyclerView.setVisibility(whoDetailList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(whoDetailList.size() > 0 ? View.GONE : View.VISIBLE);
        mUserFollowerAdapter = new UserFollowerAdapter(getActivity(), whoDetailList);
        mUserFollowerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserFollowerRecyclerView.setAdapter(mUserFollowerAdapter);


//        mUserFollowerAdapter = new UserPinsAdapter(getActivity(), AppConstants.TO_BE_EXPLORED_PIN_LIST);
//        mUserFollowerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mUserFollowerRecyclerView.setAdapter(mUserFollowerAdapter);
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        UserFollowerResponse userDetailsResponse = (UserFollowerResponse) resObj;
        mWhoDetailsList = userDetailsResponse.getWith();
        setAdapter(mWhoDetailsList);
//        ((UserProfileActivityScreen) getActivity()).mFollowingTxt.setText(String.valueOf(mWhoDetailsList.size()));
//        ((UserProfileActivityScreen) getActivity()).mFollowingImg.setImageResource(mWhoDetailsList.size() > 0 ? R.drawable.follwoing_red : R.drawable.follwing_gray);
    }

    @Override
    public void refreshFollowListAPI() {

       userFollowerApiCall();
    }
}
