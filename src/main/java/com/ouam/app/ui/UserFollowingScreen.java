package com.ouam.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.NewMessageAdapter;
import com.ouam.app.adapter.UserFollowerAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.UserFollowingResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserFollowingScreen extends BaseActivity {

    @BindView(R.id.new_message_par_lay)
    LinearLayout mNewMessageParLay;

    @BindView(R.id.new_message_header_bg_lay)
    RelativeLayout mNewMessageHeaderBgLay;

    @BindView(R.id.header_right_img_lay)
    RelativeLayout mHeaderRightImgLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.search_edt)
    EditText mSearchEdt;

    @BindView(R.id.new_message_recycler_view)
    RecyclerView mNewMessageRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValuesTxt;

    @BindView(R.id.header_left_img)
    ImageView mHeaderLeftImg;

    private ArrayList<WhoDetailEntity> mNewMsgArrList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_new_search_screen);
//        AppConstants.REFRESH_FOLLOW_LIST = NewMessageScreen.this;
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        setupUI(mNewMessageParLay);
        setHeaderView();
        userFollowingApiCall();
        trackScreenName(getString(R.string.new_message_screen));

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterNewMsgArrList(editable.toString());
            }
        });
    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mNewMessageHeaderBgLay.post(new Runnable() {
                public void run() {
                    mNewMessageHeaderBgLay.setPadding(0, getStatusBarHeight(UserFollowingScreen.this), 0, 0);

                }
            });
        }
        mHeaderTxt.setText(getResources().getText(R.string.following));
        mHeaderTxt.setTextColor(getResources().getColor(R.color.black));
        mHeaderLeftImg.setImageResource(R.drawable.back_blue);

    }

    private void userFollowingApiCall() {
        if (NetworkUtil.isNetworkAvailable(this)) {

            String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID + AppConstants.USER_FOLLOWING_URL;

            APIRequestHandler.getInstance().userFollowingApi(url, UserFollowingScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    userFollowingApiCall();
                }
            });
        }
    }

    /*Onclick*/
    @OnClick({R.id.header_left_img_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                onBackPressed();
                break;
        }
    }


    /*OnRequest Success*/
    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof UserFollowingResponse) {
            UserFollowingResponse userDetailsResponse = (UserFollowingResponse) resObj;
            mNewMsgArrList = new ArrayList<>();
            mNewMsgArrList = userDetailsResponse.getWith();
            setAdapter(mNewMsgArrList);

        }
    }
    private void filterNewMsgArrList(String filterTxtStr) {
        if (filterTxtStr.isEmpty()) {
            setAdapter(mNewMsgArrList);
        } else {
            ArrayList<WhoDetailEntity> newMsgArrList = new ArrayList<>();
            for (int posInt = 0; mNewMsgArrList.size() > posInt; posInt++) {
                if (mNewMsgArrList.get(posInt).getUsername().contains(filterTxtStr)) {
                    newMsgArrList.add(mNewMsgArrList.get(posInt));
                }
            }
            setAdapter(newMsgArrList);
        }

    }

    private void setAdapter(ArrayList<WhoDetailEntity> newMsgArrList) {
        runOnUiThread(() -> {
            mNewMessageRecyclerView.setVisibility(newMsgArrList.size() > 0 ? View.VISIBLE : View.GONE);
            mNoValuesTxt.setVisibility(newMsgArrList.size() > 0 ? View.GONE : View.VISIBLE);

            mNewMessageRecyclerView.setLayoutManager(new LinearLayoutManager(UserFollowingScreen.this));
            mNewMessageRecyclerView.setAdapter(new NewMessageAdapter(UserFollowingScreen.this, newMsgArrList));

        });
    }


    @Override
    public void onBackPressed() {
        backScreen(true);
    }
}
