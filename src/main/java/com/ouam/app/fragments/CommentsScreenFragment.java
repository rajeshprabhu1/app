package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.adapter.CommentsScreenAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.CommentInputEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.model.GetCommentsResponse;
import com.ouam.app.model.SendCommentResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsScreenFragment extends BaseFragment {

    @BindView(R.id.comment_screen_frag_par_lay)
    RelativeLayout mCommentParLay;

    @BindView(R.id.comments_screen_recycler_view)
    RecyclerView mCommentsRecyclerView;

    @BindView(R.id.comment_edit_txt)
    EditText mCommentEdtTxt;

    @BindView(R.id.comments_sent_img)
    ImageView mCommentSendImg;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    @BindView(R.id.user_img)
    RoundedImageView mUserRoundImg;

    private CommentsScreenAdapter mAdapter;

    private ArrayList<String> mCommentList;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_comments_screen, container, false);

        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }

    private void initView() {
        setupUI(mCommentParLay);
        getCommentApi();

        if(!AppConstants.COMMENT_USER_IMG.isEmpty()){
            try {
                Glide.with(this)
                        .load(AppConstants.COMMENT_USER_IMG)
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(mUserRoundImg);

            } catch (Exception ex) {
                mUserRoundImg.setImageResource(R.color.dark_blue);
            }
        }

        mCommentSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCommentEdtTxt.getText().toString().trim().isEmpty()) {
                    sendCommentApi();
                } else {
                    mCommentEdtTxt.clearAnimation();
                    mCommentEdtTxt.setAnimation(Utils.shakeError());
                }
            }
        });


    }

    private void setAdapter(ArrayList<WithEntity> withEntity) {

        mNoValueTxt.setVisibility(withEntity.size() > 0 ? View.GONE : View.VISIBLE);
        mCommentsRecyclerView.setVisibility(withEntity.size() > 0 ? View.VISIBLE : View.GONE);
        mAdapter = new CommentsScreenAdapter(getActivity(), withEntity, this, AppConstants.PROFILE_USER_ID);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentsRecyclerView.setAdapter(mAdapter);

    }


    private void sendCommentApi() {
        if (!AppConstants.POST_ENTITY.getId().isEmpty()) {
            if (NetworkUtil.isNetworkAvailable(getActivity())) {
                String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + AppConstants.POST_ENTITY.getId() + AppConstants.COMMENT_URL;
                CommentInputEntity commentInputEntity = new CommentInputEntity();
                commentInputEntity.setComment(mCommentEdtTxt.getText().toString().trim());
                APIRequestHandler.getInstance().postCommentsApiCall(url, commentInputEntity, CommentsScreenFragment.this);
            } else {
                DialogManager.getInstance().showNetworkErrorPopup(getActivity(), new InterfaceBtnCallBack() {
                    @Override
                    public void onPositiveClick() {
                        sendCommentApi();
                    }
                });
            }
        }
    }

    private void getCommentApi() {
        if (!AppConstants.POST_ENTITY.getId().isEmpty()) {
            if (NetworkUtil.isNetworkAvailable(getActivity())) {
                String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + AppConstants.POST_ENTITY.getId() + AppConstants.GET_COMMENTS_URL;
                APIRequestHandler.getInstance().getCommentsOrLikesApiCall(url, CommentsScreenFragment.this);
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
        if (resObj instanceof SendCommentResponse) {
            SendCommentResponse mResponse = (SendCommentResponse) resObj;
            if (mResponse.getStatus().equalsIgnoreCase(getString(R.string.succeeded))) {
                getCommentApi();
                mCommentEdtTxt.setText("");
            }
        }
        if (resObj instanceof GetCommentsResponse) {
            GetCommentsResponse mResponse = (GetCommentsResponse) resObj;
            if (mResponse.getStatus().equalsIgnoreCase(getString(R.string.succeeded))) {
                setAdapter(mResponse.getWith());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCommentApi();
    }
}
