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
import com.ouam.app.adapter.FeedActivityAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.ActivityFeedResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceActivityFragment extends BaseFragment implements FeedActivityAdapter.OnItemClickListener, InterfaceBtnCallBack {

    @BindView(R.id.parent_lay)
    RelativeLayout mPlaceActivityFragParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mPlaceActivityRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_common_recycler_view, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    private void initView() {
        AppConstants.UPDATE_ACTIVITY_INTERFACE = this;
        setupUI(mPlaceActivityFragParLay);

        placeActivityApiCall();

    }

    private void placeActivityApiCall() {
        if (NetworkUtil.isNetworkAvailable(getContext())) {

            String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + AppConstants.PLACE_NAME + AppConstants.USER_RECENT_URL;

            APIRequestHandler.getInstance().placeActivityApi(url, PlaceActivityFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    placeActivityApiCall();
                }
            });
        }
    }

    private void setAdapter(ArrayList<ActivityFeedEntity> activityFeedLists) {

        mPlaceActivityRecyclerView.setVisibility(activityFeedLists.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(activityFeedLists.size() > 0 ? View.GONE : View.VISIBLE);
         FeedActivityAdapter userFollowerAdapter = new FeedActivityAdapter(getActivity(), activityFeedLists, this, null,getString(R.string.user_recent),getScreenWidth());
         mPlaceActivityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlaceActivityRecyclerView.setAdapter(userFollowerAdapter);
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        ActivityFeedResponse userDetailsResponse = (ActivityFeedResponse) resObj;
        ArrayList<ActivityFeedEntity> mActivityFeedList = userDetailsResponse.getWith();
        setAdapter(mActivityFeedList);
    }

    @Override
    public void onItemClicked(ActivityFeedEntity activityFeedEntity) {

    }

    @Override
    public void onPositiveClick() {
        placeActivityApiCall();
    }
}
