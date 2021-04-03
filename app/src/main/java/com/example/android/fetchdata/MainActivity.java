package com.example.android.fetchdata;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.android.fetchdata.dataBase.WPFDataBase;
import com.example.android.fetchdata.dataBase.WPFEntity;
import com.example.android.fetchdata.databinding.ActivityMainBinding;
import com.facebook.stetho.Stetho;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

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
    //更新地圖
    private boolean othersStatus;
    private boolean TaipeiStatus;
    private boolean firstInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
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
        mainFragment = new MainFragment();
        listFragment = new ListFragment();
        drinkFragment = new DrinkFragment();
        othersStatus = false;
        TaipeiStatus = false;
        firstInstall = true;
        new Thread(() -> {
            WPFEntity first = new WPFEntity("0","0","0","0","0","0","0","0","0");
            WPFDataBase.getInstance(getBaseContext()).wpfdao().insertData(first);
            if (WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll().size() > 1)
                firstInstall = false;
            else
                runOnUiThread(() -> {
                    Toast.makeText(getBaseContext(), "地圖資料更新中請稍後", Toast.LENGTH_LONG).show();
                });
            List<WPFEntity> forDelete = WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll();
            for (int i = 0; i < forDelete.size(); i++) {
                int index = forDelete.get(i).getId();
                if (WPFDataBase.getInstance(getBaseContext()).wpfdao().findDataById(index).getUpdateTime().equals(first.getUpdateTime()))
                    WPFDataBase.getInstance(getBaseContext()).wpfdao().deleteData(index);
            }
            WPFDataBase.getInstance(getBaseContext()).wpfdao().deleteData(WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll().size());
            //地圖和資料初始化
            saveWPFTaipei();
            Log.d(TAG, String.valueOf(firstInstall));
            while (true) {
                if (TaipeiStatus) {
                    saveWPF();
                    break;
                }
                else if (!firstInstall)
                    break;
            }
            while (true) {
                if (othersStatus && TaipeiStatus && firstInstall) {
                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), "地圖資料更新中請稍後", Toast.LENGTH_LONG).show();
                    });
                    saveWPFLatLon();
                    break;
                }
                else if (!firstInstall)
                    break;
            }
        }).start();
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

    //存臺北以外淨水廠資訊
    private void saveWPF() {
        //new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();
            Request request;
            request = new Request.Builder()
                    .url("https://www.water.gov.tw/opendata/new_opendata_waterquality.csv")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //如果傳送過程有發生錯誤
                     System.out.print(e.getMessage());
                     }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //取得回傳
                    result = new String(response.body().string().getBytes(), "UTF-8");
                }
            });
            while (true) {
                if (result != null)
                    break;
            }
            //split every line
            String[] txtWPF = result.split("\\\n");
            if (WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll().size() >= 100
                    && new String(WPFDataBase.getInstance(getBaseContext()).wpfdao().findDataById(7).getUpdateTime()).equals(txtWPF[0])) {
                othersStatus = true;
                return;
            }
            updateTime = txtWPF[0];
            //parse and save data to database
            for (int i = 5; i < txtWPF.length - 2; i++) {
                String[] line = txtWPF[i].split(",");
                String update = updateTime;
                String area = line[0];
                String num = line[1];
                String tmpName = "";
                String tmpAddress = "";
                boolean forName = true;
                boolean forAddress = false;
                for (int j = 2; j < line[2].length(); j++) {
                    if (forName) {
                        if (line[2].charAt(j) == '淨' || line[2].charAt(j) == '(') {
                            forName = false;
                            continue;
                        }
                        tmpName += line[2].charAt(j);
                        continue;
                    }
                    if (!forName && !forAddress) {
                        if (line[2].charAt(j) == ')')
                            forAddress = true;
                        continue;
                    }
                    if (!forName && forAddress) {
                        if (line[2].charAt(j) == '[')
                            break;
                        tmpAddress += line[2].charAt(j);
                    }
                }
                String name = tmpName;
                String address = tmpAddress;
                String Flo = strClear(line[3]);
                String Nit = strClear(line[4]);
                WPFEntity tmp = new WPFEntity(update, area, num, name, address, "0.0", "0.0", Flo, Nit);
                WPFDataBase.getInstance(getBaseContext()).wpfdao().insertData(tmp);
            }
            othersStatus = true;
            Log.d(TAG, "saveWPF: in");
            return;
        //}).start();
    }

    //獲得淨水廠位址並標上地圖
    private void saveWPFLatLon() {
        Log.d(TAG, "saveWPFLatLon: in");
            List<WPFEntity> allWPF = WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll();
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> list = new ArrayList<>();
            Address tempAddress;
            for (int i = 5; i < allWPF.size(); i++) {
                try {
                    //用地址配合Geocode抓經緯度
                    list = geocoder.getFromLocationName(allWPF.get(i).getAddress(), 1);
                    //用地址抓不到經緯度，改用名字
                    while (true) {
                        if (list.size() > 0)
                            break;
                        else {
                            list = geocoder.getFromLocationName(allWPF.get(i).getName(), 1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //在地圖上標上淨水廠位置
                tempAddress = list.get(0);
                WPFDataBase.getInstance(getBaseContext()).wpfdao().updateData(allWPF.get(i).getName()
                        , String.valueOf(tempAddress.getLatitude()), String.valueOf(tempAddress.getLongitude()));
            }
            runOnUiThread(() -> {
                //更新地圖
                mapFragment.onMapReady(mapFragment.map);
                Toast.makeText(getBaseContext(), "地圖資料更新完成", Toast.LENGTH_LONG).show();
            });
            return;
    }

    //存臺北淨水廠資訊
    private void saveWPFTaipei() {
        //new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();
            Request request;
            request = new Request.Builder()
                    .url("https://www.water.gov.taipei/OpenData.aspx?SN=32C9B1E5898ADC58")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //如果傳送過程有發生錯誤
                    System.out.print(e.getMessage());
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //取得回傳
                    resultT = new String(response.body().string().getBytes(), "UTF-8");
                }
            });
            while (true) {
                if (resultT != null)
                    break;
            }
            JSONArray jsonArray;
            String blockText = null;
            try {
                jsonArray= new JSONArray(resultT);
                JSONObject jsonObject = jsonArray.getJSONObject(1);
                blockText = jsonObject.getString("BlockText");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String line[] = blockText.split("\\r\\n\\t\\t\\r\\n\\t\\t\\r\\n\\t\\t\\t");
            String update[] = line[73].split("\\r\\n\\t\\t\\t");
            String updateTime = update[update.length - 2];
            String updateFinal = "";
            for (int i = 5; i < updateTime.length() - 1; i++) {
                if (updateTime.charAt(i) == '-')
                    updateFinal += '/';
                else
                    updateFinal += updateTime.charAt(i);
            }
            String tmpS;
            if (updateFinal.length() == 14) {
                tmpS = updateFinal.substring(0,13) + '0' + updateFinal.charAt(13);
                updateFinal = tmpS;
            }
            //如果為最新資料則跳過
            if (WPFDataBase.getInstance(getBaseContext()).wpfdao().displayAll().size() >= 1
                && new String(WPFDataBase.getInstance(getBaseContext()).wpfdao().findDataById(2).getUpdateTime()).equals(updateFinal)) {
                TaipeiStatus = true;
                return;
            }
            //台北只有5個淨水廠，且無法直接獲得地址，直接手動輸入
            //"直潭淨水場 : 5", "長興淨水場 : 6", "公館淨水場 : 7", "雙溪淨水場 : 8", "陽明淨水場 : 11"
            String name[] = {"直潭", "長興", "公館", "雙溪", "陽明"};
            String latlon[] = {"24.942417543065122, 121.52648658059121", "25.014908255106835, 121.54878615763661", "25.013290653479363, 121.5303265213475"
                    , "25.11402620330964, 121.5692097902704", "25.151208714671203, 121.53224828397104"};
            String address[] = {"新北市新店區直潭路2號", "台北市大安區長興街131號", "台北市中正區思源街1號"
                    ,"台北市士林區至善路三段110號", "台北市北投區泉源路260號"};

            //line[14] 氟鹽
            String flouride[] = saveTaipei(line[14]);
            //line[12] 硝酸鹽氮
            String nitrate[] = saveTaipei(line[12]);

            for (int i = 0; i < 5; i++) {
                String tmpLatLon[] = latlon[i].split(",");
                WPFEntity tmp = new WPFEntity(updateFinal, "臺北區", "\'9999", name[i], address[i], tmpLatLon[0], tmpLatLon[1], flouride[i], nitrate[i]);
                WPFDataBase.getInstance(getBaseContext()).wpfdao().insertData(tmp);
            }
            TaipeiStatus = true;
            Log.d(TAG, "saveWPFTaipei: in");
            return;
        //}).start();
    }

    //處理臺北淨水廠資料用(in saveWPFTaipei())
    private String[] saveTaipei (String line) {
        String re[] = new String[5];
        String tmp[] = line.split("\\r\\n\\t\\t\\t");
        re[0] = tmp[5];
        re[1] = tmp[6];
        re[2] = tmp[7];
        re[3] = tmp[8];
        re[4] = tmp[11];
        return re;
    }

    private String strClear(String line) {
        String r = "";
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '\"' || line.charAt(i) == '=')
                continue;
            r += line.charAt(i);
        }
        return r;
    }
}