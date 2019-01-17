package com.ouam.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.NotificationAdapter;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.NotificationResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationActivityScreen extends BaseActivity {

    @BindView(R.id.notification_par_lay)
    LinearLayout mNotificationParLay;

    @BindView(R.id.notification_header_bg_lay)
    RelativeLayout mNotificationHeaderBgLay;

    @BindView(R.id.header_right_img_lay)
    RelativeLayout mHeaderRightImgLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.notification_recycler_view)
    RecyclerView mNofiticationRecyclerView;

    @BindView(R.id.no_values_txt)
    TextView mNoValuesTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_notification_screen);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        setupUI(mNotificationParLay);
        setHeaderView();
        onCallNotificationApi();
        trackScreenName(getString(R.string.notification_screen));

    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mNotificationHeaderBgLay.post(new Runnable() {
                public void run() {
                    mNotificationHeaderBgLay.setPadding(0, getStatusBarHeight(NotificationActivityScreen.this), 0, 0);

                }
            });
        }
        mHeaderRightImgLay.setVisibility(View.VISIBLE);
        mHeaderTxt.setText(getResources().getText(R.string.notifications));

    }

    private void onCallNotificationApi() {
        if (NetworkUtil.isNetworkAvailable(NotificationActivityScreen.this)) {
            String currentDateTimeString = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());

            Integer searchDate = Math.round(Integer.parseInt(currentDateTimeString) / 1000) - (606024 * 365);

            String queryUrl = AppConstants.BASE_URL + AppConstants.NOTIFICATION_URL + String.valueOf(searchDate);

            APIRequestHandler.getInstance().NofiticationApiCall(queryUrl, NotificationActivityScreen.this);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(NotificationActivityScreen.this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    onCallNotificationApi();
                }
            });
        }
    }

    /*Onclick*/
    @OnClick({R.id.header_left_img_lay, R.id.header_right_img_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                onBackPressed();
                break;
            case R.id.header_right_img_lay:
                nextScreen(SettingsActivityScreen.class, true);
                break;
        }
    }


    /*OnRequest Success*/
    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof NotificationResponse) {
            NotificationResponse response = (NotificationResponse) resObj;
            mNofiticationRecyclerView.setVisibility(response.getWith().size() > 0 ? View.VISIBLE : View.GONE);
            mNoValuesTxt.setVisibility(response.getWith().size() > 0 ? View.GONE : View.VISIBLE);
            setAdapter(response.getWith());
        }
    }

    private void setAdapter(ArrayList<WithEntity> with) {
        NotificationAdapter mAdapter = new NotificationAdapter(this, with);
        mNofiticationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNofiticationRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        backScreen(true);
    }
}
