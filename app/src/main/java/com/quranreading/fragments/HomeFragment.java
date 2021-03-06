package com.quranreading.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quranreading.qibladirection.R;

/**
 * Created by cyber on 11/30/2016.
 */

public class HomeFragment extends Fragment {

    CompassDialMenuFragment compassDialMenuFragment;
    MenuFragment menuFragment;
    private boolean isSmallDevice=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compassDialMenuFragment = new CompassDialMenuFragment();
        menuFragment = new MenuFragment();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        if ((width > 1080 && height <= 2560)) {
            isSmallDevice = false;
        } else {
            //(width == 720 && height == 1280) || (width == 540 && height == 960)
            isSmallDevice = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        if(isSmallDevice) {
            v = inflater.inflate(R.layout.fragment_index, container, false);
        }
        else
        {
            v = inflater.inflate(R.layout.fragment_index_large_phone, container, false);
        }

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container_frame_qibla, compassDialMenuFragment);
        ft.commit();


        ft = fm.beginTransaction();
        ft.add(R.id.container_frame_grid, menuFragment);
        ft.commit();


        return v;
    }
}
