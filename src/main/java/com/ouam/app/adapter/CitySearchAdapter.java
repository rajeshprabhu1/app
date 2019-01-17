package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.fragments.CitySearchFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CityDetailsActivityScreen;
import com.ouam.app.ui.SearchActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitySearchAdapter extends RecyclerView.Adapter<CitySearchAdapter.Holder> {

    private Context mContext;
    private ArrayList<WithEntity> mCitiesList;

    private CitySearchFragment mCitySearchFragment;

    public CitySearchAdapter(Context activity, CitySearchFragment citySearchFragment, ArrayList<WithEntity> citiesList) {
        mContext = activity;
        mCitiesList = citiesList;
        mCitySearchFragment = citySearchFragment;
    }

    @NonNull
    @Override
    public CitySearchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_search_activity_row, parent, false);
        return new CitySearchAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CitySearchAdapter.Holder holder, int position) {
        final WithEntity withEntity = mCitiesList.get(position);

        if (withEntity != null) {

            if (withEntity.getPhoto().isEmpty()) {
                holder.mCitiesImgBg.setVisibility(View.GONE);
            } else {
                try {
                    holder.mCitiesImgBg.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(withEntity.getPhoto())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                            .into(holder.mCitiesImg);

                } catch (Exception ex) {
                    holder.mCitiesImg.setImageResource(R.drawable.demo_img);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
        }

        holder.mPinnedType.setVisibility(View.GONE);
        holder.mNameTxt.setText(withEntity.getName());
        /*place holder str for locationTxt*/
        holder.mLocationStr = (withEntity.getLocality().isEmpty() ? "" : withEntity.getLocality()) + (withEntity.getCountry().isEmpty() ? "" : ", "+ withEntity.getCountry());
        holder.mLocationTxt.setText(holder.mLocationStr);
//        holder.mPinnedType.setText(withEntity.getPinnedAs().isEmpty() ? "" : "Pinned Type : " + withEntity.getPinnedAs());

        holder.mToBeenExploredTxt.setText(String.valueOf(withEntity.getTbePinCount()));
        holder.mBeenThereTxt.setText(String.valueOf(withEntity.getBtPinCount()));
        holder.mHiddenGemTxt.setText(String.valueOf(withEntity.getHgPinCount()));

        holder.mToBeenExploredTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? ContextCompat.getColor(mContext,R.color.accent_explore_blue) : ContextCompat.getColor(mContext,R.color.gray));
        holder.mBeenThereTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? ContextCompat.getColor(mContext,R.color.accent_been_there_red) : ContextCompat.getColor(mContext,R.color.gray));
        holder.mHiddenGemTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? ContextCompat.getColor(mContext,R.color.accent_hidden_gem_violet) : ContextCompat.getColor(mContext,R.color.gray));


        holder.mBeenThereImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? R.drawable.discover_blue : R.drawable.discover_gray);
        holder.mToBeExploredImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? R.drawable.undiscover_blue : R.drawable.undiscover_gray);
        holder.mHiddenGemImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? R.drawable.hidden_blue : R.drawable.hidden_gray);

        if (!withEntity.getPinnedAs().equalsIgnoreCase("BeenThere")) {
            holder.mBeenThereImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                    addPinInputEntity.setPinType(AppConstants.BEEN_THERE_PIN);

                    String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                    APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            mCitySearchFragment.onResume();
                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                }
            });
        } else {
            holder.mBeenThereImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                        @Override
                        public void onNegativeClick() {

                        }

                        @Override
                        public void onPositiveClick() {
                            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                            APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                @Override
                                public void onRequestSuccess(Object obj) {
                                    mCitySearchFragment.onResume();
                                }

                                @Override
                                public void onRequestFailure(Throwable r) {

                                }
                            });

                        }
                    });

                }
            });
        }

        if (!withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored")) {
            holder.mToBeExploredImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                    addPinInputEntity.setPinType(AppConstants.TO_BE_EXPLORED_PIN);

                    String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                    APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            mCitySearchFragment.onResume();

                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                }
            });
        } else {
            holder.mToBeExploredImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                        @Override
                        public void onNegativeClick() {

                        }

                        @Override
                        public void onPositiveClick() {
                            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                            APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                @Override
                                public void onRequestSuccess(Object obj) {
                                    mCitySearchFragment.onResume();
                                }

                                @Override
                                public void onRequestFailure(Throwable r) {

                                }
                            });

                        }
                    });
                }
            });
        }

        if (!withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem")) {
            holder.mHiddenGemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                    addPinInputEntity.setPinType(AppConstants.HIDDEN_GEM_PIN);

                    String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                    APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                        @Override
                        public void onRequestSuccess(Object obj) {
                            mCitySearchFragment.onResume();
                        }

                        @Override
                        public void onRequestFailure(Throwable r) {

                        }
                    });
                }
            });
        } else {
            holder.mHiddenGemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                        @Override
                        public void onNegativeClick() {

                        }

                        @Override
                        public void onPositiveClick() {
                            String url = AppConstants.BASE_URL + AppConstants.CITIES_URL + withEntity.getId() + AppConstants.PIN_URL;

                            APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                @Override
                                public void onRequestSuccess(Object obj) {
                                    mCitySearchFragment.onResume();
                                }

                                @Override
                                public void onRequestFailure(Throwable r) {

                                }
                            });

                        }
                    });
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withEntity.getId() != null || !withEntity.getId().isEmpty()) {
                    AppConstants.CITY_NAME = withEntity.getId();
                    ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mCitiesList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_lay)
        CardView mCitiesImgBg;

        @BindView(R.id.view_img)
        ImageView mCitiesImg;

        @BindView(R.id.name_txt)
        TextView mNameTxt;

        @BindView(R.id.location_txt)
        TextView mLocationTxt;

        @BindView(R.id.to_been_explored_txt)
        TextView mToBeenExploredTxt;

        @BindView(R.id.been_there_txt)
        TextView mBeenThereTxt;

        @BindView(R.id.hidden_gem_txt)
        TextView mHiddenGemTxt;

        @BindView(R.id.address_txt)
        TextView mPinnedType;

        @BindView(R.id.been_there_img)
        ImageView mBeenThereImg;

        @BindView(R.id.to_be_explored_img)
        ImageView mToBeExploredImg;

        @BindView(R.id.hidden_gem_img)
        ImageView mHiddenGemImg;

        private String mLocationStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
