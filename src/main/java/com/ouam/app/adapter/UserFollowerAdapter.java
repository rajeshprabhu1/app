package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFollowerAdapter extends RecyclerView.Adapter<UserFollowerAdapter.Holder> {

    private Context mContext;
    private ArrayList<WhoDetailEntity> mWhoList = new ArrayList<>();

    public UserFollowerAdapter(Context context, ArrayList<WhoDetailEntity> whoList) {
        mContext = context;
        mWhoList = whoList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_follower_screen, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final WhoDetailEntity whoDetailEntity = mWhoList.get(position);

        if (whoDetailEntity != null) {

            if (whoDetailEntity.getPhoto().isEmpty()) {
                holder.mUserImgLay.setVisibility(View.GONE);
            } else {
                try {
                    holder.mUserImgLay.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(whoDetailEntity.getPhoto())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                            .into(holder.mUserImg);

                } catch (Exception ex) {
                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
            holder.mUserNameTxt.setText(whoDetailEntity.getUsername());
            /*place holder for addressTxt*/
//            holder.mAddressStr = (whoDetailEntity.getCity().getName().isEmpty() ? "" : whoDetailEntity.getCity().getName() + ", ") + (whoDetailEntity.getCity().getLocality().isEmpty() ? "" : whoDetailEntity.getCity().getLocality() + ", ") + (whoDetailEntity.getCity().getCountry().isEmpty() ? "" : whoDetailEntity.getCity().getCountry());
//            holder.mAddressTxt.setText(holder.mAddressStr);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId=mWhoList.get(holder.getAdapterPosition()).getId();
                if (!userId.isEmpty()) {
                    AppConstants.PROFILE_USER_ID = userId;
//                    ((UserProfileActivityScreen) mContext).nextScreen(UserProfileActivityScreen.class, true);
                    ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mWhoList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

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

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        private String mAddressStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
