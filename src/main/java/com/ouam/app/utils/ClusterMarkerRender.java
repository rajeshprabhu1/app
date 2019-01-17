package com.ouam.app.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.ClusterInterfaceBtnCallBack;
import com.ouam.app.entity.ClusterMarkerItem;

public class ClusterMarkerRender extends DefaultClusterRenderer<ClusterMarkerItem> implements ClusterManager.OnClusterItemClickListener<ClusterMarkerItem> {

    private IconGenerator mIconGenerator;
    private RoundedImageView mMarkerUserImg;
    private Context mContext;
    private ClusterInterfaceBtnCallBack mClusterInterfaceBtnCallBack;

    public ClusterMarkerRender(final Context context, GoogleMap map, ClusterManager<ClusterMarkerItem> clusterManager, ClusterInterfaceBtnCallBack clusterInterfaceBtnCallBack) {
        super(context, map, clusterManager);
        // initialize cluster icon generator
        mContext = context;
        mClusterInterfaceBtnCallBack = clusterInterfaceBtnCallBack;
        mIconGenerator = new IconGenerator(context.getApplicationContext());
        View clusterView = LayoutInflater.from(context).inflate(R.layout.map_marker_item_view, null);
        mMarkerUserImg = clusterView.findViewById(R.id.marker_user_img);
        mIconGenerator.setContentView(clusterView);
        mIconGenerator.setBackground(null);
        clusterManager.setOnClusterItemClickListener(this);

    }


    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerItem trace, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        mMarkerUserImg.setImageResource(R.color.clusterblue); //temp image.
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }


    @Override
    protected void onClusterItemRendered(final ClusterMarkerItem clusterMarkerItem, Marker marker) {

        IconGenerator iconGenerator = new IconGenerator(mContext);
        View clusterView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_item_view, null);
        ImageView markerUserImg = clusterView.findViewById(R.id.marker_user_img);
        ImageView markerUserBgImg = clusterView.findViewById(R.id.marker_user_bg_img);
        markerUserBgImg.setImageResource(ImageUtils.userBgTypeImage(mContext, clusterMarkerItem.getPinType()));

        iconGenerator.setContentView(clusterView);
        iconGenerator.setBackground(null);

        Glide.with(mContext.getApplicationContext())
                .load(clusterMarkerItem.getImagUrl())
                .apply(new RequestOptions().error(R.color.clusterblue))
                .thumbnail(0.1f)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        marker.setVisible(true);
                        markerUserImg.setImageDrawable(resource);
                        Bitmap icon = iconGenerator.makeIcon();
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                });
    }


    /*After cluster item click listener*/
    @Override
    public boolean onClusterItemClick(ClusterMarkerItem clusterMarkerItem) {

        IconGenerator iconGenerator = new IconGenerator(mContext);
        View clusterView = LayoutInflater.from(mContext).inflate(R.layout.map_marker_item_view, null);

        RelativeLayout markerClickOverLay= clusterView.findViewById(R.id.marker_click_over_lay);
        ImageView markerClickOverLayBgImg = clusterView.findViewById(R.id.marker_click_over_lay_bg_img);
        ImageView markerUserImg = clusterView.findViewById(R.id.marker_user_img);
        ImageView markerUserBgImg = clusterView.findViewById(R.id.marker_user_bg_img);


        markerUserBgImg.setImageResource(ImageUtils.userBgTypeImage(mContext, clusterMarkerItem.getPinType()));
        markerClickOverLayBgImg.setImageResource(ImageUtils.userBgTypeImage(mContext, clusterMarkerItem.getPinType()));
        markerClickOverLay.setVisibility(View.VISIBLE);

        iconGenerator.setContentView(clusterView);
        iconGenerator.setBackground(null);


        Marker marker = getMarker(clusterMarkerItem);

        Glide.with(mContext.getApplicationContext())
                .load(clusterMarkerItem.getImagUrl())
                .thumbnail(0.1f)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        markerUserImg.setImageDrawable(resource);
//                        iconGenerator.setBackground(mContext.getResources().getDrawable(ImageUtils.userBgTypeImage(mContext, clusterMarkerItem.getPinType())));
                        Bitmap icon = iconGenerator.makeIcon();
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));

                    }
                });

        mClusterInterfaceBtnCallBack.onClusterItemClick(clusterMarkerItem.getmListPos());
        return true;
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1;
    }


}
