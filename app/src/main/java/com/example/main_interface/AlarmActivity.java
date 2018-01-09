package com.example.main_interface;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {
    public  static final int PLAY_MUSIC=1;
    public  static final int PAUSE_MUSIC=2;
    public  static final int STOP_MUSIC=3;

    private Button iknow;
    private TextView detail;
    private PlayAlarmReciever receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alarm);
        receiver=new PlayAlarmReciever();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.complete");
        registerReceiver(receiver,filter);
        detail=(TextView)findViewById(R.id.detail);
        iknow=(Button)findViewById(R.id.IKnow);
        iknow.setOnClickListener(new Click());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String Detail = bundle.getString("Detail");
            detail.setText(Detail);
        }
        playingmusic(PLAY_MUSIC);
    }
    class Click implements View.OnClickListener{

        public void onClick(View view){
            switch (view.getId()){
                //开始音乐
            /*case R.id.btn_startmusic:
                playingmusic(PLAT_MUSIC);
                break;
            //暂停
            case R.id.btn_pausemusic:
                playingmusic(PAUSE_MUSIC);
                break;*/
                //停止
                case R.id.IKnow:
                    playingmusic(STOP_MUSIC);
                    finish();
                    break;
            }
        }
    }


    private void playingmusic(int type) {
        //启动服务，播放音乐
        Intent intent=new Intent(this,PlayAlarmService.class);
        intent.putExtra("type",type);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
