package com.example.main_interface;

/**
 * Created by 赵书妍 on 2017/12/30.
 */

public class findID {
    private String ID_name;
    private int id;
    findID(String s){
        ID_name=s;
        id=-1;
    }
    public int getID(){
        switch (ID_name){
            case "药品":id=R.drawable.ic_drugs;break;
            case "水果":id=R.drawable.ic_fruits;break;
            case "肉类":id=R.drawable.ic_meat;break;
            case "零食":id=R.drawable.ic_cake;break;
            case "饮品":id=R.drawable.ic_drink;break;
            default:break;
        }
        return id;
    }
}
