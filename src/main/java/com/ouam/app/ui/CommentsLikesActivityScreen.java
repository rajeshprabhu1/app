package com.ouam.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.fragments.CommentsScreenFragment;
import com.ouam.app.fragments.LikeScreenFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DateUtil;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentsLikesActivityScreen extends BaseActivity {

    @BindView(R.id.comments_likes_par_lay)
    LinearLayout mCommentsLikesParLay;

    @BindView(R.id.comments_likes_screen_tab_lay)
    TabLayout mTabLayout;

    @BindView(R.id.comments_likes_screen_pager)
    ViewPager mViewPager;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.header_edt)
    EditText mHeaderEdt;

    @BindView(R.id.header_left_first_img_lay)
    RelativeLayout mHeaderLeftFirstImgLay;

    @BindView(R.id.header_left_second_img_lay)
    RelativeLayout mHeaderLeftSecondImgLay;

    @BindView(R.id.header_right_first_img_lay)
    public RelativeLayout mHeaderRightFirstImgLay;

    @BindView(R.id.header_right_second_img_lay)
    public RelativeLayout mHeaderRightSecondImgLay;

    @BindView(R.id.header_left_first_img)
    ImageView mHeaderLeftFirstImg;

    @BindView(R.id.header_left_second_img)
    RoundedImageView mHeaderLeftSecondImg;

    @BindView(R.id.header_right_first_txt)
    public TextView mHeaderRightFirstTxt;

    @BindView(R.id.header_right_second_txt)
    TextView mHeaderRightSecondImg;

    @BindView(R.id.adap_dtl_feed_hrs_txt)
    TextView mHoursTxt;

    @BindView(R.id.header_user_name_lay)
    RelativeLayout mHeaderUserNameLay;

    @BindView(R.id.header_search_lay)
    LinearLayout mHeaderSearchLay;

    @BindView(R.id.user_img)
    RoundedImageView mUserImg;

    @BindView(R.id.user_name_txt)
    TextView mUserDetailsTxt;

    @BindView(R.id.horizontal_linear_lay)
    LinearLayout mHorizontalLinearLay;

    @BindView(R.id.post_comment_txt)
    TextView mCommentedTxt;

    RelativeLayout mCommentsLay, mLikesLay, mCommentsMarkerLay, mLikesMarkerLay;
    LinearLayout mCommentsBgLay, mLikesBgLay;
    TextView mCommentsTxt, mLikesTxt;
    ImageView mCommentsImg, mLikesImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_comments_likes_screen);

        initView();
    }

    private void initView() {

        ButterKnife.bind(this);

        setupUI(mCommentsLikesParLay);
        setHeaderView();
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);


        initTabHeaderview();

        setUserDetails();

        trackScreenName(getString(R.string.comment_like_screen));
    }

    private void setUserDetails() {

        if (AppConstants.POST_ENTITY != null) {
            mUserDetailsTxt.setText(AppConstants.POST_ENTITY.getUser().getUsername() + " posted on");
            if (AppConstants.POST_ENTITY.getUser().getPhoto().isEmpty()) {
                mUserImg.setVisibility(View.GONE);
            } else {
                try {
                    Glide.with(this)
                            .load(AppConstants.POST_ENTITY.getUser().getPhoto())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                            .into(mUserImg);

                } catch (Exception ex) {
                    mUserImg.setImageResource(R.drawable.user_demo_icon);
                }
            }

            mCommentedTxt.setText(AppConstants.POST_ENTITY.getComment());
            if (AppConstants.POST_ENTITY.getImages().length > 0) {
                mHorizontalLinearLay.removeAllViews();
                addImageToView(AppConstants.POST_ENTITY.getImages(), mHorizontalLinearLay);
            }
            mHoursTxt.setText(DateUtil.getDateTimeDiff(String.valueOf(AppConstants.POST_CREATED_TIME), true));


        }
    }

    private void addImageToView(String[] postImages, LinearLayout mHorizantalLinearLayout) {
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.size75),
                        getResources().getDimensionPixelOffset(R.dimen.size55));
        layoutParams.setMargins(5, 5, 5, 5);
        for (int i = 0; i < postImages.length; i++) {
            ImageView iv = new ImageView(this);

//            iv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
            Glide.with(this)
                    .load(postImages[i])
                    .into(iv);
            iv.setLayoutParams(layoutParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setTag(i);
            mHorizantalLinearLayout.addView(iv);


            iv.setOnClickListener(new View.OnClickListener() {
                @Override()
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    DialogManager.getInstance().showImagePopUp(view.getContext(), postImages[pos]);
//                    DialogManager.getInstance().showToast(getApplicationContext(), "Click");

                }
            });


        }
    }

    private void initTabHeaderview() {

        /*set custom tab*/
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.tab_comments_likes_view, null, false);

        mCommentsLay = headerView.findViewById(R.id.comments_lay);
        mLikesLay = headerView.findViewById(R.id.likes_lay);

        mCommentsBgLay = headerView.findViewById(R.id.comments_inner_lay);
        mLikesBgLay = headerView.findViewById(R.id.likes_inner_lay);

        mCommentsTxt = headerView.findViewById(R.id.comments_txt);
        mLikesTxt = headerView.findViewById(R.id.likes_txt);

        mCommentsImg = headerView.findViewById(R.id.comments_img);
        mLikesImg = headerView.findViewById(R.id.likes_img);

        mCommentsMarkerLay = headerView.findViewById(R.id.comments_marker_lay);
        mLikesMarkerLay = headerView.findViewById(R.id.likes_marker_lay);

        mCommentsImg.setImageResource(R.drawable.chat_white_icon);
        mLikesImg.setImageResource(R.drawable.like_icon);

        mTabLayout.getTabAt(0).setCustomView(mCommentsLay);
        mTabLayout.getTabAt(1).setCustomView(mLikesLay);

    }

    private void setHeaderView() {

        mHeaderUserNameLay.setVisibility(View.GONE);
        mHeaderSearchLay.setVisibility(View.GONE);
        mHeaderLeftFirstImgLay.setVisibility(View.VISIBLE);
        mHeaderLeftSecondImgLay.setVisibility(View.GONE);
        mHeaderRightFirstImgLay.setVisibility(View.GONE);
        mHeaderRightSecondImgLay.setVisibility(View.INVISIBLE);

        mHeaderTxt.setVisibility(View.VISIBLE);
        mHeaderTxt.setText(getString(R.string.comments_likes));
        mHeaderLeftFirstImg.setImageResource(R.drawable.back_white);


    }

    private void setupViewPager(ViewPager viewPager) {
        CommentsLikesActivityScreen.ViewPagerAdapter adapter = new CommentsLikesActivityScreen.ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new CommentsScreenFragment());
        adapter.addFragment(new LikeScreenFragment());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mCommentsBgLay.setBackgroundResource(position == 0 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
                mLikesBgLay.setBackgroundResource(position == 1 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);

                mCommentsTxt.setTextColor(position == 0 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));
                mLikesTxt.setTextColor(position == 1 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));

                mCommentsMarkerLay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                mLikesMarkerLay.setVisibility(position == 1 ? View.VISIBLE : View.GONE);

                mCommentsImg.setImageResource(position == 0 ? R.drawable.chat_white_icon : R.drawable.chat_icon);
                mLikesImg.setImageResource(position == 0 ? R.drawable.like_icon : R.drawable.heart_red_color_icon);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    @OnClick({R.id.header_left_first_img_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_first_img_lay:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        backScreen(true);
    }

}
