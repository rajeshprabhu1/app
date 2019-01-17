package com.ouam.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ouam.app.R;
import com.ouam.app.commonInterfaces.InterfaceWithTwoArgumentCallBack;
import com.ouam.app.entity.AddPinInputEntity;
import com.ouam.app.fragments.CitySearchFragment;
import com.ouam.app.fragments.PeopleSearchFragment;
import com.ouam.app.fragments.PlaceSearchFragment;
import com.ouam.app.main.BaseActivity;
import com.ouam.app.model.AddressResponse;
import com.ouam.app.services.APIRequestHandler;
import com.ouam.app.utils.AppConstants;
import com.ouam.app.utils.DialogManager;
import com.ouam.app.utils.NetworkUtil;
import com.ouam.app.utils.PlaceJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivityScreen extends BaseActivity implements InterfaceWithTwoArgumentCallBack {

    @BindView(R.id.search_screen_tab_lay)
    TabLayout mTabLayout;

    @BindView(R.id.search_screen_pager)
    ViewPager mViewPager;

    @BindView(R.id.search_par_lay)
    RelativeLayout mSearchParLay;

    @BindView(R.id.place_suggest_atx)
    AutoCompleteTextView mAutoCompleteTextView;

    @BindView(R.id.add_bottom_lay)
    LinearLayout mAddBottomLay;

    @BindView(R.id.header_edt)
    EditText mHeaderEdt;

    @BindView(R.id.place_txt)
    TextView mCurrentPlaceTxt;

    @BindView(R.id.place_lay)
    RelativeLayout mPlaceLay;

    @BindView(R.id.place_card_lay)
    CardView mPlaceCardLay;

    @BindView(R.id.header_lay)
    RelativeLayout mHeaderLay;


    RelativeLayout mPlaceBgLay, mPeopleBgLay, mCitiesBgLay, mSearchPlaceLay, mSearchPeopleLay, mSearchCitiesLay, mSearchPlaceMarkerLay, mSearchPeopleMarkerLay, mSearchCitiesMarkerLay;
    TextView mPlaceTxt, mPeopleTxt, mCitiesTxt;

    private PlaceTask mPlacesTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_search_screen);
        initView();
    }

    //init views
    private void initView() {
        ButterKnife.bind(this);

        setupUI(mSearchParLay);
        setHeaderView();


        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);


        initTabHeaderView();


        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlacesTask = new PlaceTask();
                mPlacesTask.execute(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


//        mHeaderEdt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                AppConstants.SEARCH_PLACE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//                AppConstants.SEARCH_PEOPLE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//                AppConstants.SEARCH_CITY_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });


        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // getLatLangApi(mAutoCompleteTextView.getText().toString().trim());
                getUserAddressApi(mAutoCompleteTextView.getText().toString().trim());
            }
        });

        mHeaderEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AppConstants.SEARCH_PLACE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim().toLowerCase().replace(" ", ""));
                    AppConstants.SEARCH_PEOPLE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
                    AppConstants.SEARCH_CITY_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
                    mHeaderEdt.setCursorVisible(false);
                    hideSoftKeyboard(SearchActivityScreen.this);
                    return true;
                }
                return false;
            }
        });


        trackScreenName(getString(R.string.search_screen));


        /*set current location name*/
        mCurrentPlaceTxt.setPaintFlags(mCurrentPlaceTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mCurrentPlaceTxt.setText(AppConstants.MAP_MOVE_LOCATION_NAME);
        mAutoCompleteTextView.setText(AppConstants.MAP_MOVE_LOCATION_NAME);
        if (AppConstants.MAP_MOVE_LOCATION_NAME.length() > 0)
            mAutoCompleteTextView.setSelection(AppConstants.MAP_MOVE_LOCATION_NAME.length());
    }

    private void setHeaderView() {
        /*Set header adjustment - status bar we applied transparent color so header tack full view*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHeaderLay.post(new Runnable() {
                public void run() {
                    mHeaderLay.setPadding(0, getStatusBarHeight(SearchActivityScreen.this), 0, 0);

                }
            });
        }

    }

    private void initTabHeaderView() {

        /*set custom tab*/
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.tab_search_view, null, false);

        mSearchPlaceLay = headerView.findViewById(R.id.search_place_lay);
        mSearchPeopleLay = headerView.findViewById(R.id.search_people_lay);
        mSearchCitiesLay = headerView.findViewById(R.id.search_cities_lay);

        mPlaceBgLay = headerView.findViewById(R.id.search_place_inner_lay);
        mPeopleBgLay = headerView.findViewById(R.id.search_people_inner_lay);
        mCitiesBgLay = headerView.findViewById(R.id.search_cities_inner_lay);

        mPlaceTxt = headerView.findViewById(R.id.search_places_txt);
        mPeopleTxt = headerView.findViewById(R.id.search_people_txt);
        mCitiesTxt = headerView.findViewById(R.id.search_cities_txt);

//        mCurrentPlaceTxt = headerView.findViewById(R.id.place_txt);

//        mPlaceCardLay = headerView.findViewById(R.id.place_card_lay);


//        mSearchPlaceMarkerLay = headerView.findViewById(R.id.search_place_marker_lay);
//        mSearchPeopleMarkerLay = headerView.findViewById(R.id.search_people_marker_lay);
//        mSearchCitiesMarkerLay = headerView.findViewById(R.id.search_cities_marker_lay);

        mTabLayout.getTabAt(0).setCustomView(mSearchPlaceLay);
        mTabLayout.getTabAt(1).setCustomView(mSearchPeopleLay);
        mTabLayout.getTabAt(2).setCustomView(mSearchCitiesLay);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new PlaceSearchFragment());
        adapter.addFragment(new PeopleSearchFragment());
        adapter.addFragment(new CitySearchFragment());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

//                mPlaceBgLay.setBackgroundResource(position == 0 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
//                mPeopleBgLay.setBackgroundResource(position == 1 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
//                mCitiesBgLay.setBackgroundResource(position == 2 ? R.color.dark_blue : R.drawable.app_blue_color_with_border_bg);
//
                mPlaceTxt.setTextColor(position == 0 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));
                mPeopleTxt.setTextColor(position == 1 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));
                mCitiesTxt.setTextColor(position == 2 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.dark_blue));

//                mSearchPlaceMarkerLay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
//                mSearchPeopleMarkerLay.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
//                mSearchCitiesMarkerLay.setVisibility(position == 2 ? View.VISIBLE : View.GONE);

                mAddBottomLay.setVisibility(position == 0 ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onRequestSuccess(Object resObj) {
        super.onRequestSuccess(resObj);
        if (resObj instanceof AddressResponse) {
            AddressResponse userAddressRes = (AddressResponse) resObj;
            if (userAddressRes.getResults().size() > 0) {
                AppConstants.CURRENT_LAT = Double.parseDouble(!userAddressRes.getResults().get(0).
                        getGeometry().getLocation().getLat().isEmpty() ? userAddressRes.getResults().get(0).
                        getGeometry().getLocation().getLat() : "0.0");
                AppConstants.CURRENT_LONG = Double.parseDouble(!userAddressRes.getResults().get(0).getGeometry().getLocation().getLng().isEmpty() ?
                        userAddressRes.getResults().get(0).getGeometry().getLocation().getLng() : "0.0");

                /*update location for custom location*/
                AppConstants.MAP_MOVE_LAT = Double.parseDouble(!userAddressRes.getResults().get(0).
                        getGeometry().getLocation().getLat().isEmpty() ? userAddressRes.getResults().get(0).
                        getGeometry().getLocation().getLat() : "0.0");
                AppConstants.MAP_MOVE_LONG = Double.parseDouble(!userAddressRes.getResults().get(0).getGeometry().getLocation().getLng().isEmpty() ?
                        userAddressRes.getResults().get(0).getGeometry().getLocation().getLng() : "0.0");

                AppConstants.MAP_MOVE_LOCATION_NAME = userAddressRes.getResults().get(0).getFormatted_address();


                AppConstants.SEARCH_PLACE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
                AppConstants.SEARCH_PEOPLE_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());
                AppConstants.SEARCH_CITY_CALLBACK.searchPlace(mHeaderEdt.getText().toString().trim());

            }


        }
    }

    @Override
    public void withParams(String edtTxt, String type) {
        AddPinInputEntity addPinInputEntity = new AddPinInputEntity();
        switch (Integer.parseInt(type)) {
            case 1:
                addPinInputEntity.setPinType(getString(R.string.sub_type_been_there));
                break;
            case 2:
                addPinInputEntity.setPinType(getString(R.string.sub_type_to_be_explored));
                break;
            case 3:
                addPinInputEntity.setPinType(getString(R.string.sub_type_hidden_gem));
                break;
            default:
                addPinInputEntity.setPinType(getString(R.string.sub_type_been_there));
                break;

        }

        addPinInputEntity.setName(edtTxt);
        addPinInputEntity.setLat(AppConstants.MAP_MOVE_LAT);
        addPinInputEntity.setLon(AppConstants.MAP_MOVE_LONG);
        APIRequestHandler.getInstance().createCustomPin(addPinInputEntity, this);


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

    @OnClick({R.id.close_img, R.id.add_bottom_lay, R.id.place_suggest_atx, R.id.place_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_img:
                onBackPressed();
                break;
            case R.id.add_bottom_lay:
                DialogManager.getInstance().showPiningModePopup(SearchActivityScreen.this,
                        AppConstants.MAP_MOVE_LOCATION_NAME, true, this);

                break;
            case R.id.place_suggest_atx:
                mAutoCompleteTextView.setCursorVisible(true);
                break;
            case R.id.place_lay:
                mPlaceLay.setVisibility(View.GONE);
                mPlaceCardLay.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backScreen(true);
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    @SuppressLint("StaticFieldLeak")
    private class PlaceTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + getString(R.string.google_map_id);

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            ParserTask parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }


    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
                    return view;
                }

                ;
            };

            // Setting the adapter
            mAutoCompleteTextView.setAdapter(adapter);
        }
    }


    private void getUserAddressApi(String address) {
        if (NetworkUtil.isNetworkAvailable(this)) {

            String addressURLStr = String.format(AppConstants.GET_DETAILS_ADDRESS_URL, getString(R.string.google_map_id), address);
            APIRequestHandler.getInstance().callGetUserAddressAPI(addressURLStr, this, false);
        } else {
            getUserAddressApi(address);
        }

    }


}
