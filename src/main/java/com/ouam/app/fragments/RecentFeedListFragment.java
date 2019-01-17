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
import com.ouam.app.adapter.ActivityFeedAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.ui.CommentsLikesActivityScreen;
import com.ouam.app.ui.PlaceDetailsActivityScreen;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecentFeedListFragment extends BaseFragment implements InterfaceBtnCallBack {


    @BindView(R.id.recycler_view)
    RecyclerView mRecentFeedRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mParentLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    ActivityFeedAdapter mAdapter;
    private ArrayList<ActivityFeedEntity> mActivityFeedList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }


    private void initView() {
        setAdapter();
    }

    private void setAdapter() {

        mActivityFeedList = new ArrayList<>();

        mRecentFeedRecyclerView.setVisibility(mActivityFeedList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(mActivityFeedList.size() > 0 ? View.GONE : View.VISIBLE);

        mAdapter = new ActivityFeedAdapter(getActivity(), mActivityFeedList, this);
        mRecentFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecentFeedRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onPositiveClick() {
        if (getActivity() != null) {
            ((PlaceDetailsActivityScreen) getActivity()).nextScreen(CommentsLikesActivityScreen.class, true);
        }

    }
}
