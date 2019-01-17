package com.ouam.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.adapter.PinsAdapter;
import com.ouam.app.commonInterfaces.UpdatePagerFragmentInterface;
import com.ouam.app.entity.WhoDetailEntity;
import com.ouam.app.main.BaseFragment;
import com.ouam.app.utils.AppConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ToBeenExploredPinFragment extends BaseFragment implements UpdatePagerFragmentInterface {

    @BindView(R.id.recycler_view)
    RecyclerView mToBeExploredRecyclerView;

    @BindView(R.id.parent_lay)
    RelativeLayout mParentLay;

    @BindView(R.id.no_values_txt)
    TextView mNoValueTxt;

    PinsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_common_recycler_view, container, false);

        ButterKnife.bind(this, rootView);

        initView();

        return rootView;
    }


    private void initView() {
        AppConstants.TO_BE_EXPLORED_UPDATE_FRAGMENT_INTERFACE = this;

        setupUI(mParentLay);
        setAdapter();
    }

    private void setAdapter() {

        mToBeExploredRecyclerView.setVisibility(AppConstants.TO_BE_EXPLORED_PIN_LIST.size() > 0 ? View.VISIBLE : View.GONE);
        mNoValueTxt.setVisibility(AppConstants.TO_BE_EXPLORED_PIN_LIST.size() > 0  ? View.GONE : View.VISIBLE);

        mAdapter = new PinsAdapter(getActivity(), AppConstants.TO_BE_EXPLORED_PIN_LIST);
        mToBeExploredRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mToBeExploredRecyclerView.setAdapter(mAdapter);

    }
    @Override
    public void update(ArrayList<WhoDetailEntity> updateList) {
        setAdapter();
    }
}
