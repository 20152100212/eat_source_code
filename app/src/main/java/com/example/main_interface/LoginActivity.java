package com.example.main_interface;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity {
    public static boolean judge=true;
    Button log;//登录按钮
    Button forget;//忘记密码按钮
    Button sign;//注册按钮
    EditText et_number;//用户名编辑
    EditText et_password;//密码编辑

    private userdata user1 = new userdata();
    private String userName,userPwd;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bmob.initialize(this, "22ca2ace7668b50b45d61fe71f5d3f8d");

        ImageButton btn_back = findViewById(R.id.goback);

        View.OnClickListener mListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_register:                            //登录界面的注册按钮
                        Intent intent_Login_to_Register = new Intent(LoginActivity.this, register.class);    //切换Login Activity注册界面
                        startActivity(intent_Login_to_Register);
                        finish();
                        break;
                    case R.id.btn_login:                              //登录界面的登录按钮
                        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //切换Login Activity至User Activity
                        intent.putExtra("usernickname","aaa");
                        intent.putExtra("username","ltj");
                        startActivity(intent);
                        //Toast.makeText(SpinnerActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
                        LoginActivity.this.finish();
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                        Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();//登录成功提示*/
                        login();
                        break;
                    case R.id.tv_forget:                             //登录界面的忘记密码  跳转到修改密码界面
                        Intent intent_Login_to_reset = new Intent(LoginActivity.this, resetpassword.class);    //切换Login Activity至User Activity
                        startActivity(intent_Login_to_reset);
                        finish();
                        break;
                }

            }
        };

        //mRememberCheck = (CheckBox) findViewById(R.id.remeber_checkbox);//是否记住密码勾选
        log=(Button)findViewById(R.id.btn_login);//登录按钮
        forget=(Button )findViewById(R.id.tv_forget);//忘记密码按钮
        sign=(Button)findViewById(R.id.tv_register);//注册用户按钮
        et_number = (EditText) findViewById(R.id.et_number);
        et_password = (EditText) findViewById(R.id.et_password);

        sign.setOnClickListener(mListener);
        log.setOnClickListener(mListener);


        //点击返回
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(judge){
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                else{
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }

            }
        });
    }

    public void login() {//登录按钮监听事件

        if (isUserNameAndPwdValid()) {
            userName =et_number .getText().toString().trim();    //获取当前输入的用户名和密码信息
            userPwd = et_password.getText().toString().trim();
            BmobQuery<userdata> query =new BmobQuery<userdata>();
            query.addWhereEqualTo("userName",userName);
            query.setLimit(1);
            query.findObjects(new FindListener<userdata>() {
                @Override
                public void done(List<userdata> list, BmobException e) {
                    if(e==null && list.size()>0){
                        for(userdata user:list) {
                            if (user.getUserPwd().equals(userPwd)) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);    //切换Login Activity至User Activity
                                intent.putExtra("usernickname",user.getUserNickName());
                                intent.putExtra("username",user.getUserName());
                                startActivity(intent);
                                LoginActivity.this.finish();
                                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();//登录成功提示
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if(list.size()<=0) {
                        Toast.makeText(LoginActivity.this, getString(R.string.name_not_exist), Toast.LENGTH_SHORT).show();  //登录失败提示
                    }else
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            });
        }
    }
    public boolean isUserNameAndPwdValid() {
        if (et_number.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (et_password.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(judge){
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else{
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }

}
