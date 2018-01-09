package com.example.main_interface;

/**
 * Created by 赵书妍 on 2017/12/20.
 */

public class Item_list {
    private String name;
    private int imageId;
    private String location;
    private int lasts;
    private int clock;
    private int isMine;

    public Item_list(String na,int id,String loc,int la,int c,int ismine){
        name=na;
        imageId=id;
        location=loc;
        lasts=la;
        clock=c;
        this.isMine = ismine;
    }
    public String getName(){
        return name;
    }

    public int getImageId(){ return imageId;}

    public String getLocation(){return location;}

    public int getLasts() {return lasts;}

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public int getIsMine() {
        return isMine;
    }

    public void setIsMine(int isMine) {
        this.isMine = isMine;
    }


}
