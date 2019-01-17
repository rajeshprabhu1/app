package com.ouam.app.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.ouam.app.R;
import com.ouam.app.entity.ClusterMarkerItem;

public class ProfileClusterMarkerRender extends DefaultClusterRenderer<ClusterMarkerItem> {

    private Context mContext;

    public ProfileClusterMarkerRender(Context context, GoogleMap map, ClusterManager<ClusterMarkerItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
    }


    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarkerItem item, final MarkerOptions markerOptions) {
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.size30);
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.size25);
        BitmapDrawable bitmapDrawable;
        final Bitmap bitmap;
        switch (item.getPinType()) {
            case "1":
                bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.discovered_map_pin);
                bitmap = bitmapDrawable.getBitmap();
                break;
            case "2":
                bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.undiscovered_map_pin);
                bitmap = bitmapDrawable.getBitmap();
                break;
            case "3":
                bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.hidden_gem_map_pin);
                bitmap = bitmapDrawable.getBitmap();
                break;
            default:
                bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.user_demo_icon);
                bitmap = bitmapDrawable.getBitmap();

        }

        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

    }


}
