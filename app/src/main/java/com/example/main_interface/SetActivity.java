package com.example.main_interface;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.VersionInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class SetActivity extends AppCompatActivity {

    private TextView mUserName,mUserNickName;
    private String userName,userNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);


        mUserName=(TextView)findViewById(R.id.et_username);
        mUserNickName=(TextView)findViewById(R.id.et_usernickname);
        ImageButton btn_back=findViewById(R.id.goback);
        Button btn_logout=findViewById(R.id.logout);
        Button reset_nickname=(Button)findViewById(R.id.reset_nickname);

        Bundle bundle = this.getIntent().getExtras();
        userName = bundle.getString("username");
        userNickName = bundle.getString("usernickname");
        mUserName.setText(userName);
        mUserNickName.setText(userNickName);

        Bmob.initialize(this, "22ca2ace7668b50b45d61fe71f5d3f8d");
        /*BmobQuery<userdata> query =new BmobQuery<userdata>();
        query.addWhereEqualTo("userName",userName);
        query.setLimit(1);
        query.findObjects(new FindListener<userdata>() {
            @Override
            public void done(List<userdata> list, BmobException e) {
                if (e == null && list.size() > 0) {

                }
            }
        });
        */


        //点击返回
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(SetActivity.this,MainActivity.class);
                //startActivity(intent);
                Intent intent = new Intent(SetActivity.this, MainActivity.class);
                intent.putExtra("new_nickname",mUserNickName.getText().toString());
                intent.putExtra("logoutOrNot","false");
                SetActivity.this.setResult(3,intent);
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });
        //点击退出登录
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        reset_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(SetActivity.this);
                editText.setText(userNickName);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(SetActivity.this);
                inputDialog.setTitle("输入新昵称").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BmobQuery<userdata> query =new BmobQuery<userdata>();
                                query.addWhereEqualTo("userName",userName);
                                query.setLimit(1);
                                query.findObjects(new FindListener<userdata>() {
                                    @Override
                                    public void done(List<userdata> list, BmobException e) {
                                        if (e == null) {
                                            for(userdata user:list){
                                                userdata mUser = new userdata();
                                                mUser.setUserNickName(editText.getText().toString());
                                                mUser.update(user.getObjectId() , new UpdateListener() {
                                                @Override
                                                    public void done(BmobException e) {
                                                        if(e==null) {
                                                            Toast.makeText(SetActivity.this,getString(R.string.resetnickname_success),Toast.LENGTH_SHORT).show();
                                                            mUserNickName.setText(editText.getText().toString());
                                                            userNickName=editText.getText().toString();
                                                        }
                                                        else
                                                            Toast.makeText(SetActivity.this,getString(R.string.resetnickname_fail),Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } else{
                                            Toast.makeText(SetActivity.this,getString(R.string.resetnickname_fail),Toast.LENGTH_SHORT).show();
                                            Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                                        }
                                    }
                                });
                            }
                        }).show();

            }
        });
    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

}
