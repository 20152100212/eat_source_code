package com.example.main_interface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dell on 2017/12/18.
 */

public class MyHelper_record extends SQLiteOpenHelper{
    public static final String CRETE_GOODS="create table goods("+
    //primary key 将goods_id列设为主键    autoincrement表示id列是自增长的
    "goods_id integer primary key autoincrement,"+
    "goods_name varchar(20),"+
    "kinds varchar(20),"+
    "date_p varchar(20),"+
    //"date_l date,"+
    "baozhiqi integer,"+
    "clock integer,"+
    //"clo_time timestamp,"+
    "location text,"+
    "belongto text)";
    //"username text)";
    //创建goods表
    public static final String CRETE_ALARM="create table alarm("+
            //primary key 将goods_id列设为主键    autoincrement表示id列是自增长的
            "alarm_id integer primary key autoincrement,"+
            "goods_id integer,"+
            "everyday integer,"+
            "alarm_details text,"+
            "alarm_time_year integer,"+
            "alarm_time_month integer,"+
            "alarm_time_day integer," +
            "alarm_time_hour integer," +
            "alarm_time_min integer)";

    public MyHelper_record(Context context){
        //创建数据库Record.db
        super(context, "Record.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRETE_GOODS);
        db.execSQL(CRETE_ALARM);
    }
    //更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + "goods" );
        db.execSQL( "DROP TABLE IF EXISTS " + "alarm" );
        onCreate(db);
        Log. e("Database" ,"onUpgrade" );
    }


}
