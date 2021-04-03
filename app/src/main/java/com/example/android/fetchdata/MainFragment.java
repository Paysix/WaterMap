package com.example.android.fetchdata;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.fetchdata.databinding.FragmentMainBinding;
import com.example.android.fetchdata.databinding.FragmentMapBinding;
import com.google.android.gms.maps.SupportMapFragment;


public class MainFragment extends Fragment {

    public FragmentMainBinding fragmentMainBinding;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        View v = fragmentMainBinding.getRoot();


        return v;
    }
}