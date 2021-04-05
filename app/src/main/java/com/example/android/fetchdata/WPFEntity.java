package com.example.android.fetchdata;


public class WPFEntity {
    private String time;
    private String name; //2
    private String number; //1
    private String city; //2
    private String address; //2
    private String lat;
    private String lon;
    //檢測項目
    /*
    private String Fluoride;
    private String Nitrate;
    private String Total_Dissolved_Solids;
    private String Fecal_Coliform;
    private String Arsenic;
    private String Free_Chlorine_Residua;
    private String Turbidity;
    private String Color;
    private String Odor;
    private String Total_Alkalinity;
    private String pH;
    private String Chloride;
    private String Sulfate;
    private String Ammonia;
    private String Nitrite;
    private String Total_Hardness;
    private String Iron;
    private String Manganese;
    private String Total_Bacterial_Count;
    private String Coliform_Group;
    private String Total_Trihalomethanes;
    private String Lead;
    private String Selenium;
    private String Mercury;
    private String Zinc;
    private String Silver;
    private String Copper;
    private String Chromium;
    private String Nickel;
    private String Cadmium;
    private String Barium;
    private String Antimony;
    private String Aluminium;
    private String Haloacetic_acids;
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
