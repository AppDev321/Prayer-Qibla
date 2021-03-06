package noman.qurantrack.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quranreading.qibladirection.GlobalClass;
import com.quranreading.qibladirection.R;

import java.util.Calendar;

import noman.Ads.AdIntegration;
import noman.CommunityGlobalClass;
import noman.qurantrack.model.TargetModel;
import noman.qurantrack.sharedpreference.QuranTrackerPref;

/**
 * Created by Administrator on 3/27/2017.
 */

public class AddTarget extends AdIntegration implements View.OnClickListener {
    private Calendar calendar;
    TextView tvStartDate, tvEndDate, tvAyah;
    int curYear, curMonth, curDate;
    int sYear, sMonth, sDate;//start
    int eYear, eMonth, eDate;//end
    int userID = 0, status_date_picker;
        float    totalVerse = 6236;
    CardView lnContainerAyah;


    LinearLayout lastSavedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_set_target);

        if (!((GlobalClass) getApplication()).isPurchase) {
            super.showBannerAd(this, (LinearLayout) findViewById(R.id.linear_ad));
        }

        lastSavedContainer = (LinearLayout) findViewById(R.id.save_dates);
        lastSavedContainer.setVisibility(View.GONE);

        LinearLayout btnCross = (LinearLayout) findViewById(R.id.btn_cross);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout btnSave = (LinearLayout) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvStartDate.getText().toString().equals(getString(R.string.txt_default_date)) || tvEndDate.getText().toString().equals(getString(R.string.txt_default_date))) {
                    CommunityGlobalClass.getInstance().showShortToast("Please fill date", 500, Gravity.CENTER);
                } else {
                    if (checkNumberOfAyah()) {
                        saveTarget();
                    }
                }
            }
        });

        calendar = Calendar.getInstance();

        userID = 0;// CommunityGlobalClass.mSignInRequests.getUser_id();


        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curDate = calendar.get(Calendar.DAY_OF_MONTH);
        sDate = eDate = curDate;
        sMonth = eMonth = curMonth;
        sYear = eYear = curYear;

        //Set Current Date in boxes
        TextView tvDate = (TextView) findViewById(R.id.txt_date);
        TextView tvMonth = (TextView) findViewById(R.id.txt_month);
        TextView tvYear = (TextView) findViewById(R.id.txt_year);
        tvDate.setText("" + curDate);
        tvMonth.setText("" + CommunityGlobalClass.getMonthName(curMonth));
        tvYear.setText("" + curYear);


        tvStartDate = (TextView) findViewById(R.id.txt_start_date);
        tvEndDate = (TextView) findViewById(R.id.txt_end_date);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);


        lnContainerAyah = (CardView) findViewById(R.id.ln_container_ayah);
        lnContainerAyah.setVisibility(View.GONE);
        tvAyah = (TextView) findViewById(R.id.txt_no_ayah);
        tvAyah.setText("0");


        LinearLayout btnResult = (LinearLayout) findViewById(R.id.btn_result);
        btnResult.setOnClickListener(this);

        getSavedDatesFromPref();


    }

    void getSavedDatesFromPref() {
        QuranTrackerPref pref = new QuranTrackerPref(this);
        TargetModel modelEndDates = null;
        modelEndDates = pref.getLastSaveEndDatePref();
        TargetModel modelStartDates = null;
        modelStartDates = pref.getLastSaveStartDatePref();


        if (modelEndDates != null && modelStartDates != null) {
            lastSavedContainer.setVisibility(View.VISIBLE);
            TextView tvStartSave = (TextView) findViewById(R.id.txt_save_start_date);
            tvStartSave.setText(modelStartDates.getDate() + " - " + CommunityGlobalClass.getMonthName(modelStartDates.getMonth()) + " - " + modelStartDates.getYear());

            TextView tvEndSave = (TextView) findViewById(R.id.txt_save_end_date);
            tvEndSave.setText(modelEndDates.getDate() + " - " + CommunityGlobalClass.getMonthName(modelEndDates.getMonth()) + " - " + modelEndDates.getYear());


        }

    }

    //Show default date picker android
    DatePickerDialog.OnDateSetListener calenderDialog = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            if (status_date_picker == 0) {
                saveStartDate();
            } else {
                saveEndDate();

            }

        }

    };

    void saveStartDate() {
        sYear = calendar.get(Calendar.YEAR);
        sMonth = calendar.get(Calendar.MONTH) + 1;
        sDate = calendar.get(Calendar.DAY_OF_MONTH);
        tvStartDate.setText(sDate + " - " + CommunityGlobalClass.getMonthName(sMonth) + " - " + sYear);
        lnContainerAyah.setVisibility(View.GONE);
        tvEndDate.setText(getString(R.string.txt_default_date));
    }

    void saveEndDate() {
        eYear = calendar.get(Calendar.YEAR);
        eMonth = calendar.get(Calendar.MONTH) + 1;
        eDate = calendar.get(Calendar.DAY_OF_MONTH);
        tvEndDate.setText(eDate + " - " + CommunityGlobalClass.getMonthName(eMonth) + " - " + eYear);

        // checkNumberOfAyah();

        lnContainerAyah.setVisibility(View.GONE);
    }

    private boolean checkNumberOfAyah() {

        lnContainerAyah.setVisibility(View.VISIBLE);
        int[] sDates = {sDate, sMonth, sYear};
        int[] eDates = {eDate, eMonth, eYear};

        int daysBetween = CommunityGlobalClass.getInstance().findDaysDiff(sDates, eDates);


        if (daysBetween < 3) {
            tvEndDate.setText(getString(R.string.txt_default_date));
            CommunityGlobalClass.getInstance().showShortToast("End date should be greater than 3 days", 800, Gravity.CENTER);
            lnContainerAyah.setVisibility(View.GONE);
            return false;

        } else {
            if (daysBetween <= 0) {
                tvAyah.setText("" + totalVerse);
            } else {
                float val = totalVerse / (daysBetween + 1);
               // String s = String.format("%.2f", val);
                tvAyah.setText("" +  math(val));
            }
            return true;
        }
    }

    public  int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }
    public void saveTarget() {
        //Save End Dates here
        QuranTrackerPref pref = new QuranTrackerPref(this);
        TargetModel model = new TargetModel();
        model.setDate(eDate);
        model.setMonth(eMonth);
        model.setYear(eYear);
        pref.setLastSaveEndDatePref(model);


        model = new TargetModel();
        model.setDate(sDate);
        model.setMonth(sMonth);
        model.setYear(sYear);
        pref.setLastSaveStartDatePref(model);


        this.finish();

    }

    void startDatePicker() {
        status_date_picker = 0;
        new DatePickerDialog(AddTarget.this, calenderDialog, sYear, sMonth - 1,
                sDate).show();
    }

    void endDatePicker() {
        status_date_picker = 1;
        new DatePickerDialog(AddTarget.this, calenderDialog, eYear, eMonth - 1,
                eDate).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_start_date:
                startDatePicker();
                break;
            case R.id.txt_end_date:
                endDatePicker();
                break;
            case R.id.btn_result:
                if (tvStartDate.getText().toString().equals(getString(R.string.txt_default_date)) || tvEndDate.getText().toString().equals(getString(R.string.txt_default_date))) {
                    CommunityGlobalClass.getInstance().showShortToast("Please fill date", 500, Gravity.CENTER);
                } else {
                    checkNumberOfAyah();
                }
                break;

        }
    }
}
