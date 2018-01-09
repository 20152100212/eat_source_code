package com.example.main_interface;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.sql.Blob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity {
    private SlidingMenu slidingMenu;
    //记录用户首次点击返回键的时间
    private long firstTime=0;
    private View iv_filter;
    private ImageView pot;
    private ImageButton mail;

    //private ItemAdapter itemAdapter;
    //private ListView listView;
    private ArrayList<Item_list> itemlist;
    private MyHelper_record myHelperRecord;
    private ItemAdapter itemAdapter;
    private ListView listView;
    private TextView tv_username,menu_username,menu_usernickname;
    private TextView allItemCount,outOfDateItemCount;
    private List<String> str;
    private TextView us;
    private String currentUserName;

    public String apply_num,item_id;
    public static String nativeUserName;
    public static String nativeUserNickName;

    public int test=0;
    public static boolean add_alarm_from_main=false;
    public static int allnum=0,outOfDate=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "22ca2ace7668b50b45d61fe71f5d3f8d");

        tv_username = (TextView)findViewById(R.id.user_name);

        //侧滑
        slidingMenu = (SlidingMenu) findViewById(R.id.slidingmenu_layout);
        slidingMenu.setMode(SlidingMenu.LEFT) ;
        slidingMenu.setMenu(R.layout.layout_menu) ;
        // 设置左页的宽度
        slidingMenu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth()*3/10);
        slidingMenu.getBehindScrollScale();
        iv_filter=(View)findViewById(R.id.v_filter);

        menu_usernickname = (TextView)findViewById(R.id.menu_usernickname);
        menu_username = (TextView)findViewById(R.id.menu_username);
        allItemCount = (TextView)findViewById(R.id.all_num);
        outOfDateItemCount = (TextView)findViewById(R.id.out_of_date);


        Bundle bundle = this.getIntent().getExtras();
        String usernickname=bundle.getString("usernickname");
        String username=bundle.getString("username");
        nativeUserName = username;
        nativeUserNickName = usernickname;
        tv_username.setText(usernickname);
        menu_username.setText(username);
        menu_usernickname.setText(usernickname);

        slidingMenu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float v) {
                float alpha = v * 0.7f;
                iv_filter.setAlpha(alpha);
                if (alpha <= 0) {
                    if (iv_filter.isShown())
                        iv_filter.setVisibility(View.GONE);
                } else {
                    if (!iv_filter.isShown())
                        iv_filter.setVisibility(View.VISIBLE);
                }
            }
        });

        pot=(ImageView)findViewById(R.id.pot);
        getApplys();//获取数据库中的申请项目

        mail=(ImageButton)findViewById(R.id.mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item_id!=null) {
                    BmobQuery<applyToFG> query = new BmobQuery<applyToFG>();
                    query.getObject(item_id, new QueryListener<applyToFG>() {
                        @Override
                        public void done(applyToFG object, BmobException e) {
                            if (e == null) {
                                apply_num = object.getApplyId();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("收到家庭组申请");
                                builder.setMessage("用户\"" +object.getApplyNickName()+"\" ("+ apply_num + ") 申请与您组成家庭组好友,是否同意？");
                                builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, final int i) {
                                        applyToFG object = new applyToFG();
                                        object.setAcceptOrNot(true);
                                        object.setShareOrNot(1);
                                        object.update(item_id, new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e != null) {
                                                    Toast.makeText(MainActivity.this,"处理失败",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        applyToFG p = new applyToFG(nativeUserName,apply_num,object.getAcceptOrNot());
                                        p.setApplyNickName(nativeUserNickName);
                                        p.setShareOrNot(1);
                                        p.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String objectId,BmobException e) {
                                                if(e==null){
                                                    Toast.makeText(MainActivity.this,"已同意",Toast.LENGTH_SHORT).show();
                                                    allnum = 0;
                                                    outOfDate = 0;
                                                    itemAdapter.clear();

                                                    itemlist=new ArrayList<>();
                                                    //创建数据库
                                                    myHelperRecord=new MyHelper_record(MainActivity.this);
                                                    //打开数据库
                                                    SQLiteDatabase db=myHelperRecord.getReadableDatabase();
                                                    String sql = "delete from goods;";
                                                    db.execSQL(sql);
                                                    //str = new ArrayList<String>();
                                                    //str.add(nativeUserName);
                                                    BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
                                                    query1.addWhereEqualTo("acceptId",nativeUserName);
                                                    query1.addWhereEqualTo("acceptOrNot", true);
                                                    query1.addWhereEqualTo("shareOrNot",0);
                                                    query1.findObjects(new FindListener<applyToFG>() {
                                                        @Override
                                                        public void done(List<applyToFG> object, BmobException e) {
                                                            if (e == null && object.size()>0) {
                                                                for (applyToFG apply : object) {
                                                                    currentUserName=apply.getApplyId();
                                                                    //str.add(currentUserName);
                                                                    BmobQuery<goods> query =new BmobQuery<goods>();
                                                                    query.addWhereEqualTo("belongTo",currentUserName);
                                                                    //query.addWhereLessThan("expirationDate",3);
                                                                    query.findObjects(new FindListener<goods>() {
                                                                        @Override
                                                                        public void done(List<goods> list, BmobException e) {
                                                                            if (e == null) {
                                                                                for (goods item : list) {
                                                                                    allnum++;
                                                                                    list.size();
                                                                                    insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), currentUserName, item.getLocation());
                                                                                }
                                                                                Toast.makeText(MainActivity.this,"载入好友数据完成",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                BmobQuery<goods> query =new BmobQuery<goods>();
                                                                query.addWhereEqualTo("belongTo",nativeUserName);
                                                                //query.addWhereLessThan("expirationDate",3);
                                                                query.findObjects(new FindListener<goods>() {
                                                                    @Override
                                                                    public void done(List<goods> list, BmobException e) {
                                                                        if (e == null) {
                                                                            for (goods item : list) {
                                                                                allnum++;
                                                                                insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), nativeUserName, item.getLocation());
                                                                                Toast.makeText(MainActivity.this, "数据初始化完成", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                                                            Cursor cursor = dbt.rawQuery("select * from goods where baozhiqi<3",null);
                                                                            while (cursor.moveToNext()){
                                                                                outOfDate++;
                                                                                String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                                                                int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                                                                String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                                                                int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                                                                String location = cursor.getString(cursor.getColumnIndex("location"));
                                                                                findID imgeid = new findID(im_id);
                                                                                String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                                                                Item_list dr;
                                                                                //String lasts=String.valueOf(bao);
                                                                                if(belongto.equals(nativeUserName)) {
                                                                                    dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                                                                }else{
                                                                                    dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                                                                }
                                                                                itemlist.add(dr);//把数据库的每一行加入数组中
                                                                            }
                                                                            //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                                                            int n=itemlist.size();
                                                                            for(int i=0;i<n;i++){
                                                                                Item_list dra=itemlist.get(i);
                                                                                Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                                                                itemAdapter.add(temp);
                                                                            }
                                                                            listView.setAdapter(itemAdapter);

                                                                            allItemCount.setText(String.valueOf(allnum));
                                                                            outOfDateItemCount.setText(String.valueOf(outOfDate));
                                                                        }
                                                                    }
                                                                });
                                                            } else if(object.size()==0) {
                                                                BmobQuery<goods> query =new BmobQuery<goods>();
                                                                query.addWhereEqualTo("belongTo",nativeUserName);
                                                                //query.addWhereLessThan("expirationDate",3);
                                                                query.findObjects(new FindListener<goods>() {
                                                                    @Override
                                                                    public void done(List<goods> list, BmobException e) {
                                                                        if (e == null) {
                                                                            for(goods item:list){
                                                                                allnum++;
                                                                                insert(item.getGoodsName(),item.getKinds(),item.getExpirationDate(),item.getClock(),nativeUserName,item.getLocation());
                                                                                Toast.makeText(MainActivity.this,"数据初始化完成",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            //扫描数据库,将数据库信息放入
                                                                            SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                                                            Cursor cursor = dbt.rawQuery("select * from goods where belongto='"+nativeUserName+"' and baozhiqi<3",null);
                                                                            while (cursor.moveToNext()){
                                                                                outOfDate++;
                                                                                String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                                                                int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                                                                String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                                                                int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                                                                String location = cursor.getString(cursor.getColumnIndex("location"));
                                                                                findID imgeid = new findID(im_id);
                                                                                String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                                                                Item_list dr;
                                                                                //String lasts=String.valueOf(bao);
                                                                                //if(belongto.equals(nativeUserName)) {
                                                                                    dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                                                                //}else{
                                                                                //   dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                                                                //}
                                                                                itemlist.add(dr);//把数据库的每一行加入数组中

                                                                            }
                                                                            dbt.close();
                                                                            cursor.close();
                                                                            //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                                                            int n=itemlist.size();
                                                                            for(int i=0;i<n;i++){
                                                                                Item_list dra=itemlist.get(i);
                                                                                Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                                                                itemAdapter.add(temp);
                                                                            }
                                                                            listView.setAdapter(itemAdapter);

                                                                            allItemCount.setText(String.valueOf(allnum));
                                                                            outOfDateItemCount.setText(String.valueOf(outOfDate));
                                                                        }
                                                                    }
                                                                });

                                                            }else{
                                                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(MainActivity.this,"处理失败",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        getApplys();
                                    }
                                });
                                builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        applyToFG object = new applyToFG();
                                        object.setObjectId(item_id);
                                        object.delete(new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e != null) {
                                                    Toast.makeText(MainActivity.this,"处理失败",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        getApplys();
                                    }
                                });
                                builder.show();
                                //tv.setText("用户 "+apply_num+" 申请与您组成家庭组好友,是否同意？");
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }
                else{
                    getApplys();
                }
            }
        });
                //初始化
        itemAdapter = new ItemAdapter(this, R.layout.item_item, Item.getAllItems());
        listView = (ListView) findViewById(R.id.list_item);

        ImageView btn_menu = (ImageView) findViewById(R.id.go_menu);
        btn_menu.setOnClickListener(new onClick());
        ImageButton btn_login = (ImageButton) findViewById(R.id.head_portrait);
        btn_login.setOnClickListener(new onClick());
        Button btn_set=(Button)findViewById(R.id.set);
        btn_set.setOnClickListener(new onClick());
        Button btn_changeuser=(Button)findViewById(R.id.changeaccount_menu);
        btn_changeuser.setOnClickListener(new onClick());
        Button btn_fam=(Button)findViewById(R.id.myfamily);
        btn_fam.setOnClickListener(new onClick());
        Button btn_myitem=(Button)findViewById(R.id.myitem);
        btn_myitem.setOnClickListener(new onClick());
        Button btn_allitem=(Button)findViewById(R.id.all_item);
        btn_allitem.setOnClickListener(new onClick());
        Button btn_additem=(Button)findViewById(R.id.add_item);
        btn_additem.setOnClickListener(new onClick());
        Button btn_addalarm=(Button)findViewById(R.id.add_alarm);
        btn_addalarm.setOnClickListener(new onClick());
        menu_username = (TextView)findViewById(R.id.menu_username);


        itemAdapter.clear();
        allnum = 0;
        outOfDate = 0;
        itemAdapter.clear();

        itemlist=new ArrayList<>();
        //创建数据库
        myHelperRecord=new MyHelper_record(MainActivity.this);
        //打开数据库
        SQLiteDatabase db=myHelperRecord.getReadableDatabase();
        String sql = "delete from goods;";
        db.execSQL(sql);
        //str = new ArrayList<String>();
        //str.add(nativeUserName);
        BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
        query1.addWhereEqualTo("acceptId",nativeUserName);
        query1.addWhereEqualTo("acceptOrNot", true);
        query1.addWhereEqualTo("shareOrNot",0);
        query1.findObjects(new FindListener<applyToFG>() {
            @Override
            public void done(List<applyToFG> object, BmobException e) {
                if (e == null && object.size()>0) {
                    for (applyToFG apply : object) {
                        currentUserName=apply.getApplyId();
                        //str.add(currentUserName);
                        BmobQuery<goods> query =new BmobQuery<goods>();
                        query.addWhereEqualTo("belongTo",currentUserName);
                        //query.addWhereLessThan("expirationDate",3);
                        query.findObjects(new FindListener<goods>() {
                            @Override
                            public void done(List<goods> list, BmobException e) {
                                if (e == null) {
                                    for (goods item : list) {
                                        allnum++;
                                        list.size();
                                        insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), currentUserName, item.getLocation());
                                    }
                                    Toast.makeText(MainActivity.this,"载入好友数据完成",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    BmobQuery<goods> query =new BmobQuery<goods>();
                    query.addWhereEqualTo("belongTo",nativeUserName);
                    //query.addWhereLessThan("expirationDate",3);
                    query.findObjects(new FindListener<goods>() {
                        @Override
                        public void done(List<goods> list, BmobException e) {
                            if (e == null) {
                                for (goods item : list) {
                                    allnum++;
                                    insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), nativeUserName, item.getLocation());
                                    Toast.makeText(MainActivity.this, "共享数据初始化完成", Toast.LENGTH_SHORT).show();
                                }
                                SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                Cursor cursor = dbt.rawQuery("select * from goods where baozhiqi<3",null);
                                while (cursor.moveToNext()){
                                    outOfDate++;
                                    String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                    int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                    String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                    int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                    String location = cursor.getString(cursor.getColumnIndex("location"));
                                    findID imgeid = new findID(im_id);
                                    String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                    Item_list dr;
                                    //String lasts=String.valueOf(bao);
                                    if(belongto.equals(nativeUserName)) {
                                        dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                    }else{
                                        dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                    }
                                    itemlist.add(dr);//把数据库的每一行加入数组中
                                }
                                //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                int n=itemlist.size();
                                for(int i=0;i<n;i++){
                                    Item_list dra=itemlist.get(i);
                                    Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                    itemAdapter.add(temp);
                                }
                                listView.setAdapter(itemAdapter);

                                allItemCount.setText(String.valueOf(allnum));
                                outOfDateItemCount.setText(String.valueOf(outOfDate));
                            }
                        }
                    });
                } else if(object.size()==0) {
                    BmobQuery<goods> query =new BmobQuery<goods>();
                    query.addWhereEqualTo("belongTo",nativeUserName);
                    //query.addWhereLessThan("expirationDate",3);
                    query.findObjects(new FindListener<goods>() {
                        @Override
                        public void done(List<goods> list, BmobException e) {
                            if (e == null) {
                                for(goods item:list){
                                    allnum++;
                                    insert(item.getGoodsName(),item.getKinds(),item.getExpirationDate(),item.getClock(),nativeUserName,item.getLocation());
                                    Toast.makeText(MainActivity.this,"数据初始化完成",Toast.LENGTH_SHORT).show();
                                }
                                //扫描数据库,将数据库信息放入
                                SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                Cursor cursor = dbt.rawQuery("select * from goods where belongto='"+nativeUserName+"' and baozhiqi<3",null);
                                while (cursor.moveToNext()){
                                    outOfDate++;
                                    String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                    int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                    String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                    int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                    String location = cursor.getString(cursor.getColumnIndex("location"));
                                    findID imgeid = new findID(im_id);
                                    String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                    Item_list dr;
                                    //String lasts=String.valueOf(bao);
                                    //if(belongto.equals(nativeUserName)) {
                                    dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                    //}else{
                                    //   dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                    //}
                                    itemlist.add(dr);//把数据库的每一行加入数组中

                                }
                                dbt.close();
                                cursor.close();
                                //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                int n=itemlist.size();
                                for(int i=0;i<n;i++){
                                    Item_list dra=itemlist.get(i);
                                    Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                    itemAdapter.add(temp);
                                }
                                listView.setAdapter(itemAdapter);

                                allItemCount.setText(String.valueOf(allnum));
                                outOfDateItemCount.setText(String.valueOf(outOfDate));
                            }
                        }
                    });

                }else{
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });



    }

    protected void getApplys() {
        BmobQuery<applyToFG> query = new BmobQuery<applyToFG>();
        query.addWhereEqualTo("acceptId",nativeUserName);
        query.addWhereEqualTo("acceptOrNot", false);
        query.setLimit(1);
        query.findObjects(new FindListener<applyToFG>() {
            @Override
            public void done(List<applyToFG> object, BmobException e) {
                if (e == null && object.size() > 0) {
                    for (applyToFG apply : object) {
                        item_id = apply.getObjectId();
                        apply_num = apply.getApplyId();
                    }
                    pot.setVisibility(View.VISIBLE);
                } else if (object.size() <= 0) {
                    pot.setVisibility(View.INVISIBLE);
                    item_id = null;
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //按钮点击事件
    class onClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.go_menu:{
                    slidingMenu.toggle();
                    break;
                }
                case R.id.head_portrait: {//点击登录头像
                    LoginActivity.judge=true;
                    Intent intent = new Intent(MainActivity.this, SetActivity.class);
                    intent.putExtra("username",menu_username.getText());
                    intent.putExtra("usernickname",tv_username.getText());
                    startActivityForResult(intent,3);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                case R.id.set:{//点击设置
                    Intent intent = new Intent(MainActivity.this, SetActivity.class);
                    intent.putExtra("username",menu_username.getText());
                    intent.putExtra("usernickname",tv_username.getText());
                    startActivityForResult(intent,3);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                case R.id.changeaccount_menu:{//点击切换用户
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                case R.id.myfamily:{//点击家庭组
                    Intent intent=new Intent(MainActivity.this,Activity_familygroup.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;
                }
                case R.id.myitem:{//点击我的物品
                    Intent intent=new Intent(MainActivity.this,Activity_itemlist.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    break;
                }
                case R.id.all_item:{//点击全部物品
                    Intent intent=new Intent(MainActivity.this,Activity_itemlist.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                case R.id.add_item:{//点击添加项目
                    Intent intent=new Intent(MainActivity.this,SpinnerActivity.class);
                    SpinnerActivity.judge=true;
                    startActivityForResult(intent,1);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                case R.id.add_alarm:{//点击添加闹钟
                    add_alarm_from_main=true;
                    Intent intent=new Intent(MainActivity.this,addAlarmActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    break;
                }
                default:break;
            }
        }
    }
   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (resultCode) {
            case 3:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String usernickname = bundle.getString("new_nickname");
                        tv_username.setText(usernickname);
                        menu_usernickname.setText(usernickname);
                    }
                }
                break;
            default:
                //其它窗口的回传数据
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    //向数据库插入数据
    public void insert(String goods_name,String kinds,int expirationDate,int c,String belongTo,String location) {
        SQLiteDatabase db = myHelperRecord.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("goods_name",goods_name);
        values.put("kinds",kinds);
        values.put("baozhiqi",expirationDate);
        values.put("clock",c);
        values.put("belongto",belongTo);
        values.put("location",location);
        //values.put("date_p",produceTime);
        db.insert("goods",null,values);
        //Toast.makeText(MainActivity.this,"信息已添加",Toast.LENGTH_LONG).show();
        db.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getApplys();
        itemAdapter.clear();
        allnum = 0;
        outOfDate = 0;
        itemAdapter.clear();

        itemlist=new ArrayList<>();
        //创建数据库
        myHelperRecord=new MyHelper_record(MainActivity.this);
        //打开数据库
        SQLiteDatabase db=myHelperRecord.getReadableDatabase();
        String sql = "delete from goods;";
        db.execSQL(sql);
        //str = new ArrayList<String>();
        //str.add(nativeUserName);
        BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
        query1.addWhereEqualTo("acceptId",nativeUserName);
        query1.addWhereEqualTo("acceptOrNot", true);
        query1.addWhereEqualTo("shareOrNot",0);
        query1.findObjects(new FindListener<applyToFG>() {
            @Override
            public void done(List<applyToFG> object, BmobException e) {
                if (e == null && object.size()>0) {
                    for (applyToFG apply : object) {
                        currentUserName=apply.getApplyId();
                        //str.add(currentUserName);
                        BmobQuery<goods> query =new BmobQuery<goods>();
                        query.addWhereEqualTo("belongTo",currentUserName);
                        //query.addWhereLessThan("expirationDate",3);
                        query.findObjects(new FindListener<goods>() {
                            @Override
                            public void done(List<goods> list, BmobException e) {
                                if (e == null) {
                                    for (goods item : list) {
                                        allnum++;
                                        list.size();
                                        insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), currentUserName, item.getLocation());
                                    }
                                    Toast.makeText(MainActivity.this,"载入好友数据完成",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    BmobQuery<goods> query =new BmobQuery<goods>();
                    query.addWhereEqualTo("belongTo",nativeUserName);
                    //query.addWhereLessThan("expirationDate",3);
                    query.findObjects(new FindListener<goods>() {
                        @Override
                        public void done(List<goods> list, BmobException e) {
                            if (e == null) {
                                for (goods item : list) {
                                    allnum++;
                                    insert(item.getGoodsName(), item.getKinds(), item.getExpirationDate(), item.getClock(), nativeUserName, item.getLocation());
                                    Toast.makeText(MainActivity.this, "共享数据初始化完成", Toast.LENGTH_SHORT).show();
                                }
                                SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                Cursor cursor = dbt.rawQuery("select * from goods where baozhiqi<3",null);
                                while (cursor.moveToNext()){
                                    outOfDate++;
                                    String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                    int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                    String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                    int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                    String location = cursor.getString(cursor.getColumnIndex("location"));
                                    findID imgeid = new findID(im_id);
                                    String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                    Item_list dr;
                                    //String lasts=String.valueOf(bao);
                                    if(belongto.equals(nativeUserName)) {
                                        dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                    }else{
                                        dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                    }
                                    itemlist.add(dr);//把数据库的每一行加入数组中
                                }
                                //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                int n=itemlist.size();
                                for(int i=0;i<n;i++){
                                    Item_list dra=itemlist.get(i);
                                    Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                    itemAdapter.add(temp);
                                }
                                listView.setAdapter(itemAdapter);

                                allItemCount.setText(String.valueOf(allnum));
                                outOfDateItemCount.setText(String.valueOf(outOfDate));
                            }
                        }
                    });
                } else if(object.size()==0) {
                    BmobQuery<goods> query =new BmobQuery<goods>();
                    query.addWhereEqualTo("belongTo",nativeUserName);
                    //query.addWhereLessThan("expirationDate",3);
                    query.findObjects(new FindListener<goods>() {
                        @Override
                        public void done(List<goods> list, BmobException e) {
                            if (e == null) {
                                for(goods item:list){
                                    allnum++;
                                    insert(item.getGoodsName(),item.getKinds(),item.getExpirationDate(),item.getClock(),nativeUserName,item.getLocation());
                                    Toast.makeText(MainActivity.this,"数据初始化完成",Toast.LENGTH_SHORT).show();
                                }
                                //扫描数据库,将数据库信息放入
                                SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                                Cursor cursor = dbt.rawQuery("select * from goods where belongto='"+nativeUserName+"' and baozhiqi<3",null);
                                while (cursor.moveToNext()){
                                    outOfDate++;
                                    String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                                    int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                    String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                                    int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                                    String location = cursor.getString(cursor.getColumnIndex("location"));
                                    findID imgeid = new findID(im_id);
                                    String belongto = cursor.getString(cursor.getColumnIndex("belongto"));
                                    Item_list dr;
                                    //String lasts=String.valueOf(bao);
                                    //if(belongto.equals(nativeUserName)) {
                                    dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 1);    //student_info存一个条目的数据
                                    //}else{
                                    //   dr = new Item_list(name, imgeid.getID(), location, lasts, clock, 0);
                                    //}
                                    itemlist.add(dr);//把数据库的每一行加入数组中

                                }
                                dbt.close();
                                cursor.close();
                                //获取ListView,并通过Adapter把studentlist的信息显示到ListView
                                int n=itemlist.size();
                                for(int i=0;i<n;i++){
                                    Item_list dra=itemlist.get(i);
                                    Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                                    itemAdapter.add(temp);
                                }
                                listView.setAdapter(itemAdapter);

                                allItemCount.setText(String.valueOf(allnum));
                                outOfDateItemCount.setText(String.valueOf(outOfDate));
                            }
                        }
                    });

                }else{
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
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
