package com.ouam.app.adapter;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.database.DatabaseUtil;
import com.ouam.app.entity.ActivityFeedEntity;
import com.ouam.app.entity.OfflinePinEntity;
import com.ouam.app.utils.DateUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfflineModeAdapter extends RecyclerView.Adapter<OfflineModeAdapter.Holder> {

    private Context mContext;
    private List<OfflinePinEntity> mPinnedList;
    private DatabaseUtil mDatabaseUtil;
    private InterfaceBtnCallBack interfaceBtnCallBack;
    private Handler handler;
    private OnItemClickListener mOnItemClickListener;

    public OfflineModeAdapter(Context activity, InterfaceBtnCallBack interfaceBtnCallBack,
                              List<OfflinePinEntity> pinList, OnItemClickListener onItemClickListener) {
        mContext = activity;
        mPinnedList = pinList;
        this.interfaceBtnCallBack = interfaceBtnCallBack;
        mDatabaseUtil = new DatabaseUtil(activity);
        handler = new Handler();
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_offline_mode_screen, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        OfflinePinEntity offlinePinEntity = mPinnedList.get(holder.getAdapterPosition());
        holder.mLocationNameTxt.setText(offlinePinEntity.getLocationName());
        /*place holder str for latlongTxt*/
        String latLongStr = " " + offlinePinEntity.getLatitude() + "," + offlinePinEntity.getLongitude() + " ";
        holder.mLatLongTxt.setText(latLongStr);
        holder.mPinTypeImg.setImageResource(offlinePinEntity.getPinType().
                equals(mContext.getString(R.string.type_been_there)) ? R.drawable.discovered_map_pin :
                offlinePinEntity.getPinType().equals(mContext.getString(R.string.type_explore)) ? R.drawable.undiscovered_map_pin : R.drawable.hidden_map_pin);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                handler.removeCallbacks(this);
                holder.mTimeTxt.setText(DateUtil.getDateTimeDiff(mPinnedList.get(holder.getAdapterPosition()).getDateTime(), false));
            }
        }, 1000);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClicked(offlinePinEntity, position);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mPinnedList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_name_txt)
        TextView mLocationNameTxt;
        @BindView(R.id.lat_long_txt)
        TextView mLatLongTxt;

        @BindView(R.id.pin_type_img)
        ImageView mPinTypeImg;

        @BindView(R.id.offline_pin_time_txt)
        TextView mTimeTxt;

        @BindView(R.id.view_foreground)
        public RelativeLayout viewForeGround;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void removeItem(int position) {
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        mDatabaseUtil.deletePin(mPinnedList.get(position).getId());
        mPinnedList.remove(position);
        interfaceBtnCallBack.onPositiveClick();
        notifyItemRemoved(position);
    }

    public void restoreItem(OfflinePinEntity item, int position) {
        mPinnedList.add(position, item);
        // notify item added by position
        mDatabaseUtil.insertPinLocation(item);
        interfaceBtnCallBack.onPositiveClick();
        notifyItemInserted(position);

    }

    public interface OnItemClickListener {
        void onItemClicked(OfflinePinEntity offlinePinEntity, int pos);
    }
}
