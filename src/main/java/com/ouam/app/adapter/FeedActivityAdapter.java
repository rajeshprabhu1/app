package com.ouam.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.CommentLikeResponse;
import com.ouam.app.model.CommentUnlikeResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CityDetailsActivityScreen;
import com.ouam.app.ui.CommentsLikesActivityScreen;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.RoundedImageView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_PIN_INT = 1, TYPE_POST_INT = 2, mScreenWidthInt;
    private Context mContext;
    private ArrayList<ActivityFeedEntity> mActivityFeedArrListRes;
    private String mFromWhereStr;
    private OnItemClickListener mOnItemClickListener;
    private DiscreteScrollView mDiscreteScrollView;

    public FeedActivityAdapter(Context context, ArrayList<ActivityFeedEntity> activityFeedList,
                               OnItemClickListener onItemClickListener, DiscreteScrollView discreteScrollView,
                               String from, int screenWidthInt) {
        mContext = context;
        mActivityFeedArrListRes = activityFeedList;
        mFromWhereStr = from;
        mOnItemClickListener = onItemClickListener;
        mDiscreteScrollView = discreteScrollView;
        mScreenWidthInt = screenWidthInt;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PIN_INT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_activity_feed_row1, parent, false);
            view.getLayoutParams().width = mScreenWidthInt;
            return new PinTypeHolder(view);
        } else if (viewType == TYPE_POST_INT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_post_feeds_row, parent, false);
            view.getLayoutParams().width = mScreenWidthInt;
            return new PostTypeViewHolder(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }

    /*Load data in each row element*/
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int listPosition) {
        ActivityFeedEntity activityFeedEntity = mActivityFeedArrListRes.get(listPosition);
        switch (holder.getItemViewType()) {
            case TYPE_PIN_INT:
                initPinLayout((PinTypeHolder) holder, activityFeedEntity);
                break;
            case TYPE_POST_INT:
                initPostLayout((PostTypeViewHolder) holder, activityFeedEntity);
                break;
            default:
                break;
        }
    }

    /*Determine which layout to use for the row*/
    @Override
    public int getItemViewType(int position) {
        ActivityFeedEntity item = mActivityFeedArrListRes.get(position);
        if (item.getActivity().getActivityType().equals(mContext.getString(R.string.pin))) {
            return TYPE_PIN_INT;
        } else if (item.getActivity().getActivityType().equals(mContext.getString(R.string.post))) {
            return TYPE_POST_INT;
        } else {
            return -1;
        }
    }


    private void initPinLayout(PinTypeHolder holder, ActivityFeedEntity activityFeedEntity) {

        WhoDetailEntity userDetailsRes = activityFeedEntity.getActivity().getUser();
        WithEntity placeDetailsRes = activityFeedEntity.getActivity().getPlace();

        holder.mUserBgImg.setImageResource(ImageUtils.userBgTypeImage(mContext, activityFeedEntity.getActivity().getSubtype()));
        if (userDetailsRes.getPhoto().isEmpty()) {
            holder.mUserImgLay.setVisibility(View.GONE);
        } else {
            try {
                holder.mUserImgLay.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(userDetailsRes.getPhoto())
                        .apply(new RequestOptions().placeholder(R.drawable.profile_bg).error(R.drawable.profile_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(holder.mUserImg);
            } catch (Exception ex) {
                holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
            }
        }

        holder.mPinTypeImg.setImageResource(ImageUtils.userBatchLevelImage(userDetailsRes.getLevel()));

        holder.mUserNameTxt.setText(userDetailsRes.getUsername().trim().isEmpty() ? "" : userDetailsRes.getUsername() + " ");
        holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));
        holder.mPlaceNameTxt.setText(placeDetailsRes.getName());
        holder.mPlaceNameTxt.setVisibility(mFromWhereStr.equals(mContext.getString(R.string.user_recent)) ? View.GONE : View.VISIBLE);
        holder.mPlaceTypeImg.setImageResource(ImageUtils.placeTypeImage(mContext, activityFeedEntity.getActivity().getSubtype()));

        String commentStr = activityFeedEntity.getActivity().getCommentCount() + " " + (activityFeedEntity.getActivity().getCommentCount() > 1 ? mContext.getString(R.string.comments) : mContext.getString(R.string.comment));
        String likeStr = activityFeedEntity.getActivity().getLikeCount() > 1 ? activityFeedEntity.getActivity().getLikeCount() + " Likes" : activityFeedEntity.getActivity().getLikeCount() + " Like";
        holder.mLikeImg.setImageResource(activityFeedEntity.getActivity().isYouLike() ? R.drawable.heart_red_color_icon : R.drawable.like_icon);
        holder.mCommentCountTxt.setText(commentStr);
        holder.mLikeCountTxt.setText(likeStr);


        holder.mUserImgLay.setOnClickListener(v -> {
            if (!userDetailsRes.getId().isEmpty()) {
                int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
                AppConstants.PROFILE_USER_ID = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getUser().getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }
        });

        holder.mPlaceNameTxt.setOnClickListener(v -> {

            final RecyclerView.Adapter adapter = mDiscreteScrollView.getAdapter();
            int destinationNewPosInt = holder.getAdapterPosition();

            if (adapter instanceof InfiniteScrollAdapter) {
                destinationNewPosInt = ((InfiniteScrollAdapter) adapter).getRealPosition(destinationNewPosInt);
            }

            WithEntity placeDetailsClickRes = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPlace();
            if (!placeDetailsClickRes.getProviderIDs().getFs().isEmpty() && !placeDetailsClickRes.getId().isEmpty()) {
                AppConstants.PLACE_NAME = placeDetailsClickRes.getId() + "%7Cfs:" + placeDetailsClickRes.getProviderIDs().getFs();
                AppConstants.pinType = placeDetailsClickRes.getPinnedAs();
                ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
            }
        });

        holder.mUserNameTxt.setOnClickListener(v -> {

            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            WhoDetailEntity userDetailsClickRes = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getUser();
            if (!userDetailsClickRes.getId().trim().isEmpty()) {
                AppConstants.PROFILE_USER_ID = userDetailsClickRes.getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }

        });

        holder.mRightArrowImg.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            WhoDetailEntity userDetailsClickRes = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getUser();
            if (!userDetailsClickRes.getId().trim().isEmpty()) {
                AppConstants.PROFILE_USER_ID = userDetailsClickRes.getId();
                ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
            }
        });


        holder.itemView.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            mOnItemClickListener.onItemClicked(mActivityFeedArrListRes.get(destinationNewPosInt));
        });

    }

    private void initPostLayout(PostTypeViewHolder holder, ActivityFeedEntity activityFeedEntity) {

        WhoDetailEntity userDetailsRes = activityFeedEntity.getActivity().getPost().getUser();
        WithEntity placeDetailsRes = activityFeedEntity.getActivity().getPost().getPlace();

        holder.mUserBgImg.setImageResource(ImageUtils.userBgTypeImage(mContext, activityFeedEntity.getActivity().getSubtype()));

        if (userDetailsRes.getPhoto().isEmpty()) {
            holder.mUserImg.setImageResource(R.drawable.profile_bg);
        } else {
            try {
                Glide.with(mContext)
                        .load(userDetailsRes.getPhoto())
                        .apply(new RequestOptions().error(R.drawable.profile_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(holder.mUserImg);
            } catch (Exception ex) {
                holder.mUserImg.setImageResource(R.drawable.profile_bg);
            }
        }

        holder.mUserNameTxt.setText(userDetailsRes.getUsername().trim().isEmpty() ? "" : userDetailsRes.getUsername() + " ");
        holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));
        holder.mPlaceNameTxt.setText(placeDetailsRes.getName());
        holder.mPlaceNameTxt.setVisibility(mFromWhereStr.equals(mContext.getString(R.string.user_recent)) ? View.GONE : View.VISIBLE);
        holder.mAddressTxt.setText(getAddress(activityFeedEntity));
        holder.mAddressTxt.setVisibility(mFromWhereStr.equals(mContext.getString(R.string.user_recent)) ? View.GONE : View.VISIBLE);

        holder.mCommentTxt.setText(Html.fromHtml("&ldquo; " + activityFeedEntity.getActivity().getPost().getComment().replaceAll(",", "") + " &rdquo;"));

        String commentStr = activityFeedEntity.getActivity().getPost().getCommentCount() + " " + (activityFeedEntity.getActivity().getPost().getCommentCount() > 1 ? mContext.getString(R.string.comments) : mContext.getString(R.string.comment));
        String likeStr = activityFeedEntity.getActivity().getPost().getLikeCount() + " " + (activityFeedEntity.getActivity().getPost().getLikeCount() > 1 ? mContext.getString(R.string.likes) : mContext.getString(R.string.like));
        holder.mCommentCountTxt.setText(commentStr);
        holder.mLikeCountTxt.setText(likeStr);
        holder.mLikeImg.setImageResource(activityFeedEntity.getActivity().getPost().isYouLike() ? R.drawable.heart_red_color_icon : R.drawable.like_icon);

        if (activityFeedEntity.getActivity().getPost().getImages().length > 0) {
            holder.mHorizontalLinearLayout.removeAllViews();
            addImageToView(activityFeedEntity.getActivity().getPost().getImages(), holder.mHorizontalLinearLayout);
        }

        holder.mUserImg.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            AppConstants.PROFILE_USER_ID = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getUser().getId();
            ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
        });

        holder.mUserNameTxt.setOnClickListener(v -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            AppConstants.PROFILE_USER_ID = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getUser().getId();
            ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
        });

        holder.mPlaceNameTxt.setOnClickListener(view -> {

            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            WithEntity placeDetailsClickRes = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getPlace();

            if (!placeDetailsClickRes.getProviderIDs().getFs().isEmpty() && !placeDetailsClickRes.getId().isEmpty()) {
                AppConstants.PLACE_NAME = placeDetailsClickRes.getId() + "%7Cfs:" + placeDetailsClickRes.getProviderIDs().getFs();
                AppConstants.pinType = placeDetailsClickRes.getPinnedAs();
                ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
            }

        });

        holder.mAddressTxt.setOnClickListener(v -> {

            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            if (!mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPlace().getCity().getId().isEmpty()) {
                AppConstants.CITY_NAME = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPlace().getCity().getId();
            } else if (mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getCity() != null
                    && !mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getCity().getId().isEmpty()) {
                AppConstants.CITY_NAME = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getCity().getId();
            } else if (!mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getPlace().getCity().getId().isEmpty()) {
                AppConstants.CITY_NAME = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getPlace().getCity().getId();
            }
            ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);


        });

        holder.mCommentImg.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            AppConstants.POST_ENTITY = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost();
            AppConstants.POST_CREATED_TIME = mActivityFeedArrListRes.get(destinationNewPosInt).getCreated();
            ((BaseActivity) mContext).nextScreen(CommentsLikesActivityScreen.class, true);
        });

        holder.mLikeImg.setOnClickListener(v -> {

            if (NetworkUtil.isNetworkAvailable(mContext)) {

                int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
                if (mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().isYouLike()) {
                    String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getId() + AppConstants.LIKE_URL;
                    APIRequestHandler.getInstance().commentUnLikeApiCall(url, mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            if (obj instanceof CommentUnlikeResponse) {
                                CommentUnlikeResponse mRespone = (CommentUnlikeResponse) obj;
                                if (mRespone.getStatus().equalsIgnoreCase(mContext.getString(R.string.succeeded))) {
                                    DialogManager.getInstance().showToast(mContext, mRespone.getStatus() + " " + mRespone.getBy() + " " + mRespone.getThe());
                                    holder.mLikeImg.setImageResource(R.drawable.like_icon);
                                    mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().setYouLike(false);
                                    int likeCountInt = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getLikeCount() - 1;
                                    mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().setLikeCount(likeCountInt);
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                } else {
                    String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getId() + AppConstants.LIKE_URL;
                    APIRequestHandler.getInstance().commentlikeApiCall(url, mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            if (obj instanceof CommentLikeResponse) {
                                CommentLikeResponse mRespone = (CommentLikeResponse) obj;
                                if (mRespone.getStatus().equalsIgnoreCase(mContext.getString(R.string.succeeded))) {
                                    DialogManager.getInstance().showToast(mContext, mRespone.getStatus() + " " + mRespone.getBy() + " " + mRespone.getThe());
                                    holder.mLikeImg.setImageResource(R.drawable.heart_red_color_icon);
                                    mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().setYouLike(true);
                                    int likeCountInt = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().getLikeCount() + 1;
                                    mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost().setLikeCount(likeCountInt);
                                    notifyDataSetChanged();

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

        holder.mSeeAllCommentTxt.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            AppConstants.POST_ENTITY = mActivityFeedArrListRes.get(destinationNewPosInt).getActivity().getPost();
            AppConstants.POST_CREATED_TIME = mActivityFeedArrListRes.get(destinationNewPosInt).getCreated();
            ((BaseActivity) mContext).nextScreen(CommentsLikesActivityScreen.class, true);
        });

        holder.itemView.setOnClickListener(view -> {
            int destinationNewPosInt = getRealPositionInt(holder.getAdapterPosition());
            mOnItemClickListener.onItemClicked(mActivityFeedArrListRes.get(destinationNewPosInt));
        });

    }


    private void addImageToView(String[] postImages, LinearLayout mHorizontalLinearLayout) {
        CardView.LayoutParams layoutParams =
                new CardView.LayoutParams(mContext.getResources().getDimensionPixelOffset(R.dimen.size80),
                        mContext.getResources().getDimensionPixelOffset(R.dimen.size60));
        int marginInt = mContext.getResources().getDimensionPixelOffset(R.dimen.size7);
        layoutParams.setMargins(0, 0, marginInt, 0);


        for (int posInt = 0; posInt < postImages.length; posInt++) {

            CardView cardView = new CardView(mContext);
            cardView.setRadius(15f);
            cardView.setLayoutParams(layoutParams);


            ImageView imageView = new ImageView(mContext);

            Glide.with(mContext)
                    .load(postImages[posInt])
                    .apply(new RequestOptions().error(R.drawable.header_bg).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                    .into(imageView);

            imageView.setLayoutParams(new CardView.LayoutParams(mContext.getResources().getDimensionPixelOffset(R.dimen.size80),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.size60)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(posInt);
            cardView.addView(imageView);
            mHorizontalLinearLayout.addView(cardView);

            imageView.setOnClickListener(view -> {
                int pos = (int) view.getTag();
                DialogManager.getInstance().showImagePopUp(mContext, postImages[pos]);
            });
        }
    }

    public class PinTypeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_bg_img)
        ImageView mUserBgImg;

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.pin_type_image)
        ImageView mPinTypeImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.place_name_txt)
        TextView mPlaceNameTxt;

        @BindView(R.id.place_type_img)
        ImageView mPlaceTypeImg;

        @BindView(R.id.like_img)
        ImageView mLikeImg;

        @BindView(R.id.comment_count_txt)
        TextView mCommentCountTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;

        @BindView(R.id.right_arrow_img)
        ImageView mRightArrowImg;


        PinTypeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class PostTypeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_bg_img)
        ImageView mUserBgImg;

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.camera_image)
        ImageView mCameraImg;

        @BindView(R.id.corner_img)
        ImageView mTypeCornerImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.see_all_txt)
        TextView mSeeAllCommentTxt;

        @BindView(R.id.like_img)
        ImageView mLikeImg;

        @BindView(R.id.comment_img)
        ImageView mCommentImg;

        @BindView(R.id.comment_count_txt)
        TextView mCommentCountTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;

        @BindView(R.id.place_name_txt)
        TextView mPlaceNameTxt;

        @BindView(R.id.horizontal_linear_lay)
        LinearLayout mHorizontalLinearLayout;

        PostTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnItemClickListener {
        void onItemClicked(ActivityFeedEntity activityFeedEntity);

    }

    private StringBuilder getAddress(ActivityFeedEntity activityFeedEntity) {
        StringBuilder address = new StringBuilder();

        if (activityFeedEntity.getActivity().getCity() != null) {
            if (!activityFeedEntity.getActivity().getCity().getName().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getCity().getName());
            }
            if (!activityFeedEntity.getActivity().getCity().getLocality().isEmpty()) {
                address.append(", ").append(activityFeedEntity.getActivity().getCity().getLocality());
            }
            if (!activityFeedEntity.getActivity().getCity().getCountry().isEmpty()) {
                address.append(", ").append(activityFeedEntity.getActivity().getCity().getCountry());
            }

        } else {
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getName().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getName()).append(", ");
            }
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getLocality().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getLocality()).append(", ");
            }
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getCountry().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getCountry());
            }
        }

        return address;
    }


    @Override
    public int getItemCount() {
        return mActivityFeedArrListRes.size();
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
