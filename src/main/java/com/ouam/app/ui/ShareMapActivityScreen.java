package com.ouam.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ouam.app.R;
import com.ouam.app.entity.TinyBodyEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.ShareMapResponse;
import com.ouam.app.model.TinyUrlResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.NetworkUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import static com.ouam.app.utils.ImageUtils.getOutputMediaFile;

public class ShareMapActivityScreen extends BaseActivity {

    @BindView(R.id.share_map_par_lay)
    LinearLayout mShareMapParLay;

    @BindView(R.id.share_map_header_bg_lay)
    RelativeLayout mshareMapHeaderBgLay;

    @BindView(R.id.header_txt)
    TextView mHeaderTxt;

    @BindView(R.id.share_map_img)
    ImageView mShareMapImg;

    @BindView(R.id.been_there_txt)
    TextView mBeenThereTxt;

    @BindView(R.id.to_be_explore_txt)
    TextView mToBeExploredTxt;

    @BindView(R.id.hidden_gem_txt)
    TextView mHiddenGemTxt;

    @BindView(R.id.share_map_btn)
    Button mShareMapBtn;

    private String mMapUrlStr = "" , mTinyUrlStr = "";
    TinyBodyEntity mTinyBodyEntity = new TinyBodyEntity();

    private  String urlmap = "http://tinyurl.com/api-create.php?url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_share_map_screen);

        initView();
    }

    private void initView() {

        ButterKnife.bind(this);

        setupUI(mShareMapParLay);

        setHeaderView();

        callGetShareMap();
    }

    private void setHeaderView() {

            /*Set header adjustment - status bar we applied transparent color so header tack full view*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mshareMapHeaderBgLay.post(() -> mshareMapHeaderBgLay.setPadding(0, getStatusBarHeight(ShareMapActivityScreen.this), 0, 0));
            }


        mHeaderTxt.setText(getString(R.string.share_your_map));

        mBeenThereTxt.setText(AppConstants.BEEN_THERE_MAP_STR);
        mToBeExploredTxt.setText(AppConstants.TO_BE_EXPLORE_MAP_STR);
        mHiddenGemTxt.setText(AppConstants.HIDDEN_GEM_MAP_STR);


    }

    private void callTinyUrlApi(){
        if (NetworkUtil.isNetworkAvailable(ShareMapActivityScreen.this)){
            String url = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=AIzaSyA8OCEjkGgzkuNxlTZHtCRXS8-UobuY900";
            TinyBodyEntity tinyBodyEntity = new TinyBodyEntity();
            tinyBodyEntity.setLongUrl("https://ouam.page.link?link="+mMapUrlStr);
//            tinyBodyEntity.setLongUrl("https://ouam.page.link?link=https://ouam-cdn.s3.amazonaws.com/users/us-east-2:e1a4231c-62d7-4b84-9d46-5ea412884c43/profile/mapImage/map.png");
            APIRequestHandler.getInstance().TinyUrlApiCall(tinyBodyEntity,url,ShareMapActivityScreen.this);
        }
        else {
            callTinyUrlApi();
        }
    }
    private void callGetShareMap() {
        if (NetworkUtil.isNetworkAvailable(ShareMapActivityScreen.this)) {
            if (!AppConstants.PROFILE_USER_ID.isEmpty()) {
                String url = AppConstants.BASE_URL + AppConstants.USER_URL + AppConstants.PROFILE_USER_ID + AppConstants.MAP_SHARE_URL;
                Log.e("dds", url);
                APIRequestHandler.getInstance().shareMapApiCall(url, this);
            }

        } else {
            callGetShareMap();
        }

    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof ShareMapResponse) {
            ShareMapResponse response = (ShareMapResponse) resObj;

            if (response.getStatus().equalsIgnoreCase(getString(R.string.succeeded))) {
                mMapUrlStr = response.getWith();
//                mTinyBodyEntity.setLongUrl(mMapUrlStr);
                mShareMapBtn.setText(getResources().getString(R.string.share_your_map));
                callTinyUrlApi();

                try {
                    Glide.with(this)
                            .load(response.getWith())
                            .apply(new RequestOptions().placeholder(R.color.blue).error(R.color.blue))
                            .into(mShareMapImg);

                } catch (Exception ex) {
                    Log.e(AppConstants.TAG, ex.getMessage());
                }


            }
        }
        if (resObj instanceof TinyUrlResponse){
            TinyUrlResponse response = (TinyUrlResponse)resObj;
            if (!response.getShortLink().isEmpty() && response.getShortLink() != null){
                mTinyUrlStr = response.getShortLink();

            }
        }
    }

    @OnClick({R.id.header_left_img_lay, R.id.share_map_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_left_img_lay:
                onBackPressed();
                break;
            case R.id.share_map_btn:
//                if (!mTinyUrlStr.trim().isEmpty()) {
                shareImage(mShareMapImg);
//                }
//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("text/plain");
//                    i.putExtra(Intent.EXTRA_SUBJECT, "Share your map");
//                    i.putExtra(Intent.EXTRA_TEXT, mMapUrlStr);
//                    startActivity(Intent.createChooser(i, "Share URL"));
                // } else {
                //      DialogManager.getInstance().showToast(ShareMapActivityScreen.this, "Loading your map screen please wait");
                //   }
                //   break;
        }
    }


    private void shareImage(View mShareMapImg ) {
        // String shareTxt = "Your Share Msg";
        Uri bmpUri = (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", Objects.requireNonNull(saveToInternalStorage(this,mShareMapImg))) : Uri.fromFile(saveToInternalStorage(this,mShareMapImg));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TEXT,mTinyUrlStr);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image!"));
    }


    /*Store image from image with strip line*/
    public File saveToInternalStorage(Context context, View mShareMapImg) {

        if (context != null && mShareMapImg != null  ) {
            mShareMapImg.setDrawingCacheEnabled(true);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            mShareMapImg.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String filename =   context.getString(R.string.search) + "Download" + context.getString(R.string.home_city) + new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss:SSS",
                    Locale.getDefault()).format(new Date());
            // External sdcard location
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(context.getString(R.string.app_name), context.getString(R.string.online_txt) + " "
                            + context.getString(R.string.app_name) + " " + context.getString(R.string.feed));
                }
            }

            File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + filename + ".jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(mediaFile);
                outputStream.write(bytes.toByteArray());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mediaFile;
        }else {
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        backScreen(true);
    }
}
