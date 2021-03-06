package quran.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.quranreading.adapter.SurahsListAdapter;
import com.quranreading.ads.AnalyticSingaltonClass;
import com.quranreading.qibladirection.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import quran.activities.BookmarksActivity;
import quran.activities.SajdasActivity;
import quran.activities.StopSignsActivity;
import quran.activities.SurahActivity;
import quran.model.IndexListModel;
import noman.sharedpreference.SurahsSharedPref;

public class SurahListFragment extends Fragment implements OnClickListener, TextWatcher {

	// Store instance variables
	public static final String POSITION = "position";
	private AppCompatActivity mActivity;
	private ArrayList<IndexListModel> dataList = new ArrayList<IndexListModel>();
	private ArrayList<IndexListModel> surahNamesList = new ArrayList<IndexListModel>();

	ImageView btnSearch, btnLastRead, btnBookmark, btnStopSigns, btnSajdas, btnSearchCross, btnSearchBack;
	LinearLayout layoutSearch, layoutOptions;
	EditText etSearchName;
	SurahsListAdapter adapterSurahList;

	boolean inProcess = false;

	@Override
	public void onAttach(Context activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (AppCompatActivity) activity;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeIndexList();
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_surahs, container, false);

		btnSearch = (ImageView) view.findViewById(R.id.btn_option_search);
		btnLastRead = (ImageView) view.findViewById(R.id.btn_option_lastread);
		btnBookmark = (ImageView) view.findViewById(R.id.btn_option_bookmark);
		btnStopSigns = (ImageView) view.findViewById(R.id.btn_option_stopsigns);
		btnSajdas = (ImageView) view.findViewById(R.id.btn_option_sajda);
		btnSearchBack = (ImageView) view.findViewById(R.id.btn_search_back);
		btnSearchCross = (ImageView) view.findViewById(R.id.btn_search_cross);

		btnSearch.setOnClickListener(this);
		btnLastRead.setOnClickListener(this);
		btnBookmark.setOnClickListener(this);
		btnStopSigns.setOnClickListener(this);
		btnSajdas.setOnClickListener(this);
		btnSearchBack.setOnClickListener(this);
		btnSearchCross.setOnClickListener(this);

		layoutSearch = (LinearLayout) view.findViewById(R.id.layout_surah_search);
		layoutOptions = (LinearLayout) view.findViewById(R.id.layout_surah_options);

		etSearchName = (EditText) view.findViewById(R.id.edit_search);
		etSearchName.addTextChangedListener(this);

		ListView listViewApps = (ListView) view.findViewById(R.id.listViewSurahsList);

		listViewApps.setAdapter(adapterSurahList);
		listViewApps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				// TODO Auto-generated method stub

				if(!inProcess)
				{
					inProcess = true;
					sendAnalyticsData();
					Intent intent = new Intent(mActivity, SurahActivity.class);
					intent.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, dataList.get(position).getItemPosition() + 1);
					getActivity().startActivity(intent);
					hideSearchBar();
					// showInterstitialAd();
				}
			}
		});
		return view;
	}

	public void initializeIndexList() {
		dataList.clear();
		surahNamesList.clear();

		int resrcTimeArray = mActivity.getResources().getIdentifier("surah_names", "array", mActivity.getPackageName());

		if(resrcTimeArray > 0)
		{

			String[] engNamesData = getResources().getStringArray(resrcTimeArray);

			resrcTimeArray = mActivity.getResources().getIdentifier("surahNamesArabic", "array", mActivity.getPackageName());
			String[] arabicNamesData = getResources().getStringArray(resrcTimeArray);

			resrcTimeArray = mActivity.getResources().getIdentifier("noOfVerses", "array", mActivity.getPackageName());
			int[] surahSizes = getResources().getIntArray(resrcTimeArray);

			resrcTimeArray = mActivity.getResources().getIdentifier("revealedPlaces", "array", mActivity.getPackageName());
			String[] revealedPlaces = getResources().getStringArray(resrcTimeArray);
			ArrayList<String> engSurahNames = new ArrayList<String>(Arrays.asList(engNamesData));

			for (int pos = 0; pos < engSurahNames.size(); pos++)
			{
				IndexListModel data = new IndexListModel(pos + 1, engSurahNames.get(pos), arabicNamesData[pos], revealedPlaces[pos], surahSizes[pos], pos);
				surahNamesList.add(data);
			}

			dataList.addAll(surahNamesList);
		}
		adapterSurahList = new SurahsListAdapter(getActivity(), dataList);
	}

	private void sendAnalyticsData() {
		AnalyticSingaltonClass.getInstance(mActivity.getBaseContext()).sendScreenAnalytics("Surahs Screen");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
		case R.id.btn_option_search:
			showSearchBar();
			break;
		case R.id.btn_option_lastread:

			openLastRead();

			break;
		case R.id.btn_option_bookmark:
		{
			if(!inProcess)
			{
				inProcess = true;
				Intent intent = new Intent(mActivity, BookmarksActivity.class);
				startActivity(intent);
			}
		}
			break;
		case R.id.btn_option_stopsigns:
			if(!inProcess)
			{
				inProcess = true;
				startActivity(new Intent(mActivity, StopSignsActivity.class));
			}
			break;
		case R.id.btn_option_sajda:
		{
			if(!inProcess)
			{
				inProcess = true;
				Intent intent = new Intent(mActivity, SajdasActivity.class);
				startActivity(intent);
			}
		}
			break;

		case R.id.btn_search_back:
			hideSearchBar();
			break;

		case R.id.btn_search_cross:
			hideSearchBar();
			break;

		default:
			break;
		}
	}

	private void openLastRead() {
		SurahsSharedPref settngPref = new SurahsSharedPref(mActivity);
		if(settngPref.getLastRead() >= 0)
		{
			if(!inProcess)
			{
				inProcess = true;
				Intent end_actvty = new Intent(mActivity, SurahActivity.class);
				end_actvty.putExtra(SurahActivity.KEY_EXTRA_SURAH_NO, settngPref.getLastReadSurah());
				end_actvty.putExtra(SurahActivity.KEY_EXTRA_AYAH_NO, settngPref.getLastRead());
				startActivity(end_actvty);
			}
		}
		else
		{
			showShortToast(getString(R.string.last_read_not_saved), 500);
		}
	}

	private void showShortToast(String message, int milliesTime) {

		if(getString(R.string.device).equals("large"))
		{
			final Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			final Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
			toast.show();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toast.cancel();
				}
			}, milliesTime);
		}
	}

	private void hideSoftKeyboard() {

		etSearchName.clearFocus();
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(etSearchName.getWindowToken(), 0);
	}

	private void showSoftKeyboard() {

		etSearchName.requestFocus();
		InputMethodManager keyboard = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.showSoftInput(etSearchName, 0);
	}

	private void showSearchBar() {
		showSoftKeyboard();
		layoutSearch.setVisibility(View.VISIBLE);
		layoutOptions.setVisibility(View.GONE);
	}

	private void hideSearchBar() {

		hideSoftKeyboard();
		dataList.clear();
		dataList.addAll(surahNamesList);
		etSearchName.setText("");
		adapterSurahList.notifyDataSetChanged();
		layoutSearch.setVisibility(View.GONE);
		layoutOptions.setVisibility(View.VISIBLE);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String searchText = etSearchName.getText().toString().trim().toLowerCase(Locale.US);
		int textlength = searchText.length();

		dataList.clear();

		int size = surahNamesList.size();

		for (int i = 0; i < size; i++)
		{
			String value = surahNamesList.get(i).getEngSurahName().trim();

			if(textlength <= value.length())
			{
				if(value.toLowerCase(Locale.US).contains(searchText))
				{
					dataList.add(surahNamesList.get(i));
				}
				else
				{
					value = value.replace("'", "");
					value = value.replace("-", "");
					value = value.replace(" ", "");

					if(value.toLowerCase(Locale.US).contains(searchText))
					{
						dataList.add(surahNamesList.get(i));
					}
				}
			}
		}

		adapterSurahList.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		inProcess = false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		hideSearchBar();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if(!isVisibleToUser && etSearchName != null)
		{
			hideSearchBar();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
}
