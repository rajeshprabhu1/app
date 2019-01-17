package com.ouam.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.entity.CityEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.Holder> {

    private Context mContext;

    private ArrayList<WithEntity> mLocationList;
    private OnItemClickListener listener;
    private String mPinType;

    public LocationListAdapter(Context activity, ArrayList<WithEntity> locationList, String type,
                               OnItemClickListener onItemClickListener) {
        mContext = activity;
        listener = onItemClickListener;
        mLocationList = locationList;
        mPinType = type;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_location_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {

        final CityEntity cityEntity = mLocationList.get(position).getCity();
        holder.mPlaceNameTxt.setText(mLocationList.get(position).getName());
        holder.mPlaceTypeTxt.setText(mLocationList.get(position).getCategory().toUpperCase());
        /*place holder str for cityNameTxt*/
        holder.mCityStr = (cityEntity.getName().isEmpty() ? "" : cityEntity.getName() + ", ") + (cityEntity.getLocality().isEmpty() ? "" : cityEntity.getLocality() + ", ") + (cityEntity.getCountry().isEmpty() ? "" : cityEntity.getCountry());
        holder.mCityNameTxt.setText(holder.mCityStr);

//        if (mLocationList.get(position).isChecked()) {
//            holder.mPinTypeImg.setImageResource(mPinType.equals(mContext.getString(R.string.type_been_there)) ?
//                    R.drawable.check_icon : mPinType.equals(mContext.getString(R.string.type_explore)) ?
//                    R.drawable.plane_icon : R.drawable.gem_icon);
//        } else {
//            holder.mPinTypeImg.setImageResource(mPinType.equals(mContext.getString(R.string.type_been_there)) ?
//                    R.drawable.check_grey_icon : mPinType.equals(mContext.getString(R.string.type_explore)) ?
//                    R.drawable.plane_icon_gray : R.drawable.gem_icon_gray);
//        }

//        setPinImage(position, holder.mPinTypeImg);
        if (!mLocationList.get(position).getBestPhoto().isEmpty()) {
            holder.mImageLay.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(mLocationList.get(position).getBestPhoto())
                    .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                    .into(holder.mLocationImg);
        } else {
            holder.mImageLay.setVisibility(View.GONE);
        }


        holder.mRowLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mLocationList.get(position).getPinnedAs().
                        equalsIgnoreCase(mContext.getString(R.string.sub_type_been_there)) &&
                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_been_there))
                        || mLocationList.get(position).getPinnedAs().
                        equalsIgnoreCase(mContext.getString(R.string.sub_type_to_be_explored)) &&
                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_explore))
                        || mLocationList.get(position).getPinnedAs().
                        equalsIgnoreCase(mContext.getString(R.string.sub_type_hidden_gem)) &&
                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_hidden_gem))) {


                    String url = AppConstants.BASE_URL + AppConstants.PLACE_URL +
                            mLocationList.get(position).getId() + "%7Cfs:" +
                            mLocationList.get(position).getProviderIDs().getFs()
                            + AppConstants.PIN_URL;

                    APIRequestHandler.getInstance().deletePinApiWithCallback(url, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            mLocationList.get(position).setPinnedAs("");
//                            setPinImage(position, holder.mPinTypeImg);

//                            DialogManager.getInstance().showToast(mContext, "removed");


                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });


                } else {
                    mLocationList.get(holder.getAdapterPosition()).setChecked(!mLocationList.get(holder.getAdapterPosition()).isChecked());
                    for (int i = 0; i < mLocationList.size(); i++) {
                        if (holder.getAdapterPosition() != i) {
                            mLocationList.get(i).setChecked(false);
                        }
                    }
                    listener.setCity(mLocationList.get(holder.getAdapterPosition()));
                    listener.onItemClicked();
                    notifyDataSetChanged();
                }


            }
        });

//        holder.mPinTypeImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mLocationList.get(position).getPinnedAs().
//                        equalsIgnoreCase(mContext.getString(R.string.subtype_been_there)) &&
//                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_been_there))
//                        || mLocationList.get(position).getPinnedAs().
//                        equalsIgnoreCase(mContext.getString(R.string.subtype_to_be_explored)) &&
//                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_explore))
//                        || mLocationList.get(position).getPinnedAs().
//                        equalsIgnoreCase(mContext.getString(R.string.sub_type_hidden_gem)) &&
//                        mPinType.equalsIgnoreCase(mContext.getString(R.string.type_hidden_gem))) {
//
//
//                    String url = AppConstants.BASE_URL + AppConstants.PLACE_URL +
//                            mLocationList.get(position).getId() + "%7Cfs:" +
//                            mLocationList.get(position).getProviderIDs().getFs()
//                            + AppConstants.PIN_URL;
//
//                    APIRequestHandler.getInstance().deletePinApiWithCallback(url, new InterfaceApiResponseCallBack() {
//                        @Override
//                        public void onRequestSuccess(Object obj) {
//                            mLocationList.get(position).setPinnedAs("");
//                            setPinImage(position, holder.mPinTypeImg);
//
////                            DialogManager.getInstance().showToast(mContext, "removed");
//
//
//                        }
//
//                        @Override
//                        public void onRequestFailure(Throwable r) {
//
//                        }
//                    });
//
//
//                }
//
//
//            }
//        });

    }



    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.location_img)
        ImageView mLocationImg;

        @BindView(R.id.place_name_txt)
        TextView mPlaceNameTxt;

        @BindView(R.id.place_type_txt)
        TextView mPlaceTypeTxt;

        @BindView(R.id.city_name_txt)
        TextView mCityNameTxt;

//        @BindView(R.id.pin_type_img)
//        ImageView mPinTypeImg;

        @BindView(R.id.view_lay)
        CardView mImageLay;

        @BindView(R.id.row_id)
        RelativeLayout mRowLay;

        private String mCityStr = "";

        public Holder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public interface OnItemClickListener {
        void setCity(WithEntity mEntity);
        void onItemClicked();

    }
}