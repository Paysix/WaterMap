package com.example.android.fetchdata;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.fetchdata.databinding.FragmentDrinkBinding;
import com.example.android.fetchdata.databinding.FragmentMainBinding;
import com.google.android.gms.maps.SupportMapFragment;

public class DrinkFragment extends Fragment {

    public DrinkFragment() {
        // Required empty public constructor
    }

    private FragmentDrinkBinding fragmentMainBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMainBinding = FragmentDrinkBinding.inflate(inflater, container, false);
        View v = fragmentMainBinding.getRoot();


        return v;
    }
}