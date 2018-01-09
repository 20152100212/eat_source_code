package com.example.main_interface;

//import android.content.res.Resources;
//import android.content.res.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ImageButton;

import android.view.Window;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class Activity_itemlist extends AppCompatActivity {

    private Button search;
    private ImageButton addItem,goback;

    private TextView title;

    private SQLiteDatabase db;
    private ArrayList<Item_list> itemlist;
    private MyHelper_record myHelperRecord;
    private ItemAdapter itemAdapter;
    private ListView listView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //创建数据库
            myHelperRecord=new MyHelper_record(Activity_itemlist.this);
            //打开数据库
            db=myHelperRecord.getReadableDatabase();
            itemlist=new ArrayList<>();
            String sqlStatement = "";
            switch (item.getItemId()) {
                case R.id.navigation_allitem:
                    Toast.makeText(Activity_itemlist.this,"全部",Toast.LENGTH_SHORT).show();
                    sqlStatement = "select * from goods order by baozhiqi asc";
                    display(sqlStatement);
                    return true;
                case R.id.navigation_food:
                    Toast.makeText(Activity_itemlist.this,"食品",Toast.LENGTH_SHORT).show();
                    sqlStatement = "select * from goods where kinds='水果' " +
                            "UNION select * from goods where kinds='肉类'" +
                            "UNION select * from goods where kinds='饮品'" +
                            "UNION select * from goods where kinds='零食'" +
                            "order by baozhiqi asc";
                    display(sqlStatement);
                    return true;
                case R.id.navigation_medicine:
                    Toast.makeText(Activity_itemlist.this,"药品",Toast.LENGTH_SHORT).show();
                    //扫描数据库,将数据库信息放入
                    sqlStatement = "select * from goods where kinds='药品'order by baozhiqi asc";
                    display(sqlStatement);
                    return true;
                case R.id.navigation_reminding:
                    Toast.makeText(Activity_itemlist.this,"提醒项",Toast.LENGTH_SHORT).show();
                    sqlStatement = "select * from goods where clock=0 order by baozhiqi asc";
                    display(sqlStatement);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofitem);

        itemAdapter = new ItemAdapter(this, R.layout.item_item, Item.getAllItems());
        listView = (ListView) findViewById(R.id.list_item);
        listView.setAdapter(itemAdapter);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "设置提醒");
                menu.add(0, 1, 0, "删除该条");
            }
        });

        search=(Button)findViewById(R.id.search);
        search.setOnClickListener(new MyListener());

        goback=(ImageButton)findViewById(R.id.goback);
        goback.setOnClickListener(new MyListener());

        addItem=(ImageButton)findViewById(R.id.additem);
        addItem.setOnClickListener(new MyListener());

        title=(TextView)findViewById(R.id.listofitem_titlebar) ;

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //创建数据库
        myHelperRecord=new MyHelper_record(Activity_itemlist.this);
        //打开数据库
        db=myHelperRecord.getReadableDatabase();
        itemlist=new ArrayList<>();
        //扫描数据库,将数据库信息放入
        display("select * from goods order by baozhiqi asc");
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //info.id得到listview中选择的条目绑定的id
        String id = String.valueOf(info.id);
        int pos=info.position;
        View itemView = info.targetView;

        switch (item.getItemId()) {
            case 0:
                return true;
            case 1:
                //System.out.println("删除"+info.id);
                TextView tv_name= (TextView) itemView.findViewById(R.id.item_name);
                TextView tv_location= (TextView) itemView.findViewById(R.id.item_location);
                TextView tv_lasts= (TextView) itemView.findViewById(R.id.item_lasts);
                String goods_name=tv_name.getText().toString();
                String goods_location=tv_location.getText().toString();
                String baozhiqi=tv_lasts.getText().toString();
                int goods_expirationDate=Integer.parseInt(baozhiqi);
                MyHelper_record dbhelper = new MyHelper_record(Activity_itemlist.this);
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                //db.execSQL("delete from goods where good_name=?, location=?, date_p=?, belongto=?",
                //        new Object[]{goods_name, goods_location, goods_expirationDate,MainActivity.nativeUserName});
                db.execSQL("delete from goods where goods_name='"+goods_name+"' and location='"+goods_location+
                        "' and baozhiqi="+baozhiqi+" and belongto='"+MainActivity.nativeUserName+"'");
                BmobQuery<goods> query = new BmobQuery<goods>();
                query.addWhereEqualTo("goodsName", goods_name);
                query.addWhereEqualTo("expirationDate", goods_expirationDate);
                query.addWhereEqualTo("location", goods_location);
                query.addWhereEqualTo("belongTo", MainActivity.nativeUserName);
                query.setLimit(50);
                query.findObjects(new FindListener<goods>() {
                    @Override
                    public void done(List<goods> object, BmobException e) {
                        if(e==null){
                            for (goods good : object) {
                                goods g = new goods();
                                g.setObjectId(good.getObjectId());
                                g.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            Toast.makeText(Activity_itemlist.this,"删除成功",Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(Activity_itemlist.this,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(Activity_itemlist.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                itemAdapter.remove(itemAdapter.getItem(pos));
                MainActivity.allnum--;
                if(goods_expirationDate<3)
                    MainActivity.outOfDate--;
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    class MyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.search: {
                    Intent intent=new Intent(Activity_itemlist.this,SearchItem.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_up,R.anim.translate_out_up);
                    break;
                }
                case R.id.goback:{
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    //用于获取状态栏高度：状态栏高度84，宽度1440，标题textview1054
                    /*Resources resources = getResources();
                    int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
                    int height = resources.getDimensionPixelSize(resourceId);
                    Toast.makeText(Activity_itemlist.this, String.valueOf(height),Toast.LENGTH_SHORT).show();
                    */
                    break;

                }
                case R.id.additem:{
                    Intent intent=new Intent(Activity_itemlist.this,SpinnerActivity.class);
                    SpinnerActivity.judge=false;
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.scale_out,R.anim.no_anim);
                    break;
                }
            }
        }
    }
    protected void display(String sqlStatement){
        Cursor cursor = db.rawQuery(sqlStatement,null);
        while (cursor.moveToNext()){
            /*int lasts=0;
            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
            String str=cursor.getString(cursor.getColumnIndex("date_p"));
            Date outdate= null;//str表示yyyy年MM月dd HH:mm:ss格式字符串
            try {
                outdate = format.parse(str);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            Date nowdate=new Date();
            lasts=daysBetween(nowdate,outdate);*/
            String name = cursor.getString(cursor.getColumnIndex("goods_name"));
            int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
            String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
            String location=cursor.getString(cursor.getColumnIndex("location"));
            int clock = cursor.getInt(cursor.getColumnIndex("clock"));
            findID imgeid = new findID(im_id);
            String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
            Item_list dr;
            //String lasts=String.valueOf(bao);
            if(belongto.equals(MainActivity.nativeUserName)) {
                dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
            }else{
                dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
            }
            itemlist.add(dr);//把数据库的每一行加入数组中
        }
        //获取ListView,并通过Adapter把studentlist的信息显示到ListView
        int n=itemlist.size();
        itemAdapter.clear();
        for(int i=0;i<n;i++){
            Item_list dra=itemlist.get(i);
            Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
            itemAdapter.add(temp);
            listView.setAdapter(itemAdapter);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode) {
            case 1:         // 子窗口InputActivity的回传数据
                if (data != null) {
                /*
                Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //处理代码在此地
                        //提取inputActivity的数据
                        String goods_name=bundle.getString("goodsname");
                        String kinds=bundle.getString("kinds");
                        //String date_p=bundle.getString("date_p");
                        //String date_l=bundle.getString("date_l");
                        int baozhiqi=bundle.getInt("baozhiqi");
                        //String clo_time=bundle.getString("clo_time");
                        String belongto=bundle.getString("belongto");
                        String location=bundle.getString("location");
                        //insert(goods_name,kinds,date_p,date_l,baozhiqi,clo_time,location);//插入到数据库中
                        insert(goods_name,kinds,baozhiqi,belongto,location);//插入到数据库中
                        //显示到listview上
                        String lasts=String.valueOf(baozhiqi);
                        findID fid=new findID(kinds);
                        Item temp=new Item(goods_name,fid.getID(),location,baozhiqi);
                        itemAdapter.add(temp);
                        listView.setAdapter(itemAdapter);
                    }*/
                }
                break;
            default:
                //其它窗口的回传数据
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    //向数据库插入数据
    /*public void insert(String goods_name,String kinds,String date_p,String date_l,int baozhiqi,String clo_time,String location) {
        SQLiteDatabase db = myHelperRecord.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goods_name",goods_name);
        values.put("kinds",kinds);
        values.put("date_p",date_p);
        values.put("date_p",date_l);
        values.put("baozhiqi",baozhiqi);
        values.put("clo_time",clo_time);
        values.put("location",location);
        db.insert("goods",null,values);
        Toast.makeText(Activity_itemlist.this,"信息已添加",Toast.LENGTH_LONG).show();
        db.close();
    }*/
    public void insert(String goods_name,String kinds,int expirationDate,int c,String belongTo,String location) {
        SQLiteDatabase db = myHelperRecord.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goods_name",goods_name);
        values.put("kinds",kinds);
        values.put("baozhiqi",expirationDate);
        values.put("clock",c);
        values.put("belongto",belongTo);
        values.put("location",location);
        db.insert("goods",null,values);
        Toast.makeText(Activity_itemlist.this,"信息已添加",Toast.LENGTH_LONG).show();
        db.close();
    }
    public static int daysBetween(Date date1,Date date2)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
}
