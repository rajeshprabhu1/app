package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.entity.PeopleSearchEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.FollowResponse;
import com.ouam.app.model.UnFollowResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.SearchActivityScreen;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleSearchAdapter extends RecyclerView.Adapter<PeopleSearchAdapter.Holder> {

    private Context mContext;
    private ArrayList<PeopleSearchEntity> mPeopleSearchList = new ArrayList<>();
    private String mUserIDStr = "";

    public PeopleSearchAdapter(Context activity, String mUserIDStr, ArrayList<PeopleSearchEntity> peopleSearchList) {
        mPeopleSearchList = peopleSearchList;
        mContext = activity;
        this.mUserIDStr = mUserIDStr;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_people_search_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final PeopleSearchEntity peopleSearchEntity = mPeopleSearchList.get(position);

        if (peopleSearchEntity != null) {
            if (peopleSearchEntity.getUser().getPhoto().isEmpty()) {
                holder.mUserImgLay.setVisibility(View.GONE);
            } else {
                try {
                    holder.mUserImgLay.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(peopleSearchEntity.getUser().getPhoto())
                            .into(holder.mUserImg);
                } catch (Exception ex) {
                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
        }
        holder.mUserNameTxt.setText(peopleSearchEntity.getUser().getUsername());
        /*place holder str for locationTxt*/
        holder.mLocationTxt.setText((peopleSearchEntity.getUser().getCity().getName().isEmpty() ? "" : peopleSearchEntity.getUser().getCity().getName() + ", " + (peopleSearchEntity.getUser().getCity().getLocality().isEmpty() ? "" : peopleSearchEntity.getUser().getCity().getLocality()) + ", ") + (peopleSearchEntity.getUser().getCity().getCountry().isEmpty() ? "" : peopleSearchEntity.getUser().getCity().getCountry()));


        holder.mFollowBtn.setVisibility(peopleSearchEntity.getUser().getId().equals(mUserIDStr) ? View.INVISIBLE : View.VISIBLE);
        holder.mFollowBtn.setText(peopleSearchEntity.isYouFollow() ? mContext.getString(R.string.following).toUpperCase(Locale.US) : mContext.getString(R.string.follow).toUpperCase(Locale.US));
        holder.mFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mFollowBtn.getText().toString().equalsIgnoreCase(mContext.getString(R.string.follow))) {
                    holder.mFollowBtn.setText(mContext.getString(R.string.following).toUpperCase(Locale.US));
                    holder.mFollowBtn.setBackground(mContext.getResources().getDrawable(R.drawable.following_btn));
                    holder.mFollowBtn.setTextColor(mContext.getResources().getColor(R.color.white));
                    APIRequestHandler.getInstance().followApi(peopleSearchEntity.getUser().getId(), mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            FollowResponse mResponse = (FollowResponse) obj;
                            if (mResponse.getStatus().equals(mContext.getString(R.string.succeeded))) {
                                peopleSearchEntity.setYouFollow(true);
                            }

                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                } else {
                    holder.mFollowBtn.setText(mContext.getString(R.string.follow));
                    APIRequestHandler.getInstance().unFollowApi(peopleSearchEntity.getUser().getId(), mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            UnFollowResponse mResponse = (UnFollowResponse) obj;
                            if (mResponse.getStatus().equals(mContext.getString(R.string.succeeded))) {
                                peopleSearchEntity.setYouFollow(false);
                            }
                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                }

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!peopleSearchEntity.getUser().getId().isEmpty() || peopleSearchEntity.getUser() != null) {
                    AppConstants.PROFILE_USER_ID = peopleSearchEntity.getUser().getId();
                    ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mPeopleSearchList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.locatin_txt)
        TextView mLocationTxt;

        @BindView(R.id.follow_btn)
        Button mFollowBtn;

        @BindView(R.id.parent_lay)
        LinearLayout mParentLay;

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        private String mLocationStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
