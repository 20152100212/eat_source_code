package com.example.main_interface;

/**
 * Created by lenovo on 2017/11/21.
 */

import java.util.ArrayList;
import java.util.List;

public class Item {

    private String name;
    private int imageId;
    private String location;
    private int lasts;
    private int clock;
    private int isMine;


    public Item(String name, int imageId, String loc, int lasts,int c,int ismine) {
        this.name = name;
        this.imageId = imageId;
        this.location = loc;
        this.lasts = lasts;
        this.clock = c;
        this.isMine = ismine;
    }

    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<Item>();
        //items.add(new Item("面包",R.mipmap.ic_bread,"1盒",""));
        //items.add(new Item("感冒灵", R.mipmap.ic_ganmaoling,"1盒",""));
        //items.add(new Item("披萨",R.drawable.ic_pizza,"1份","1天"));
        //items.add(new Item("咖啡", R.drawable.ic_cafe,"1盒","6月"));

        return items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String loc) {
        this.location = loc;
    }

    public int getLasts(){
        return lasts;
    }

    public void setLasts(int lasts){
        this.lasts = lasts;
    }

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

