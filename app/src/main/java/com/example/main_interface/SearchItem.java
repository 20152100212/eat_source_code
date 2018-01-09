package com.example.main_interface;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lenovo on 2017/11/18.
 */

public class SearchItem extends AppCompatActivity {

    private Button cancel;

    private SearchView               searchView;
    private Context                  context;
    private MyHandler                handler;

    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
    private String                   currentSearchTip;

    private FamilyGroupAdapter familyGroupAdapter;
    private ItemAdapter itemAdapter;
    private ArrayList<Item_list> itemlist;
    private ListView listView,listView2;
    private TextView cannot_find_item,cannot_find_fg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = getApplicationContext();
        handler = new MyHandler();

        listView=(ListView)findViewById(R.id.listView) ;
        listView2=(ListView)findViewById(R.id.listView2) ;
        cannot_find_item=(TextView)findViewById(R.id.cannot_finditem);
        cannot_find_fg=(TextView)findViewById(R.id.cannot_findfg);
        itemAdapter = new ItemAdapter(this, R.layout.item_item, Item.getAllItems());
        List<applyToFG> items = new ArrayList<applyToFG>();
        familyGroupAdapter = new FamilyGroupAdapter(this, R.layout.fg_item,items );

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
        setTitle(" ");
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.activity_search, null);
        */

        //searchView = (SearchView)customActionBarView.findViewById(R.id.searchView);
        searchView = (SearchView)findViewById(R.id.searchView);
        if(searchView==null){return;}
        int id=searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView=(TextView)searchView.findViewById(id);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(14);
        textView.setHintTextColor(Color.GRAY);

        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(context, "搜索中", Toast.LENGTH_SHORT).show();
                familyGroupAdapter.clear();
                itemAdapter.clear();
                BmobQuery<applyToFG> queryBmob = new BmobQuery<applyToFG>();
                queryBmob.addWhereEqualTo("applyId",query);
                queryBmob.addWhereEqualTo("acceptId",MainActivity.nativeUserName);
                queryBmob.addWhereEqualTo("acceptOrNot", true);
                queryBmob.setLimit(50);
                queryBmob.findObjects(new FindListener<applyToFG>() {
                    @Override
                    public void done(List<applyToFG> object, BmobException e) {
                        if(e==null && object.size()>0){
                            for (applyToFG apply : object) {
                                applyToFG temp= new applyToFG(apply.getApplyId(),apply.getAcceptId(),apply.getAcceptOrNot());
                                temp.setShareOrNot(apply.getShareOrNot());
                                temp.setApplyNickName(apply.getApplyNickName());
                                familyGroupAdapter.add(temp);
                            }
                            cannot_find_fg.setVisibility(View.INVISIBLE);
                            listView2.setAdapter(familyGroupAdapter);
                        }else if(object.size()==0) {
                            cannot_find_fg.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(context, "搜索失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                MyHelper_record myHelperRecord=new MyHelper_record(SearchItem.this);
                SQLiteDatabase dbt=myHelperRecord.getReadableDatabase();
                itemlist=new ArrayList<>();
                Cursor cursor = dbt.rawQuery("select * from goods where goods_name='"+query+"'",null);
                if(cursor.getCount()<=0){
                    cannot_find_item.setVisibility(View.VISIBLE);
                }
                else
                    cannot_find_item.setVisibility(View.INVISIBLE);
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("goods_name"));
                    int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                    String im_id = cursor.getString(cursor.getColumnIndex("kinds"));//获取物品种类
                    int clock = cursor.getInt(cursor.getColumnIndex("clock"));
                    String location = cursor.getString(cursor.getColumnIndex("location"));
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
                for(int i=0;i<n;i++){
                    Item_list dra=itemlist.get(i);
                    Item temp=new Item(dra.getName(),dra.getImageId(),dra.getLocation(),dra.getLasts(),dra.getClock(),dra.getIsMine());
                    itemAdapter.add(temp);
                }
                listView.setAdapter(itemAdapter);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                /*if (newText != null && newText.length() > 0) {
                    currentSearchTip = newText;
                    showSearchTip(newText);
                }*/
                return true;
            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                | Gravity.RIGHT);
       // actionBar.setCustomView(customActionBarView, params);

        // show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



        searchView.setIconified(false);
        searchView.setOnCloseListener(new OnCloseListener() {

            @Override
            public boolean onClose() {
                // to avoid click x button and the edittext hidden
                return true;
            }
        });

        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
                overridePendingTransition(R.anim.translate_out_down,R.anim.translate_down);
            }

        });

    }

    public void showSearchTip(String newText) {
        // excute after 500ms, and when excute, judge current search tip and newText
        schedule(new SearchTipThread(newText), 500);
    }

    class SearchTipThread implements Runnable {

        String newText;

        public SearchTipThread(String newText){
            this.newText = newText;
        }

        public void run() {
            // keep only one thread to load current search tip, u can get data from network here
            if (newText != null && newText.equals(currentSearchTip)) {
                handler.sendMessage(handler.obtainMessage(1, newText + " search tip"));
            }
        }
    }

    public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
        return scheduledExecutor.schedule(command, delayTimeMills, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }


    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}
