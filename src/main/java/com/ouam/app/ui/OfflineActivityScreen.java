package com.ouam.app.ui;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.utils.DialogManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfflineActivityScreen extends BaseActivity {

    @BindView(R.id.offline_activity_par_lay)
    RelativeLayout mOfflineParLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_offline_screen);

        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        setupUI(mOfflineParLay);

        DialogManager.getInstance().showAlertPopup(OfflineActivityScreen.this, getString(R.string.offline_content), new InterfaceBtnCallBack() {
            @Override
            public void onPositiveClick() {
                nextScreen(OfflinePiningModeActivityScreen.class,false);
            }
        });
    }
}
