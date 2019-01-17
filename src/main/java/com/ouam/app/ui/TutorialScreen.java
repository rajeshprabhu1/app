package com.ouam.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceBtnCallBack;
import com.ouam.app.entity.LoginInputEntity;
import com.ouam.app.entity.UserDetailsEntity;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.LoginResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PreferenceUtil;
import com.rd.PageIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

public class TutorialScreen extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.page_indicator_view)
    PageIndicatorView mPageIndicatorView;

    @BindView(R.id.skip_txt)
    TextView mSkipTxt;

    private CallbackManager mFbCallBackManager;
    private GoogleApiClient mGoogleApiClient;

    private GifImageView mFirstGifImg, mSecondGifImg;


    private String mLoginTypeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_tutorial_screen);
        initView();
    }

    //init views
    private void initView() {
        ButterKnife.bind(this);

        /*Facebook login*/
        initFbLoginService();

        /*Google login*/
        initGoogleLoginService();

        /*welcome screen - Added layout to array list*/
        ArrayList<Integer> tutorialXmlIntArrList = new ArrayList<>();

        tutorialXmlIntArrList.add(R.layout.ui_tutorial_child_view_one);
        tutorialXmlIntArrList.add(R.layout.ui_tutorial_child_view_two);
        tutorialXmlIntArrList.add(R.layout.ui_tutorial_child_view_third);


        mViewPager.setAdapter(new CustomPagerAdapter(this, tutorialXmlIntArrList));
        mPageIndicatorView.setViewPager(mViewPager);

        //page change tracker
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicatorView.setSelection(position);

                if(position==0&&mFirstGifImg!=null){

                    mFirstGifImg.setImageResource(R.drawable.tutorial_first_gif_img);
                }else if(position==1&&mSecondGifImg!=null){

                    mSecondGifImg.setImageResource(R.drawable.tutorial_second_gif_img);
                }
//                mSkipTxt.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        trackScreenName(getString(R.string.tutorila_screen));

    }


    @OnClick({R.id.skip_txt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_txt:
                skipFlow();
                break;
        }
    }


    /*Init Fb service*/
    private void initFbLoginService() {
        mFbCallBackManager = CallbackManager.Factory.create();
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        LoginManager.getInstance().registerCallback(mFbCallBackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        DialogManager.getInstance().hideProgress();
                        getFacebookProfileDetails(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        DialogManager.getInstance().hideProgress();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        DialogManager.getInstance().hideProgress();
                    }
                });

    }


    /*Init Google service*/
    private void initGoogleLoginService() {
        GoogleSignInOptions googleSigninOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestIdToken("1097194285465-btfplsrksss39eomqmum0rfk2gfj9tu7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOption)
                .build();


    }


    /*get user details from FB*/
    private void getFacebookProfileDetails(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject,
                                            GraphResponse graphResponse) {

                        String fbUserNameStr, fbUserFirstNameStr, fbUserLastNameStr, fbIdStr, fbEmailStr;

                        try {
                            fbUserNameStr = (jsonObject.get("name") != null && !jsonObject.getString("name").isEmpty()) ? jsonObject.getString("name") : "";
                            fbUserFirstNameStr = (jsonObject.get("first_name") != null && !jsonObject.getString("first_name").isEmpty()) ? jsonObject.getString("first_name") : "";
                            fbUserLastNameStr = (jsonObject.get("last_name") != null && !jsonObject.getString("last_name").isEmpty()) ? jsonObject.getString("last_name") : "";

                            fbIdStr = (jsonObject.get("id") != null && !jsonObject.getString("id").isEmpty()) ? jsonObject.getString("id") : "";
                            fbEmailStr = (jsonObject.get(AppConstants.EMAIL) != null && !jsonObject.getString(AppConstants.EMAIL).isEmpty()) ? jsonObject.getString(AppConstants.EMAIL) : "";


                            if (fbEmailStr.isEmpty()) {
                                DialogManager.getInstance().showAlertPopup(TutorialScreen.this, getString(R.string.no_email_id_fb), TutorialScreen.this);
                            } else {

                                AppConstants.USER_NAME = fbUserNameStr;
                                AppConstants.USER_FIRST_NAME = fbUserFirstNameStr;
                                AppConstants.PLATFORM_ID = fbIdStr;
                                AppConstants.PLATFORM = "fb";
                                AppConstants.USER_IMAGE = getString(R.string.fb_image_url) + fbIdStr +
                                        getString(R.string.fb_concat_image);
                                AppConstants.USER_EMAIL = fbEmailStr;
                                AppConstants.SOCIAL_ACCESS_TOKEN = loginResult.getAccessToken().getToken();

                                loginAPICall();

                            }

                        } catch (Exception e) {
                            DialogManager.getInstance().showAlertPopup(TutorialScreen.this, getString(R.string.no_email_id_fb), TutorialScreen.this);
                            Log.e(AppConstants.TAG, e.toString());
                        }

                        /*logout from fb acc*/
                        LoginManager.getInstance().logOut();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields",
                "id,name,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /*google after success*/
    private void handleSignInResult(GoogleSignInResult result) {

        DialogManager.getInstance().hideProgress();
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

            if (googleSignInAccount == null) {
                DialogManager.getInstance().showAlertPopup(TutorialScreen.this, getString(R.string.please_enter_password), TutorialScreen.this);
            } else {

                sysOut("googleSignInAccount.getIdToken(): "+googleSignInAccount.getIdToken());
                sysOut("googleSignInAccount.getId(): "+googleSignInAccount.getId());

                AppConstants.USER_NAME = googleSignInAccount.getDisplayName();
                AppConstants.USER_FIRST_NAME = googleSignInAccount.getGivenName();
                AppConstants.USER_EMAIL = googleSignInAccount.getEmail();
                AppConstants.PLATFORM_ID = googleSignInAccount.getId();
                AppConstants.PLATFORM = "google";
                AppConstants.USER_IMAGE = googleSignInAccount.getPhotoUrl() == null ? "" : googleSignInAccount.getPhotoUrl().toString();
                AppConstants.SOCIAL_ACCESS_TOKEN = googleSignInAccount.getIdToken();

                /*login api call*/
                //TODO api call
                loginAPICall();
//                nextScreen(GetStartedScreen.class, false);


            }

        } else {
            // Signed out, show unauthenticated UI.
            DialogManager.getInstance().showToast(this, getString(R.string.google_unauthenticated));
        }

        /*logout from google acc*/
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*get Access token and find new user API*/
    public void loginAPICall() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            LoginInputEntity loginInputEntity = new LoginInputEntity();
            loginInputEntity.setProvider(mLoginTypeStr);
            sysOut("mLoginTypeStr:"+mLoginTypeStr);
            sysOut("AppConstants.SOCIAL_ACCESS_TOKEN:"+AppConstants.SOCIAL_ACCESS_TOKEN);
            loginInputEntity.setAccessToken(AppConstants.SOCIAL_ACCESS_TOKEN);
            APIRequestHandler.getInstance().loginApiCall(TutorialScreen.this, loginInputEntity);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    loginAPICall();
                }
            });
        }
    }


    //class for adding page viewer
    private class CustomPagerAdapter extends PagerAdapter {
        private Context mContext;
        private ArrayList<Integer> mTutorialXmlArrList;

        CustomPagerAdapter(Context context, ArrayList<Integer> tutorialXmlIntArrList) {
            mContext = context;
            mTutorialXmlArrList = tutorialXmlIntArrList;
        }

        @Override
        public int getCount() {
            return mTutorialXmlArrList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewGroup rooViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(mTutorialXmlArrList.get(position),
                    container, false);


            if (position == 0) {
                mFirstGifImg = rooViewGroup.findViewById(R.id.first_gif_img);
            } else if (position == 1) {
                mSecondGifImg = rooViewGroup.findViewById(R.id.second_gif_img);
            }

            if (position == 2) {
                Button loginWithFaceBookBtn = rooViewGroup.findViewById(R.id.login_with_facebook_btn);
                Button loginWithGoogleBtn = rooViewGroup.findViewById(R.id.login_with_google_btn);

                loginWithFaceBookBtn.setOnClickListener(view -> {
                    facebookLogin();
                });

                loginWithGoogleBtn.setOnClickListener(view -> {
                    googleLogin();
                });

            }

            container.addView(rooViewGroup);
            return rooViewGroup;

        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }

    }


    private void facebookLogin() {

        if (NetworkUtil.isNetworkAvailable(this)) {
            mLoginTypeStr = AppConstants.FACEBOOK;
            DialogManager.getInstance().showProgress(this);
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    facebookLogin();
                }
            });
        }

    }

    private void googleLogin() {

        if (NetworkUtil.isNetworkAvailable(this)) {
            mLoginTypeStr = AppConstants.GOOGLE;
            DialogManager.getInstance().showProgress(this);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, 99);

        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    googleLogin();
                }

            });
        }
    }

    private void skipFlow() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            AppConstants.USER_NAME = "";
            AppConstants.USER_FIRST_NAME = "";
            AppConstants.USER_EMAIL = "";
            AppConstants.USER_IMAGE = "";
            nextScreen(GetStartedScreen.class, false);
        } else {
            DialogManager.getInstance().showNetworkErrorPopup(this, new InterfaceBtnCallBack() {
                @Override
                public void onPositiveClick() {
                    skipFlow();
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (mLoginTypeStr.equals(AppConstants.FACEBOOK)) {
            mFbCallBackManager.onActivityResult(requestCode, resultCode, data);
        } else if (mLoginTypeStr.equals(AppConstants.GOOGLE)) {
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }

    }




    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof LoginResponse) {
            LoginResponse mResponse = (LoginResponse) resObj;
            if (mResponse.getStatus().equals(getString(R.string.succeeded))) {

                AppConstants.ACCESS_TOKEN = mResponse.getWith().getAccess_token();
                AppConstants.PROFILE_USER_ID = mResponse.getWith().getUser_id();
                if (mResponse.getWith().getUser_name().isEmpty()) {
                    nextScreen(GetStartedScreen.class, false);

                } else {
                    UserDetailsEntity mUserDetail = new UserDetailsEntity();
                    mUserDetail.setUserName(mResponse.getWith().getUser_name());
                    mUserDetail.setAuthorizationToken(mResponse.getWith().getAccess_token());
                    mUserDetail.setUserId(mResponse.getWith().getUser_id());

                    AppConstants.USER_NAME = mResponse.getWith().getUser_name();
                    PreferenceUtil.storeBoolPreferenceValue(TutorialScreen.this,
                            AppConstants.LOGIN_STATUS, true);
                    PreferenceUtil.storeUserDetails(TutorialScreen.this, mUserDetail);

                    nextScreen(HomeActivityFeedScreen.class, false);
                }

                DialogManager.getInstance().showToast(this, mResponse.getStatus() + " " + mResponse.getBy() + " " + mResponse.getThe());

            }

        }


    }

    @Override
    public void onRequestFailure(Throwable t) {
        super.onRequestFailure(t);
    }
}
