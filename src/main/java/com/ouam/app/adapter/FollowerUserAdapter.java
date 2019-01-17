package com.ouam.app.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ouam.app.R;

import butterknife.ButterKnife;

public class FollowerUserAdapter extends RecyclerView.Adapter<FollowerUserAdapter.Holder> {

    private Context mContext;


    public FollowerUserAdapter(Context activity) {
        mContext = activity;
        //   mWithList = withEntityArrayList;
    }

    @NonNull
    @Override
    public FollowerUserAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_follower_screen, parent, false);
        return new FollowerUserAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerUserAdapter.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }


    public class Holder extends RecyclerView.ViewHolder {


        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
