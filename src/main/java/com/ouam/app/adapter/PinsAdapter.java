package com.ouam.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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


public class PinsAdapter extends RecyclerView.Adapter<PinsAdapter.Holder> {

    private Context mContext;
    private ArrayList<WhoDetailEntity> mWithList = new ArrayList<>();

    public PinsAdapter(Context activity, ArrayList<WhoDetailEntity> withEntityArrayList) {
        mContext = activity;
        mWithList = withEntityArrayList;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_been_there_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final WhoDetailEntity withEntity = mWithList.get(position);

        if (withEntity != null) {
            if (withEntity.getPhoto().isEmpty()) {
                holder.mUserImgLay.setVisibility(View.GONE);
            } else {
                try {
                    holder.mUserImgLay.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(withEntity.getPhoto())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                            .into(holder.mUserImg);
                } catch (Exception ex) {
                    holder.mUserImg.setImageResource(R.drawable.demo_img);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }

//            holder.mCreatedTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(withEntity.getCreated()), true));
            holder.mUserNameTxt.setText(withEntity.getUsername().isEmpty() ? "" : withEntity.getUsername());
            holder.mUserPinEmailTxt.setText((withEntity.getCity().getName().isEmpty() ? "" : withEntity.getCity().getName()) + (withEntity.getCity().getLocality().isEmpty() ? "" : withEntity.getCity().getLocality() + ", ") + (withEntity.getCity().getCountry().isEmpty() ? "" : withEntity.getCity().getCountry()));
            String pinStr = "0" + " " + mContext.getString(R.string.pins);
            String followerStr =  "0" + " " + mContext.getString(R.string.followers);
            holder.mPinCountTxt.setText(pinStr);
            holder.mFollowerCountTxt.setText(followerStr);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (withEntity.getId() != null && !withEntity.getId().isEmpty()) {
                        AppConstants.PROFILE_USER_ID = withEntity.getId();
                        ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);

                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mWithList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.pins_user_name)
        TextView mUserNameTxt;

        @BindView(R.id.pins_user_email)
        TextView mUserPinEmailTxt;

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        @BindView(R.id.created_txt)
        TextView mCreatedTxt;

        @BindView(R.id.follower_count_txt)
        TextView mFollowerCountTxt;

        @BindView(R.id.pin_count_txt)
        TextView mPinCountTxt;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
