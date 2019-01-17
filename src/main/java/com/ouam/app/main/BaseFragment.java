package com.ouam.app.main;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceTwoBtnCallBack;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;

import java.io.IOException;
import java.net.ConnectException;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BaseFragment extends Fragment implements InterfaceTwoBtnCallBack {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    protected void setupUI(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View mInnerView = ((ViewGroup) view).getChildAt(i);
                setupUI(mInnerView);
            }
        }
    }

    protected void hideSoftKeyboard() {
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(INPUT_METHOD_SERVICE);

                if (getActivity().getCurrentFocus() != null
                        && getActivity().getCurrentFocus().getWindowToken() != null) {
                    mInputMethodManager.hideSoftInputFromWindow(getActivity()
                            .getCurrentFocus().getWindowToken(), 0);

                }
            }
        } catch (Exception e) {
            Log.e(getActivity().getClass().getSimpleName(), e.getMessage());
        }

    }

    protected int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (getActivity() != null)
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels;
    }

    public void sysOut(String msg) {
        try {
            System.out.println(msg);
        } catch (Exception e) {
            Log.e(AppConstants.TAG, msg);
        }
    }

    /*For trigger manually OnResume*/
    public void onFragmentResume() {
    }

    /*set BackPressed option manually */
    public void onFragmentBackPressed() {

    }

    /*Direct to next activity and finish the current activity*/
    public void nextScreenWithFinish(Class<?> clazz, boolean animType) {
        nextScreen(clazz, true, animType);
    }

    /*Direct to next activity and without finish the current activity*/
    public void nextScreenWithOutFinish(Class<?> clazz, boolean animType) {
        nextScreen(clazz, false, animType);
    }

    /*Direct to previous activity and finish the current activity*/
    public void previousScreenWithFinish(Class<?> clazz, boolean animType) {
        previousScreen(clazz, true, animType);
    }

    /*Direct to previous activity and without finish the current activity*/
    public void previousScreenWithOutFinish(Class<?> clazz, boolean animType) {
        previousScreen(clazz, false, animType);
    }

    /*Direct to next activity*/
    public void nextScreen(Class<?> clazz, boolean finish, boolean animType) {
        Intent nextScreenIntent = new Intent(getActivity(), clazz);
        nextScreenIntent = clearBackGroundActivity(clazz, nextScreenIntent);
        getActivity().startActivity(nextScreenIntent);
        if (animType) {
            getActivity().overridePendingTransition(R.anim.slide_up,
                    R.anim.slide_down);
        } else {
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }

        if (finish)
            getActivity().finish();

    }


    /*Redirect to previous activity*/
    private void previousScreen(Class<?> clazz, boolean finish, boolean animType) {
        Intent previousScreenIntent = new Intent(getActivity(), clazz);
        previousScreenIntent = clearBackGroundActivity(clazz, previousScreenIntent);
        getActivity().startActivity(previousScreenIntent);
        if (animType) {
            getActivity().overridePendingTransition(R.anim.slide_up,
                    R.anim.slide_down);
        } else {
            getActivity().overridePendingTransition(R.anim.slide_out_right,
                    R.anim.slide_in_left);
        }
        if (finish)
            getActivity().finish();
    }


    /*Clear the all background activity*/
    private Intent clearBackGroundActivity(Class<?> clazz, Intent screenIntent) {
        String classStr = clazz.getSimpleName();
        screenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        return screenIntent;

    }

    /*Finish current activity*/
    public void finishScreen() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_out_right,
                R.anim.slide_in_left);

    }

    /*Permission call back*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /*API call back success*/
    public void onRequestSuccess(Object resObj) {

    }

    /*API call back failure*/
    public void onRequestFailure(Throwable t) {

        if (getActivity() != null) {
            if (t instanceof IOException || t.getCause() instanceof ConnectException || t.getCause() instanceof java.net.UnknownHostException
                    || t.getMessage() == null) {
                DialogManager.getInstance().showAlertPopup(getActivity(), getString(R.string.no_internet), this);
            } else if (t.getCause() instanceof java.net.SocketTimeoutException) {
                DialogManager.getInstance().showAlertPopup(getActivity(), getString(R.string.connect_time_out), this);
            } else if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                DialogManager.getInstance().showAlertPopup(getActivity(), t.getMessage(), this);
            }
        }
    }


    protected void baseFragmentAlertDismiss(Dialog dialog) {
        /*To check if the dialog is shown, if the dialog is shown it will be cancelled */
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(AppConstants.TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onPositiveClick() {

    }

    @Override
    public void onNegativeClick() {

    }
}
