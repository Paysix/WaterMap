package com.example.android.fetchdata;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.android.fetchdata.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Navigation Drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String TAG = "TEST";
    private ActivityMainBinding binding;
    private String updateTime;
    //臺北以外淨水廠資訊回傳
    private String result;
    //臺北淨水廠資訊回傳
    private String resultT;
    //地圖Fragment
    private MapFragment mapFragment;
    //主畫面Fragment
    private MainFragment mainFragment;
    //列表Fragment
    private ListFragment listFragment;
    //喝水Fragment
    private DrinkFragment drinkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigation;
        //Navigation Drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Main:
                        getSupportFragmentManager().beginTransaction()
                                .show(mainFragment)
                                .hide(mapFragment)
                                .hide(drinkFragment)
                                .hide(listFragment)
                                .commit();
                        break;
                    case R.id.Map:
                        getSupportFragmentManager().beginTransaction()
                                .show(mapFragment)
                                .hide(mainFragment)
                                .hide(drinkFragment)
                                .hide(listFragment)
                                .commit();
                        break;
                    case R.id.List:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mainFragment)
                                .hide(mapFragment)
                                .hide(drinkFragment)
                                .show(listFragment)
                                .commit();
                        break;
                    case R.id.Drink:
                        getSupportFragmentManager().beginTransaction()
                                .hide(mainFragment)
                                .hide(mapFragment)
                                .show(drinkFragment)
                                .hide(listFragment)
                                .commit();
                        break;
                }
                return false;
            }
        });
        //Action Bar
        ActionBar actBar = getSupportActionBar();
        actBar.setDisplayHomeAsUpEnabled(true);
        actBar.setHomeButtonEnabled(true);
        //Action Bar Toggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        mapFragment = new MapFragment();
        mapFragment = new MapFragment();
        mainFragment = new MainFragment();
        listFragment = new ListFragment();
        drinkFragment = new DrinkFragment();
        //添加畫面
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragMain, mainFragment, "main_fragment")
                .add(R.id.fragMain, mapFragment, "map_fragment")
                .add(R.id.fragMain, drinkFragment, "drink_fragment")
                .add(R.id.fragMain, listFragment, "list_fragment")
                .hide(mapFragment)
                .hide(drinkFragment)
                .hide(listFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}