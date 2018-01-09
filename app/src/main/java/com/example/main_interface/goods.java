package com.example.main_interface;

import cn.bmob.v3.BmobObject;

/**
 * Created by 赵书妍 on 2018/1/3.
 */

public class goods extends BmobObject {

    private String goodsName,kinds,location,produceDate;
    private int expirationDate;
    private int clock;
    private int isMine;
    private String belongTo;

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setProduceDate(String pdate){this.produceDate = pdate;}

    public String getKinds() {
        return kinds;
    }

    public void setKinds(String kinds) {
        this.kinds = kinds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(int expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getProduceDate(){return produceDate;}

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public int getIsMine() {
        if(belongTo.equals(MainActivity.nativeUserName))
            return 1;
        else
            return 0;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
    }





}
