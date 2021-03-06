package com.ouam.app.adapter;


import android.content.Context;
import android.nfc.cardemulation.CardEmulation;
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
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.ui.CityDetailsActivityScreen;
import com.ouam.app.ui.PlaceDetailsActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPinsAdapter extends RecyclerView.Adapter<UserPinsAdapter.Holder> {

    private Context mContext;
    private ArrayList<WhoDetailEntity> mWithList = new ArrayList<>();
    private boolean mIsMyProfile;

    public UserPinsAdapter(Context activity, ArrayList<WhoDetailEntity> withEntityArrayList, boolean isMyProfile) {
        mContext = activity;
        mWithList = withEntityArrayList;
        mIsMyProfile = isMyProfile;

    }

    @NonNull
    @Override
    public UserPinsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_place_search_activity_row, parent, false);
        return new UserPinsAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserPinsAdapter.Holder holder, int position) {
        final WhoDetailEntity whoDetailEntity = mWithList.get(position);

        if (whoDetailEntity != null) {

            if (whoDetailEntity.getBestPhoto().isEmpty()) {
                holder.mPlaceImgLay.setVisibility(View.GONE);
            } else {

                try {
                    holder.mPlaceImgLay.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(whoDetailEntity.getBestPhoto())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                            .into(holder.mViewImg);
                } catch (Exception ex) {
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
            holder.mToBeenExploredTxt.setTextColor(whoDetailEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? ContextCompat.getColor(mContext, R.color.accent_explore_blue) : ContextCompat.getColor(mContext, R.color.gray));
            holder.mBeenThereTxt.setTextColor(whoDetailEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? ContextCompat.getColor(mContext, R.color.accent_been_there_red) : ContextCompat.getColor(mContext, R.color.gray));
            holder.mHiddenGemTxt.setTextColor(whoDetailEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? ContextCompat.getColor(mContext, R.color.accent_hidden_gem_violet) : ContextCompat.getColor(mContext, R.color.gray));

            holder.mLocationStr = (whoDetailEntity.getCity().getName().isEmpty() ? "" : whoDetailEntity.getCity().getName() + ", ") + (whoDetailEntity.getCity().getLocality().isEmpty() ? "" : whoDetailEntity.getCity().getLocality() + ", ") + (whoDetailEntity.getCity().getCountry().isEmpty() ? "" : whoDetailEntity.getCity().getCountry());
            holder.mLocationTxt.setText(whoDetailEntity.getPinnedAs().equalsIgnoreCase("beenthere") ? "Been There / Done That" : whoDetailEntity.getPinnedAs().equalsIgnoreCase("tobeexplored") ? "To Be Explored / Done That" : whoDetailEntity.getPinnedAs().equalsIgnoreCase("hiddengem") ? "Hidden Gem / Done That" : "");

            holder.mBeenThereImg.setImageResource(whoDetailEntity.getPinnedAs().equalsIgnoreCase("BeenThere") ? R.drawable.discover_blue : R.drawable.discover_gray);
            holder.mToBeExploredImg.setImageResource(whoDetailEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored") ? R.drawable.undiscover_blue : R.drawable.undiscover_gray);
            holder.mHiddenGemImg.setImageResource(whoDetailEntity.getPinnedAs().equalsIgnoreCase("HiddenGem") ? R.drawable.hidden_blue : R.drawable.hidden_gray);

            holder.mNameTxt.setText(!whoDetailEntity.getName().isEmpty() ? whoDetailEntity.getName() : "");
            holder.mAddressTxt.setText(holder.mLocationStr);
            holder.mCategoryTxt.setText(whoDetailEntity.getCategory().isEmpty() ? "" : whoDetailEntity.getCategory().toUpperCase());
            holder.mToBeenExploredTxt.setText(whoDetailEntity.getTbePinCount());
            holder.mBeenThereTxt.setText(whoDetailEntity.getBtPinCount());
            holder.mHiddenGemTxt.setText(whoDetailEntity.getHgPinCount());

            holder.mNameTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");

                        AppConstants.PLACE_NAME = whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2];

                        AppConstants.pinType = whoDetailEntity.getPinnedAs();
                        ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
                    }

                }
            });

            holder.mAddressTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (whoDetailEntity.getCity().getId() != null || !whoDetailEntity.getCity().getId().isEmpty()) {
                        AppConstants.CITY_NAME = whoDetailEntity.getCity().getId();
                        ((BaseActivity) mContext).nextScreen(CityDetailsActivityScreen.class, true);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty()) {
                        String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");

                        AppConstants.PLACE_NAME = whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2];
//                        AppConstants.PLACE_NAME = withEntity.getId() + "%7Cfs:" + withEntity.getProviderIDs();
                        AppConstants.pinType = "none";
                        ((BaseActivity) mContext).nextScreen(PlaceDetailsActivityScreen.class, true);
                    }

                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    if (mIsMyProfile) {

                        DialogManager.getInstance().showOptionPopup(mContext, "Do you want to delete Pin?", mContext.getString(R.string.yes), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                if (!whoDetailEntity.getProviderIDs().isEmpty()) {
                                    String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");


                                    String url = AppConstants.BASE_URL + AppConstants.PLACE_URL +
                                            whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2]
                                            + AppConstants.PIN_URL;

                                    APIRequestHandler.getInstance().deletePinApiWithCallback(url, new InterfaceApiResponseCallBack() {
                                        @Override
                                        public void onRequestSuccess(Object obj) {
                                            DialogManager.getInstance().showToast(mContext, "removed");
                                            AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();

                                        }

                                        @Override
                                        public void onRequestFailure(Throwable r) {

                                        }
                                    });
                                }
                            }
                        });
                    }

                    return true;

                }
            });

        }

        holder.mBeenThereImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoDetailEntity.getPinnedAs().equalsIgnoreCase("BeenThere")) {


                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.BEEN_THERE_PIN);

                        String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                            }

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
                if (whoDetailEntity.getPinnedAs().equalsIgnoreCase("ToBeExplored")) {

                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.TO_BE_EXPLORED_PIN);

                        String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2]+ AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                            }

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
                if (whoDetailEntity.getPinnedAs().equalsIgnoreCase("HiddenGem")) {
                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        DialogManager.getInstance().showOptionPopup(mContext, mContext.getString(R.string.pin_type_delete_msg), mContext.getString(R.string.delete), mContext.getString(R.string.no), new InterfaceTwoBtnCallBack() {
                            @Override
                            public void onNegativeClick() {

                            }

                            @Override
                            public void onPositiveClick() {
                                String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");
                                String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                                APIRequestHandler.getInstance().deletePinApi(url, mContext, new InterfaceApiResponseCallBack() {
                                    @Override
                                    public void onRequestSuccess(Object obj) {
                                        AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                                    }

                                    @Override
                                    public void onRequestFailure(Throwable r) {

                                    }
                                });

                            }
                        });
                    }
                } else {
                    if (!whoDetailEntity.getProviderIDs().isEmpty() && !whoDetailEntity.getId().isEmpty() || whoDetailEntity.getProviderIDs() != null && whoDetailEntity.getId() != null) {
                        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

                        addPinInputEntity.setPinType(AppConstants.HIDDEN_GEM_PIN);

                        String[] providerIdArr = whoDetailEntity.getProviderIDs().split("\"");

                        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + whoDetailEntity.getId() + "%7Cfs:" + providerIdArr[providerIdArr.length - 2] + AppConstants.PIN_URL;

                        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, mContext, new InterfaceApiResponseCallBack() {
                            @Override
                            public void onRequestSuccess(Object obj) {
                                AppConstants.REFRESH_USER_PIN_LIST_INTERFACE.refreshUserPinListApi();                            }

                            @Override
                            public void onRequestFailure(Throwable r) {

                            }
                        });
                    }

                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return mWithList.size();
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


        private String mUserNameStr = "", mAddressStr = "";

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
