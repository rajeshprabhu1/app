package com.ouam.app.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceWithTwoArgumentCallBack;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.OUAMApplication;
import com.rd.PageIndicatorView;

import java.util.ArrayList;


public class DialogManager {

    /*Init variable*/
    private Dialog mProgressDialog, mNetworkErrorDialog, mAlertDialog,
            mOptionDialog, mImageUploadDialog, mShowImgDialog,mPlaceDetailsDialog,
            mAddBeenThereDialog, mShowOfflinePinDialog, mWelcomeDialog;
    private Toast mToast;
    private String mPinType = "";

    private boolean isPinSelectBool = false;

    /*Init dialog instance*/
    private static final DialogManager sDialogInstance = new DialogManager();

    public static DialogManager getInstance() {
        return sDialogInstance;
    }

    /*Init toast*/
    public void showToast(Context context, String message) {

        try {
            /*To check if the toast is projected, if projected it will be cancelled */
            if (mToast != null && mToast.getView().isShown()) {
                mToast.cancel();
            }

            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            TextView toastTxt = mToast.getView().findViewById(
                    android.R.id.message);
            toastTxt.setTypeface(Typeface.SANS_SERIF);
            mToast.show();


        } catch (Exception e) {
            Log.e(AppConstants.TAG, e.toString());
        }
    }

    /*Default dialog init method*/
    private Dialog getDialog(Context context, int layout) {

        Dialog mCommonDialog;
        mCommonDialog = new Dialog(context);
        mCommonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mCommonDialog.getWindow() != null) {
            mCommonDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mCommonDialog.setContentView(layout);
            mCommonDialog.getWindow().setGravity(Gravity.CENTER);
            mCommonDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }
        mCommonDialog.setCancelable(false);
        mCommonDialog.setCanceledOnTouchOutside(false);

        return mCommonDialog;
    }

    public void showAlertPopup(Context context, String messageStr, final InterfaceBtnCallBack dialogAlertInterface) {

        alertDismiss(mAlertDialog);
        mAlertDialog = getDialog(context, R.layout.popup_basic_alert);

        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mAlertDialog.getWindow();

        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
        }

        TextView alertMsgTxt;
        Button alertPositiveBtn;

        /*Init view*/
        alertMsgTxt = mAlertDialog.findViewById(R.id.alert_msg_txt);
        alertPositiveBtn = mAlertDialog.findViewById(R.id.alert_positive_btn);

        /*Set data*/
        alertMsgTxt.setText(messageStr);
        alertPositiveBtn.setText(context.getString(R.string.ok));

        //Check and set button visibility
        alertPositiveBtn.setVisibility(View.VISIBLE);

        alertPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                dialogAlertInterface.onPositiveClick();
            }
        });

        alertShowing(mAlertDialog);


    }
    public void showPlaceDetailsPopup(Context context, WithEntity mWithEntity) {

        alertDismiss(mPlaceDetailsDialog);
        mPlaceDetailsDialog = getDialog(context, R.layout.popup_place_detail_dialog);

        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mPlaceDetailsDialog.getWindow();

        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.TOP);
        }

        String mAddressStr, mPhoneNumStr, mWebSiteStr;
        final TextView mailTxt, addressTxt, phoneTxt;

        mailTxt= mPlaceDetailsDialog.findViewById(R.id.web_txt);
        addressTxt= mPlaceDetailsDialog.findViewById(R.id.address_txt);
        phoneTxt= mPlaceDetailsDialog.findViewById(R.id.phone_txt);
            if (mWithEntity != null){
                mAddressStr = (mWithEntity.getAddress().isEmpty() ? "" : mWithEntity.getAddress() + "\n") + ("")+
                        (mWithEntity.getCity().getName().isEmpty() ? "" : mWithEntity.getCity().getName()) +
                        (mWithEntity.getCity().getLocality().isEmpty() ? "" : ", " + mWithEntity.getCity().getLocality()) +
                        (mWithEntity.getCity().getCountry().isEmpty() ? "" : ", " + mWithEntity.getCity().getCountry()  +
                                (mWithEntity.getPostalCode().isEmpty() ? "" : " " + mWithEntity.getPostalCode() ));
                addressTxt.setText(mAddressStr);
                mWebSiteStr = mWithEntity.getWebsite();
                mPhoneNumStr = mWithEntity.getPhoneNumber();
                phoneTxt.setText(String.valueOf(mPhoneNumStr).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
                // mPlaceDetailsPhoneNumTxt.setText(withEntity.getPhoneNumber().isEmpty() ? "" : "" + withEntity.getPhoneNumber());
                mailTxt.setText(mWithEntity.getWebsite().isEmpty() ? "" : mWithEntity.getWebsite());
            }else {

            }
            addressTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(mWithEntity.getName());
                    stringBuilder.append(mWithEntity.getAddress());
                    stringBuilder.append(mWithEntity.getCity().getLocality());
                    stringBuilder.append(mWithEntity.getCity().getCountry());

                    String address = stringBuilder.toString();
                    Log.e("fhf",address);
                    String map = "http://maps.google.co.in/maps?q=" + address;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    mPlaceDetailsDialog.getContext().startActivity(intent);
                }
            });

        mPlaceDetailsDialog.setCancelable(true);
        mPlaceDetailsDialog.setCanceledOnTouchOutside(true);
        alertShowing(mPlaceDetailsDialog);


    }

    public void showPiningModePopup(final Context context, final String latLongStr, boolean isOnline,
                                    final InterfaceWithTwoArgumentCallBack interfaceWithTwoArgumentCallBack) {
        alertDismiss(mShowOfflinePinDialog);
        mShowOfflinePinDialog = getDialog(context, R.layout.popup_off_line_mode_screen);


        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        final Window window = mShowOfflinePinDialog.getWindow();
        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
            window.getAttributes().windowAnimations = R.style.PopupBottomAnimation;
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }

        final Button saveBtn;
        final ImageView closeImg, beenThereImg, toBeExploredImg, hiddenGemImg;
        final EditText placeNameEdt;
        final TextInputLayout textInputLayout;
        final TextView latLongTxt, titleTxt;
        final LinearLayout offlineLayout;



        /*Init view*/
        closeImg = mShowOfflinePinDialog.findViewById(R.id.pop_up_off_line_close_img);
        saveBtn = mShowOfflinePinDialog.findViewById(R.id.offline_save_btn);
        placeNameEdt = mShowOfflinePinDialog.findViewById(R.id.popup_offline_place_name_edt);
        beenThereImg = mShowOfflinePinDialog.findViewById(R.id.popup_offline_been_there_img);
        toBeExploredImg = mShowOfflinePinDialog.findViewById(R.id.popup_offline_to_been_explored_img);
        hiddenGemImg = mShowOfflinePinDialog.findViewById(R.id.popup_offline_hidden_img);
        textInputLayout = mShowOfflinePinDialog.findViewById(R.id.offline_text_input_layout);
        latLongTxt = mShowOfflinePinDialog.findViewById(R.id.offline_popup_lat_long_txt);
        titleTxt = mShowOfflinePinDialog.findViewById(R.id.title_txt);
        offlineLayout = mShowOfflinePinDialog.findViewById(R.id.offline_lay);

        latLongTxt.setText(latLongStr);
        titleTxt.setText(isOnline ? context.getString(R.string.pin_custom_location) : context.getString(R.string.store_current_location));

        beenThereImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPinSelectBool = true;
                mPinType = setPinBackResourceImage(view.getId(), beenThereImg, toBeExploredImg, hiddenGemImg, saveBtn);
                offlineLayout.setBackgroundResource(R.drawable.discover_popup_background);
            }
        });

        toBeExploredImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPinSelectBool = true;
                mPinType = setPinBackResourceImage(view.getId(), beenThereImg, toBeExploredImg, hiddenGemImg, saveBtn);
                offlineLayout.setBackgroundResource(R.drawable.undiscover_popup_background);
            }
        });

        hiddenGemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPinSelectBool = true;
                mPinType = setPinBackResourceImage(view.getId(), beenThereImg, toBeExploredImg, hiddenGemImg, saveBtn);
                offlineLayout.setBackgroundResource(R.drawable.hidden_popup_background);
            }
        });

        placeNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setBackgroundResource(R.drawable.edittext_white_bg);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        placeNameEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                placeNameEdt.setFocusableInTouchMode(true);
                placeNameEdt.setFocusable(true);
                return false;
            }
        });

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowOfflinePinDialog.dismiss();


            }
        });
        mShowOfflinePinDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (placeNameEdt.getText().toString().trim().isEmpty()) {
                    textInputLayout.clearAnimation();
                    textInputLayout.setAnimation(Utils.shakeError());
                    placeNameEdt.setError(context.getString(R.string.enter_place_name));
                    textInputLayout.setBackgroundResource(R.drawable.edit_text_error_bg);

                } else if (!isPinSelectBool) {
                    DialogManager.getInstance().showToast(context, context.getString(R.string.select_pin_type));
                } else {

                    interfaceWithTwoArgumentCallBack.withParams(placeNameEdt.getText().toString().trim(), mPinType);
                    mShowOfflinePinDialog.dismiss();
                }

            }
        });

        alertShowing(mShowOfflinePinDialog);

    }

    private String setPinBackResourceImage(int checkId, ImageView beenThereImg, ImageView toExploredImg, ImageView hiddenGemImg, Button saveBtnLay) {
        beenThereImg.setImageResource(checkId == beenThereImg.getId() ? R.drawable.discovered_map_pin : R.drawable.discover_gray);
        toExploredImg.setImageResource(checkId == toExploredImg.getId() ? R.drawable.undiscovered_map_pin : R.drawable.undiscover_gray);
        hiddenGemImg.setImageResource(checkId == hiddenGemImg.getId() ? R.drawable.hidden_map_pin : R.drawable.hidden_gray);
//        saveBtnLay.setBackgroundResource(R.drawable.blue_btn);
        return String.valueOf(checkId == beenThereImg.getId() ? 1 : checkId == toExploredImg.getId() ? 2 : 3);
    }


    public void showNetworkErrorPopup(Context context, final InterfaceBtnCallBack dialogAlertInterface) {

        alertDismiss(mNetworkErrorDialog);
        context = (context == null ? OUAMApplication.getContext() : context);

        mNetworkErrorDialog = getDialog(context, R.layout.popup_network_alert);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mNetworkErrorDialog.getWindow();

        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);
            window.getAttributes().windowAnimations = R.style.PopupBottomAnimation;
        }

        Button retryBtn;

        //Init View
        retryBtn = mNetworkErrorDialog.findViewById(R.id.retry_btn);


        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetworkErrorDialog.dismiss();
                dialogAlertInterface.onPositiveClick();
            }
        });

        alertShowing(mNetworkErrorDialog);
    }

    public void showImageUploadPopup(final Context context, String header, String btn1Name, String btn2Name, final InterfaceTwoBtnCallBack mCallback) {
        alertDismiss(mImageUploadDialog);
        mImageUploadDialog = getDialog(context, R.layout.popup_photo_selection);
        TextView alertMsgTxt;
        Button firstBtn, secondBtn, cancelBtn;


        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mImageUploadDialog.getWindow();

        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.BOTTOM);
            window.getAttributes().windowAnimations = R.style.PopupBottomAnimation;

        }

        /*Init view*/
        alertMsgTxt = mImageUploadDialog.findViewById(R.id.alert_msg_txt);
        firstBtn = mImageUploadDialog.findViewById(R.id.first_btn);
        secondBtn = mImageUploadDialog.findViewById(R.id.second_btn);
        cancelBtn = mImageUploadDialog.findViewById(R.id.cancel_btn);

        /*Set data*/
        alertMsgTxt.setText(header);
        firstBtn.setText(btn1Name);
        secondBtn.setText(btn2Name);

        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUploadDialog.dismiss();
                mCallback.onNegativeClick();
            }
        });
        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageUploadDialog.dismiss();
                mCallback.onPositiveClick();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageUploadDialog.dismiss();
            }
        });
        mImageUploadDialog.setCanceledOnTouchOutside(true);

        alertShowing(mImageUploadDialog);
    }


    public void showOptionPopup(Context context, String message, String firstBtnName, String secondBtnName,
                                final InterfaceTwoBtnCallBack dialogAlertInterface) {
        alertDismiss(mOptionDialog);
        mOptionDialog = getDialog(context, R.layout.popup_basic_alert);

        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mOptionDialog.getWindow();

        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
        }

        TextView msgTxt;
        Button firstBtn, secondBtn;
        View view;

        /*Init view*/
        msgTxt = mOptionDialog.findViewById(R.id.alert_msg_txt);
        firstBtn = mOptionDialog.findViewById(R.id.alert_negative_btn);
        secondBtn = mOptionDialog.findViewById(R.id.alert_positive_btn);

        firstBtn.setVisibility(View.VISIBLE);

        /*Set data*/
        msgTxt.setText(message);
        firstBtn.setText(firstBtnName);
        secondBtn.setText(secondBtnName);

        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionDialog.dismiss();
                dialogAlertInterface.onPositiveClick();
            }
        });
        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionDialog.dismiss();
                dialogAlertInterface.onNegativeClick();
            }
        });

        alertShowing(mOptionDialog);

    }


    public void showImagePopUp(Context context, String imageUrl) {
        alertDismiss(mShowImgDialog);
        mShowImgDialog = getDialog(context, R.layout.popup_image_view_lay);
        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mShowImgDialog.getWindow();
        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
        }
        ImageView imageView, closeImage;
        imageView = mShowImgDialog.findViewById(R.id.image);
        closeImage = mShowImgDialog.findViewById(R.id.close_img);


        if (imageUrl.isEmpty()) {
            imageView.setImageResource(R.color.blue);
        } else {
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions().error(R.color.blue))
                        .into(imageView);

            } catch (Exception ex) {
                imageView.setImageResource(R.color.blue);
            }
        }

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowImgDialog.dismiss();
            }
        });
        mShowImgDialog.setCanceledOnTouchOutside(true);
        alertShowing(mShowImgDialog);

    }


    public void showProgress(Context context) {

        /*To check if the progressbar is shown, if the progressbar is shown it will be cancelled */
        hideProgress();

        /*Init progress dialog*/
        mProgressDialog = new Dialog(context);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mProgressDialog.getWindow() != null) {
            mProgressDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mProgressDialog.setContentView(R.layout.popup_progress);
            mProgressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mProgressDialog.getWindow().setGravity(Gravity.CENTER);
            mProgressDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        /*To check if the dialog is null or not. if it'border_with_transparent_bg not a null, the dialog will be shown orelse it will not get appeared*/
        try {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            Log.e(AppConstants.TAG, e.getMessage());
        }
    }

    public void hideProgress() {
        /*hide progress*/
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
                Log.e(AppConstants.TAG, e.getMessage());
            }
        }
    }


    public void showWelcomePopup(Context context, String userImgURLStr) {

        alertDismiss(mWelcomeDialog);
        mWelcomeDialog = getDialog(context, R.layout.popup_preview);

        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        Window window = mWelcomeDialog.getWindow();

        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
        }
        ViewPager mViewPager = mWelcomeDialog.findViewById(R.id.view_pager);
        PageIndicatorView mPageIndicatorView = mWelcomeDialog.findViewById(R.id.page_indicator_view);
        ImageView closeImg = mWelcomeDialog.findViewById(R.id.close_img);


        ArrayList<Integer> tutorialXmlIntArrList = new ArrayList<>();

        tutorialXmlIntArrList.add(R.layout.popup_tutorial_one);
        tutorialXmlIntArrList.add(R.layout.popup_tutorial_three);
        tutorialXmlIntArrList.add(R.layout.popup_tutorial_two);


        mViewPager.setAdapter(new DialogManager.CustomPagerAdapter(context, tutorialXmlIntArrList, userImgURLStr));
        mPageIndicatorView.setViewPager(mViewPager);

        //page change tracker
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWelcomeDialog.dismiss();

            }
        });

        alertShowing(mWelcomeDialog);
    }

    private void alertShowing(Dialog dialog) {
        /*To check if the dialog is null or not. if it'border_with_transparent_bg not a null, the dialog will be shown orelse it will not get appeared*/
        if (dialog != null) {
            try {
                dialog.show();
            } catch (Exception e) {
                Log.e(AppConstants.TAG, e.getMessage());
            }
        }
    }

    private void alertDismiss(Dialog dialog) {
        /*To check if the dialog is shown, if the dialog is shown it will be cancelled */
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(AppConstants.TAG, e.getMessage());
            }
        }

    }

    /*View pager*/
    private class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Integer> mTutorialXmlArrList;
        private String mUserImgURLStr;

        CustomPagerAdapter(Context context, ArrayList<Integer> tutorialXmlIntArrList, String userImgURLStr) {
            mContext = context;
            mTutorialXmlArrList = tutorialXmlIntArrList;
            mUserImgURLStr = userImgURLStr;
        }

        @Override
        public int getCount() {
            return mTutorialXmlArrList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewGroup rooViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(mTutorialXmlArrList.get(position),
                    container, false);

            if (position == 2) {
                RoundedImageView userImg = rooViewGroup.findViewById(R.id.user_img);
                if (mUserImgURLStr.isEmpty()) {
                    userImg.setImageResource(R.color.dark_blue);
                } else {
                    try {
                        Glide.with(mContext)
                                .load(mUserImgURLStr)
                                .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                .into(userImg);

                    } catch (Exception ex) {
                        userImg.setImageResource(R.color.dark_blue);
                    }
                }
            }

            container.addView(rooViewGroup);
            return rooViewGroup;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }



}
