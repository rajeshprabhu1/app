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
import com.ouam.app.adapter.ProfileRecentActivityAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.ActivityFeedResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRecentFragment extends BaseFragment implements ProfileRecentActivityAdapter.OnItemClickListener {

    @BindView(R.id.parent_lay)
    RelativeLayout mUserRecentParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mUserRecentRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    private ArrayList<ActivityFeedEntity> mActivityFeedList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_common_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    private void initView() {

        setupUI(mUserRecentParLay);

        userRecentApiCall();
    }

    private void userRecentApiCall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {

            String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID + AppConstants.USER_RECENT_URL;

            APIRequestHandler.getInstance().userRecentApi(url, UserRecentFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    userRecentApiCall();
                }
            });
        }
    }

    private void setAdapter(ArrayList<ActivityFeedEntity> activityFeedLists) {


        mUserRecentRecyclerView.setVisibility(activityFeedLists.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(activityFeedLists.size() > 0 ? View.GONE : View.VISIBLE);
        ProfileRecentActivityAdapter userRecentAdapter = new ProfileRecentActivityAdapter(getActivity(), activityFeedLists,this);
        mUserRecentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUserRecentRecyclerView.setAdapter(userRecentAdapter);
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        ActivityFeedResponse userDetailsResponse = (ActivityFeedResponse) resObj;
        mActivityFeedList = userDetailsResponse.getWith();
        setAdapter(mActivityFeedList);
    }

    @Override
    public void onItemClicked(ActivityFeedEntity activityFeedEntity) {

    }
}
