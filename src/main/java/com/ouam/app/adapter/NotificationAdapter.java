package com.ouam.app.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ouam.app.R;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    private Context mContext;
    private ArrayList<WithEntity> mWithArrayList = new ArrayList<>();

    public NotificationAdapter(Context activity, ArrayList<WithEntity> with) {
        mContext = activity;
        mWithArrayList = with;
    }

    @NonNull
    @Override
    public NotificationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_notification_screen, parent, false);
        return new NotificationAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.Holder holder, int position) {

        final WithEntity withEntityList = mWithArrayList.get(position);

        if (withEntityList!=null){
            holder.mMessageTxt.setText(withEntityList.getActivity().getMessage());
            holder.mNotificationTxt.setText(DateUtil.getDateTimeDiff(withEntityList.getCreated(),true));

            if (withEntityList.getActivity().getType().equalsIgnoreCase("post_like")){
                holder.mUserNameTxt.setText(withEntityList.getActivity().getLikingUser().getUsername());
//                holder.mLikeImg.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!withEntityList.getActivity().getLikingUser().getId().isEmpty() || withEntityList.getActivity().getLikingUser().getId() != null) {
                            AppConstants.PROFILE_USER_ID = withEntityList.getActivity().getLikingUser().getId();
                            ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
                        }
                    }
                });
                try {
                    holder.mUserImage.setVisibility(View.VISIBLE);

                    Glide
                            .with(mContext)
                            .load(withEntityList.getActivity().getLikingUser().getPhoto())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    holder.mUserImage.setImageDrawable(resource); // Possibly runOnUiThread()

                                }
                            });

                } catch (Exception ex) {
                    holder.mUserImage.setImageResource(R.color.dark_blue);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }


            }
            if (withEntityList.getActivity().getType().equalsIgnoreCase("post_comment")){
                holder.mUserNameTxt.setText(withEntityList.getActivity().getCommentingUser().getUsername());
//                holder.mLikeImg.setVisibility(View.GONE);
              //  holder.mNotificationTxt.setText(withEntityList.getAddress());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!withEntityList.getActivity().getCommentingUser().getId().isEmpty() || withEntityList.getActivity().getCommentingUser().getId() != null) {
                            AppConstants.PROFILE_USER_ID = withEntityList.getActivity().getCommentingUser().getId();
                            ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
                        }
                    }
                });
                try {
                    holder.mUserImage.setVisibility(View.VISIBLE);

                    Glide
                            .with(mContext)
                            .load(withEntityList.getActivity().getCommentingUser().getPhoto())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                                    holder.mUserImage.setImageDrawable(resource); // Possibly runOnUiThread()

                                }
                            });

                } catch (Exception ex) {
                    holder.mUserImage.setImageResource(R.color.dark_blue);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
            if (withEntityList.getActivity().getType().equalsIgnoreCase("user_followed")){
                holder.mUserNameTxt.setText(withEntityList.getActivity().getUser().getUsername());
//                holder.mLikeImg.setVisibility(View.VISIBLE);
              //  holder.mNotificationTxt.setText(withEntityList.getAddress());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!withEntityList.getActivity().getUser().getId().isEmpty() || withEntityList.getActivity().getUser().getId() != null) {
                            AppConstants.PROFILE_USER_ID = withEntityList.getActivity().getUser().getId();
                            ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
                        }
                    }
                });
                try {
                    holder.mUserImage.setVisibility(View.VISIBLE);

                    Glide
                            .with(mContext)
                            .load(withEntityList.getActivity().getUser().getPhoto())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    holder.mUserImage.setImageDrawable(resource); // Possibly runOnUiThread()

                                }
                            });

                } catch (Exception ex) {
                    holder.mUserImage.setImageResource(R.color.dark_blue);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
        }

    }


    @Override
    public int getItemCount() {
        return mWithArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        RoundedImageView mUserImage;

//        @BindView(R.id.edit_profile_img)
//        ImageView mLikeImg;

        @BindView(R.id.notify_user_name)
        TextView mUserNameTxt;

        @BindView(R.id.notify_message)
        TextView mMessageTxt;

//        @BindView(R.id.notify_city)
//        TextView mNotifyCity;

        @BindView(R.id.notification_hrs_txt)
        TextView mNotificationTxt;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}


