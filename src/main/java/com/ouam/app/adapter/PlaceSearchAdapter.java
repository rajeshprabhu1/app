package com.ouam.app.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.ouam.app.fragments.PlaceSearchFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.ui.SearchActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.Holder> {

    private Context mContext;
    private ArrayList<WithEntity> mPlaceList;

    private PlaceSearchFragment mPlaceSearchFragment;

    public PlaceSearchAdapter(Context activity, PlaceSearchFragment placeSearchFragment, ArrayList<WithEntity> placeList) {
        mContext = activity;
        mPlaceList = placeList;
        mPlaceSearchFragment = placeSearchFragment;

    }

    @NonNull
    @Override
    public PlaceSearchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_search_activity_row, parent, false);
        return new PlaceSearchAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceSearchAdapter.Holder holder, int position) {

        final WithEntity withEntity = mPlaceList.get(position);

        if (withEntity.getBestPhoto().isEmpty()) {
            holder.mPlaceImgLay.setVisibility(View.GONE);
        } else {
            try {
                holder.mPlaceImgLay.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(withEntity.getBestPhoto())
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                        .into(holder.mViewImg);

            } catch (Exception ex) {
                holder.mViewImg.setImageResource(R.drawable.demo_img);
                Log.e(AppConstants.TAG, ex.getMessage());
            }
        }


        holder.mNameTxt.setText(withEntity.getName());
        /*place holder for locationTxt*/
        holder.mAddressTxt.setVisibility(withEntity.getAddress().isEmpty() ? View.GONE : View.VISIBLE);
        holder.mAddressTxt.setText(withEntity.getAddress());
        holder.mLocationStr = ((withEntity.getCity().getName().isEmpty() ? "" : withEntity.getCity().getName() + ",") + (withEntity.getCity().getLocality().isEmpty() ? "" : withEntity.getCity().getLocality() + ", ") + (withEntity.getCity().getCountry().isEmpty() ? "" : withEntity.getCity().getCountry()));
        holder.mLocationTxt.setVisibility(holder.mLocationStr.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mLocationTxt.setText(holder.mLocationStr);
        holder.mCategoryTxt.setVisibility(withEntity.getCategory().isEmpty() ? View.GONE : View.VISIBLE);
        holder.mCategoryTxt.setText(withEntity.getCategory().toUpperCase());
        holder.mToBeenExploredTxt.setText(String.valueOf(withEntity.getTbePinCount()));
        holder.mBeenThereTxt.setText(String.valueOf(withEntity.getBtPinCount()));
        holder.mHiddenGemTxt.setText(String.valueOf(withEntity.getHgPinCount()));
        holder.mToBeenExploredTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? ContextCompat.getColor(mContext, R.color.accent_explore_blue) : ContextCompat.getColor(mContext, R.color.gray));
        holder.mBeenThereTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? ContextCompat.getColor(mContext, R.color.accent_been_there_red) : ContextCompat.getColor(mContext, R.color.gray));
        holder.mHiddenGemTxt.setTextColor(withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? ContextCompat.getColor(mContext, R.color.accent_hidden_gem_violet) : ContextCompat.getColor(mContext, R.color.gray));

        holder.mBeenThereImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? R.drawable.discover_blue : R.drawable.discover_gray);
        holder.mToBeExploredImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? R.drawable.undiscover_blue : R.drawable.undiscover_gray);
        holder.mHiddenGemImg.setImageResource(withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? R.drawable.hidden_blue : R.drawable.hidden_gray);


        holder.mBeenThereImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withEntity.getPinnedAs().equalsIgnoreCase("BeenThere")) {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
//                                        mPlaceSearchFragment.onResume();
                                        withEntity.setPinnedAs("None");
                                        if (withEntity.getBtPinCount() != 0) {
                                            withEntity.setBtPinCount(withEntity.getBtPinCount() - 1);
                                        }

                                        notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.BEEN_THERE_PIN);

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                withEntity.setPinnedAs("BeenThere");
                                withEntity.setBtPinCount(withEntity.getBtPinCount() + 1);
                                notifyDataSetChanged();
//                                mPlaceSearchFragment.onResume();

                            }

                            @Override
                            public void onRequestFailure(Throwable r) {

                            }
                        });
                    }

                }


            }
        });


        holder.mToBeExploredImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored")) {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
//                                        mPlaceSearchFragment.onResume();
                                        withEntity.setPinnedAs("None");
                                        if (withEntity.getTbePinCount() != 0) {
                                            withEntity.setTbePinCount(withEntity.getTbePinCount() - 1);
                                        }
                                        notifyDataSetChanged();


                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.TO_BE_EXPLORED_PIN);

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
//                                mPlaceSearchFragment.onResume();
                                withEntity.setPinnedAs("ToBeExplored");
                                withEntity.setTbePinCount(withEntity.getTbePinCount() + 1);

                                notifyDataSetChanged();


                            }

                            @Override
                            public void onRequestFailure(Throwable r) {

                            }
                        });
                    }


                }
            }
        });


        holder.mHiddenGemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (withEntity.getPinnedAs().equalsIgnoreCase("HiddenGem")) {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
//                                        mPlaceSearchFragment.onResume();
                                        withEntity.setPinnedAs("None");
                                        if (withEntity.getHgPinCount() != 0) {
                                            withEntity.setHgPinCount(withEntity.getHgPinCount() - 1);
                                        }
                                        notifyDataSetChanged();


                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.HIDDEN_GEM_PIN);

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
//                                mPlaceSearchFragment.onResume();
                                withEntity.setPinnedAs("HiddenGem");
                                withEntity.setHgPinCount(withEntity.getHgPinCount() + 1);

                                notifyDataSetChanged();


                            }

                            @Override
                            public void onRequestFailure(Throwable r) {

                            }
                        });
                    }

                }

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!withEntity.getProviderIDs().getFs().isEmpty() && !withEntity.getId().isEmpty() || withEntity.getProviderIDs().getFs() != null && withEntity.getId() != null) {
                    AppConstants.PLACE_NAME = withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs().getFs();
                    AppConstants.pinType = withEntity.getPinnedAs();
                    ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
//                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append(withEntity.getName());
//                    stringBuilder.append(withEntity.getAddress());
//                    stringBuilder.append(withEntity.getCity().getLocality());
//                    stringBuilder.append(withEntity.getCity().getCountry());
//
//                    String address = stringBuilder.toString();
//                    Log.e("fhf",address);
//                String map = "http://maps.google.co.in/maps?q=" + address;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
//                mContext.startActivity(intent);

                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_lay)
        CardView mPlaceImgLay;

        @BindView(R.id.view_img)
        ImageView mViewImg;

        @BindView(R.id.name_txt)
        TextView mNameTxt;

        @BindView(R.id.location_txt)
        TextView mLocationTxt;

        @BindView(R.id.category_txt)
        TextView mCategoryTxt;

        @BindView(R.id.address_txt)
        TextView mAddressTxt;

        @BindView(R.id.to_been_explored_txt)
        TextView mToBeenExploredTxt;

        @BindView(R.id.been_there_txt)
        TextView mBeenThereTxt;

        @BindView(R.id.hidden_gem_txt)
        TextView mHiddenGemTxt;

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
