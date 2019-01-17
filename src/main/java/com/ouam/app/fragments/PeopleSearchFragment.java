package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.PeopleSearchAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.SearchPlaceInterface;
import com.ouam.app.entity.PeopleSearchEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.PeopleSearchResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PeopleSearchFragment extends BaseFragment implements SearchPlaceInterface {


    @BindView(R.id.recycler_view)
    RecyclerView mSearchRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mSearchPeopleParLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    String mUserNameStr = "", mUserIDStr = "";

    private ArrayList<PeopleSearchEntity> mPeopleSearchList;

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
        AppConstants.SEARCH_PEOPLE_CALLBACK = this;

        setupUI(mSearchPeopleParLay);

        //peopleSearchAPICall("", true);

        mNoValueTxt.setVisibility(View.VISIBLE);


        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(getContext(), AppConstants.USER_DETAILS);
        UserDetailsEntity mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);
        mUserIDStr = mUserDetailsEntityRes.getUserId();
    }

    private void setAdapter(ArrayList<PeopleSearchEntity> mPeopleSearchList) {

        mSearchRecyclerView.setVisibility(mPeopleSearchList.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(mPeopleSearchList.size() > 0 ? View.GONE : View.VISIBLE);

        PeopleSearchAdapter mAdapter = new PeopleSearchAdapter(getActivity(), mUserIDStr,mPeopleSearchList);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchRecyclerView.setAdapter(mAdapter);

    }

    private void peopleSearchAPICall(final String name, boolean isShow) {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            String url = AppConstants.BASE_URL + AppConstants.USER_SEARCH_URL + name;

            APIRequestHandler.getInstance().peopleSearchAPICall(url, isShow, PeopleSearchFragment.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    peopleSearchAPICall(name, true);
                }
            });
        }


    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        PeopleSearchResponse peopleSearchResponse = (PeopleSearchResponse) resObj;
        mPeopleSearchList = peopleSearchResponse.getWith();
        setAdapter(mPeopleSearchList);

    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
        setAdapter(new ArrayList<PeopleSearchEntity>());
    }

    @Override
    public void searchPlace(String name) {
        mUserNameStr = name;
        if (!name.isEmpty()) {
            peopleSearchAPICall(name, false);
        } else {
            setAdapter(new ArrayList<>());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mUserNameStr.isEmpty()) {
            peopleSearchAPICall(mUserNameStr, false);
        } else {
            setAdapter(new ArrayList<>());
        }
    }
}
