package com.example.main_interface;

import cn.bmob.v3.BmobObject;

/**
 * Created by 赵书妍 on 2017/12/28.
 */

public class userdata extends BmobObject{
    private String userName;                  //用户名
    private String userPwd;                   //用户密码
    private String userNickName;              //用户昵称
    //private int userId;                       //用户ID号
    //public int pwdresetFlag=0;

    public userdata(){}

    public userdata(String userName, String userPwd,String userNickName) {  //这里只采用用户名和密码
        this.userName = userName;
        this.userPwd = userPwd;
        this.userNickName = userNickName;
    }
    //获取用户名
    public String getUserName() {             //获取用户名
        return userName;
    }
    //设置用户名
    public void setUserName(String userName) {  //输入用户名
        this.userName = userName;
    }
    //获取用户密码
    public String getUserPwd() {                //获取用户密码
        return userPwd;
    }
    //设置用户密码
    public void setUserPwd(String userPwd) {     //输入用户密码
        this.userPwd = userPwd;
    }

    //获取用户昵称
    public String getUserNickName() {                   //获取用户ID号
        return userNickName;
    }
    //设置用户i昵称
    public void setUserNickName(String usernickname){       //设置用户ID号
        this.userNickName = usernickname;
    }

}
