package com.example.main_interface;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2017/12/29.
 */

public class applyToFG extends BmobObject {

    private String applyId;private String applyNickName;
    private String acceptId;
    private boolean acceptOrNot;
    private int shareOrNot;

    public  applyToFG(){}

    public applyToFG(String applyid,String acceptid,boolean judge){
        this.acceptId=acceptid;
        this.applyId=applyid;
        this.acceptOrNot=judge;
    }
    public String getAcceptId() {
        return acceptId;
    }

    public String getApplyId() {
        return applyId;
    }

    public String getApplyNickName() {
        return applyNickName;
    }

    public void setApplyNickName(String applyNickName) {
        this.applyNickName = applyNickName;
    }

    public Boolean getAcceptOrNot(){ return acceptOrNot;}

    public void setAcceptOrNot(Boolean acceptornot) {
        this.acceptOrNot= acceptornot;
    }

    public int getShareOrNot() {
        return shareOrNot;
    }

    public void setShareOrNot(int shareOrNot) {
        this.shareOrNot = shareOrNot;
    }


}
