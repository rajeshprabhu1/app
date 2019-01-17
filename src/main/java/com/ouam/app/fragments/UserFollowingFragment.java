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
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.UserFollowingResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFollowingFragment extends BaseFragment  {

    @BindView(R.id.parent_lay)
    RelativeLayout mUserFollowingPayLay;

    @BindView(R.id.recycler_view)
    RecyclerView mUserFollowingRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;


    private ArrayList<WhoDetailEntity> mWhoDetailsList = new ArrayList<>();

    private UserFollowerAdapter mUserFollowingAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);


        initView();

        return rootView;
    }

    private void initView() {

        setupUI(mUserFollowingPayLay);

        userFollowingApiCall();
    }

    private void userFollowingApiCall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {

            String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID + AppConstants.USER_FOLLOWING_URL;

            APIRequestHandler.getInstance().userFollowingApi(url, UserFollowingFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    userFollowingApiCall();
                }
            });
        }
    }

    private void setAdapter(ArrayList<WhoDetailEntity> whoDetailList) {

        mUserFollowingRecyclerView.setVisibility(whoDetailList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(whoDetailList.size() > 0 ? View.GONE : View.VISIBLE);

        mUserFollowingAdapter = new UserFollowerAdapter(getActivity(),whoDetailList);
        mUserFollowingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserFollowingRecyclerView.setAdapter(mUserFollowingAdapter);

    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        UserFollowingResponse userDetailsResponse = (UserFollowingResponse) resObj;
        mWhoDetailsList = userDetailsResponse.getWith();
        setAdapter(mWhoDetailsList);
//        ((UserProfileActivityScreen) getActivity()).mFollowTxt.setText(String.valueOf(mWhoDetailsList.size()));
//        ((UserProfileActivityScreen) getActivity()).mFollowImg.setImageResource(mWhoDetailsList.size() > 0 ? R.drawable.following_me_red : R.drawable.following_me_gray);

    }

}
