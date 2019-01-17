package com.ouam.app.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.adapter.LocationListAdapter;
import com.ouam.app.commonInterfaces.InterfaceApiResponseCallBack;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.commonInterfaces.RefreshPinListInterface;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.model.AddPinResponse;
import com.ouam.app.model.PinPostResponse;
import com.ouam.app.model.PlacesSearchResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PathUtils;
import com.ouam.app.utils.Utils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class PlacePinDialogFragment extends DialogFragment implements InterfaceApiResponseCallBack {

    private Dialog mAddBeenThereDialog;
    private String mType, mPinType;
    private static WithEntity mWithEntity = null;

    private WithEntity mSelectedPlaceEntity = new WithEntity();
    private Uri mPictureFileUri;
    public final int REQUEST_CAMERA = 999;
    public final int REQUEST_GALLERY = 888;
    private String mUserPostImageOnePathStr = "", mUserPostImageTwoPathStr = "", mCommentStr = "";
    private RecyclerView placeRecyclerView;
    private RelativeLayout searchLay, firstImageLay, searchPinLay;
    private LinearLayout addPinLay, mCameraImgLay;
    public static RefreshPinListInterface mPinListInterface;

    private EditText postEdt;
    private ImageView mAddImg, mPinSelectTypeImg, locationImg, firstImg, firstCloseImg, cameraImg, mTypeImg, mType1Img;
    private TextView titleTxt, placeTypeTxt, placeNameTxt, locationNameTxt, addPinLayTitleTxt, mCountTxt, mNoValueTxt;
    private CardView mCardView;
    private int mCountInt = 0;

    private String mURLStr;

    public PlacePinDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PlacePinDialogFragment newInstance(RefreshPinListInterface refreshPinListInterface, String type, WithEntity withEntity) {
        mPinListInterface = refreshPinListInterface;
        mWithEntity = withEntity;
        PlacePinDialogFragment frag = new PlacePinDialogFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString("type");


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        alertDismiss(mAddBeenThereDialog);

        mAddBeenThereDialog = getDialog(getContext(), R.layout.popup_place_pin_dialog);

        WindowManager.LayoutParams LayoutParams = new WindowManager.LayoutParams();
        final Window window = mAddBeenThereDialog.getWindow();
        if (window != null) {
            LayoutParams.copyFrom(window.getAttributes());
            LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(LayoutParams);
            window.setGravity(Gravity.CENTER);
            window.getAttributes().windowAnimations = R.style.PopupBottomAnimation;
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }

        final ImageView closeImg, pinTypeImg, addCustomImg, mClearImg, backImg, cameraImg;
        final EditText searchEdt;
        final Button addPostBtn;

        /*Init view*/
//        titleTxt = mAddBeenThereDialog.findViewById(R.id.title_txt);
//        closeImg = mAddBeenThereDialog.findViewById(R.id.close_img);
//        searchEdt = mAddBeenThereDialog.findViewById(R.id.search_edt);
        placeRecyclerView = mAddBeenThereDialog.findViewById(R.id.places_recycler_view);
//        searchLay = mAddBeenThereDialog.findViewById(R.id.search_lay);
        addPinLay = mAddBeenThereDialog.findViewById(R.id.add_pin_lay);
        backImg = mAddBeenThereDialog.findViewById(R.id.back_img);
        addPostBtn = mAddBeenThereDialog.findViewById(R.id.add_post_btn);
//        pinTypeImg = mAddBeenThereDialog.findViewById(R.id.pin_type_img);
//        searchPinLay = mAddBeenThereDialog.findViewById(R.id.search_pin_lay);
        cameraImg = mAddBeenThereDialog.findViewById(R.id.capture_photo_img);
//        mTypeImg =mAddBeenThereDialog.findViewById(R.id.type_img);
        mType1Img  =mAddBeenThereDialog.findViewById(R.id.type1_img);
        mAddImg = mAddBeenThereDialog.findViewById(R.id.add_img);
        mCountTxt = mAddBeenThereDialog.findViewById(R.id.image_count_txt);
        mCameraImgLay = mAddBeenThereDialog.findViewById(R.id.camera_icon_lay);
        firstImg = mAddBeenThereDialog.findViewById(R.id.post_upload_frst_img);
//        secondImg = mAddBeenThereDialog.findViewById(R.id.post_upload_second_img);
        firstCloseImg = mAddBeenThereDialog.findViewById(R.id.post_frst_photo_close_img);
//        secondClostImg = mAddBeenThereDialog.findViewById(R.id.post_second_photo_close_img);
        firstImageLay = mAddBeenThereDialog.findViewById(R.id.post_upload_frst_img_lay);
//        secondImageLay = mAddBeenThereDialog.findViewById(R.id.post_upload_second_img_lay);
        mNoValueTxt = mAddBeenThereDialog.findViewById(R.id.no_values_txt);
        mCardView = mAddBeenThereDialog.findViewById(R.id.view_lay);
        locationImg = mAddBeenThereDialog.findViewById(R.id.location_img);
        placeNameTxt = mAddBeenThereDialog.findViewById(R.id.place_name_txt);
        placeTypeTxt = mAddBeenThereDialog.findViewById(R.id.place_type_txt);
        locationNameTxt = mAddBeenThereDialog.findViewById(R.id.place_location_name_txt);
        postEdt = mAddBeenThereDialog.findViewById(R.id.pin_post_edt);
        addPinLayTitleTxt = mAddBeenThereDialog.findViewById(R.id.add_pin_lay_title_txt);
//        mPinSelectTypeImg = mAddBeenThereDialog.findViewById(R.id.select_pin_type_img);


        if (mWithEntity != null) {
            backImg.setImageResource(R.drawable.close_icon);
            if (mWithEntity.getBestPhoto().isEmpty()) {
                mCardView.setVisibility(View.GONE);
            } else {
                try {
                    Glide.with(getContext())
                            .load(mWithEntity.getBestPhoto())
                            .into(locationImg);
                } catch (Exception ex) {
                    locationImg.setImageResource(R.drawable.demo_img);
                    Log.e(AppConstants.TAG, ex.getMessage());
                }
            }
            placeNameTxt.setText(mWithEntity.getName());
            placeTypeTxt.setText(mWithEntity.getCategory());
            locationNameTxt.setText(mWithEntity.getAddress());
//            searchLay.setVisibility(View.GONE);
            addPinLayTitleTxt.setVisibility(View.VISIBLE);
            addPinLay.setVisibility(View.VISIBLE);
            mCountTxt.setText(String.valueOf(mCountInt));
        } else {
//            searchLay.setVisibility(View.GONE);
            //  addPinLayTitleTxt.setVisibility(View.GONE);
        }

        if (!AppConstants.HOME_SCREEN_PHOTO.trim().isEmpty()) {
            mUserPostImageOnePathStr = AppConstants.HOME_SCREEN_PHOTO;
            if (mCountInt == 0) {
                mCountInt = mCountInt + 1;
            }
            mCountTxt.setText(String.valueOf(mCountInt));
            mCountTxt.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
            mAddImg.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
//            firstImg.setVisibility(View.VISIBLE);
            firstImageLay.setVisibility(View.VISIBLE);
//            firstCloseImg.setVisibility(View.VISIBLE);
            try {

                Glide.with(getApplicationContext())
                        .load(mUserPostImageOnePathStr)
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(firstImg);
            } catch (Exception ex) {
                Log.e(AppConstants.TAG, ex.getMessage());
            }
        }

        if(getActivity()!=null)
            switch (Integer.parseInt(mType)) {
                case 1:
//                searchBackGround.setStroke(5, getResources().getColor(R.color.accent_been_there_red));
//                addPinBackGround.setStroke(5, getResources().getColor(R.color.accent_been_there_red));
//                    searchLay.setBackgroundResource(R.drawable.discover_popup_background);
//                    mTypeImg.setImageResource(R.drawable.discovered_map_pin);
                    mType1Img.setImageResource(R.drawable.discovered_map_pin);
                    addPinLay.setBackgroundResource(R.drawable.discover_popup_background);
                    addPostBtn.setBackgroundResource(R.drawable.discover_popup_background);
                    addPostBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.discovered_bg_start_color));
//                pinTypeImg.setImageResource(R.drawable.been_there_icon);
                    cameraImg.setImageResource(R.drawable.discover_camera_icon);
//                    searchPinLay.setBackgroundResource(R.drawable.edittext_discover_bg);
                    addPinLayTitleTxt.setText(R.string.discovered);
//                    titleTxt.setText(R.string.discovered);
                    mPinType = AppConstants.BEEN_THERE_PIN;
                    break;
                case 2:
//                searchBackGround.setStroke(5, getResources().getColor(R.color.accent_explore_blue));
//                addPinBackGround.setStroke(5, getResources().getColor(R.color.accent_explore_blue));
//                pinTypeImg.setImageResource(R.drawable.to_be_explored_icon);
//                    searchLay.setBackgroundResource(R.drawable.undiscover_popup_background);
                    addPinLay.setBackgroundResource(R.drawable.undiscover_popup_background);
//                    mTypeImg.setImageResource(R.drawable.undiscovered_map_pin);
                    mType1Img.setImageResource(R.drawable.undiscovered_map_pin);
                    cameraImg.setImageResource(R.drawable.undiscover_camera_icon);
                    addPostBtn.setBackgroundResource(R.drawable.undiscover_popup_background);
                    addPostBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.un_discovered_bg_start_color));
//                    searchPinLay.setBackgroundResource(R.drawable.edittext_undiscover_bg);
                    addPinLayTitleTxt.setText(R.string.undiscovered);
//                    titleTxt.setText(R.string.undiscovered);
                    mPinType = AppConstants.TO_BE_EXPLORED_PIN;
                    break;
                case 3:
//                searchBackGround.setStroke(5, getResources().getColor(R.color.accent_hidden_gem_violet));
//                addPinBackGround.setStroke(5, getResources().getColor(R.color.accent_hidden_gem_violet));
//                pinTypeImg.setImageResource(R.drawable.hidden_gem_icon);
//                    searchLay.setBackgroundResource(R.drawable.hidden_popup_background);
                    addPinLay.setBackgroundResource(R.drawable.hidden_popup_background);
//                    mTypeImg.setImageResource(R.drawable.hidden_map_pin);
                    mType1Img.setImageResource(R.drawable.hidden_map_pin);
                    cameraImg.setImageResource(R.drawable.hidden_camera_icon);
                    addPostBtn.setBackgroundResource(R.drawable.hidden_popup_background);
                    addPostBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.hidden_gem_bg_start_color));
//                    searchPinLay.setBackgroundResource(R.drawable.edittext_hidden_bg);
                    addPinLayTitleTxt.setText(R.string.hidden_gem);
//                    titleTxt.setText(R.string.hidden_gem);
                    mPinType = AppConstants.HIDDEN_GEM_PIN;
                    break;


            }

        if (mWithEntity == null) {

            Double latitude = AppConstants.PHOTO_LATITUDE != 0.0 ? AppConstants.PHOTO_LATITUDE : AppConstants.CURRENT_LAT;
            Double longitude = AppConstants.PHOTO_LONGITUDE != 0.0 ? AppConstants.PHOTO_LONGITUDE : AppConstants.CURRENT_LONG;
            if (NetworkUtil.isNetworkAvailable(getContext())) {
                mURLStr = AppConstants.BASE_URL + AppConstants.PLACE_SEARCH_URL + AppConstants.LAT_URL +
                        latitude + AppConstants.LON_URL +
                        longitude + AppConstants.RADIUS_URL + AppConstants.RADIUS;
                APIRequestHandler.getInstance().placesSearchApi(mURLStr, getContext(), this);
            }
        }

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCommentStr = postEdt.getText().toString();
                alertDismiss(mAddBeenThereDialog);
                addPostApi();
            }
        });

        firstCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountInt == 1) {
//                    firstCloseImg.setVisibility(View.GONE);
                    firstImageLay.setVisibility(View.GONE);
                    mUserPostImageOnePathStr = "";
                    mCountInt = 0;

                } else if (mCountInt == 2) {

//                    firstCloseImg.setVisibility(View.VISIBLE);
                    firstImageLay.setVisibility(View.VISIBLE);
                    mUserPostImageTwoPathStr = "";
                    mCountInt = 1;

                    try {

                        Glide.with(getApplicationContext())
                                .load(mUserPostImageOnePathStr)
                                .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                .into(firstImg);
                    } catch (Exception ex) {
                        Log.e(AppConstants.TAG, ex.getMessage());
                    }

                }

                mAddImg.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                mCountTxt.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                mCountTxt.setText(String.valueOf(mCountInt));
            }
        });


//        closeImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDismiss(mAddBeenThereDialog);
//            }
//        });

        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCountInt < 2) {
                    uploadImage();
                } else {
                    mCameraImgLay.clearAnimation();
                    mCameraImgLay.setAnimation(Utils.shakeError());
                }

            }
        });
//
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                alertDismiss(mAddBeenThereDialog);
//                if (mWithEntity != null) {
                    mAddBeenThereDialog.dismiss();
//                } else {
////                    deleteAPi(mSelectedPlaceEntity);
//                    addPinLay.setVisibility(View.GONE);
//                    searchLay.setVisibility(View.VISIBLE);
//                }

            }
        });

//        searchEdt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                callPlaceSearchAPI(searchEdt.getText().toString());
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

//        searchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    callPlaceSearchAPI(searchEdt.getText().toString());
//
//                    return true;
//                }
//                return false;
//            }
//        });

//        searchEdt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchEdt.setCursorVisible(true);
//            }
//        });
//



        alertShowing(mAddBeenThereDialog);
        return mAddBeenThereDialog;
//
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        AppConstants.HOME_SCREEN_PHOTO = "";
        mCountInt = 0;

    }

    /*api call*/
    private void callPlaceSearchAPI(String placeStr) {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            Double latitude = AppConstants.PHOTO_LATITUDE != 0.0 ? AppConstants.PHOTO_LATITUDE : AppConstants.CURRENT_LAT;
            Double longitude = AppConstants.PHOTO_LONGITUDE != 0.0 ? AppConstants.PHOTO_LONGITUDE : AppConstants.CURRENT_LONG;
            String url = AppConstants.BASE_URL + AppConstants.PLACE_SEARCH_URL + AppConstants.LAT_URL +
                    latitude + AppConstants.LON_URL +
                    longitude + AppConstants.RADIUS_URL + AppConstants.RADIUS + AppConstants.QUERY + placeStr;

            APIRequestHandler.getInstance().placesSearchApi(url, getActivity(), this);

        } else {
            DialogManager.getInstance().showNetworkErrorPopup(getContext(), new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    callPlaceSearchAPI(placeStr);

                }
            });
        }

    }

    private void addPinApi(WithEntity mSelectedPlaceEntity) {

        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

        addPinInputEntity.setPinType(mPinType);

        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + mSelectedPlaceEntity.getId() + "%7Cfs:" + mSelectedPlaceEntity.getProviderIDs().getFs() + AppConstants.PIN_URL;

        System.out.println("Pin url---"+url);
        APIRequestHandler.getInstance().addPinApi(addPinInputEntity, url, getContext(), new InterfaceApiResponseCallBack() {
            @Override
            public void onRequestSuccess(Object obj) {

                AddPinResponse addPinResponse = (AddPinResponse) obj;
                if (addPinResponse.getStatus().equals(getString(R.string.succeeded))) {
                    mPinListInterface.refreshUserPinListApi();
                    DialogManager.getInstance().showToast(getApplicationContext(), addPinResponse.getStatus() + " " + addPinResponse.getBy() + " " + addPinResponse.getThe());
                }
            }

            @Override
            public void onRequestFailure(Throwable r) {

            }
        });


    }


    private void uploadImage() {
        DialogManager.getInstance().showImageUploadPopup(getContext(),
                getString(R.string.select_photo), getString(R.string.take_photo), getString(R.string.choose_existing_photo), new InterfaceTwoBtnCallBack() {
                    @Override
                    public void onNegativeClick() {
                        captureImage();
                    }

                    @Override
                    public void onPositiveClick() {
                        galleryImage();
                    }
                });
    }


    private void addPostApi() {
        String placeIdStr = "";
        if (mWithEntity != null) {
            placeIdStr = mWithEntity.getId();
        } else {
            placeIdStr = mSelectedPlaceEntity.getId() + "%7Cfs:" + mSelectedPlaceEntity.getProviderIDs().getFs();
        }

        String url = AppConstants.BASE_URL + AppConstants.PLACE_URL + placeIdStr + AppConstants.POST_URL;
        System.out.println("Post url---"+url);
        APIRequestHandler.getInstance().addPostApi(url, mCommentStr, mUserPostImageOnePathStr, mUserPostImageTwoPathStr, getContext(), new InterfaceApiResponseCallBack() {
            @Override
            public void onRequestSuccess(Object obj) {
                PinPostResponse pinPostResponse = (PinPostResponse) obj;
                pinPostResponse.getWithEntity().getAccess_token();
                DialogManager.getInstance().showToast(getApplicationContext(), String.valueOf(pinPostResponse.getStatus() + " " + pinPostResponse.getBy() + " " + pinPostResponse.getThe()));
                alertDismiss(mAddBeenThereDialog);


                AppConstants.PHOTO_LATITUDE = 0.0;
                AppConstants.PHOTO_LONGITUDE = 0.0;
                mPinListInterface.refreshUserPinListApi();

            }

            @Override
            public void onRequestFailure(Throwable r) {

            }
        });
    }

    /*open camera Image*/
    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureFileUri = (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider",
                ImageUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE)) :
                Uri.fromFile(ImageUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureFileUri);

        AppConstants.PIN_DIALOG_ACTIVITY_RESULT_FLAG = true;
        // start the image capture Intent
        getActivity().startActivityForResult(intent, REQUEST_CAMERA);

    }

    /*open gallery Image*/
    private void galleryImage() {

        Intent j = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        AppConstants.PIN_DIALOG_ACTIVITY_RESULT_FLAG = true;
        getActivity().startActivityForResult(j, REQUEST_GALLERY);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /*Open Camera Request Check*/
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    AppConstants.IS_CALLED_ALREADY = false;

                    try {
                        mCountInt = mCountInt + 1;
                        if (mCountInt == 1) {
                            mUserPostImageOnePathStr = mPictureFileUri.getPath();
//                            firstImg.setVisibility(View.VISIBLE);
                            firstImageLay.setVisibility(View.VISIBLE);
//                            firstCloseImg.setVisibility(View.VISIBLE);

                            try {

                                Glide.with(getApplicationContext())
                                        .load(mUserPostImageOnePathStr)
                                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                        .into(firstImg);
                            } catch (Exception ex) {
                                Log.e(AppConstants.TAG, ex.getMessage());
                            }

                        } else if (mCountInt == 2) {
                            mUserPostImageTwoPathStr = mPictureFileUri.getPath();
//                            firstImg.setVisibility(View.VISIBLE);
                            firstImageLay.setVisibility(View.VISIBLE);
//                            firstCloseImg.setVisibility(View.VISIBLE);

                            try {

                                Glide.with(getApplicationContext())
                                        .load(mUserPostImageTwoPathStr)
                                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                        .into(firstImg);
                            } catch (Exception ex) {
                                Log.e(AppConstants.TAG, ex.getMessage());
                            }

                        }


                        mAddImg.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                        mCountTxt.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                        mCountTxt.setText(String.valueOf(mCountInt));
//                        Glide.with(this)
//                                .load(mPictureFileUri).error(R.drawable.user_demo_icon)
//                                .into(mProfileImg);

                    } catch (Exception e) {
                        e.printStackTrace();
//                        mProfileImg.setImageResource(R.drawable.user_demo_icon);
                    }


                } else {
                    if (resultCode == RESULT_CANCELED) {
                        /*Cancelling the image capture process by the user*/

                    } else {
                        /*image capture getting failed due to certail technical issues*/

                    }
                }
                break;
            /*Open Photo Gallery Request Check*/
            case REQUEST_GALLERY:

                if (resultCode == RESULT_OK) {
                    // successfully captured the image
                    // display it in image view
                    AppConstants.IS_CALLED_ALREADY = false;

                    Uri selectedImagePath = data.getData();

                    try {

                        mCountInt = mCountInt + 1;

                        if (mCountInt == 1) {
                            mUserPostImageOnePathStr = PathUtils.getPath(getContext(), selectedImagePath);

//                            firstImg.setVisibility(View.VISIBLE);
                            firstImageLay.setVisibility(View.VISIBLE);
//                            firstCloseImg.setVisibility(View.VISIBLE);

                            try {

                                Glide.with(getApplicationContext())
                                        .load(mUserPostImageOnePathStr)
                                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                        .into(firstImg);
                            } catch (Exception ex) {
                                Log.e(AppConstants.TAG, ex.getMessage());
                            }


                        } else if (mCountInt == 2) {
                            mUserPostImageTwoPathStr = PathUtils.getPath(getContext(), selectedImagePath);
//                            firstImg.setVisibility(View.VISIBLE);
                            firstImageLay.setVisibility(View.VISIBLE);
//                            firstCloseImg.setVisibility(View.VISIBLE);

                            try {

                                Glide.with(getApplicationContext())
                                        .load(mUserPostImageTwoPathStr)
                                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                        .into(firstImg);
                            } catch (Exception ex) {
                                Log.e(AppConstants.TAG, ex.getMessage());
                            }


                        }


                        mAddImg.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                        mCountTxt.setVisibility(mCountInt > 0 ? View.VISIBLE : View.GONE);
                        mCountTxt.setText(String.valueOf(mCountInt));

//                        Glide.with(this)
//                                .load(selectedImagePath)
//                                .into(mProfileImg);

                    } catch (Exception e) {
                        e.printStackTrace();
//                        mProfileImg.setImageResource(R.drawable.user_demo_icon);
                    }

                } else {
                    if (resultCode == RESULT_CANCELED) {
                        /*Cancelling the image capture process by the user*/

                    } else {
                        /*image capture getting failed due to certail technical issues*/

                    }
                }
                break;

        }
    }


    @Override
    public void onRequestSuccess(Object obj) {

        PlacesSearchResponse placesSearchResponse = (PlacesSearchResponse) obj;
        final LocationListAdapter locationListAdapter = new LocationListAdapter(getContext(), placesSearchResponse.getWith(), mType,
                new LocationListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked() {

                        addPinLay.setVisibility(View.VISIBLE);
//                        searchLay.setVisibility(View.GONE);

                    }

                    @Override
                    public void setCity(WithEntity mEntity) {

                        mSelectedPlaceEntity = mEntity;
                        addPinLay.setVisibility(View.VISIBLE);
                        postEdt.setHint(getString(R.string.post_hint) + " " + mEntity.getName());
//                        searchLay.setVisibility(View.GONE);

                        if (mWithEntity != null) {
                            placeNameTxt.setText(mWithEntity.getName());
                            placeTypeTxt.setText(mWithEntity.getCategory());
                            locationNameTxt.setText(mWithEntity.getAddress());

                            if (mWithEntity.getBestPhoto().isEmpty()) {
                                mCardView.setVisibility(View.GONE);
                            } else {
                                try {
                                    Glide.with(getContext())
                                            .load(mWithEntity.getBestPhoto())
                                            .into(locationImg);
                                } catch (Exception ex) {
                                    locationImg.setImageResource(R.drawable.demo_img);
                                    Log.e(AppConstants.TAG, ex.getMessage());
                                }
                            }


                        } else {
                            placeNameTxt.setText(mEntity.getName());
                            placeTypeTxt.setText(mEntity.getCategory());
                            locationNameTxt.setText(mEntity.getCity().getName().isEmpty() ? "" : (mEntity.getCity().getName() + ",") + (mEntity.getCity().getLocality().isEmpty() ? " " : mEntity.getCity().getLocality() + ",") + mEntity.getCity().getCountry());
                            if (mEntity.getBestPhoto().isEmpty()) {
                                mCardView.setVisibility(View.GONE);
                            } else {
                                try {
                                    Glide.with(getContext())
                                            .load(mEntity.getBestPhoto())
                                            .into(locationImg);
                                } catch (Exception ex) {
                                    locationImg.setImageResource(R.drawable.demo_img);
                                    Log.e(AppConstants.TAG, ex.getMessage());
                                }
                            }

                        }
//
//                        mPinSelectTypeImg.setImageResource(mEntity.getPinnedAs().
//                                equalsIgnoreCase(getString(R.string.subtype_been_there)) ? R.drawable.check_icon : mEntity.getPinnedAs().equalsIgnoreCase(getString(R.string.subtype_to_be_explored)) ? R.drawable.plane_icon :
//                                mEntity.getPinnedAs().equalsIgnoreCase(getString(R.string.sub_type_hidden_gem)) ? R.drawable.gem_icon :
//                                        mType.equalsIgnoreCase(getString(R.string.type_been_there)) ? R.drawable.check_icon :
//                                                mType.equalsIgnoreCase(getString(R.string.type_explore)) ? R.drawable.plane_icon :
//                                                        mType.equalsIgnoreCase(getString(R.string.type_hidden_gem)) ? R.drawable.hidden_gem_icon : R.drawable.check_icon);

//                        mPinSelectTypeImg.setImageResource(mType.equalsIgnoreCase(getString(R.string.type_been_there)) ? R.drawable.check_icon :
//                                mType.equalsIgnoreCase(getString(R.string.type_explore)) ? R.drawable.plane_icon :
//                                        mType.equalsIgnoreCase(getString(R.string.type_hidden_gem)) ? R.drawable.gem_icon : R.drawable.check_icon);
//                        if (checked) {
                        addPinApi(mSelectedPlaceEntity);
//                        } else {
//                            deleteAPi(mSelectedPlaceEntity);
//                        }

                    }
                });
//        placeRecyclerView.setVisibility(placesSearchResponse.getWith().size() > 0 ? View.VISIBLE : View.GONE);
//        mNoValueTxt.setVisibility(placesSearchResponse.getWith().size() > 0 ? View.GONE : View.VISIBLE);
//        placeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        placeRecyclerView.setAdapter(locationListAdapter);
    }

    @Override
    public void onRequestFailure(Throwable r) {

    }
}
