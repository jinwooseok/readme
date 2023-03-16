package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class NoticeListAdapter extends BaseAdapter {
    private Context context;
    private List<Notice> noticedList;
    public NoticeListAdapter(Context context, List<Notice> noticedList){
        this.context = context;
        this.noticedList = noticedList;
    }
    @Override
    public int getCount(){
        return noticedList.size();
    }
    @Override
    public Object getItem(int position){
        return noticedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = View.inflate(context,R.layout.boards,null);
        TextView noticeText = (TextView) v.findViewById(R.id.noticeText);
        TextView nameText = (TextView) v.findViewById(R.id.nameText);
        TextView dateText = (TextView) v.findViewById(R.id.dateText);
        noticeText.setText(noticedList.get(position).getNotice());
        nameText.setText(noticedList.get(position).getName());
        dateText.setText(noticedList.get(position).getDate());
        v.setTag(noticedList.get(position).getNotice());
        return v;
    }
}
