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

import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.UserPinsAdapter;
import com.ouam.app.commonInterfaces.UpdatePagerFragmentInterface;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.PreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BeenThereListFragment extends BaseFragment implements UpdatePagerFragmentInterface {


    @BindView(R.id.recycler_view)
    RecyclerView mBeenThereRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mParentLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValuesTxt;

    UserPinsAdapter mAdapter;
    boolean isMyProfile = false;

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
        AppConstants.BEEN_THERE_UPDATE_FRAGMENT_INTERFACE = this;
        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(getActivity(), AppConstants.USER_DETAILS);
        UserDetailsEntity mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);
        isMyProfile = mUserDetailsEntityRes.getUserId().equals(AppConstants.PROFILE_USER_ID);
        setupUI(mParentLay);
        setAdapter();
    }

    private  void setAdapter() {

        mBeenThereRecyclerView.setVisibility(AppConstants.BEEN_THERE_PIN_LIST.size()>0 ? View.VISIBLE : View.GONE);
        mNoValuesTxt.setVisibility(AppConstants.BEEN_THERE_PIN_LIST.size()>0 ? View.GONE : View.VISIBLE);

        mAdapter = new UserPinsAdapter(getActivity(), AppConstants.BEEN_THERE_PIN_LIST,isMyProfile);
        mBeenThereRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBeenThereRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void update(ArrayList<WhoDetailEntity> updateList) {
        setAdapter();
    }
}
