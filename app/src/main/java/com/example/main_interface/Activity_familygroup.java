package com.example.main_interface;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class Activity_familygroup extends AppCompatActivity {

    private Button search;
    private ImageButton addItem;
    private ImageButton goback;
    //private applyToFG temp;
    private FamilyGroupAdapter familyGroupAdapter;
    private List<applyToFG> items;
    private ListView listView;
    private String username;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_allmember:
                    familyGroupAdapter.clear();
                    Toast.makeText(Activity_familygroup.this, "全部", Toast.LENGTH_SHORT).show();
                    BmobQuery<applyToFG> query = new BmobQuery<applyToFG>();
                    query.addWhereEqualTo("acceptId",MainActivity.nativeUserName);
                    query.addWhereEqualTo("acceptOrNot", true);
                    query.findObjects(new FindListener<applyToFG>() {
                        @Override
                        public void done(List<applyToFG> object, BmobException e) {
                            if (e == null && object.size() > 0) {
                                for (applyToFG apply : object) {
                                    applyToFG temp= new applyToFG(apply.getApplyId(),apply.getAcceptId(),apply.getAcceptOrNot());
                                    temp.setShareOrNot(apply.getShareOrNot());
                                    temp.setApplyNickName(apply.getApplyNickName());
                                    familyGroupAdapter.add(temp);
                                }
                                listView.setAdapter(familyGroupAdapter);
                            } else if (object.size() <= 0) {
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                    return true;
                case R.id.navigation_share:
                    familyGroupAdapter.clear();
                    Toast.makeText(Activity_familygroup.this, "共享用户", Toast.LENGTH_SHORT).show();
                    BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
                    query1.addWhereEqualTo("acceptId",MainActivity.nativeUserName);
                    query1.addWhereEqualTo("acceptOrNot", true);
                    query1.addWhereEqualTo("shareOrNot",0);
                    query1.findObjects(new FindListener<applyToFG>() {
                        @Override
                        public void done(List<applyToFG> object, BmobException e) {
                            if (e == null && object.size() > 0) {
                                for (applyToFG apply : object) {
                                    applyToFG temp= new applyToFG(apply.getAcceptId(),apply.getApplyId(),apply.getAcceptOrNot());
                                    temp.setShareOrNot(apply.getShareOrNot());
                                    temp.setApplyNickName(apply.getApplyNickName());
                                    familyGroupAdapter.add(temp);
                                }
                                listView.setAdapter(familyGroupAdapter);
                            } else if (object.size() <= 0) {
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                    return true;
                case R.id.navigation_reminded:
                    familyGroupAdapter.clear();
                    Toast.makeText(Activity_familygroup.this, "提醒TA", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familygroup);

        //点击返回
        goback = (ImageButton) findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(Activity_familygroup.this,MainActivity.class);
                //startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        });

        addItem=(ImageButton)findViewById(R.id.additem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(Activity_familygroup.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(Activity_familygroup.this);
                inputDialog.setTitle("请输入对方账号").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String user_name = editText.getText().toString();
                                if(!user_name.equals("")) {
                                    final BmobQuery<userdata> query = new BmobQuery<userdata>();
                                    query.addWhereEqualTo("userName", editText.getText().toString());
                                    query.setLimit(1);
                                    query.findObjects(new FindListener<userdata>() {
                                        @Override
                                        public void done(List<userdata> list, BmobException e) {
                                            if (e == null && list.size()>0) {
                                                for (userdata user : list) {
                                                    BmobQuery<applyToFG> query1 = new BmobQuery<>();
                                                    query1.addWhereEqualTo("applyId",MainActivity.nativeUserName);
                                                    query1.addWhereEqualTo("acceptId",user.getUserName());
                                                    query1.addWhereEqualTo("acceptOrNot",true);
                                                    query1.findObjects(new FindListener<applyToFG>() {
                                                        @Override
                                                        public void done(List<applyToFG> list, BmobException e) {
                                                            if(e==null && list.size()>0){
                                                                Toast.makeText(Activity_familygroup.this,"对方已经是你的好友",Toast.LENGTH_SHORT).show();
                                                            }else if(list.size()<=0){
                                                                applyToFG apply = new applyToFG(MainActivity.nativeUserName,user_name,false);
                                                                apply.setShareOrNot(1);
                                                                apply.setApplyNickName(MainActivity.nativeUserNickName);
                                                                apply.save(new SaveListener<String>() {
                                                                    @Override
                                                                    public void done(String objectId,BmobException e) {
                                                                        if(e==null){
                                                                            Toast.makeText(Activity_familygroup.this,"已发出申请",Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(Activity_familygroup.this,"对方尚未接受,请耐心等待",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            } else if(list.size()<=0) {
                                                Toast.makeText(Activity_familygroup.this, "用户不存在", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(Activity_familygroup.this, "发送申请失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                else
                                    Toast.makeText(Activity_familygroup.this, "输入不可为空", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        //点击搜索
        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_familygroup.this, SearchItem.class);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_up, R.anim.translate_out_up);
            }
        });

        items = new ArrayList<applyToFG>();
        familyGroupAdapter = new FamilyGroupAdapter(this, R.layout.fg_item,items );
        listView = (ListView)findViewById(R.id.list_item);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择操作");
                menu.add(0, 0, 0, "设置共享");
                menu.add(0, 1, 0, "删除此好友");
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        BmobQuery<applyToFG> query = new BmobQuery<applyToFG>();
        query.addWhereEqualTo("acceptId",MainActivity.nativeUserName);
        query.addWhereEqualTo("acceptOrNot", true);
        query.findObjects(new FindListener<applyToFG>() {
            @Override
            public void done(List<applyToFG> object, BmobException e) {
                if (e == null && object.size() > 0) {
                    for (applyToFG apply : object) {
                        applyToFG temp= new applyToFG(apply.getApplyId(),apply.getAcceptId(),apply.getAcceptOrNot());
                        temp.setShareOrNot(apply.getShareOrNot());
                        temp.setApplyNickName(apply.getApplyNickName());
                        familyGroupAdapter.add(temp);
                    }
                    listView.setAdapter(familyGroupAdapter);
                } else if (object.size() <= 0) {
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos=info.position;
        View itemView = info.targetView;

        switch (item.getItemId()) {
            case 0:
                TextView tv_applyname= (TextView) itemView.findViewById(R.id.item_username);
                username=tv_applyname.getText().toString();
                BmobQuery<applyToFG> query = new BmobQuery<applyToFG>();
                query.addWhereEqualTo("applyId", username);
                query.addWhereEqualTo("acceptId", MainActivity.nativeUserName);
                query.setLimit(50);
                query.findObjects(new FindListener<applyToFG>() {
                    @Override
                    public void done(List<applyToFG> object, BmobException e) {
                        if(e==null){
                            for (applyToFG apply : object) {
                                if(apply.getShareOrNot()==0){
                                    Toast.makeText(Activity_familygroup.this,"已共享",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                applyToFG set = new applyToFG();
                                set.setAcceptOrNot(true);
                                set.setShareOrNot(0);
                                set.update(apply.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e!=null){
                                            Toast.makeText(Activity_familygroup.this,"设置共享失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
                            query1.addWhereEqualTo("applyId", MainActivity.nativeUserName);
                            query1.addWhereEqualTo("acceptId", username);
                            query1.setLimit(50);
                            query1.findObjects(new FindListener<applyToFG>() {
                                @Override
                                public void done(List<applyToFG> object, BmobException e) {
                                    if(e==null){
                                        for (applyToFG apply : object) {
                                            applyToFG set = new applyToFG();
                                            set.setAcceptOrNot(true);
                                            set.setShareOrNot(0);
                                            set.update(apply.getObjectId(), new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        BmobQuery<goods> query2 = new BmobQuery<goods>();
                                                        query2.addWhereEqualTo("belongTo",username);
                                                        query2.findObjects(new FindListener<goods>() {
                                                            @Override
                                                            public void done(List<goods> list, BmobException e) {
                                                                if(e==null){
                                                                    /*for(goods item_insert:list){
                                                                        insert(item_insert.getGoodsName(),item_insert.getKinds(),item_insert.getExpirationDate(),
                                                                                item_insert.getClock(),item_insert.getBelongTo(),item_insert.getLocation());
                                                                    }*/
                                                                    Toast.makeText(Activity_familygroup.this,"设置共享成功",Toast.LENGTH_SHORT).show();
                                                                } else{
                                                                    Toast.makeText(Activity_familygroup.this,"设置共享失败",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(Activity_familygroup.this,"设置共享失败",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(Activity_familygroup.this,"设置共享失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Activity_familygroup.this,"设置共享失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                familyGroupAdapter.clear();
                Toast.makeText(Activity_familygroup.this, "全部", Toast.LENGTH_SHORT).show();
                BmobQuery<applyToFG> queryRef = new BmobQuery<applyToFG>();
                queryRef.addWhereEqualTo("acceptId",MainActivity.nativeUserName);
                queryRef.addWhereEqualTo("acceptOrNot", true);
                queryRef.findObjects(new FindListener<applyToFG>() {
                    @Override
                    public void done(List<applyToFG> object, BmobException e) {
                        if (e == null && object.size() > 0) {
                            for (applyToFG apply : object) {
                                MainActivity.allnum++;
                                applyToFG temp= new applyToFG(apply.getApplyId(),apply.getAcceptId(),apply.getAcceptOrNot());
                                temp.setShareOrNot(apply.getShareOrNot());
                                temp.setApplyNickName(apply.getApplyNickName());
                                familyGroupAdapter.add(temp);
                            }
                            listView.setAdapter(familyGroupAdapter);
                        } else if (object.size() <= 0) {
                        } else {
                            Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                return true;
            case 1:
                TextView tv_username= (TextView) itemView.findViewById(R.id.item_username);
                username=tv_username.getText().toString();
                BmobQuery<applyToFG> queryDel = new BmobQuery<applyToFG>();
                queryDel.addWhereEqualTo("applyId", username);
                queryDel.addWhereEqualTo("acceptId", MainActivity.nativeUserName);
                queryDel.setLimit(50);
                queryDel.findObjects(new FindListener<applyToFG>() {
                    @Override
                    public void done(List<applyToFG> object, BmobException e) {
                        if(e==null){
                            for (applyToFG apply : object) {
                                applyToFG del = new applyToFG();
                                del.setObjectId(apply.getObjectId());
                                del.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e!=null){
                                            Toast.makeText(Activity_familygroup.this,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            BmobQuery<applyToFG> query1 = new BmobQuery<applyToFG>();
                            query1.addWhereEqualTo("applyId", MainActivity.nativeUserName);
                            query1.addWhereEqualTo("acceptId", username);
                            query1.setLimit(50);
                            query1.findObjects(new FindListener<applyToFG>() {
                                @Override
                                public void done(List<applyToFG> object, BmobException e) {
                                    if(e==null){
                                        for (applyToFG apply : object) {
                                            applyToFG del = new applyToFG();
                                            del.setObjectId(apply.getObjectId());
                                            del.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        MyHelper_record dbhelper = new MyHelper_record(Activity_familygroup.this);
                                                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                                                        Cursor cursor = db.rawQuery("select * from goods where belongto='"+username+"'",null);
                                                        while (cursor.moveToNext()) {
                                                            MainActivity.allnum--;
                                                            int lasts = cursor.getInt(cursor.getColumnIndex("baozhiqi"));
                                                            if(lasts<3)
                                                                MainActivity.outOfDate--;
                                                        }
                                                        db.execSQL("delete from goods where belongto='"+username+"'");
                                                        Toast.makeText(Activity_familygroup.this,"删除成功",Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(Activity_familygroup.this,"删除失败",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(Activity_familygroup.this,"删除失败",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(Activity_familygroup.this,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                familyGroupAdapter.remove(familyGroupAdapter.getItem(pos));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void insert(String goods_name,String kinds,int expirationDate,int c,String belongTo,String location) {
        MyHelper_record myHelperRecord=new MyHelper_record(Activity_familygroup.this);
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

}
