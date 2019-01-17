package com.ouam.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.CityEntity;
import com.ouam.app.entity.UserEntity;
import com.ouam.app.entity.UserProfileEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.FollowResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.RoundedImageView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceFollowRecommendListAdapter extends RecyclerView.Adapter<PlaceFollowRecommendListAdapter.Holder> {

    private final int mScreenWidthInt;
    private Context mContext;
    private ArrayList<UserProfileEntity> mFollowUserDetailsArrListRes;
    private DiscreteScrollView mDiscreteScrollView;
    private InterfaceBtnCallBack mFollowInterfaceBtnCallBack;
    private boolean mIsFirstRecommendBool;

    public PlaceFollowRecommendListAdapter(Context context, ArrayList<UserProfileEntity> activityFeedList,
                                           DiscreteScrollView discreteScrollView, int screenWidthInt, boolean isFirstRecommendBool, InterfaceBtnCallBack followInterfaceBtnCallBack) {
        mContext = context;
        mFollowUserDetailsArrListRes = activityFeedList;
        mDiscreteScrollView = discreteScrollView;
        mScreenWidthInt = screenWidthInt;
        mIsFirstRecommendBool = isFirstRecommendBool;
        this.mFollowInterfaceBtnCallBack = followInterfaceBtnCallBack;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_plc_recommendation_follow_view, parent, false);
        view.getLayoutParams().width = mScreenWidthInt;
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int posInt) {
        UserEntity userDetailsRes = mFollowUserDetailsArrListRes.get(posInt).getUser();
        CityEntity placeDetailsRes = mFollowUserDetailsArrListRes.get(posInt).getUser().getCity();


        if (userDetailsRes.getPhoto().isEmpty()) {
            holder.mUserImgLay.setVisibility(View.GONE);
        } else {
            try {
                holder.mUserImgLay.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(userDetailsRes.getPhoto())
                        .apply(new RequestOptions().error(R.drawable.profile_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(holder.mUserImg);
            } catch (Exception ex) {
                holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
            }
        }

        holder.mPinTypeImg.setImageResource(ImageUtils.userBatchLevelImage(userDetailsRes.getLevel()));
        holder.mIntroTxt.setText(mContext.getString(!mIsFirstRecommendBool ? R.string.cong_plc : R.string.based_on_pin));
        holder.mIntroTxt.setVisibility(posInt > 0 ? View.GONE : View.VISIBLE);

        holder.mUserNameTxt.setText(userDetailsRes.getUsername().trim().isEmpty() ? "" : userDetailsRes.getUsername() + " ");
        holder.mPlaceNameTxt.setText(placeDetailsRes.getName());

//        String pinCountStr = userDetailsRes.get+ " " + (activityFeedEntity.getActivity().getCommentCount() > 1 ? mContext.getString(R.string.comments) : mContext.getString(R.string.comment));
//        String followerCountStr = activityFeedEntity.getActivity().getLikeCount() > 1 ? activityFeedEntity.getActivity().getLikeCount() + " Likes" : activityFeedEntity.getActivity().getLikeCount() + " Like";
//        holder.mPinCountTxt.setText(pinCountStr);
//        holder.mFollowerCountTxt.setText(followerCountStr);
        holder.mPinCountTxt.setText("250 Pins");
        holder.mFollowerCountTxt.setText("182 Followers");


        holder.mUserImgLay.setOnClickListener(v -> {
            if (!userDetailsRes.getId().isEmpty()) {
                int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
                AppConstants.PROFILE_USER_ID = mFollowUserDetailsArrListRes.get(destinationNewPosInt).getUser().getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }
        });


        holder.mUserNameTxt.setOnClickListener(v -> {

            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            UserEntity userDetailsClickRes = mFollowUserDetailsArrListRes.get(destinationNewPosInt).getUser();
            if (!userDetailsClickRes.getId().trim().isEmpty()) {
                AppConstants.PROFILE_USER_ID = userDetailsClickRes.getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }

        });

        holder.mRightArrowImg.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            UserEntity userDetailsClickRes = mFollowUserDetailsArrListRes.get(destinationNewPosInt).getUser();
            if (!userDetailsClickRes.getId().trim().isEmpty()) {
                AppConstants.PROFILE_USER_ID = userDetailsClickRes.getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }
        });

        holder.mCloseImg.setOnClickListener(v -> mDiscreteScrollView.setVisibility(View.GONE));
        holder.mDismissNotificationTxt.setOnClickListener(v -> mDiscreteScrollView.setVisibility(View.GONE));
        holder.mFollowBtn.setOnClickListener(v -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            followAPI(mFollowUserDetailsArrListRes.get(destinationNewPosInt).getUser().getId());
        });


    }

    private void followAPI(String followUserIDStr) {
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            APIRequestHandler.getInstance().followApi(followUserIDStr, mContext, new InterfaceApiResponseCallBack() {
                @Override
                public void onRequestSuccess(Object obj) {
                    FollowResponse mResponse = (FollowResponse) obj;
                    if (mResponse.getStatus().equals(mContext.getString(R.string.succeeded))) {
                        mFollowInterfaceBtnCallBack.onPositiveClick();
                    }
                }

                @Override
                public void onRequestFailure(Throwable r) {

                }
            });
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(mContext, () -> followAPI(followUserIDStr));
        }
    }


    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.pin_type_image)
        ImageView mPinTypeImg;

        @BindView(R.id.intro_txt)
        TextView mIntroTxt;

        @BindView(R.id.close_img)
        ImageView mCloseImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.place_name_txt)
        TextView mPlaceNameTxt;

        @BindView(R.id.follow_btn)
        Button mFollowBtn;

        @BindView(R.id.pin_count_txt)
        TextView mPinCountTxt;

        @BindView(R.id.follower_count_txt)
        TextView mFollowerCountTxt;

        @BindView(R.id.right_arrow_img)
        ImageView mRightArrowImg;

        @BindView(R.id.dismiss_notification_txt)
        TextView mDismissNotificationTxt;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    public int getItemCount() {
        return mFollowUserDetailsArrListRes.size();
    }

    private int getRealPositionInt(int holderCurrentPosInt) {
        final RecyclerView.Adapter adapter = mDiscreteScrollView.getAdapter();
        int destinationNewPosInt = holderCurrentPosInt;
        if (adapter instanceof InfiniteScrollAdapter) {
            destinationNewPosInt = ((InfiniteScrollAdapter) adapter).getRealPosition(holderCurrentPosInt);
        }
        return destinationNewPosInt;
    }
}
