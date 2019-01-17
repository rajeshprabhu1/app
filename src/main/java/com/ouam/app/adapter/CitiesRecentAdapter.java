package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class CitiesRecentAdapter extends RecyclerView.Adapter<CitiesRecentAdapter.Holder> {

    private Context mContext;
    private ArrayList<WithEntity> mPlaceList;


    public CitiesRecentAdapter(Context activity, ArrayList<WithEntity> placeList) {
        mContext = activity;
        mPlaceList = placeList;

    }

    @NonNull
    @Override
    public CitiesRecentAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_cities_recent, parent, false);
        return new CitiesRecentAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CitiesRecentAdapter.Holder holder, int position) {

        WithEntity withEntity = mPlaceList.get(position);
        if (withEntity != null) {

            holder.mPinTypeImg.setImageResource(withEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_been_there)) ? R.drawable.been_there_icon : withEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_to_be_explored)) ? R.drawable.to_be_explored_icon : R.drawable.hidden_gem_map_pin);

            if (withEntity.getActivity().getUser().getPhoto().isEmpty()) {
                holder.mUserImg.setVisibility(View.GONE);
            } else {
                try {
                    holder.mUserImg.setVisibility(View.VISIBLE);

                    Glide
                            .with(mContext)
                            .load(withEntity.getActivity().getUser().getPhoto())
                            .into(holder.mUserImg);

                } catch (Exception ex) {


                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
                }
            }

            holder.mRoundUserImgBorder.setImageResource(withEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_been_there)) ? R.color.accent_been_there_red : withEntity.getActivity().getSubtype().equals(mContext.getString(R.string.sub_type_to_be_explored)) ? R.color.accent_explore_blue : R.color.accent_hidden_gem_violet);

//            holder.mPlaceNameTxt.setText(withEntity.getActivity().getPlace().getName());
            holder.mPlaceNameTxt.setVisibility(View.GONE);

//            holder.mAddressTxt.setVisibility(withEntity.getActivity().getCity() != null ? View.VISIBLE : View.GONE);
//            holder.mAddressTxt.setText(getAddress(withEntity));
            holder.mAddressTxt.setVisibility(View.GONE);

            holder.mUserNameTxt.setText(withEntity.getActivity().getUser().getUsername().trim().isEmpty() ? "" : withEntity.getActivity().getUser().getUsername() + " ");

            holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(withEntity.getCreated()), true));

            holder.mTravelStatusTxt.setText(withEntity.getActivity().getSubtype().equalsIgnoreCase("beenthere") ? "Been There / Done That" : withEntity.getActivity().getSubtype().equalsIgnoreCase("tobeexplored") ? "To Be Explored / Done That" : withEntity.getActivity().getSubtype().equalsIgnoreCase("hiddengem") ? "Hidden Gem / Done That" : "");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (withEntity.getActivity().getUser().getId() != null && !withEntity.getActivity().getUser().getId().isEmpty()) {
                        AppConstants.PROFILE_USER_ID = withEntity.getActivity().getUser().getId();
                        ((BaseActivity) mContext).nextScreen(UserProfileActivityScreen.class, true);

                    }
                }
            });

        }


    }

    private StringBuilder getAddress(WithEntity withEntity) {
        StringBuilder address = new StringBuilder();

        if (withEntity.getActivity().getCity() != null) {
            if (!withEntity.getActivity().getCity().getName().isEmpty()) {
                address.append(withEntity.getActivity().getCity().getName());
            }
            if (!withEntity.getActivity().getCity().getLocality().isEmpty()) {
                address.append(", " + withEntity.getActivity().getCity().getLocality());
            }
            if (!withEntity.getActivity().getCity().getCountry().isEmpty()) {
                address.append(", " + withEntity.getActivity().getCity().getCountry());
            }

        }
        return address;
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
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

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.travel_status_txt)
        TextView mTravelStatusTxt;

        @BindView(R.id.right_arrow_img)
        ImageView mRightArrowImg;

        @BindView(R.id.user_bg_img)
        RoundedImageView mRoundUserImgBorder;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
