package com.example.main_interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lenovo on 2017/11/21.
 */

public class FamilyGroupAdapter extends ArrayAdapter<applyToFG>{

    public FamilyGroupAdapter(Context context, int resource, List<applyToFG> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取数据
        applyToFG item= getItem(position);

        // 创建布局
        View oneItemView = LayoutInflater.from(getContext()).inflate(R.layout.fg_item, parent, false);

        // 获取ImageView和TextView
        TextView name = (TextView) oneItemView.findViewById(R.id.item_username);
        //ImageView imageView = (ImageView) oneItemView.findViewById(R.id.item_small_imageView);
        //TextView account = (TextView) oneItemView.findViewById(R.id.item_account);
        TextView nickname = (TextView) oneItemView.findViewById(R.id.item_nickname);
        TextView reminder = (TextView) oneItemView.findViewById(R.id.item_reminder);

        // 根据数据设置ImageView和TextView的展现
        name.setText(item.getApplyId());
        nickname.setText(item.getApplyNickName());
        reminder.setVisibility(item.getShareOrNot()*4);  //0可见


        return oneItemView;
    }
}
