package com.example.main_interface;

/**
 * Created by 赵书妍 on 2017/12/28.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import  android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class resetpassword extends AppCompatActivity {

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd_old;                        //密码编辑
    private EditText mPwd_new;                        //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    Button mSureButton;                       //确定按钮
    Button mCancelButton;                     //取消按钮
    private int result=0;
    private String object_id;
    private String userName,userPwd_old,userPwd_new,userPwdCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_resetpwd);
        Bmob.initialize(this, "22ca2ace7668b50b45d61fe71f5d3f8d");
        mAccount = (EditText) findViewById(R.id.username);
        mPwd_old = (EditText) findViewById(R.id.et_oldpwd);
        mPwd_new = (EditText) findViewById(R.id.et_newpwd);
        mPwdCheck = (EditText) findViewById(R.id.et_newpsd_again);//重新确认密码
        mSureButton = (Button) findViewById(R.id.reset_ok);//确认修改
        mCancelButton = (Button) findViewById(R.id.reset_cancel);
        mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_resetpwd_Listener);
    }

    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reset_ok:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
                case R.id.reset_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Resetpwd_to_Login = new Intent(resetpassword.this, LoginActivity.class);    //切换Resetpwd Activity至Login Activity
                    startActivity(intent_Resetpwd_to_Login);
                    finish();
                    break;
            }
        }
    };

    public void resetpwd_check() {
        if (isUserNameAndPwdValid()) {
            userName = mAccount.getText().toString().trim();
            userPwd_old = mPwd_old.getText().toString().trim();
            userPwd_new = mPwd_new.getText().toString().trim();
            userPwdCheck = mPwdCheck.getText().toString().trim();
            BmobQuery<userdata> query =new BmobQuery<userdata>();
            query.addWhereEqualTo("userName",userName);
            query.setLimit(1);
            query.findObjects(new FindListener<userdata>() {
                @Override
                public void done(List<userdata> list, BmobException e) {
                    if(e==null && list.size()>0 ){
                        for(userdata user:list) {
                            if (user.getUserPwd().equals(userPwd_old)) {
                                if (!userPwd_new.equals(userPwdCheck)) {           //两次密码输入不一样
                                    Toast.makeText(resetpassword.this, getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                                    //return;
                                } else {
                                    userdata mUser = new userdata(userName, userPwd_new, user.getUserNickName());
                                    mUser.update(user.getObjectId() , new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                Toast.makeText(resetpassword.this,getString(R.string.resetpwd_success),Toast.LENGTH_SHORT).show();
                                                Intent intent_Register_to_Login = new Intent(resetpassword.this, LoginActivity.class);    //切换User Activity至Login Activity
                                                startActivity(intent_Register_to_Login);
                                                finish();
                                            }else{
                                                Toast.makeText(resetpassword.this,getString(R.string.resetpwd_fail),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(resetpassword.this, getString(R.string.pwd_not_fit_user), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else if (list.size() <= 0) {
                        Toast.makeText(resetpassword.this, getString(R.string.name_not_exist),Toast.LENGTH_SHORT).show();  //登录失败提示
                    }else{
                        Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    public boolean isUserNameAndPwdValid() {
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
