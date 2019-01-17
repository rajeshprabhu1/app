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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.CommentLikeResponse;
import com.ouam.app.model.CommentUnlikeResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CityDetailsActivityScreen;
import com.ouam.app.ui.CommentsLikesActivityScreen;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileRecentActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_PIN = 1;
    private static final int TYPE_POST = 2;
    private Context mContext;
    private ArrayList<ActivityFeedEntity> mActivityFeedList;
    private OnItemClickListener mOnItemClickListener;

    public ProfileRecentActivityAdapter(Context context,
                                        ArrayList<ActivityFeedEntity> mActivityFeedList, OnItemClickListener onItemClickListener) {
        mContext = context;
        this.mActivityFeedList = mActivityFeedList;
        mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PIN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_recent_pin_user_profile_screen, parent, false);
            return new PinTypeHolder(view);
        } else if (viewType == TYPE_POST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_recent_post_user_profile_screen, parent, false);
            return new PostTypeViewHolder(view);
        } else {
            throw new RuntimeException("The type has to be ONE or TWO");
        }
    }


    // load data in each row element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int listPosition) {
        ActivityFeedEntity activityFeedEntity = mActivityFeedList.get(listPosition);
        switch (holder.getItemViewType()) {
            case TYPE_PIN:
                initPinLayout((PinTypeHolder) holder, activityFeedEntity);
                break;
            case TYPE_POST:
                initPostLayout((PostTypeViewHolder) holder, activityFeedEntity);
                break;
            default:
                break;
        }
    }


    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        ActivityFeedEntity item = mActivityFeedList.get(position);
        if (item.getActivity().getActivityType().equals(mContext.getString(R.string.pin))) {
            return TYPE_PIN;
        } else if (item.getActivity().getActivityType().equals(mContext.getString(R.string.post))) {
            return TYPE_POST;
        } else {
            return -1;
        }
    }


    private void initPinLayout(PinTypeHolder holder, ActivityFeedEntity activityFeedEntity) {
        holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));


        holder.mPlaceNameTxt.setVisibility(activityFeedEntity.getActivity().getPlace().getName().isEmpty() ? View.GONE : View.VISIBLE);

        holder.mPlaceNameTxt.setText(activityFeedEntity.getActivity().getPlace().getName());
        if (activityFeedEntity.getActivity().getPlace().getCity() != null) {
            holder.mAddressTxt.setText(getPlaceAddress(activityFeedEntity));
        }
        if (activityFeedEntity.getActivity().getCity() != null) {
            holder.mAddressTxt.setText(getAddress(activityFeedEntity));
        }

        holder.mTravelStatusTxt.setText(activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("beenthere") ? "Been There / Done That" : activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("tobeexplored") ? "To Be Explored / Done That" : activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("hiddengem") ? "Hidden Gem / Done That" : "");
        Glide.with(mContext)
                .load(activityFeedEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_been_there)) ? R.drawable.discovered_map_pin : activityFeedEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_to_be_explored)) ? R.drawable.undiscovered_map_pin :
                        R.drawable.hidden_map_pin)
                .into(holder.mPinTypeImg);
        Glide.with(mContext)
                .load(activityFeedEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_been_there)) ? R.drawable.place_type_discovered : activityFeedEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_to_be_explored)) ? R.drawable.place_type_un_discovered :
                        R.drawable.place_type_hidden_gem)
                .into(holder.mUserPinImg);
        holder.mPlaceNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activityFeedEntity.getActivity().getPlace().getId().isEmpty() && !activityFeedEntity.getActivity().getPlace().getProviderIDs().getFs().isEmpty()) {
                    AppConstants.PLACE_NAME = activityFeedEntity.getActivity().getPlace().getId() + "%7Cfs:" + activityFeedEntity.getActivity().getPlace().getProviderIDs().getFs();
                    AppConstants.pinType = activityFeedEntity.getActivity().getPlace().getPinnedAs();
                    ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
                }

            }
        });

        holder.mAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activityFeedEntity.getActivity().getPlace().getCity().getId().isEmpty()) {
                    AppConstants.CITY_NAME = activityFeedEntity.getActivity().getPlace().getCity().getId();
                    ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);
                } else if (!activityFeedEntity.getActivity().getCity().getId().isEmpty()) {
                    AppConstants.CITY_NAME = activityFeedEntity.getActivity().getCity().getId();
                    ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {

            if (!activityFeedEntity.getActivity().getPlace().getProviderIDs().getFs().isEmpty() && !activityFeedEntity.getActivity().getPlace().getId().isEmpty()) {
                AppConstants.PLACE_NAME = activityFeedEntity.getActivity().getPlace().getId() + "%7Cfs:" + activityFeedEntity.getActivity().getPlace().getProviderIDs().getFs();
                AppConstants.pinType = activityFeedEntity.getActivity().getPlace().getPinnedAs();
                ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
            }

        });
        String commentStr = activityFeedEntity.getActivity().getCommentCount() + " " + (activityFeedEntity.getActivity().getCommentCount() > 1 ? mContext.getString(R.string.comments) : mContext.getString(R.string.comment));
        String likeStr = activityFeedEntity.getActivity().getLikeCount() > 1 ? activityFeedEntity.getActivity().getLikeCount() + " Likes" : activityFeedEntity.getActivity().getLikeCount() + " Like";
        holder.mLikeImg.setImageResource(activityFeedEntity.getActivity().isYouLike() ? R.drawable.heart_red_color_icon : R.drawable.like_icon);
        holder.mCommentCountTxt.setText(commentStr);
        holder.mLikeCountTxt.setText(likeStr);
    }

    private void initPostLayout(PostTypeViewHolder holder, ActivityFeedEntity activityFeedEntity) {
        holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));
        holder.mCommentTxt.setText(Html.fromHtml("&ldquo; " + activityFeedEntity.getActivity().getPost().getComment().replaceAll(",", "") + " &rdquo;"));

        holder.mPlaceTxt.setText(activityFeedEntity.getActivity().getPost().getPlace().getName());
//        holder.mAddressTxt.setText(getAddress(activityFeedEntity));

        if (activityFeedEntity.getActivity().getPost().getImages().length > 0) {
            holder.mHorizantalLinearLayout.setVisibility(View.VISIBLE);
            holder.mHorizantalLinearLayout.removeAllViews();
            addImageToView(activityFeedEntity.getActivity().getPost().getImages(), holder.mHorizantalLinearLayout);
        } else {
            holder.mHorizantalLinearLayout.setVisibility(View.GONE);
        }


//        holder.mAddressTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!activityFeedEntity.getActivity().getPlace().getCity().getId().isEmpty()) {
//                    AppConstants.CITY_NAME = activityFeedEntity.getActivity().getPlace().getCity().getId();
//                } else if (activityFeedEntity.getActivity().getCity() != null
//                        && !activityFeedEntity.getActivity().getCity().getId().isEmpty()) {
//                    AppConstants.CITY_NAME = activityFeedEntity.getActivity().getCity().getId();
//                } else if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getId().isEmpty()) {
//                    AppConstants.CITY_NAME = activityFeedEntity.getActivity().getPost().getPlace().getCity().getId();
//
//                }
//                ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);
//
//
//            }
//        });


        holder.mPlaceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activityFeedEntity.getActivity().getPost().getPlace().getProviderIDs().getFs().isEmpty() && !activityFeedEntity.getActivity().getPost().getPlace().getId().isEmpty()) {
                    AppConstants.PLACE_NAME = activityFeedEntity.getActivity().getPost().getPlace().getId() + "%7Cfs:" + activityFeedEntity.getActivity().getPost().getPlace().getProviderIDs().getFs();
                    AppConstants.pinType = activityFeedEntity.getActivity().getPost().getPlace().getPinnedAs();
                    ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
                }

            }
        });


        String commentStr = activityFeedEntity.getActivity().getPost().getCommentCount() > 1 ? activityFeedEntity.getActivity().getPost().getCommentCount() + " Comments" : activityFeedEntity.getActivity().getPost().getCommentCount() + " Comment";
        String likeStr = activityFeedEntity.getActivity().getPost().getLikeCount() > 1 ? activityFeedEntity.getActivity().getPost().getLikeCount() + " Likes" : activityFeedEntity.getActivity().getPost().getLikeCount() + " Like";
        holder.mCommentLikeImg.setImageResource(activityFeedEntity.getActivity().getPost().isYouLike() ? R.drawable.heart_red_color_icon : R.drawable.like_icon);
        holder.mCommentCountTxt.setText(commentStr);
        holder.mLikeCountTxt.setText(likeStr);


        holder.mCommentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConstants.POST_ENTITY = activityFeedEntity.getActivity().getPost();
                AppConstants.POST_CREATED_TIME = activityFeedEntity.getCreated();
                ((BaseActivity) mContext).nextScreen(CommentsLikesActivityScreen.class, true);
            }
        });

//        holder.mSeeAllCommentTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppConstants.POST_ENTITY = activityFeedEntity.getActivity().getPost();
//                AppConstants.POST_CREATED_TIME = activityFeedEntity.getCreated();
//                ((BaseActivity) mContext).nextScreen(CommentsLikesActivityScreen.class, true);
//            }
//        });


        holder.mCommentLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtil.isNetworkAvailable(mContext)) {
                    if (activityFeedEntity.getActivity().getPost().isYouLike()) {
                        String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + activityFeedEntity.getActivity().getPost().getId() + AppConstants.LIKE_URL;
                        APIRequestHandler.getInstance().commentUnLikeApiCall(url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                if (obj instanceof CommentUnlikeResponse) {
                                    CommentUnlikeResponse mRespone = (CommentUnlikeResponse) obj;
                                    if (mRespone.getStatus().equalsIgnoreCase(mContext.getString(R.string.succeeded))) {
                                        DialogManager.getInstance().showToast(mContext, mRespone.getStatus() + " " + mRespone.getBy() + " " + mRespone.getThe());
                                        holder.mCommentLikeImg.setImageResource(R.drawable.like_icon);
                                        activityFeedEntity.getActivity().getPost().setYouLike(false);
                                        int likeCountInt = activityFeedEntity.getActivity().getPost().getLikeCount() - 1;
//                                        holder.mLikeCountTxt.setText(likeCountInt > 1 ? likeCountInt + " Likes" : likeCountInt + " Like");
                                        activityFeedEntity.getActivity().getPost().setLikeCount(likeCountInt);
                                        notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onRequestFailure(Throwable r) {

                            }
                        });
                    } else {
                        String url = AppConstants.BASE_URL + AppConstants.POST_COMMENT_URL + activityFeedEntity.getActivity().getPost().getId() + AppConstants.LIKE_URL;
                        APIRequestHandler.getInstance().commentlikeApiCall(url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                if (obj instanceof CommentLikeResponse) {
                                    CommentLikeResponse mRespone = (CommentLikeResponse) obj;
                                    if (mRespone.getStatus().equalsIgnoreCase(mContext.getString(R.string.succeeded))) {
                                        DialogManager.getInstance().showToast(mContext, mRespone.getStatus() + " " + mRespone.getBy() + " " + mRespone.getThe());
                                        holder.mCommentLikeImg.setImageResource(R.drawable.heart_red_color_icon);
                                        activityFeedEntity.getActivity().getPost().setYouLike(true);
                                        int likeCountInt = activityFeedEntity.getActivity().getPost().getLikeCount() + 1;
//                                        holder.mLikeCountTxt.setText(likeCountInt > 1 ? likeCountInt + " Likes" : likeCountInt + " Like");
                                        activityFeedEntity.getActivity().getPost().setLikeCount(likeCountInt);
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

            }
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

//            cardView.setBackground(mContext.getResources().getDrawable(R.color.transparent));
//            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.transparent));
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

        @BindView(R.id.pin_type_image)
        ImageView mPinTypeImg;


        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.place_name_txt)
        TextView mPlaceNameTxt;

        @BindView(R.id.travel_status_txt)
        TextView mTravelStatusTxt;

        @BindView(R.id.user_pin_img)
        ImageView mUserPinImg;

        @BindView(R.id.comment_count_txt)
        TextView mCommentCountTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;

        @BindView(R.id.like_img)
        ImageView mLikeImg;

        public PinTypeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class PostTypeViewHolder extends RecyclerView.ViewHolder {

//
//        @BindView(R.id.address_txt)
//        TextView mAddressTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.place_txt)
        TextView mPlaceTxt;

        @BindView(R.id.horizontal_linear_lay)
        LinearLayout mHorizantalLinearLayout;

        @BindView(R.id.like_img)
        ImageView mCommentLikeImg;

        @BindView(R.id.comment_count_txt)
        TextView mCommentCountTxt;

        @BindView(R.id.like_count_txt)
        TextView mLikeCountTxt;


        @BindView(R.id.comment_txt)
        TextView mCommentTxt;

        @BindView(R.id.comment_img)
        ImageView mCommentImg;

//        @BindView(R.id.see_all_txt)
//        TextView mSeeAllCommentTxt;


        public PostTypeViewHolder(View itemView) {
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
                address.append(activityFeedEntity.getActivity().getCity().getName() + ", ");
            }
            if (!activityFeedEntity.getActivity().getCity().getLocality().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getCity().getLocality() + ", ");
            }
            if (!activityFeedEntity.getActivity().getCity().getCountry().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getCity().getCountry());
            }

        } else {
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getName().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getName() + ", ");
            }
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getLocality().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getLocality() + ", ");
            }
            if (!activityFeedEntity.getActivity().getPost().getPlace().getCity().getCountry().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPost().getPlace().getCity().getCountry());
            }

        }
        return address;
    }

    private StringBuilder getPlaceAddress(ActivityFeedEntity activityFeedEntity) {
        StringBuilder address = new StringBuilder();

        if (activityFeedEntity.getActivity().getPlace().getCity() != null) {
            if (!activityFeedEntity.getActivity().getPlace().getCity().getName().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPlace().getCity().getName() + ", ");
            }
            if (!activityFeedEntity.getActivity().getPlace().getCity().getLocality().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPlace().getCity().getLocality() + ", ");
            }
            if (!activityFeedEntity.getActivity().getPlace().getCity().getCountry().isEmpty()) {
                address.append(activityFeedEntity.getActivity().getPlace().getCity().getCountry());
            }

        }
        return address;
    }

    @Override
    public int getItemCount() {
        return mActivityFeedList.size();
    }
}
