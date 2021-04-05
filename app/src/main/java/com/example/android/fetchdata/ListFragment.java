package com.example.android.fetchdata;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.fetchdata.databinding.FragmentListBinding;
import com.example.android.fetchdata.recycleView.RecycleAdapter;
import com.example.android.fetchdata.recycleView.WPFEntityComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListFragment extends Fragment {


    public ListFragment() {
        // Required empty public constructor
    }

    private FragmentListBinding fragmentListBinding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<WPFEntity> allWPF = new ArrayList();
    private String TAG = "HAHA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        View v = fragmentListBinding.getRoot();

        new Thread(() -> {
            db.collection("WPFdata")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    allWPF.add(document.toObject(WPFEntity.class));
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(() ->{
                fragmentListBinding.recycleList.setLayoutManager(new LinearLayoutManager(getContext()));
                Collections.sort(allWPF, new WPFEntityComparator());
                fragmentListBinding.recycleList.setAdapter(new RecycleAdapter(allWPF));
            });
        }).start();

        return v;
    }


}