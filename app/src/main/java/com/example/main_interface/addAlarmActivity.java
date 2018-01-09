package com.example.main_interface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.main_interface.view.ChangeDatePopwindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class addAlarmActivity extends AppCompatActivity {

    private EditText detail;
    private TimePicker my_alarm_time;
    private Switch swt_everyday;
    private static final int INTERVAL = 1000 * 5;// 24h
    private AlarmManager myalarm;
    private int hour=-1;
    private int minutes=-1;
    private boolean alarm_everyday=false;
    private MyHelper_record myHelperalarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        myHelperalarm=new MyHelper_record(addAlarmActivity.this);
        ImageButton btn_back = findViewById(R.id.goback);
        ImageButton btn_fin=findViewById(R.id.set_complete);
        detail=(EditText)findViewById(R.id.alarm_delail);
        my_alarm_time=(TimePicker)findViewById(R.id.alarm_timePicker);
        swt_everyday=(Switch)findViewById(R.id.alarm_everyday) ;
        // 获取当前时间
        Calendar systime = Calendar.getInstance();
        systime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        if(hour == -1 && minutes == -1) {
            hour = systime.get(Calendar.HOUR_OF_DAY);
            minutes = systime.get(Calendar.MINUTE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            my_alarm_time.setHour(hour);//设置当前小时为系统时间
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            my_alarm_time.setMinute(minutes);//设置当前分钟为系统时间
        }
        //获取设置的分钟和秒
        //hour=myclock.getHour();
        //minutes=myclock.getMinute();
        my_alarm_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour=hourOfDay;
                minutes=minute;
            }
        });
        //点击返回
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Intent intent=new Intent(addAlarmActivity.this,SpinnerActivity.class);
                addAlarmActivity.this.setResult(0,intent);
                addAlarmActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        //点击完成
        btn_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.add_alarm_from_main==true){
                    if(swt_everyday.isChecked()){
                        setEveryDayAlarm();
                        alarm_everyday=true;
                    }else{
                        setOnceAlarm();
                        alarm_everyday=false;
                    }
                }
                else{
                    if(SpinnerActivity.setAlarm==0){
                        Intent intent=new Intent(addAlarmActivity.this,SpinnerActivity.class);
                        //传数据到MainActivity
                        String de=detail.getText().toString();
                        if(de.equals("")){
                            Toast.makeText(addAlarmActivity.this,"名字不能为空",Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        intent.putExtra("alarm_details",de);
                        intent.putExtra("alarm_time_hour",hour);
                        intent.putExtra("alarm_time_min",minutes);
                        intent.putExtra("isornoteveryday",alarm_everyday);
                        addAlarmActivity.this.setResult(2,intent);
                        //Toast.makeText(SpinnerActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
                        addAlarmActivity.this.finish();
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    }
                }
                finish();
            }
        });

    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
    public void setEveryDayAlarm(){
        Intent intent = new Intent(addAlarmActivity.this, AlarmActivity.class);
        intent.putExtra("Detail",detail.getText().toString());
        PendingIntent sender = PendingIntent.getActivity(addAlarmActivity.this, 1, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 进行闹铃注册
        myalarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        myalarm.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,sender);

        Toast.makeText(addAlarmActivity.this, "设置每日闹铃成功!", Toast.LENGTH_SHORT).show();
    }
    public void setOnceAlarm(){
        Intent intent = new Intent(addAlarmActivity.this, AlarmActivity.class);
        intent.putExtra("Detail",detail.getText().toString());
        PendingIntent sender = PendingIntent.getActivity(addAlarmActivity.this, 1, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 进行闹铃注册
        myalarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        myalarm.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);

        Toast.makeText(addAlarmActivity.this, "设置单次闹铃成功!", Toast.LENGTH_SHORT).show();
    }

}
