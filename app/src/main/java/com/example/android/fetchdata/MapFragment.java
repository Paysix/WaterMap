package com.example.android.fetchdata;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.android.fetchdata.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    FragmentMapBinding mapBinding;
    //use to get our location
    private FusedLocationProviderClient client;
    private MarkerOptions options;
    private String TAG = "HAHA";
    private List<WPFEntity> WPFs = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        View v = mapBinding.getRoot();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapBinding.btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //定位
                client = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        boolean ready = false;
        //初始化地圖鏡頭
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.639478, 120.971412), 7.4f));
        Toast.makeText(getActivity(),"地圖資料更新中", Toast.LENGTH_LONG);
        new Thread(() -> {
            db.collection("WPFdata")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "save");
                                    WPFs.add(document.toObject(WPFEntity.class));
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
            getActivity().runOnUiThread(() -> {
                for (WPFEntity e : WPFs) {
                    Log.d(TAG, "in2");
                    map.addMarker(new MarkerOptions()
                            .title(e.getName() + "淨水廠")
                            .position(new LatLng(Double.parseDouble(e.getLat()), Double.parseDouble(e.getLon()))));
                }
            });
            Log.d(TAG, WPFs.size() + " iii");
        }).start();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String tmp = marker.getTitle();
                String name = "";
                for (int i = 0; i < tmp.length(); i++) {
                    if (tmp.charAt(i) == '淨')
                        break;
                    name += tmp.charAt(i);
                }
                String finalName = name;
                new Thread(() -> {
                    if (new String("您的位置").equals(finalName))
                        return;
                    WPFEntity wpfEntity = WPFs.get(getArrayIndex(finalName));
                    getActivity().runOnUiThread(() -> {
                        Dialog WPFDialog = new Dialog(getActivity());
                        WPFDialog.setContentView(R.layout.wpf_dialog);
                        WPFDialog.setCancelable(true);
                        TextView txtName = WPFDialog.findViewById(R.id.txtName);
                        txtName.setText(wpfEntity.getName() + "淨水廠");
                        TextView txtTime = WPFDialog.findViewById(R.id.txtUpdate);
                        txtTime.setText(wpfEntity.getTime());
                        TextView txtFlu = WPFDialog.findViewById(R.id.txtFlu);
                        txtFlu.setText("0");
                        TextView txtNit = WPFDialog.findViewById(R.id.txtNit);
                        txtNit.setText("0");
                        WPFDialog.show();
                    });
                }).start();
                return true;
            }
        });

    }

    //定位
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    options = new MarkerOptions()
                            .position(latLng)
                            .title("您的位置");
                    map.addMarker(options).showInfoWindow();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
                }
            }
        });

    }
    //定位授權
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private int getArrayIndex(String name) {
        int result = 0;
        for (int i = 0; i < WPFs.size(); i++) {
            if (WPFs.get(i).getName().equals(name))
                return i;
        }
        return result;
    }
}