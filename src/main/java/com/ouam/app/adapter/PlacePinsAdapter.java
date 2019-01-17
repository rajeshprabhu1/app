package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ouam.app.R;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacePinsAdapter  extends RecyclerView.Adapter<PlacePinsAdapter.Holder> {

    private Context mContext;

    private ArrayList<WhoDetailEntity> mWhoDetailList = new ArrayList<>();

    public PlacePinsAdapter(Context context, ArrayList<WhoDetailEntity> whoDetailList) {
        mContext = context;
        mWhoDetailList = whoDetailList;

    }

    @NonNull
    @Override
    public PlacePinsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_pins_row, parent, false);
        return new PlacePinsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlacePinsAdapter.Holder holder, int position) {
        final WhoDetailEntity whoDetailEntity = mWhoDetailList.get(position);

        if (whoDetailEntity != null) {

            if (whoDetailEntity.getPhoto().isEmpty()) {
                holder.mUserImgLay.setVisibility(View.GONE);
            } else {
                try {
                    holder.mUserImgLay.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(whoDetailEntity.getPhoto())
                            .into(holder.mUserImg);
                } catch (Exception ex) {
                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
            holder.mUserNameTxt.setText((whoDetailEntity).getUsername().isEmpty() ? "" : whoDetailEntity.getUsername());
            /*place holder str for addressTxt*/
            holder.mAddressStr = (whoDetailEntity.getHomeCity().getName().isEmpty() ? "" : whoDetailEntity.getHomeCity().getName() + ", ") + (whoDetailEntity.getHomeCity().getLocality().isEmpty() ? "" : whoDetailEntity.getHomeCity().getLocality() + ", ") + (whoDetailEntity.getHomeCity().getCountry().isEmpty() ? "" : whoDetailEntity.getHomeCity().getCountry());
            holder.mAddressTxt.setText(whoDetailEntity.getEmail());

            holder.mPinImg.setImageResource(whoDetailEntity.getPinType().equals( mContext.getString(R.string.type_been_there)) ? R.drawable.check_icon : whoDetailEntity.getPinType().equals(mContext.getString(R.string.type_explore)) ? R.drawable.plane_icon : R.drawable.gem_icon);
        }

    }


    @Override
    public int getItemCount() {
        return mWhoDetailList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        RoundedImageView mUserImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;


        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.pins_img)
        ImageView mPinImg;

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        private String mAddressStr;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
