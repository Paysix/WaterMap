package com.example.android.fetchdata.recycleView;

import com.example.android.fetchdata.WPFEntity;

import java.util.Comparator;

public class WPFEntityComparator implements Comparator<WPFEntity> {
    @Override
    public int compare(WPFEntity t1, WPFEntity t2) {
        String[] cities = {"基隆市", "新北市", "桃園市", "新竹縣", "新竹市", "苗栗縣", "臺中市",
                "彰化縣", "南投縣", "雲林縣", "嘉義縣", "嘉義市", "臺南市", "高雄市",
                "屏東縣", "澎湖縣", "宜蘭縣", "花蓮縣", "臺東縣"};
        int o1 = 0;
        int o2 = 0;
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].equals(t1.getCity())) {
                o1 = i;
                break;
            }
        }
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].equals(t2.getCity())) {
                o2 = i;
                break;
            }
        }
        return Integer.compare(o1, o2);
    }
}
