package com.ouam.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.DetailsUpdateFragmentInterface;
import com.ouam.app.entity.WithEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceDetailFragment extends BaseFragment implements DetailsUpdateFragmentInterface {

    @BindView(R.id.place_details_par_lay)
    RelativeLayout mPlaceDetailsParLay;

    @BindView(R.id.place_details_name_txt)
    TextView mPlaceDetailsNameTxt;

    @BindView(R.id.place_details_address_txt)
    TextView mPlaceDetailsAddressTxt;

    @BindView(R.id.place_details_phone_num_txt)
    TextView mPlaceDetailsPhoneNumTxt;

    @BindView(R.id.place_details_web_site_txt)
    TextView mPlaceDetailsWebSiteTxt;

    @BindView(R.id.place_catagery_txt)
    TextView mPlaceCatageryTxt;

    @BindView(R.id.no_values_txt)
    TextView mNoValuesTxt;


    String mAddressStr, mPhoneNumStr, mWebSiteStr;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = layoutInflater.inflate(R.layout.frag_place_detail, container, false);
        ButterKnife.bind(this, rootView);

        initView();
        return rootView;
    }

    private void initView() {
        setupUI(mPlaceDetailsParLay);


        AppConstants.WITH_DETAILS_INTERFACE = this;
    }

    private void setDetailsData(WithEntity withEntity) {
        if (withEntity != null){
            mNoValuesTxt.setVisibility(View.GONE);
            mPlaceDetailsNameTxt.setText(withEntity.getName().isEmpty() ? "" : withEntity.getName());
            mAddressStr = (withEntity.getAddress().isEmpty() ? "" : withEntity.getAddress() + "\n") + ("")+
                    (withEntity.getCity().getName().isEmpty() ? "" : withEntity.getCity().getName()) +
                    (withEntity.getCity().getLocality().isEmpty() ? "" : ", " + withEntity.getCity().getLocality()) +
                    (withEntity.getCity().getCountry().isEmpty() ? "" : ", " + withEntity.getCity().getCountry()  +
                            (withEntity.getPostalCode().isEmpty() ? "" : " " + withEntity.getPostalCode() ));
            mPlaceDetailsAddressTxt.setText(mAddressStr);
            mWebSiteStr = withEntity.getWebsite();
            mPhoneNumStr = withEntity.getPhoneNumber();
            mPlaceDetailsPhoneNumTxt.setText(String.valueOf(mPhoneNumStr).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
            mPlaceCatageryTxt.setText(withEntity.getCategory().isEmpty() ? "" : withEntity.getCategory());
           // mPlaceDetailsPhoneNumTxt.setText(withEntity.getPhoneNumber().isEmpty() ? "" : "" + withEntity.getPhoneNumber());
            mPlaceDetailsWebSiteTxt.setText(withEntity.getWebsite().isEmpty() ? "" : withEntity.getWebsite());
        }else {
            mNoValuesTxt.setVisibility(View.VISIBLE);
        }
mPlaceDetailsNameTxt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
                            StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(withEntity.getName());
                    stringBuilder.append(withEntity.getAddress());
                    stringBuilder.append(withEntity.getCity().getLocality());
                    stringBuilder.append(withEntity.getCity().getCountry());

                    String address = stringBuilder.toString();
                    Log.e("fhf",address);
                String map = "http://maps.google.co.in/maps?q=" + address;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                getContext().startActivity(intent);
    }
});
        mPlaceDetailsAddressTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(withEntity.getName());
                stringBuilder.append(withEntity.getAddress());
                stringBuilder.append(withEntity.getCity().getLocality());
                stringBuilder.append(withEntity.getCity().getCountry());

                String address = stringBuilder.toString();
                Log.e("fhf",address);
                String map = "http://maps.google.co.in/maps?q=" + address;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                getContext().startActivity(intent);
            }
        });

    }

    @OnClick({R.id.place_details_phone_num_txt, R.id.place_details_web_site_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_details_phone_num_txt:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mPhoneNumStr, null)));
                break;

            case R.id.place_details_web_site_txt:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mWebSiteStr)));
                break;

        }
    }

    @Override
    public void update(WithEntity withEntity) {
        setDetailsData(withEntity);
    }
}