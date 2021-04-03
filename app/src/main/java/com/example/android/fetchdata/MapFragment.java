package com.example.android.fetchdata;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.fetchdata.dataBase.WPFDataBase;
import com.example.android.fetchdata.dataBase.WPFEntity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    FragmentMapBinding mapBinding;
    //use to get our location
    private FusedLocationProviderClient client;
    private MarkerOptions options;

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

        //初始化地圖鏡頭
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.639478, 120.971412), 7.4f));

        //標上淨水廠Marker
        new Thread(() -> {
            List<WPFEntity> allWPF = WPFDataBase.getInstance(getContext()).wpfdao().displayAll();
            if (allWPF != null) {
                for (int i = 0; i < allWPF.size(); i++) {
                    int j = i;
                    getActivity().runOnUiThread(() -> {
                        map.addMarker(new MarkerOptions()
                                .title(allWPF.get(j).getName() + "淨水廠")
                                .position(new LatLng(Double.parseDouble(allWPF.get(j).getLat()), Double.parseDouble(allWPF.get(j).getLon()))));
                    });
                }
            }
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
                    WPFEntity wpfEntity = WPFDataBase.getInstance(getContext()).wpfdao().findDataByName(finalName);
                    getActivity().runOnUiThread(() -> {
                        Dialog WPFDialog = new Dialog(getActivity());
                        WPFDialog.setContentView(R.layout.wpf_dialog);
                        WPFDialog.setCancelable(true);
                        TextView txtName = WPFDialog.findViewById(R.id.txtName);
                        txtName.setText(wpfEntity.getName() + "淨水廠");
                        TextView txtTime = WPFDialog.findViewById(R.id.txtUpdate);
                        txtTime.setText(wpfEntity.getUpdateTime());
                        TextView txtFlu = WPFDialog.findViewById(R.id.txtFlu);
                        txtFlu.setText(wpfEntity.getFluoride());
                        TextView txtNit = WPFDialog.findViewById(R.id.txtNit);
                        txtNit.setText(wpfEntity.getNitrate());
                        WPFDialog.show();
                    });
                }).start();
                return true;
            }
        });
    }
    //定位
    private  void getLocation() {
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
}