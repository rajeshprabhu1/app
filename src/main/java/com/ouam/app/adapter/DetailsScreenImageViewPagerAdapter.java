package com.ouam.app.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;

public class DetailsScreenImageViewPagerAdapter extends PagerAdapter {
    Context context;
    public String[] photos;

    public DetailsScreenImageViewPagerAdapter(Context context, String[] photo) {
        this.context = context;
        photos = photo;
    }

    @Override
    public int getCount() {
        return photos.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        try {
            Glide.with(context)
                    .load(photos[position])
                    .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                    .into(imageView);
        } catch (Exception ex) {
            imageView.setImageResource(R.drawable.blue_bg);
        }
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
