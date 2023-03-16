package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {
    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<Notice> noticedList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards);
        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticedList = new ArrayList<Notice>();
        noticedList.add(new Notice("공지사항6","","2023-02-06"));
        noticedList.add(new Notice("공지사항5","","2023-02-06"));
        noticedList.add(new Notice("공지사항4","","2023-02-06"));
        noticedList.add(new Notice("공지사항3","","2023-02-06"));
        noticedList.add(new Notice("공지사항2","","2023-02-06"));
        noticedList.add(new Notice("공지사항1","","2023-02-06"));

        adapter = new NoticeListAdapter(getApplicationContext(),noticedList);
        noticeListView.setAdapter(adapter);

        final Button courseButton = (Button) findViewById(R.id.courseButton);
        final Button statisticsButton = (Button) findViewById(R.id.statisticsButton);
        final Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        final LinearLayout notice = (LinearLayout) findViewById(R.id.notice);

        Button changeDisplay = (Button) findViewById(R.id.changeDisplay);
        changeDisplay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        courseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notice.setVisibility(View.VISIBLE);
                courseButton.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                statisticsButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                scheduleButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new Fragment());
                fragmentTransaction.commit();
            }
        });
        scheduleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notice.setVisibility(View.GONE);
                courseButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                statisticsButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                scheduleButton.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new Fragment());
                fragmentTransaction.commit();
            }
        });
        statisticsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                notice.setVisibility(View.GONE);
                courseButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                statisticsButton.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                scheduleButton.setBackgroundColor(Color.parseColor("#afe3ff"));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new Fragment());
                fragmentTransaction.commit();
            }
        });
    }
}
