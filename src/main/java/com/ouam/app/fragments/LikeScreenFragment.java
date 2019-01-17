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
import com.ouam.app.adapter.LikeScreenAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.GetCommentsResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikeScreenFragment extends BaseFragment {

    @BindView(R.id.parent_lay)
    RelativeLayout mParLay;

    @BindView(R.id.recycler_view)
    RecyclerView mCommentsRecyclerView;

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

        setupUI(mParLay);
        getCommentApi();

    }

    private void setAdapter(ArrayList<WithEntity> withEntities) {


        mNoValueTxt.setVisibility(withEntities.size() > 0 ? View.GONE : View.VISIBLE);
        mCommentsRecyclerView.setVisibility(withEntities.size() > 0 ? View.VISIBLE : View.GONE);
        LikeScreenAdapter mAdapter = new LikeScreenAdapter(getActivity(),withEntities);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentsRecyclerView.setAdapter(mAdapter);

    }

    private void getCommentApi() {
        if (!AppConstants.POST_ENTITY.getId().isEmpty()) {
            if (NetworkUtil.isNetworkAvailable(getActivity())) {
                String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + AppConstants.POST_ENTITY.getId() + AppConstants.GET_LIKES_URL;
                APIRequestHandler.getInstance().getCommentsOrLikesApiCall(url,
                        LikeScreenFragment.this);
            } else {
                DialogManager.getInstance().showNetworkErrorPopup(getActivity(), new InterfaceBtnCallBack() {
                    @Override
                    public void onPositiveClick() {
                        getCommentApi();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if(resObj instanceof GetCommentsResponse){
            GetCommentsResponse mRespose = (GetCommentsResponse)resObj;
            if (mRespose.getStatus().equalsIgnoreCase(getString(R.string.succeeded))){
                setAdapter(mRespose.getWith());

            }
        }
    }
}
