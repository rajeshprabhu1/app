package com.ouam.app.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.CityWithEntity;
import com.ouam.app.fragments.CitiesTrendingPlaceFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CityDetailsActivityScreen;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesTrendingAdapter extends RecyclerView.Adapter<CitiesTrendingAdapter.Holder> {

    private Context mContext;
    private ArrayList<CityWithEntity> mCitiesList;

    private CitiesTrendingPlaceFragment mCitiesTrendingPlaceFragment;

    public CitiesTrendingAdapter(Context activity, CitiesTrendingPlaceFragment citiesTrendingPlaceFragment, ArrayList<CityWithEntity> citiesList) {
        mContext = activity;
        mCitiesList = citiesList;
        mCitiesTrendingPlaceFragment = citiesTrendingPlaceFragment;
    }

    @Override
    public CitiesTrendingAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_search_activity_row, parent, false);
        return new CitiesTrendingAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(final CitiesTrendingAdapter.Holder holder, int position) {
        final CityWithEntity withEntity = mCitiesList.get(position);

        holder.mCitiesImg.setVisibility(View.GONE);
        holder.mViewLay.setVisibility(View.GONE);

        if (!withEntity.getId().isEmpty() && !withEntity.getProviderIDs().isEmpty()) {
            if (!withEntity.getPinnedAs().equalsIgnoreCase("BeenThere")) {
                holder.mBeenThereImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.BEEN_THERE_PIN);

                        String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                mCitiesTrendingPlaceFragment.onResume();
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

                                String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length-2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        mCitiesTrendingPlaceFragment.onResume();
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

                        String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length-2] + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                mCitiesTrendingPlaceFragment.onResume();

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
                                String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length-2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        mCitiesTrendingPlaceFragment.onResume();
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

                        String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length-2] + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                mCitiesTrendingPlaceFragment.onResume();
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

                                String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length-2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        mCitiesTrendingPlaceFragment.onResume();
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
        }
        holder.mNameTxt.setText(withEntity.getName().isEmpty() ? "" : withEntity.getName());
        holder.mAddressTxt.setText((withEntity.getCity().getName().isEmpty() ? "" : withEntity.getCity().getName() + ", ") + (withEntity.getCity().getLocality().isEmpty() ? "" : withEntity.getCity().getLocality() + ", ") + (withEntity.getCity().getCountry().isEmpty() ? "" : withEntity.getCity().getCountry() + "."));
        holder.mToBeenExplored_Txt.setText(withEntity.getTbePinCount());
        holder.mBeenThereTxt.setText(withEntity.getBtPinCount());
        holder.mHiddenGemTxt.setText(withEntity.getHgPinCount());
        holder.mCategoryTxt.setText(withEntity.getCategory().isEmpty() ? "" : withEntity.getCategory());

        holder.mPinTypeTxt.setVisibility(withEntity.getPinnedAs().equalsIgnoreCase("none") ? View.GONE : View.VISIBLE);
        holder.mPinTypeTxt.setText(withEntity.getPinnedAs().equalsIgnoreCase("beenthere") ? "Been There / Done That" : withEntity.getPinnedAs().equalsIgnoreCase("tobeexplored") ? "To Be Explored / Done That" : withEntity.getPinnedAs().equalsIgnoreCase("hiddengem") ? "Hidden Gem / Done That" : "");

        holder.mBeenThereImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? R.drawable.check_icon : R.drawable.check_grey_icon);
        holder.mToBeExploredImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? R.drawable.plane_icon : R.drawable.plane_icon_gray);
        holder.mHiddenGemImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? R.drawable.gem_icon : R.drawable.gem_icon_gray);

        holder.mNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!withEntity.getProviderIDs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs() != null && withEntity.getId() != null) {

                    String[] providerIdArr = withEntity.getProviderIDs().split("\"");

                    AppConstants.PLACE_NAME = withEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2];
                    AppConstants.pinType = withEntity.getPinnedAs();
                    ((CityDetailsActivityScreen) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
                }

            }
        });

        holder.mAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withEntity.getCity().getId() != null || !withEntity.getCity().getId().isEmpty()) {
                    AppConstants.CITY_NAME = withEntity.getCity().getId();
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
        RelativeLayout mViewLay;

        @BindView(R.id.view_img)
        ImageView mCitiesImg;

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.name_txt)
        TextView mNameTxt;

        @BindView(R.id.location_txt)
        TextView mPinTypeTxt;

        @BindView(R.id.to_been_explored_txt)
        TextView mToBeenExplored_Txt;

        @BindView(R.id.been_there_txt)
        TextView mBeenThereTxt;

        @BindView(R.id.hidden_gem_txt)
        TextView mHiddenGemTxt;

        @BindView(R.id.category_txt)
        TextView mCategoryTxt;

        @BindView(R.id.been_there_img)
        ImageView mBeenThereImg;

        @BindView(R.id.to_be_explored_img)
        ImageView mToBeExploredImg;

        @BindView(R.id.hidden_gem_img)
        ImageView mHiddenGemImg;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
