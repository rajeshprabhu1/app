package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.fragments.CommentsScreenFragment;
import com.ouam.app.model.CommentDeleteResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CommentsLikesActivityScreen;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsScreenAdapter extends RecyclerView.Adapter<CommentsScreenAdapter.Holder> {

    private Context mContext;

    private ArrayList<WithEntity> mWithEntities;

    CommentsScreenFragment mCommentsScreenFragment;

    String mUserId;

    public CommentsScreenAdapter(Context context, ArrayList<WithEntity> withEntities, CommentsScreenFragment commentsScreenFragment, String userId) {
        mContext = context;
        mWithEntities = withEntities;
        mCommentsScreenFragment = commentsScreenFragment;
        mUserId = userId;
    }

    @NonNull
    @Override
    public CommentsScreenAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_comments_screen_row, parent, false);
        return new CommentsScreenAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsScreenAdapter.Holder holder, int position) {
        WithEntity mWithEntity = mWithEntities.get(position);
        if (mWithEntity.getUser().getPhoto().isEmpty()) {
            holder.mUserImage.setVisibility(View.GONE);
        } else {
            try {
                holder.mUserImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(mWithEntity.getUser().getPhoto())
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(holder.mUserImage);

            } catch (Exception ex) {
                holder.mUserImage.setImageResource(R.drawable.user_demo_icon);
            }
        }
        holder.mUserName.setText(mWithEntity.getUser().getUsername());
        holder.mCommentTxt.setText(mWithEntity.getComment());
        holder.mTimeTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(mWithEntity.getCreated()), true));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWithEntity.getUser().getId().isEmpty()) {
                    AppConstants.PROFILE_USER_ID = mWithEntity.getUser().getId();
                    ((CommentsLikesActivityScreen) mContext).nextScreen(UserProfileActivityScreen.class, true);
                }
            }
        });

        holder.mMyProfileBool = mUserId.equalsIgnoreCase(mWithEntity.getUser().getId());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.mMyProfileBool) {

                    DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.delete_comment), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                        @Override
                        public void onNegativeClick() {

                        }

                        @Override
                        public void onPositiveClick() {
                            if (NetworkUtil.isNetworkAvailable(mContext)) {
                                String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + AppConstants.POST_ENTITY.getId() + AppConstants.COMMENT_URL + "/" + mWithEntity.getId();
                                APIRequestHandler.getInstance().deleteCommentsApiCall(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        if (obj instanceof CommentDeleteResponse) {
                                            CommentDeleteResponse mResponse = (CommentDeleteResponse) obj;
                                            if (mResponse.getStatus().equalsIgnoreCase(mContext.getString(R.string.succeeded))) {
                                                DialogManager.getInstance().showToast(mContext, mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe());
                                                mCommentsScreenFragment.onResume();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });
                            }
                        }
                    });
                }
                return false;
            }

        });


    }


    @Override
    public int getItemCount() {
        return mWithEntities.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        @BindView(R.id.user_img)
        RoundedImageView mUserImage;

        @BindView(R.id.user_name)
        TextView mUserName;

        @BindView(R.id.comments_time_txt)
        TextView mTimeTxt;

        boolean mMyProfileBool;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
