package com.example.android.fetchdata.dataBase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "WPFTable")
public class WPFEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String updateTime;
    private String lat;
    private String lon;
    private String area;
    private String num; //'9999 代表臺北 (臺北沒有編號)
    private String name;
    private String address;
    //可更換為需要水質資訊
    private String Fluoride; //氟鹽
    private String Nitrate; //硝酸鹽氮

    public WPFEntity(String updateTime ,String area, String num, String name, String address, String lat, String lon, String Fluoride, String Nitrate) {
        this.updateTime = updateTime;
        this.area = area;
        this.num = num;
        this.name = name;
        this.address = address;
        this.Fluoride = Fluoride;
        this.Nitrate = Nitrate;
        this.lat = lat;
        this.lon = lon;
    }

    @Ignore
    public WPFEntity(int id, String updateTime, String area, String num, String name, String address, String lat, String lon, String Fluoride, String Nitrate) {
        this.id = id;
        this.updateTime = updateTime;
        this.area = area;
        this.num = num;
        this.name = name;
        this.address = address;
        this.Fluoride = Fluoride;
        this.Nitrate = Nitrate;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFluoride() {
        return Fluoride;
    }

    public void setFluoride(String fluoride) {
        Fluoride = fluoride;
    }

    public String getNitrate() {
        return Nitrate;
    }

    public void setNitrate(String nitrate) {
        Nitrate = nitrate;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
