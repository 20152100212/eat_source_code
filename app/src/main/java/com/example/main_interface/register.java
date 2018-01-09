package com.example.main_interface;

/**
 * Created by 赵书妍 on 2017/12/28.
 */

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class register extends AppCompatActivity{
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private EditText mNickName;                       //昵称编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private int result=0;                              //用于判断用户是否存在
    private String object_id;
    private String userName,userPwd,userPwdCheck,userNickName;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Bmob.initialize(this, "22ca2ace7668b50b45d61fe71f5d3f8d");

        mAccount = (EditText) findViewById(R.id.register_username);
        mPwd = (EditText) findViewById(R.id.register_password);
        mPwdCheck = (EditText) findViewById(R.id.pwd_again);
        mNickName = (EditText)findViewById(R.id.register_usernickname);

        mSureButton = (Button) findViewById(R.id.register_sign);
        mCancelButton = (Button) findViewById(R.id.register_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_sign:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(register.this,LoginActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };
    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            userName = mAccount.getText().toString().trim();
            userPwd = mPwd.getText().toString().trim();
            userPwdCheck = mPwdCheck.getText().toString().trim();
            userNickName = mNickName.getText().toString().trim();
            //检查用户是否存在
            BmobQuery<userdata> query =new BmobQuery<userdata>();
            query.addWhereEqualTo("userName",userName);
            query.setLimit(1);
            query.findObjects(new FindListener<userdata>() {
                @Override
                public void done(List<userdata> list, BmobException e) {
                    if(e==null && list.size()>0 ){
                        Toast.makeText(register.this, getString(R.string.name_already_exist),Toast.LENGTH_SHORT).show();
                    }else if (e==null &&list.size() <= 0) {
                        if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
                            Toast.makeText(register.this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
                            return ;
                        } else {
                            userdata mUser = new userdata(userName, userPwd, userNickName);
                            mUser.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Intent intent = new Intent(register.this, LoginActivity.class);    //切换Login Activity至User Activity
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                        Toast.makeText(register.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(register.this, getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }
    public boolean isUserNameAndPwdValid() {
        if (mNickName.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.usernickname_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}