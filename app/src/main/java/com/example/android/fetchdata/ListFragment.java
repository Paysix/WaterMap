package com.example.android.fetchdata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.fetchdata.dataBase.WPFDataBase;
import com.example.android.fetchdata.dataBase.WPFEntity;
import com.example.android.fetchdata.databinding.FragmentListBinding;
import com.example.android.fetchdata.recycleView.RecycleAdapter;

import java.util.List;


public class ListFragment extends Fragment {


    public ListFragment() {
        // Required empty public constructor
    }

    private FragmentListBinding fragmentListBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        View v = fragmentListBinding.getRoot();
        new Thread(() -> {
            List<WPFEntity> allWPF = WPFDataBase.getInstance(getContext()).wpfdao().displayAll();
            getActivity().runOnUiThread(() ->{
                fragmentListBinding.recycleList.setLayoutManager(new LinearLayoutManager(getContext()));
                fragmentListBinding.recycleList.setAdapter(new RecycleAdapter(allWPF));
            });
        }).start();
        return v;
    }


}