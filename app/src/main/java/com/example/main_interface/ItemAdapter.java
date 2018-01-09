package com.example.main_interface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by lenovo on 2017/11/21.
 */

public class ItemAdapter extends ArrayAdapter<Item>{

    public ItemAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取数据
        Item item = getItem(position);

        // 创建布局
        View oneItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_item, parent, false);

        // 获取ImageView和TextView
        TextView name = (TextView) oneItemView.findViewById(R.id.item_name);
        ImageView imageView = (ImageView) oneItemView.findViewById(R.id.item_small_imageView);
        //TextView account = (TextView) oneItemView.findViewById(R.id.item_account);
        TextView ismine = (TextView) oneItemView.findViewById(R.id.item_ismine);
        TextView location = (TextView) oneItemView.findViewById(R.id.item_location);
        TextView lasts = (TextView) oneItemView.findViewById(R.id.item_lasts);
        TextView reminder = (TextView) oneItemView.findViewById(R.id.item_reminder);

        // 根据数据设置ImageView和TextView的展现
        name.setText(item.getName());
        imageView.setImageResource(item.getImageId());
        location.setText(item.getLocation());
        lasts.setText(String.valueOf(item.getLasts()));
        reminder.setVisibility(item.getClock()*4);  //0可见
        ismine.setVisibility(item.getIsMine()*4);


        return oneItemView;
    }
}
