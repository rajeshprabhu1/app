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

import com.ouam.app.R;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.utils.DateUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRecentAdapter extends RecyclerView.Adapter<UserRecentAdapter.Holder> {

    private Context mContext;
    private ArrayList<ActivityFeedEntity> mActivityFeedList = new ArrayList<>();

    public UserRecentAdapter(Context activity, ArrayList<ActivityFeedEntity> activityFeedEntityArrayList) {
        mContext = activity;
        mActivityFeedList = activityFeedEntityArrayList;

    }

    @NonNull
    @Override
    public UserRecentAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_traveler_recent_row, parent, false);
        return new UserRecentAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserRecentAdapter.Holder holder, int position) {
        final ActivityFeedEntity activityFeedEntity = mActivityFeedList.get(position);

        if (activityFeedEntity != null) {

//            if (activityFeedEntity.getWho().getPhotoUrl().isEmpty()) {
//                holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//            } else {
//                try {
//                    Glide.with(mContext)
//                            .load(activityFeedEntity.getWho().getPhotoUrl())
//                            .error(R.drawable.demo_img)
//                            .into(holder.mUserImg);
//                } catch (Exception ex) {
//                    holder.mUserImg.setImageResource(R.drawable.user_demo_icon);
//                    Log.e(AppConstants.TAG, ex.getMessage());
//                }
//            }
            holder.mUserImgLay.setVisibility(activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("none") ? View.GONE : View.VISIBLE);
            holder.mPinTypeImg.setImageResource(activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("tobeexplored") ? R.drawable.check_icon : activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("beenthere") ? R.drawable.plane_icon : activityFeedEntity.getActivity().getSubtype().equalsIgnoreCase("hiddengem") ? R.drawable.gem_icon : 0);
            /*Placeholder for usernameTxt and addressTxt*/
            if (activityFeedEntity.getActivity().getPlace() != null) {
                holder.mUserNameStr = (activityFeedEntity.getActivity().getPlace().getName()).isEmpty() ? "" : activityFeedEntity.getActivity().getPlace().getName() + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : "  " + activityFeedEntity.getActivity().getActivityType());
                holder.mAddressStr = (activityFeedEntity.getActivity().getPlace().getCategory().isEmpty() ? "" : activityFeedEntity.getActivity().getPlace().getCategory());

            } else {
                holder.mUserNameStr = (activityFeedEntity.getActivity().getCity().getName().isEmpty() ? "" : activityFeedEntity.getActivity().getCity().getName() + (activityFeedEntity.getActivity().getActivityType().isEmpty() ? "" : "  " + activityFeedEntity.getActivity().getActivityType()));
                holder.mAddressStr = (activityFeedEntity.getActivity().getCity().getLocality().isEmpty() ? "" : activityFeedEntity.getActivity().getCity().getLocality() + "\n") + (activityFeedEntity.getActivity().getCity().getCountry().isEmpty() ? "" : activityFeedEntity.getActivity().getCity().getCountry());
            }


            holder.mUserNameTxt.setText(holder.mUserNameStr);
            holder.mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(activityFeedEntity.getCreated()), true));
            holder.mTravelStatusTxt.setText(activityFeedEntity.getActivity().getSubtype().isEmpty() ? "" : activityFeedEntity.getActivity().getSubtype());
            holder.mAddressTxt.setText(holder.mAddressStr);
        }

    }


    @Override
    public int getItemCount() {
        return mActivityFeedList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img_lay)
        RelativeLayout mUserImgLay;

        @BindView(R.id.pin_type_img)
        ImageView mPinTypeImg;

        @BindView(R.id.user_name_txt)
        TextView mUserNameTxt;

        @BindView(R.id.hours_txt)
        TextView mHoursTxt;

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.travel_status_txt)
        TextView mTravelStatusTxt;

        private String mUserNameStr = "", mAddressStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
