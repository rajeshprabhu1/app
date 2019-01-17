package com.ouam.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.ouam.app.R;
import com.ouam.app.adapter.OfflineModeAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.commonInterfaces.InterfaceWithTwoArgumentCallBack;
import com.ouam.app.database.DatabaseUtil;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.entity.CommonResultEntity;
import com.ouam.app.entity.OfflinePinEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AddressUtil;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.ouam.app.utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfflinePiningModeActivityScreen extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        InterfaceWithTwoArgumentCallBack, InterfaceBtnCallBack, OfflineModeAdapter.OnItemClickListener {

    @BindView(R.id.offline_pin_mode_par_lay)
    RelativeLayout mOfflinePinModeParLay;

    @BindView(R.id.offline_pin_lat_lng_txt)
    TextView mOfflinePinLatLngTxt;

    @BindView(R.id.offline_pin_list_lay)
    LinearLayout mOfflinePinListLay;

    @BindView(R.id.offline_mode_pin_recycler_view)
    RecyclerView mOfflinePinRecyclerView;

    @BindView(R.id.offline_pin_empty_lay)
    LinearLayout mOfflinePinEmptyListLay;

    @BindView(R.id.online_pin_mode_txt)
    TextView mOnlinePinModeTxt;

    @BindView(R.id.offline_pin_btn_mode_lay)
    LinearLayout mOfflinePinModeBtnLay;




    private ArrayList<OfflinePinEntity> mPinList = new ArrayList<>();

    private DatabaseUtil mDataBase;
    private GoogleApiClient mGoogleApiClient;
    private OfflineModeAdapter mAdapter;

    private final int REQUEST_CHECK_SETTINGS = 300;

    private String mLatitudeStr = "0.0", mLongitudeStr = "0.0";
    private int mDeletePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_offline_pining_mode_screen);

        initView();
    }

    private void initView() {

        ButterKnife.bind(this);

        setupUI(mOfflinePinModeParLay);

        mDataBase = new DatabaseUtil(this);

        setAdapter();

        addingLayPinVisible();

        initGoogleAPIClient();


        Gson gson = new Gson();
        String json = PreferenceUtil.getStringValue(this, AppConstants.USER_DETAILS);
        UserDetailsEntity mUserDetailsEntityRes = gson.fromJson(json, UserDetailsEntity.class);

        if (mUserDetailsEntityRes != null) {
            AppConstants.ACCESS_TOKEN = mUserDetailsEntityRes.getAuthorizationToken();
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


    @OnClick({R.id.offline_pin_btn_mode_lay})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.offline_pin_btn_mode_lay:
                DialogManager.getInstance().showPiningModePopup(OfflinePiningModeActivityScreen.this, mLatitudeStr + " , " + mLongitudeStr, false, this);
                break;
        }

    }

    private void addingLayPinVisible() {
        setAdapter();
        mOfflinePinModeBtnLay.setVisibility(!NetworkUtil.isNetworkAvailable(OfflinePiningModeActivityScreen.this) ? View.VISIBLE : View.GONE);
        mOnlinePinModeTxt.setVisibility(NetworkUtil.isNetworkAvailable(OfflinePiningModeActivityScreen.this) ? View.VISIBLE : View.GONE);
        if (mDataBase.getAllPins().size() > 0) {
            mOfflinePinListLay.setVisibility(View.VISIBLE);
            mOfflinePinEmptyListLay.setVisibility(View.GONE);
        } else {
            mOfflinePinListLay.setVisibility(View.GONE);
            mOfflinePinEmptyListLay.setVisibility(View.VISIBLE);
        }

        int s1 = mDataBase.getAllPins().size();
        if (NetworkUtil.isNetworkAvailable(this) && mDataBase.getAllPins().size() == 0) {
            nextScreen(HomeActivityFeedScreen.class, false);
        }
    }

    private void addPin(String edtTxt, String type) {
        OfflinePinEntity offlinePinEntity = new OfflinePinEntity();
        switch (Integer.parseInt(type)) {
            case 1:
                offlinePinEntity.setLocationName(edtTxt);
                offlinePinEntity.setLatitude(mLatitudeStr);
                offlinePinEntity.setLongitude(mLongitudeStr);
                offlinePinEntity.setPinType(type);

                if (!mDataBase.checkExist(offlinePinEntity)) {
                    mDataBase.insertPinLocation(offlinePinEntity);
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.location_pinned));


                } else {
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.already_exist));

                }

                break;
            case 2:
                offlinePinEntity.setLocationName(edtTxt);
                offlinePinEntity.setLatitude(mLatitudeStr);
                offlinePinEntity.setLongitude(mLongitudeStr);
                offlinePinEntity.setPinType(type);

                if (!mDataBase.checkExist(offlinePinEntity)) {
                    mDataBase.insertPinLocation(offlinePinEntity);
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.location_pinned));

                } else {
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.already_exist));

                }
                break;
            case 3:
                offlinePinEntity.setLocationName(edtTxt);
                offlinePinEntity.setLatitude(mLatitudeStr);
                offlinePinEntity.setLongitude(mLongitudeStr);
                offlinePinEntity.setPinType(type);

                if (!mDataBase.checkExist(offlinePinEntity)) {
                    mDataBase.insertPinLocation(offlinePinEntity);
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.location_pinned));

                } else {
                    DialogManager.getInstance().showToast(OfflinePiningModeActivityScreen.this, getString(R.string.already_exist));

                }
                break;
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
                                status.startResolutionForResult(OfflinePiningModeActivityScreen.this, REQUEST_CHECK_SETTINGS);
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

    private void setCurrentLocation() {
        FusedLocationProviderClient mLastLocation = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            permissionsAccessLocation();
            return;
        }
        mLastLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override

            public void onSuccess(final Location location) {
                if (location != null) {
                    mLatitudeStr = String.valueOf(location.getLatitude());
                    mLongitudeStr = String.valueOf(location.getLongitude());
                    mOfflinePinLatLngTxt.setText(" " + mLatitudeStr + "  " + mLongitudeStr + " ");
                }

            }
        });

    }


    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof CommonResultEntity) {
            CommonResultEntity mResponse = (CommonResultEntity) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {
                mAdapter.removeItem(mDeletePosition);

            }
        }
    }

    private void setAdapter() {
        mPinList = mDataBase.getAllPins();
        Collections.reverse(mPinList);

        mAdapter = new OfflineModeAdapter(this, this, mPinList, this);
        mOfflinePinRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOfflinePinRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mOfflinePinRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mOfflinePinRecyclerView);


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof OfflineModeAdapter.Holder) {
            // get the removed item name to display it in snack bar
            String name = mPinList.get(viewHolder.getAdapterPosition()).getLocationName();

            // backup of removed item for undo purpose
            final OfflinePinEntity deletedItem = mPinList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
//            Snackbar snackbar = Snackbar
//                    .make(mOfflinePinModeParLay, name + " removed from pin list!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    mAdapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
        }
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

    @Override
    public void withParams(String edtTxt, String type) {
        addPin(edtTxt, type);
        addingLayPinVisible();

    }

    @Override
    public void onPositiveClick() {
        addingLayPinVisible();

    }

    @Override
    public void onItemClicked(OfflinePinEntity offlinePinEntity, int position) {
        if (NetworkUtil.isNetworkAvailable(this)) {
            AddPinInputEntity addPinInputEntity = new AddPinInputEntity();

            addPinInputEntity.setName(offlinePinEntity.getLocationName());
            addPinInputEntity.setLat(Double.parseDouble(offlinePinEntity.getLatitude()));
            addPinInputEntity.setLon(Double.parseDouble(offlinePinEntity.getLongitude()));


            switch (Integer.parseInt(offlinePinEntity.getPinType())) {
                case 1:
                    addPinInputEntity.setPinType(getString(R.string.sub_type_been_there));
                    break;
                case 2:
                    addPinInputEntity.setPinType(getString(R.string.sub_type_to_be_explored));
                    break;
                case 3:
                    addPinInputEntity.setPinType(getString(R.string.sub_type_hidden_gem));
                    break;
                default:
                    addPinInputEntity.setPinType(getString(R.string.sub_type_been_there));
                    break;
            }
            mDeletePosition = position;
            APIRequestHandler.getInstance().createCustomPin(addPinInputEntity, this);


        } else {
            DialogManager.getInstance().showToast(this, getString(R.string.no_internet));
        }
    }
}
