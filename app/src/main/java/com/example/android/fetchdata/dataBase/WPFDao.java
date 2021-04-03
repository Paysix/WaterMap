package com.example.android.fetchdata.dataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WPFDao {

    String tableName = "WPFTable";

    //簡單新增淨水廠資訊(直接創建一個WPFEntity並傳入)
    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    void insertData(WPFEntity wpfEntity);

    //新增淨水廠資訊(直接傳入淨水廠資訊)
    @Query("INSERT INTO "+tableName+"(updateTime,area,num,name,address,lat,lon,Fluoride,Nitrate) VALUES(:updateTime,:area,:num,:name,:address,:lat,:lon,:Fluoride,:Nitrate)")
    void insertData(String updateTime, String area,String num,String name,String address, String lat, String lon,String Fluoride, String Nitrate);

    //獲取整個Database，用List<WPFEntity>存
    @Query("SELECT * FROM " + tableName)
    List<WPFEntity> displayAll();

    //獲取單個淨水廠資訊，用id搜尋
    @Query("SELECT * FROM " + tableName +" WHERE id = :id")
    WPFEntity findDataById(int id);

    //獲取單個淨水廠資訊，用name搜尋
    @Query("SELECT * FROM " + tableName +" WHERE name = :name")
    WPFEntity findDataByName(String name);

    //更新淨水廠資訊(直接創建一個WPFEntity並傳入)
    @Update
    void updateData(WPFEntity wpfEntity);

    //更新單個淨水廠資訊，用名字搜尋
    @Query("UPDATE "+tableName+" SET lat=:lat,lon=:lon WHERE name = :name" )
    void updateData(String name,String lat, String lon);

    //刪除淨水廠資訊
    @Delete
    void deleteData(WPFEntity wpfEntity);

    //刪除單個淨水廠資訊，用id搜尋
    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
    void deleteData(int id);
}
