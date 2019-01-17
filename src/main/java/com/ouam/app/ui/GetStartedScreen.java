package com.ouam.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.chat.SendBirdUtils;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.entity.ImageUploadResultEntity;
import com.ouam.app.entity.LoginInputEntity;
import com.ouam.app.entity.SignUpEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.AddressResponse;
import com.ouam.app.model.ProfileUpdateResponse;
import com.ouam.app.model.UserNameExistResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AddressUtil;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.ImageUtils;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PathUtils;
import com.ouam.app.utils.PlaceJSONParser;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RoundedImageView;
import com.ouam.app.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class GetStartedScreen extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.parent_lay)
    LinearLayout mParentLay;

    @BindView(R.id.get_start_header_bg_lay)
    RelativeLayout mGetStartHeaderBgLay;

    @BindView(R.id.user_name_edt)
    EditText mUserNameEdt;

    @BindView(R.id.user_name_txt)
    TextView mUserNameTxt;

    @BindView(R.id.user_name_text_input_lay)
    TextInputLayout mUserNameTxtInputLay;

    @BindView(R.id.home_city_txt_input_lay)
    TextInputLayout mHomeCityTxtInputLay;

    @BindView(R.id.user_img)
    RoundedImageView mProfileImg;

    @BindView(R.id.get_started_btn)
    Button mGetStartBtnTxt;

    @BindView(R.id.location_lay)
    RelativeLayout mLocationLay;

    @BindView(R.id.down_img)
    ImageView mDownImg;

    @BindView(R.id.terms_cond_lay)
    LinearLayout mTermsCondLay;

//    @BindView(R.id.home_city)
//    EditText mLocationEdt;

    @BindView(R.id.place_suggest_atx)
    AutoCompleteTextView mAutoCompleteTextView;

    @BindView(R.id.terms_cond_img)
    ImageView mTermsCondImg;

    private GoogleApiClient mGoogleApiClient;
    private final int REQUEST_CHECK_SETTINGS = 300;
    private Uri mPictureFileUri;
    public final int REQUEST_CAMERA = 999;
    public final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public final int REQUEST_GALLERY = 888;


    private UserDetailsEntity mUserDetailsEntityRes;
    private String mLatitudeStr = "0.0", mLongitudeStr = "0.0", mHomeCityStr, mHomeStateStr, mHomeCountryStr;

    private PlaceTask mPlacesTask;
    private String mUserProfileImagePath = "";

    private File mUserImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_get_started_screen);

        initView();

    }

    //init views

    private void initView() {
        ButterKnife.bind(this);

        setupUI(mParentLay);

        setHeaderView();

        setData();

        initGoogleAPIClient();

//        setCurrentLocation();

        mUserNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserNameTxtInputLay.setBackgroundResource(R.drawable.edittext_white_bg);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlacesTask = new GetStartedScreen.PlaceTask();
                mPlacesTask.execute(s.toString());
                mLocationLay.setBackgroundResource(R.drawable.edittext_white_bg);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // getLatLangApi(mAutoCompleteTextView.getText().toString().trim());
                getUserAddressApi(mAutoCompleteTextView.getText().toString().trim());
            }
        });

//        mHeaderEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    AppConstants.SEARCH_PLACE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//                    AppConstants.SEARCH_PEOPLE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//                    AppConstants.SEARCH_CITY_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//                    return true;
//                }
//                return false;
//            }
//        });

        mTermsCondImg.setTag(0);

    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mGetStartHeaderBgLay.post(new Runnable() {
                public void run() {
                    mGetStartHeaderBgLay.setPadding(0, getStatusBarHeight(GetStartedScreen.this), 0, 0);
                }
            });
        }
    }

    @OnClick({R.id.down_img, R.id.profile_img_lay, R.id.terms_cond_img, R.id.get_started_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_img:
                onBackPressed();
                break;
            case R.id.profile_img_lay:
                if (checkPermission()) {
                    uploadImage();
                }
                break;
            case R.id.terms_cond_img:
                mTermsCondImg.setImageResource(mTermsCondImg.getTag().equals(0) ? R.drawable.checked : R.drawable.un_checked);
                mTermsCondImg.setTag(mTermsCondImg.getTag().equals(0) ? 1 : 0);
                break;

//            case R.id.location_lay:
            case R.id.place_suggest_atx:
                if (NetworkUtil.isNetworkAvailable(this)) {
                    findPlace();
                } else {
                    DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                        @Override
                        public void onPositiveClick() {
                            findPlace();
                        }
                    });
                }
                break;
            case R.id.get_started_btn:
                filedValidation();
                break;
        }
    }


    private void filedValidation() {
        if (mUserNameEdt.getText().toString().trim().isEmpty()) {
            mUserNameTxtInputLay.clearAnimation();
            mUserNameTxtInputLay.setAnimation(Utils.shakeError());
            mUserNameEdt.setError(getString(R.string.enter_user_name));
            mUserNameTxtInputLay.setBackgroundResource(R.drawable.edit_text_error_bg);
        } else if (mAutoCompleteTextView.getText().toString().isEmpty()) {
            mLocationLay.clearAnimation();
            mLocationLay.setAnimation(Utils.shakeError());
            mAutoCompleteTextView.setError(getString(R.string.enter_location));
            mLocationLay.setBackgroundResource(R.drawable.edit_text_error_bg);
        } else if (mTermsCondLay.getVisibility() == View.VISIBLE && mTermsCondImg.getTag().equals(0)) {
            mTermsCondImg.clearAnimation();
            mTermsCondImg.setAnimation(Utils.shakeError());
            DialogManager.getInstance().showToast(this, getString(R.string.plz_agree_terms_cond));
        } else {

            mUserDetailsEntityRes = new UserDetailsEntity();
            mUserDetailsEntityRes.setName(mUserNameEdt.getText().toString().trim());
            mUserDetailsEntityRes.setFirstname(AppConstants.USER_FIRST_NAME);
            mUserDetailsEntityRes.setEmail(AppConstants.EMAIL);
            mUserDetailsEntityRes.setImage(AppConstants.USER_IMAGE);
            mUserDetailsEntityRes.setLocation(mAutoCompleteTextView.getText().toString().trim());
            mUserDetailsEntityRes.setLatitude(mLatitudeStr);
            mUserDetailsEntityRes.setLongitude(mLongitudeStr);
            mUserDetailsEntityRes.setFBAccesToken(AppConstants.SOCIAL_ACCESS_TOKEN);
            mUserDetailsEntityRes.setAuthorizationToken(AppConstants.ACCESS_TOKEN);
            mUserDetailsEntityRes.setUserId(AppConstants.PROFILE_USER_ID);


            if (PreferenceUtil.getBoolPreferenceValue(GetStartedScreen.this, AppConstants.LOGIN_STATUS)) {
                if (AppConstants.USER_NAME.equals(mUserNameEdt.getText().toString().trim())) {
                    profileUpdateAPICall();
                } else {
                    checkUserNameExistAPI();
                }
            } else {
                checkUserNameExistAPI();
            }
        }
    }

    /*Init Google API clients*/
    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setData() {

        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(this, AppConstants.USER_DETAILS);
        mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);

        mGetStartBtnTxt.setText(PreferenceUtil.getBoolPreferenceValue(this, AppConstants.LOGIN_STATUS) ? getString(R.string.all_done) :
                getString(R.string.let_Undiscover));

        /*Edit flow*/
        if (mUserDetailsEntityRes != null) {

            mDownImg.setVisibility(mUserDetailsEntityRes.getName().isEmpty() ? View.GONE : View.VISIBLE);
            mTermsCondLay.setVisibility(mUserDetailsEntityRes.getName().isEmpty() ? View.VISIBLE : View.INVISIBLE);

            mUserNameEdt.setText(mUserDetailsEntityRes.getUserName());
            mUserNameEdt.setSelection(mUserNameEdt.getText().length());
            mUserNameTxt.setText(!mUserDetailsEntityRes.getName().isEmpty() ? mUserDetailsEntityRes.getName() + ", " +
                    getString(R.string.welcome_to) : getString(R.string.welcome_to));
            if (mUserDetailsEntityRes.getImage().isEmpty()) {
                mProfileImg.setImageResource(R.color.app_color);
            } else {
                try {

                    Glide.with(this)
                            .load(mUserDetailsEntityRes.getImage())
                            .into(mProfileImg);

                    AppConstants.USER_IMAGE = mUserDetailsEntityRes.getImage();

                } catch (Exception ex) {
                    mProfileImg.setImageResource(R.color.app_color);
                }
            }

            trackScreenName(getString(R.string.profile_edit_screen));

//
//
        } else {
            /*create flow*/
            mUserNameEdt.setText(AppConstants.USER_NAME.replace(" ", ""));
            mUserNameEdt.setSelection(mUserNameEdt.getText().length());
            mUserNameTxt.setText(!AppConstants.USER_FIRST_NAME.isEmpty() ? AppConstants.USER_FIRST_NAME + ", " +
                    getString(R.string.welcome_to) : getString(R.string.welcome_to));


            mDownImg.setVisibility(View.GONE);
            mTermsCondLay.setVisibility( View.VISIBLE );
            if (AppConstants.USER_IMAGE.isEmpty()) {
                mProfileImg.setImageResource(R.drawable.user_demo_icon);
            } else {
                try {

                    Glide.with(this)
                            .load(AppConstants.USER_IMAGE)
                            .into(mProfileImg);
                } catch (Exception ex) {
                    mProfileImg.setImageResource(R.drawable.user_demo_icon);
                }
            }
            trackScreenName(getString(R.string.get_started_screen));


        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationManager mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocManager != null && mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                initGoogleAPIClient();
            } else {
                setCurrentLocation();
            }
        } else {
            LocationSettingsRequest.Builder locSettingsReqBuilder = new LocationSettingsRequest.Builder().
                    addLocationRequest(AddressUtil.createLocationRequest());
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locSettingsReqBuilder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // API call.
                            if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                                initGoogleAPIClient();
                            } else {
                                setCurrentLocation();
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(GetStartedScreen.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }

                }
            });


        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*Google Place API PlaceAutoComplete*/
    public void findPlace() {
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(GetStartedScreen.this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    /*Set Current Location*/
    private void setCurrentLocation() {

        FusedLocationProviderClient mLastLocation = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if (!permissionsAccessLocation())
                return;
        }
        mLastLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override

            public void onSuccess(final Location location) {
                if (location != null) {

                    mLatitudeStr = mUserDetailsEntityRes != null &&
                            !mUserDetailsEntityRes.getLatitude().isEmpty() ? mUserDetailsEntityRes.getLatitude() : String.valueOf(location.getLatitude());
                    mLongitudeStr = mUserDetailsEntityRes != null &&
                            !mUserDetailsEntityRes.getLongitude().isEmpty() ? mUserDetailsEntityRes.getLongitude() : String.valueOf(location.getLongitude());
                    try {

                        ArrayList<String> addressList = AddressUtil.getAddressFromLatLng(GetStartedScreen.this,
                                Double.parseDouble(mLatitudeStr), Double.parseDouble(mLongitudeStr));
                        if (addressList.size() > 0) {
                            mHomeCityStr = addressList.get(1);
                            mHomeCountryStr = addressList.get(2);
                            mHomeStateStr = addressList.get(3);

                            if (mUserDetailsEntityRes == null || mUserDetailsEntityRes.getLocation().isEmpty()) {
                                mAutoCompleteTextView.setText(addressList.get(0));
                            } else {
                                mAutoCompleteTextView.setText(mUserDetailsEntityRes.getLocation());
                            }
                            getUserAddressApi(mAutoCompleteTextView.getText().toString().trim());

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
        });

    }


    /*Ask permission on Location access*/
    private boolean permissionsAccessLocation() {
        boolean addPermission = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissionCoarseLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            addPermission = askAccessPermission(listPermissionsNeeded, 1, new InterfaceTwoBtnCallBack() {
                @Override
                public void onNegativeClick() {

                }


                @Override
                public void onPositiveClick() {
                    setCurrentLocation();
                }


            });
        }

        return addPermission;
    }

    /* Ask for permission on Camera access*/
    private boolean checkPermission() {

        boolean addPermission = true;
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int readStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            addPermission = askAccessPermission(listPermissionsNeeded, 1, new InterfaceTwoBtnCallBack() {
                @Override
                public void onNegativeClick() {

                }


                @Override
                public void onPositiveClick() {
                    uploadImage();
                }


            });
        }

        return addPermission;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            /*Open Camera Request Check*/
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {

                    try {
                        mUserProfileImagePath = mPictureFileUri.getPath();
                        AppConstants.USER_IMAGE = mUserProfileImagePath;

                        mUserImageFile = new File(mPictureFileUri.getPath());

                        Glide.with(this)
                                .load(mPictureFileUri)
                                .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                                .into(mProfileImg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mProfileImg.setImageResource(R.drawable.user_demo_icon);
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
                    Uri selectedImagePath = data.getData();

                    try {
                        mUserProfileImagePath = PathUtils.getPath(this, selectedImagePath);
                        AppConstants.USER_IMAGE = mUserProfileImagePath;
                        mUserImageFile = new File(PathUtils.getPath(this, selectedImagePath));

                        Glide.with(this)
                                .load(selectedImagePath)
                                .into(mProfileImg);

                    } catch (Exception e) {
                        e.printStackTrace();
                        mProfileImg.setImageResource(R.drawable.user_demo_icon);
                    }

                } else {
                    if (resultCode == RESULT_CANCELED) {
                        /*Cancelling the image capture process by the user*/

                    } else {
                        /*image capture getting failed due to certail technical issues*/
                    }
                }
                break;

            /*Google API Client Check*/
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                            initGoogleAPIClient();
                        } else {
                            setCurrentLocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // user does not want to update setting. Handle it in a way that it will to affect your app functionality
                        break;
                }
                break;

//            /*Google Place API Request Code Check*/
//            case PLACE_AUTOCOMPLETE_REQUEST_CODE:
//
//                if (resultCode == RESULT_OK) {
//                    Place place = PlaceAutocomplete.getPlace(this, data);
//                    mAutoCompleteTextView.setText(place.getAddress().toString().trim());
//                    mLatitudeStr = String.valueOf(place.getLatLng().latitude);
//                    mLongitudeStr = String.valueOf(place.getLatLng().longitude);
//                    try {
//                        mLocality = AddressUtil.getAddressFromLatLng(GetStartedScreen.this,
//                                place.getLatLng().latitude, place.getLatLng().longitude).get(0);
//                        mHomeCity = AddressUtil.getAddressFromLatLng(GetStartedScreen.this,
//                                place.getLatLng().latitude, place.getLatLng().longitude).get(1);
//                        mHomeCountry = AddressUtil.getAddressFromLatLng(GetStartedScreen.this,
//                                place.getLatLng().latitude, place.getLatLng().longitude).get(2);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                    Status status = PlaceAutocomplete.getStatus(this, data);
//
//                } else if (resultCode == RESULT_CANCELED) {
//                    // The user canceled the operation.
//                }
//
//                break;

        }
    }


    @Override
    public void onBackPressed() {
        backScreen(false);
    }

    /*open camera Image*/
    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureFileUri = (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                ImageUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE)) :
                Uri.fromFile(ImageUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureFileUri);


        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    /*open gallery Image*/
    private void galleryImage() {

        Intent j = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(j, REQUEST_GALLERY);
    }

    private void uploadImage() {
        DialogManager.getInstance().showImageUploadPopup(this,
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

    /*update profile Image from FACEBOOK when user Login*/

    private void uploadFBProfileImage(String userFBImage) {

        URL url = null;
        try {
            url = new URL(userFBImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String urlPath = url.getPath();

        APIRequestHandler.getInstance().profileImageUploadApiCall(urlPath.substring(urlPath.lastIndexOf('/') + 1), this);

    }


    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof ProfileUpdateResponse) {
            ProfileUpdateResponse mResponse = (ProfileUpdateResponse) resObj;
            if (mResponse.getStatus().equals(
                    getString(R.string.succeeded))) {
                mUserDetailsEntityRes.setUserName(mUserNameEdt.getText().toString().trim());
                PreferenceUtil.storeUserDetails(GetStartedScreen.this, mUserDetailsEntityRes);
                /*Calling profile image upload api*/
                if (!mUserProfileImagePath.isEmpty()) {
                    APIRequestHandler.getInstance().profileImageUploadApiCall(mUserProfileImagePath, this);
                } else {
                    PreferenceUtil.storeBoolPreferenceValue(GetStartedScreen.this,
                            AppConstants.LOGIN_STATUS, true);
                    // DialogManager.getInstance().showToast(this, mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe());
                    nextScreen(HomeActivityFeedScreen.class, false);

                    if (PreferenceUtil.getBoolPreferenceValue(this, AppConstants.LOGIN_STATUS))

                        SendBirdUtils.updateCurrentUserInfo(this, mUserNameEdt.getText().toString().trim());
                }

            }

        }
        if (resObj instanceof UserNameExistResponse) {
            UserNameExistResponse mResponse = (UserNameExistResponse) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                if (mResponse.getWith().isExists()) {
                    mUserNameTxtInputLay.clearAnimation();
                    mUserNameTxtInputLay.setAnimation(Utils.shakeError());
                    mUserNameEdt.setError(getString(R.string.exists_error));
                    mUserNameTxtInputLay.setBackgroundResource(R.drawable.edit_text_error_bg);
                } else {

                    mUserNameTxtInputLay.setBackgroundResource(R.drawable.edittext_white_bg);
                    profileUpdateAPICall();

                }

            }
        }

        if (resObj instanceof AddressResponse) {
            AddressResponse mResponse = (AddressResponse) resObj;

            if (mResponse.getResults().size() > 0) {
                try {
                    ArrayList<String> addressList = AddressUtil.getAddressFromLatLng(this,
                            Double.parseDouble(mResponse.getResults().get(0).getGeometry().getLocation().getLat()),
                            Double.parseDouble(mResponse.getResults().get(0).getGeometry().getLocation().getLng()));

                    if (addressList.size() > 0) {
                        mHomeCityStr = addressList.get(1);
                    }
                    if (addressList.size() > 2) {
                        mHomeCountryStr = addressList.get(2);
                        mHomeStateStr = addressList.get(3);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


//        if (resObj instanceof LoginResponse) {
//            LoginResponse mResponse = (LoginResponse) resObj;
//            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
//                PreferenceUtil.storeBoolPreferenceValue(GetStartedScreen.this,
//                        AppConstants.LOGIN_STATUS, true);
//                mUserDetailsEntityRes.setAuthorizationToken(mResponse.getWith().getAccess_token());
//                mUserDetailsEntityRes.setUserId(mResponse.getWith().getUser_id());
//                AppConstants.ACCESS_TOKEN = mResponse.getWith().getAccess_token();
//                PreferenceUtil.storeUserDetails(GetStartedScreen.this, mUserDetailsEntityRes);
//                APIRequestHandler.getInstance().profileImageUploadApiCall(AppConstants.USER_IMAGE, GetStartedScreen.this);
//                DialogManager.getInstance().showToast(this, mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe());
//                nextScreen(HomeActivityFeedScreen.class, false);
//            }
//
//        }
        if (resObj instanceof ImageUploadResultEntity) {
            ImageUploadResultEntity mImageUploadResponse = (ImageUploadResultEntity) resObj;
            if (mImageUploadResponse.getStatus().equals(getString(R.string.succeeded))) {
//                DialogManager.getInstance().showToast(this, mImageUploadResponse.getStatus());
                PreferenceUtil.storeBoolPreferenceValue(GetStartedScreen.this,
                        AppConstants.LOGIN_STATUS, true);
                nextScreen(HomeActivityFeedScreen.class, false);


                if (PreferenceUtil.getBoolPreferenceValue(this, AppConstants.LOGIN_STATUS))
                    SendBirdUtils.updateCurrentUserProfileImage(mUserImageFile, mUserNameEdt.getText().toString().trim());


            }
        }


    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
    }


    /*UserName exits API call*/

    public void checkUserNameExistAPI() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String queryUrl = AppConstants.BASE_URL + AppConstants.USER_URL + mUserNameEdt.getText().toString().trim() + AppConstants.USER_NAME_EXIST_URL;
            APIRequestHandler.getInstance().userNameExistApi(queryUrl, this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    checkUserNameExistAPI();
                }
            });
        }
    }

    /*Sign Up API Call*/
    public void signUpAPICall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            SignUpEntity signUpEntity = new SignUpEntity();
            signUpEntity.setUsername(mUserNameEdt.getText().toString().trim());
            signUpEntity.setHomeCity(mHomeStateStr);
            signUpEntity.setHomeCountry(mHomeCountryStr);
            signUpEntity.setHomeLocality(mHomeCityStr);
            signUpEntity.setPlatform(AppConstants.PLATFORM);
            signUpEntity.setPlatformId(AppConstants.PLATFORM_ID);

            /*Sign Up API Call*/
            APIRequestHandler.getInstance().signUpAPICall(signUpEntity, GetStartedScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    signUpAPICall();
                }
            });
        }
    }

    /*Profile Update API Call*/
    public void profileUpdateAPICall() {

        if (NetworkUtil.isNetworkAvailable(this)) {
            SignUpEntity signUpEntity = new SignUpEntity();
            signUpEntity.setUsername(mUserNameEdt.getText().toString().trim());

            signUpEntity.setHomeCity(mHomeStateStr);
            signUpEntity.setHomeCountry(mHomeCountryStr);
            signUpEntity.setHomeLocality(mHomeCityStr);

            signUpEntity.setPlatform(AppConstants.PLATFORM);
            signUpEntity.setPlatformId(AppConstants.PLATFORM_ID);
            signUpEntity.setEmail(AppConstants.USER_EMAIL);

            if (mUserProfileImagePath.isEmpty()) {
                /*sending image from FB or Google profile image*/
                signUpEntity.setProfileImageURL(AppConstants.USER_IMAGE);
            } else {
                signUpEntity.setProfileImageURL("");
            }

            /*Profile Update API Call*/
            APIRequestHandler.getInstance().profileUpdateAPICall(signUpEntity, GetStartedScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    signUpAPICall();
                }
            });
        }
    }


    /*Login API Call*/
    public void loginAPICall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            LoginInputEntity loginInputEntity = new LoginInputEntity();
            loginInputEntity.setProvider(AppConstants.FACEBOOK);
            loginInputEntity.setAccessToken(AppConstants.SOCIAL_ACCESS_TOKEN);
            APIRequestHandler.getInstance().loginApiCall(GetStartedScreen.this, loginInputEntity);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    loginAPICall();
                }
            });
        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    @SuppressLint("StaticFieldLeak")
    private class PlaceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + getString(R.string.google_api_key);

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            GetStartedScreen.ParserTask parserTask = new GetStartedScreen.ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
                    return view;
                }


            };

            // Setting the adapter
            mAutoCompleteTextView.setAdapter(adapter);
        }
    }


    private void getUserAddressApi(String addressStr) {
        if (NetworkUtil.isNetworkAvailable(this)) {
            String addressURLStr = String.format(AppConstants.GET_DETAILS_ADDRESS_URL, getString(R.string.google_map_id), addressStr);
            APIRequestHandler.getInstance().callGetUserAddressAPI(addressURLStr, this, true);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    getUserAddressApi(addressStr);
                }
            });
        }

    }


}
