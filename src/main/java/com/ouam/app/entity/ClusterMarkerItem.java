package com.ouam.app.entity;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarkerItem implements ClusterItem {

    private final LatLng mPosition;
    private String imagUrl;
    private double mLat;
    private double mLang;
    private String mPinType;
    private String mPlaceName;


    private int mListPos = 0;


    public ClusterMarkerItem(double lat, double lng, String pinType, String url, String empty, int listPos) {
        mPosition = new LatLng(lat, lng);
        mLat = lat;
        mLang = lng;
        imagUrl = url;
        mPinType = pinType;
        mListPos = listPos;
    }


    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLang() {
        return mLang;
    }

    public void setmLang(double mLang) {
        this.mLang = mLang;
    }

    public ClusterMarkerItem(double lat, double lng, String pinType, String placeName, int listPos) {
        mPosition = new LatLng(lat, lng);
        mPinType = pinType;

        mPlaceName = placeName;
        mListPos = listPos;


    }


    public String getImagUrl() {
        return imagUrl;
    }

    public String getmPlaceName() {
        return mPlaceName;
    }

    public String getPinType() {
        return mPinType == null ? "" : mPinType;
    }

    public void setPinType(String pinType) {
        this.mPinType = pinType;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }


    public LatLng getmPosition() {
        return mPosition;
    }

    public void setImagUrl(String imagUrl) {
        this.imagUrl = imagUrl;
    }

    public String getmPinType() {
        return mPinType;
    }

    public void setmPinType(String mPinType) {
        this.mPinType = mPinType;
    }

    public void setmPlaceName(String mPlaceName) {
        this.mPlaceName = mPlaceName;
    }

    public int getmListPos() {
        return mListPos;
    }

    public void setmListPos(int mListPos) {
        this.mListPos = mListPos;
    }

}
