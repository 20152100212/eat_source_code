package com.example.main_interface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;

import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main_interface.view.ChangeDatePopwindow;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by ThinkPad on 2017/11/16.
 */

public class SpinnerActivity extends AppCompatActivity {
    private List<String> list = new ArrayList<String>();
    //private TextView myTextView;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;
    private Button selectDate,outofDate,complete,cancel;
    public static boolean judge=false;

    private EditText EtName;
    private EditText EtMonth;
    private EditText EtDay;
    private CheckBox checkBox;
    private EditText ETplace;

    private String Detail="";
    private int Year=-1;
    private int Month=-1;
    private int Day=-1;
    private int outYear=-1;
    private int outMonth=-1;
    private int outDay=-1;
    private int Hour=-1;
    private int Minute=-1;
    private boolean iseveryday=false;

    public static int setAlarm = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordinfo);

        //初始化
        EtName = (EditText) findViewById(R.id.nameET);
        EtMonth = (EditText) findViewById(R.id.monthET);
        EtDay = (EditText) findViewById(R.id.dayET);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        ETplace = (EditText) findViewById(R.id.placeET);
        checkBox.setOnCheckedChangeListener(checkboxlister);

        mySpinner=(Spinner)findViewById(R.id.spinner_kinds);

        selectDate= (Button) findViewById(R.id.selectDate);
        outofDate= (Button) findViewById(R.id.outofDate);

        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!judge) {
                    finish();
                    overridePendingTransition(R.anim.no_anim, R.anim.scale_out);
                }
                else
                {
                    finish();
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }
            }

        });

        complete=(Button)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name=EtName.getText().toString();
                if(name.equals("")){
                    Toast.makeText(SpinnerActivity.this,"名字不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }
                String kinds=mySpinner.getSelectedItem().toString();
                if(kinds.equals("")){
                    Toast.makeText(SpinnerActivity.this,"种类不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }
                String date_p=selectDate.getText().toString();
                if(date_p.equals("")){
                    Toast.makeText(SpinnerActivity.this,"生产日期不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }
                String month=EtMonth.getText().toString();
                String day=EtDay.getText().toString();
                if(month.equals("") && day.equals("")){
                    Toast.makeText(SpinnerActivity.this,"保质期不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }
                int baozhiqi=0;
                try {
                    if(month.equals("")){
                        month="0";
                    }
                    if(day.equals("")){
                        day="0";
                    }
                    baozhiqi=Integer.parseInt(month)*30+Integer.parseInt(day);//计算保质期天数
                    if(baozhiqi<3)
                        MainActivity.outOfDate++;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                //Date produceDate=new Date(selectDate.getText().toString());
                String date_l=outofDate.getText().toString();
                String location=ETplace.getText().toString();
                goods new_good = new goods();
                new_good.setGoodsName(name);
                new_good.setKinds(kinds);
                new_good.setProduceDate(date_p);
                new_good.setExpirationDate(baozhiqi);
                new_good.setClock(setAlarm);
                new_good.setBelongTo(MainActivity.nativeUserName);
                new_good.setLocation(location);
                new_good.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(SpinnerActivity.this,"添加数据成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(SpinnerActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                int good_id=insert(name,kinds,baozhiqi,setAlarm,MainActivity.nativeUserName,location);//获取相应项目主键值
                if(date_l.equals("")&&good_id==-1){
                    Detail=name+" 过期了！";
                    insert_outdate_alarm(Detail,outYear,outMonth,outDay,8,0,good_id,0);//添加对应的过期闹钟到数据库
                    //设定过期闹钟提醒
                    setOutDateAlarm(outYear,outMonth-1,outDay,8,0,-good_id);
                }else{
                    outYear=Year+Integer.parseInt(month)/12+(Month+Integer.parseInt(month))/12;
                    outMonth=(Month+Integer.parseInt(month))%12+(Day+Integer.parseInt(day))/30;
                    outDay=(Day+Integer.parseInt(day))%30;
                    Detail=name+" 过期了！";
                    insert_outdate_alarm(Detail,outYear,outMonth,outDay,8,0,good_id,0);//添加对应的过期闹钟到数据库
                    //设定过期闹钟提醒
                    setOutDateAlarm(outYear,outMonth-1,outDay,8,0,-good_id);
                }
                if(setAlarm==0){

                    if (iseveryday){
                        int myid1=insert_myalarm(Detail,Year,Month,Day,Hour,Minute,-1,1);
                        setEveryDayAlarm(myid1,Hour,Minute);
                    }
                    else{
                        int myid2=insert_myalarm(Detail,Year,Month,Day,Hour,Minute,-1,0);
                        setOnceAlarm(myid2,Hour,Minute);
                    }

                }
                MainActivity.allnum++;
                SpinnerActivity.this.finish();
                overridePendingTransition(R.anim.translate_right,R.anim.translate_out_right);
            }

        });


        //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        list.add("");
        list.add("药品");
        list.add("水果");
        list.add("肉类");
        list.add("零食");
        list.add("饮品");
        list.add("其他");
        /*list.add("乳或乳制品");
        list.add("谷物");
        list.add("肉类");
        list.add("饮品");
        list.add("新鲜蔬果");
        list.add("肉及肉制品");
        list.add("蛋及蛋制品");
        list.add("酒类");
        list.add("特殊营养品");
        list.add("水产品");
        list.add("零食");
        list.add("其他");
        */
        //myTextView = (TextView)findViewById(R.id.TextView_city);
        mySpinner = (Spinner)findViewById(R.id.spinner_kinds);

        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        adapter = new ArrayAdapter<String>(this,R.layout.item_spinselect, list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
               // String a=adapter.getItem(arg2);
                //Toast.makeText(this,R.string.a,Toast.LENGTH_SHORT).show();
              //  myTextView.setText("您选择的是："+ ));
                /* 将mySpinner 显示*/
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
             //   myTextView.setText("NONE");
                arg0.setVisibility(View.VISIBLE);
            }
        });
        /*下拉菜单弹出的内容选项触屏事件处理*/
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                return false;
            }
        });
        /*下拉菜单弹出的内容选项焦点改变事件处理*/
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void selectDate(View v){
        if (v.getId()==R.id.selectDate){
            ChangeDatePopwindow mChangeBirthDialog = new ChangeDatePopwindow(
                    this);
            mChangeBirthDialog.setDate("2018", "1", "1");
            mChangeBirthDialog.showAtLocation(selectDate, Gravity.BOTTOM, 0, 0);
            mChangeBirthDialog.setBirthdayListener(new ChangeDatePopwindow.OnBirthListener() {

                @Override
                public void onClick(String year, String month, String day) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(SpinnerActivity.this,year + "-" + month + "-" + day,Toast.LENGTH_LONG).show();
                    //StringBuilder sb = new StringBuilder();
                    //sb.append(year.substring(0, year.length())).append("-").append(month.substring(0, day.length())).append("-").append(day);
                    //str[0] = year + "-" + month + "-" + day;
                    //selectDate.setText(year + "年 " + month + "月 " + day + "日");
                    selectDate.setText(year + "-" + month + "-" + day);
                    Year=Integer.parseInt(year);
                    Month = Integer.parseInt(month);
                    Day=Integer.parseInt(day);
                }
            });
        }

    }

    //private void selectDate() {
        //final String[] str = new String[1];

        //return str;
    //}

    public void outofDate(View v){
        if (v.getId()==R.id.outofDate){
            // String[] str = new String[10];
            ChangeDatePopwindow mChangeBirthDialog = new ChangeDatePopwindow(
                    this);
            mChangeBirthDialog.setDate("2018", "1", "1");
            mChangeBirthDialog.showAtLocation(outofDate, Gravity.BOTTOM, 0, 0);
            mChangeBirthDialog.setBirthdayListener(new ChangeDatePopwindow.OnBirthListener() {

                @Override
                public void onClick(String year, String month, String day) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(SpinnerActivity.this,year + "-" + month + "-" + day,Toast.LENGTH_LONG).show();
                    //StringBuilder sb = new StringBuilder();
                    //sb.append(year.substring(0, year.length() - 1)).append("-").append(month.substring(0, day.length() - 1)).append("-").append(day);
                    //str[0] = year + "-" + month + "-" + day;
                    //str[1] = sb.toString();
                    //outofDate.setText(year + "年 " + month + "月 " + day + "日");
                    outofDate.setText(year + "-" + month + "-" + day);
                    outYear=Integer.parseInt(year);
                    outMonth=Integer.parseInt(month);
                    outDay=Integer.parseInt(day);
                }
            });
        }

    }

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (!judge) {
            overridePendingTransition(R.anim.no_anim, R.anim.scale_out);
        }
        else
        {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }


    private CheckBox.OnCheckedChangeListener checkboxlister = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            if (checkBox.isChecked()){
                setAlarm=0;
                Intent intent=new Intent(SpinnerActivity.this,addAlarmActivity.class);
                startActivityForResult(intent,2);
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
            else{
                setAlarm=1;
            }
        }
    };


    public int insert(String goods_name,String kinds,int expirationDate,int c,String belongTo,String location) {
        int goodsid=-1;
        MyHelper_record myHelperRecord = new MyHelper_record(SpinnerActivity.this);
        SQLiteDatabase db = myHelperRecord.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goods_name",goods_name);
        values.put("kinds",kinds);
        values.put("baozhiqi",expirationDate);
        values.put("clock",c);
        values.put("belongto",belongTo);
        values.put("location",location);
        //values.put("date_p",produceTime);
        db.insert("goods",null,values);
        //Toast.makeText(SpinnerActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
        Cursor cursor = db.rawQuery("SELECT MAX(goods_id) AS thisgood FROM goods",null);
        while(cursor.moveToNext()){
            goodsid = cursor.getInt(cursor.getColumnIndex("thisgood"));
        }
        cursor.close();
        db.close();
        return goodsid;
    }
    //添加过期提醒
    public void insert_outdate_alarm(String detail,int year,int month,int day,int hour,int minute,int goods_id,int everyday){
        MyHelper_record myHelperalarm=new MyHelper_record(SpinnerActivity.this);
        SQLiteDatabase db = myHelperalarm.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("alarm_details",detail);
        values.put("alarm_time_year",year);
        values.put("alarm_time_month",month);
        values.put("alarm_time_day",day);
        values.put("alarm_time_hour",hour);
        values.put("alarm_time_min",minute);
        values.put("goods_id",goods_id);
        values.put("everyday",everyday);
        //values.put("date_p",produceTime);
        db.insert("alarm",null,values);
        //Toast.makeText(SpinnerActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
        db.close();
    }
    //添加自定义闹钟
    public int insert_myalarm(String detail,int year,int month,int day,int hour,int minute,int goods_id,int everyday){
        MyHelper_record myHelperalarm=new MyHelper_record(SpinnerActivity.this);
        int myalarmid=-1;
        SQLiteDatabase db = myHelperalarm.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("alarm_details",detail);
        values.put("alarm_time_year",year);
        values.put("alarm_time_month",month);
        values.put("alarm_time_day",day);
        values.put("alarm_time_hour",hour);
        values.put("alarm_time_min",minute);
        values.put("goods_id",goods_id);
        values.put("everyday",everyday);
        //values.put("date_p",produceTime);
        db.insert("alarm",null,values);
        Cursor cursor = db.rawQuery("SELECT MAX(alarm_id) AS id FROM alarm",null);
        while(cursor.moveToNext()){
            myalarmid = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        //Toast.makeText(SpinnerActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
        db.close();
        return myalarmid;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode) {
            case 0:
                checkBox.setChecked(false);
                break;
            case 2:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Detail = bundle.getString("alarm_details");
                        Hour = bundle.getInt("alarm_time_hour");
                        Minute = bundle.getInt("alarm_time_min");
                        iseveryday = bundle.getBoolean("isornoteveryday");
                    }
                }
                break;
            default:
                //其它窗口的回传数据
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setOutDateAlarm(int Year,int Mon,int Day,int Hour,int Min,int key){
        Calendar myCal1 = Calendar.getInstance();
        long     nowTime  = myCal1.getTimeInMillis();//这是当前的时间
        Calendar myCal2 = Calendar.getInstance();
        //      myCal.set(Calendar.HOUR_OF_DAY,hour);
        //      myCal.set(Calendar.MINUTE,minutes);
        myCal2.set(Year, Mon, Day, Hour, Min);
        long    shutDownTime = myCal2.getTimeInMillis();
        Intent intent3=new Intent(this,AlarmActivity.class);
        intent3.putExtra("Detail",Detail);
        PendingIntent pi3=PendingIntent.getActivity(this, key, intent3,0);
        //设置一个PendingIntent对象，发送广播
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        //获取AlarmManager对象
        long triggerAtTime = SystemClock.elapsedRealtime() + shutDownTime-nowTime;
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime , pi3);
        //Toast.makeText(SpinnerActivity.this, "设置过期闹铃成功!", Toast.LENGTH_SHORT).show();
    }
    public void setEveryDayAlarm(int key,int h,int m){
        Intent intent = new Intent(SpinnerActivity.this, AlarmActivity.class);
        intent.putExtra("Detail",Detail);
        PendingIntent sender = PendingIntent.getActivity(SpinnerActivity.this, key, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 进行闹铃注册
        AlarmManager myalarm;
        myalarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        myalarm.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,sender);

        Toast.makeText(SpinnerActivity.this, "设置每日闹铃成功!", Toast.LENGTH_SHORT).show();
    }
    public void setOnceAlarm(int key,int h,int m){
        Intent intent = new Intent(SpinnerActivity.this, AlarmActivity.class);
        intent.putExtra("Detail",Detail);
        PendingIntent sender = PendingIntent.getActivity(SpinnerActivity.this, key, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 进行闹铃注册
        AlarmManager myalarm;
        myalarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        myalarm.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);

        Toast.makeText(SpinnerActivity.this, "设置单次闹铃成功!", Toast.LENGTH_SHORT).show();
    }
}
