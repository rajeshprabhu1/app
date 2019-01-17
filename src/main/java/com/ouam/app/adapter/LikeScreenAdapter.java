package com.ouam.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.ui.CommentsLikesActivityScreen;
import com.ouam.app.ui.UserProfileActivityScreen;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikeScreenAdapter extends RecyclerView.Adapter<LikeScreenAdapter.Holder> {

    private Context mContext;
    private ArrayList<WithEntity> mWithEntity;

    public LikeScreenAdapter(Context context, ArrayList<WithEntity> withEntities) {
        mContext = context;
        mWithEntity = withEntities;
    }

    @NonNull
    @Override
    public LikeScreenAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adap_like_screen_row, parent, false);
        return new LikeScreenAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LikeScreenAdapter.Holder holder, int position) {

        WithEntity withEntity = mWithEntity.get(position);

        holder.mUserName.setText(withEntity.getUsername());
        holder.mTimeTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(withEntity.getCreated()), true));
        if (withEntity.getPhoto().isEmpty()) {
            holder.mUserImage.setVisibility(View.GONE);
        } else {
            try {
                holder.mUserImage.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(withEntity.getPhoto())
                        .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                        .into(holder.mUserImage);

            } catch (Exception ex) {
                holder.mUserImage.setImageResource(R.drawable.user_demo_icon);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!withEntity.getId().isEmpty()) {
                    AppConstants.PROFILE_USER_ID = withEntity.getId();
                    ((CommentsLikesActivityScreen) mContext).nextScreen(UserProfileActivityScreen.class, true);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mWithEntity.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        @BindView(R.id.user_img)
        RoundedImageView mUserImage;

        @BindView(R.id.user_name)
        TextView mUserName;

        @BindView(R.id.comments_time_txt)
        TextView mTimeTxt;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
