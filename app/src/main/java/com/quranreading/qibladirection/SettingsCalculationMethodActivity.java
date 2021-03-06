package com.quranreading.qibladirection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.quranreading.sharedPreference.PrayerTimeSettingsPref;

/**
 * Created by cyber on 12/20/2016.
 */

public class SettingsCalculationMethodActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // google ads
    AdView adview;
    ImageView adImage;
    private static final String LOG_TAG = "Ads";
    private final Handler adsHandler = new Handler();
    private int timerValue = 3000, networkRefreshTime = 10000;
    Context context = this;

    ListView listView;
    CalculationMethodAdapter adapter;
    String[] methodNames, methodAngles;
    int[] methodIndexs;
    int selectedPosition;
    boolean inProccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated methodIndex stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        LinearLayout backBtn = (LinearLayout) findViewById(R.id.toolbar_btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        TextView tvHeading = (TextView) findViewById(R.id.txt_toolbar);
        tvHeading.setText(R.string.settings);

        tvHeading.setText(R.string.calculation_method);

        // Ads
        initializeAds();

        listView = (ListView) findViewById(R.id.listview_language);

        methodNames = getResources().getStringArray(R.array.array_calculation_methods_new);
        methodAngles = getResources().getStringArray(R.array.array_calculation_methods_angles);
        methodIndexs = getResources().getIntArray(R.array.array_calculation_methods_index);

        selectedPosition = new PrayerTimeSettingsPref(this).getCalculationMethodIndex();

        adapter = new CalculationMethodAdapter(context, methodNames);

        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
    }

    private void initializeAds() {
        adview = (AdView) findViewById(R.id.adView);
        adImage = (ImageView) findViewById(R.id.adimg);
        adImage.setVisibility(View.GONE);
        adview.setVisibility(View.GONE);

        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }
        setAdsListener();
    }

    public void onBackButtonClick(View v) {
        onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        PrayerTimeSettingsPref prayerTimeSettingsPref = new PrayerTimeSettingsPref(SettingsCalculationMethodActivity.this);
        prayerTimeSettingsPref.setCalculationMethod(methodIndexs[position]);
        prayerTimeSettingsPref.setCalculationMethodIndex(position);

        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated methodIndex stub
        super.onResume();
        inProccess = false;

        if (((GlobalClass) getApplication()).isPurchase) {
            adImage.setVisibility(View.GONE);
            adview.setVisibility(View.GONE);
        } else {
            startAdsCall();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated methodIndex stub
        super.onPause();

        if (!((GlobalClass) getApplication()).isPurchase) {
            stopAdsCall();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated methodIndex stub
        super.onDestroy();

        if (!((GlobalClass) getApplication()).isPurchase) {

            destroyAds();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////
    //////////////////////////
    //////////////////////////
    public void onClickAdImage(View view) {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        return (netInfo != null && netInfo.isConnected() && netInfo.isAvailable());
    }

    private Runnable sendUpdatesAdsToUI = new Runnable() {
        public void run() {
            Log.v(LOG_TAG, "Recall");
            updateUIAds();
        }
    };

    private final void updateUIAds() {
        if (isNetworkConnected()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adview.loadAd(adRequest);
        } else {
            timerValue = networkRefreshTime;
            adsHandler.removeCallbacks(sendUpdatesAdsToUI);
            adsHandler.postDelayed(sendUpdatesAdsToUI, timerValue);
        }
    }

    public void startAdsCall() {
        Log.i(LOG_TAG, "Starts");
        if (isNetworkConnected()) {
            this.adview.setVisibility(View.VISIBLE);
        } else {
            this.adview.setVisibility(View.GONE);
        }

        adview.resume();
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adsHandler.postDelayed(sendUpdatesAdsToUI, 0);
    }

    public void stopAdsCall() {
        Log.e(LOG_TAG, "Ends");
        adsHandler.removeCallbacks(sendUpdatesAdsToUI);
        adview.pause();
    }

    public void destroyAds() {
        Log.e(LOG_TAG, "Destroy");
        adview.destroy();
        adview = null;
    }

    private void setAdsListener() {
        adview.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(LOG_TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int error) {
                String message = "onAdFailedToLoad: " + getErrorReason(error);
                Log.d(LOG_TAG, message);
                adview.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(LOG_TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.d(LOG_TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                Log.d(LOG_TAG, "onAdLoaded");
                adview.setVisibility(View.VISIBLE);

            }
        });
    }

    private String getErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }


    class CalculationMethodAdapter extends BaseAdapter {

        private Context mContext;
        String[] methods;

        public CalculationMethodAdapter(Context context, String[] dataList) {
            this.mContext = context;
            this.methods = dataList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated methodIndex stub
            return methods.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated methodIndex stub
            return methods[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated methodIndex stub
            return position;
        }

        /* private view holder class */
        private class ViewHolder {
            public ImageView imgSelection;
            public TextView tvMehtods;
            public TextView tvMehtodsDegree;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated methodIndex stub

            ViewHolder holder = null;
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.row_calculation_methods, null);

                holder = new ViewHolder();

                holder.tvMehtods = (TextView) convertView.findViewById(R.id.tv_mehtod_names);
                holder.tvMehtodsDegree = (TextView) convertView.findViewById(R.id.tv_mehtod_names_degree);
                holder.imgSelection = (ImageView) convertView.findViewById(R.id.img_selection);

                holder.tvMehtods.setTypeface(((GlobalClass) mContext.getApplicationContext()).faceRobotoL);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvMehtods.setText(methods[position]);
            holder.tvMehtodsDegree.setText(methodAngles[position]);

            if (position == selectedPosition) {
                holder.imgSelection.setImageResource(R.drawable.ic_radio_chk);
            } else {
                holder.imgSelection.setImageResource(R.drawable.ic_radio_uncheck);
            }

            return convertView;
        }
    }
}