package com.ouam.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ActivityFeedAdapter extends RecyclerView.Adapter<ActivityFeedAdapter.Holder> {

    private Context mContext;
    //    public static final int VIEW_TYPE_ODD = 1;
//    public static final int VIEW_TYPE_EVEN = 2;
    private InterfaceBtnCallBack interfaceBtnCallBack;
    private ArrayList<ActivityFeedEntity> mActivityFeedList = new ArrayList<>();

    public ActivityFeedAdapter(Context activity, ArrayList<ActivityFeedEntity> activityFeedEntityArrayList, InterfaceBtnCallBack interfaceBtnCallBack) {
        mContext = activity;
        mActivityFeedList = activityFeedEntityArrayList;
        this.interfaceBtnCallBack = interfaceBtnCallBack;
    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_activity_feed_row, parent, false);
////        return new Holder(view);
//
//
//        View view;
////        switch (viewType) {
////            case VIEW_TYPE_ODD:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_activity_feed_row, parent, false);
//                return new ActivityFeedAdapter(view);
////            case VIEW_TYPE_EVEN:
////                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_details_feed_row, parent, false);
////                return new FeedDetailsTypeViewHolder(view);
////        }
////        return null;
//
//    }

//    @Override
//    public void onBindViewHolder(final ChatAdapter.Holder holder, int position) {
//        final ActivityFeedEntity activityFeedEntity = mActivityFeedList.get(position);
//
//        if (mActivityFeedList.size() != 0){
//
//        }
////        if (position % 2 != 0)
////            ((FeedDetailsTypeViewHolder) holder).mSeeAllCommentTxt.setOnClickListener(new View.OnClickListener() {
////
////                @Override
////                public void onClick(View view) {
////                    interfaceBtnCallBack.onPositiveClick();
////                }
////            });
//    }


//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }
//
//    @Override
//    public int getItemCount() {
//        return mActivityFeedList.size();
//    }
//
//    public class FeedTypeViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.user_img_lay)
//        RelativeLayout mUserImgLay;
//
//        @BindView(R.id.user_image)
//        ImageView mUserImg;
//
//        @BindView(R.id.user_name_txt)
//        TextView mUserNameTxt;
//
//        @BindView(R.id.hours_txt)
//        TextView mHoursTxt;
//
//        @BindView(R.id.travel_status_txt)
//        TextView mTravelStatusTxt;
//
//
//        public FeedTypeViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }

    //    public class FeedDetailsTypeViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.see_all_txt)
//        TextView mSeeAllCommentTxt;
//
//        public FeedDetailsTypeViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
    @NonNull
    @Override
    public ActivityFeedAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_activity_feed_row, parent, false);
        return new ActivityFeedAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActivityFeedAdapter.Holder holder, int position) {
        final ActivityFeedEntity activityFeedEntity = mActivityFeedList.get(position);

        if (activityFeedEntity != null) {

            /*activity type pin*/
            if (activityFeedEntity.getActivity().getActivityType().equals("pin")) {

                if (activityFeedEntity.getActivity().getUser().getPhoto().isEmpty()) {
                    holder.mUserImg.setVisibility(View.GONE);
                } else {
                    try {
                        holder.mUserImg.setVisibility(View.VISIBLE);

                        Glide
                                .with(mContext)
                                .load(activityFeedEntity.getActivity().getUser().getPhoto())
                                .into(holder.mUserImg);


                    } catch (Exception ex) {
                        holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                        Log.e(AppConstants.TAG, ex.getMessage());
                    }
                }

                holder.mRoundUserImgBorder.setImageResource(activityFeedEntity.getActivity().getSubtype().equals("beenthere") ? R.color.accent_been_there_red : activityFeedEntity.getActivity().getSubtype().equals("tobeexplored") ? R.color.accent_explore_blue : R.color.accent_hidden_gem_violet);
                holder.mUserNameTxt.setText((activityFeedEntity.getActivity().getUser().getUsername().isEmpty() ? "" : activityFeedEntity.getActivity().getUser().getUsername() + "  ") + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : activityFeedEntity.getActivity().getActivityType()));

            } else {

                /*activity type post*/

                if (activityFeedEntity.getActivity().getPost() != null) {
                    holder.mUserNameTxt.setText((activityFeedEntity.getActivity().getPost().getUser().getUsername().isEmpty() ? "" : activityFeedEntity.getActivity().getPost().getUser().getUsername() + "  ") + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : activityFeedEntity.getActivity().getActivityType()));
                    holder.mRoundUserImgBorder.setImageResource(R.color.black);

                    if (activityFeedEntity.getActivity().getPost().getUser().getPhoto().isEmpty()) {
                        holder.mUserImg.setVisibility(View.GONE);
                    } else {
                        try {
                            holder.mUserImg.setVisibility(View.VISIBLE);

                            Glide
                                    .with(mContext)
                                    .load(activityFeedEntity.getActivity().getPost().getUser().getPhoto())
                                    .into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                            holder.mUserImg.setImageDrawable(resource); // Possibly runOnUiThread()

                                        }
                                    });

                        } catch (Exception ex) {
                            holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                            Log.e(AppConstants.TAG, ex.getMessage());
                        }
                    }


                }

            }
            holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));
            holder.mTravelStatusTxt.setText(activityFeedEntity.getActivity().getSubtype());

//            if (activityFeedEntity.getActivity().getPlace() != null) {
//                if (activityFeedEntity.getActivity().getUser().getPhoto().isEmpty()) {
//                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//                } else {
//                    try {
//                        Glide.with(mContext)
//                                .load(activityFeedEntity.getActivity().getUser().getPhoto())
//                                .into(holder.mUserImg);
//                    } catch (Exception ex) {
//                        holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//                        Log.e(AppConstants.TAG, ex.getMessage());
//                    }
//                }
//
//                holder.mRoundUserImgBorder.setImageResource(activityFeedEntity.getActivity().getSubtype().equals("beenthere") ? R.color.accent_been_there_red : activityFeedEntity.getActivity().getSubtype().equals("tobeexplored") ? R.color.accent_explore_blue : activityFeedEntity.getActivity().getSubtype().equals("hiddengem") ? R.color.accent_hidden_gem_violet : R.color.black);
//
//            /*address and username place holder strings*/
//                holder.mAddressStr = (activityFeedEntity.getActivity().getPlace().getName().isEmpty() ? "" : activityFeedEntity.getActivity().getPlace().getName());
//                holder.mUserNameStr = (activityFeedEntity.getActivity().getUser().getUsername().isEmpty() ? "" : activityFeedEntity.getActivity().getUser().getUsername() + "  ") + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : activityFeedEntity.getActivity().getActivityType());
//                Log.d("USER_NAME", "" + activityFeedEntity.getActivity().getUser().getUsername());
//
//                holder.mUserNameTxt.setText(holder.mUserNameStr);
//                holder.mHoursTxt.setText(activityFeedEntity.getActivity().getPlace().getCategory().isEmpty() ? "" : activityFeedEntity.getActivity().getPlace().getCategory());
////                holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(activityFeedEntity.getTimestamp(), true));
//                holder.mTravelStatusTxt.setText(activityFeedEntity.getActivity().getSubtype().isEmpty() ? "" : activityFeedEntity.getActivity().getSubtype());
//                holder.mAddressTxt.setText(holder.mAddressStr);
//            }
//
//            if (activityFeedEntity.getActivity().getPost() != null) {
//                if (activityFeedEntity.getActivity().getPost().getUser().getPhoto().isEmpty()) {
//                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//                } else {
//                    try {
//                        Glide.with(mContext)
//                                .load(activityFeedEntity.getActivity().getPost().getUser().getPhoto())
//                                .into(holder.mUserImg);
//                    } catch (Exception ex) {
//                        holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//                        Log.e(AppConstants.TAG, ex.getMessage());
//                    }
//                }
//            /*address and username place holder strings*/
//                holder.mAddressStr = (activityFeedEntity.getActivity().getPost().getPlace().getName().isEmpty() ? "" : activityFeedEntity.getActivity().getPost().getPlace().getName());
//                holder.mUserNameStr = (activityFeedEntity.getActivity().getPost().getUser().getUsername().isEmpty() ? "" : activityFeedEntity.getActivity().getPost().getUser().getUsername() + "  ") + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : activityFeedEntity.getActivity().getActivityType());
//
//                holder.mUserNameTxt.setText(holder.mUserNameStr);
//                holder.mHoursTxt.setText(activityFeedEntity.getActivity().getPost().getPlace().getCategory().isEmpty() ? "" : activityFeedEntity.getActivity().getPost().getPlace().getCategory());
//                holder.mTravelStatusTxt.setText(activityFeedEntity.getActivity().getEntityType().isEmpty() ? "" : activityFeedEntity.getActivity().getEntityType());
//                holder.mAddressTxt.setText(holder.mAddressStr);
//            }


//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AppConstants.USER_NAME = activityFeedEntity.getWho().getUsername();
//                    ((HomeActivityFeedScreen) mContext).nextScreen(UserProfileActivityScreen.class, true);
//                }
//            });
        }

    }


    @Override
    public int getItemCount() {
        return mActivityFeedList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.travel_status_txt)
        TextView mTravelStatusTxt;

        @BindView(R.id.user_bg_img)
        RoundedImageView mRoundUserImgBorder;

        private String mAddressStr = "", mUserNameStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
