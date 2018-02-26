package com.example.a409lab00.interactiveclassroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ming on 2017/11/22.
 */

public class PopBroadcast  extends Activity{


    String token;
    List<Broadcast> broadcasts;


    WebApi webapi=new WebApi();

    //4lists
    List<Broadcast> normal;
    List<Broadcast> key;
    List<Broadcast> material;
    List<Broadcast> question;
    //4lists

    int bid=-1;

    ListView lv;

    public ArrayAdapter<Broadcast> listAdapter_normal;
    public ArrayAdapter<Broadcast> listAdapter_key;
    public ArrayAdapter<Broadcast> listAdapter_material;
    public ArrayAdapter<Broadcast> listAdapter_question;

    Button noramlbtn;
    Button keybtn;
    Button materialbtn;
    Button questionbtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popbroadcast);
        normal=new ArrayList<>();
        key=new ArrayList<>();
        material=new ArrayList<>();
        question=new ArrayList<>();


        keybtn=(Button)findViewById(R.id.key);
        noramlbtn=(Button)findViewById(R.id.normal);
        materialbtn=(Button)findViewById(R.id.material);
        questionbtn=(Button)findViewById(R.id.question);
        AllToGreen();
        noramlbtn.setBackgroundColor(Color.BLUE);
         lv =(ListView)findViewById(R.id.broadcasts);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels * 0.8);
        int height = (int) (dm.heightPixels * 0.8);
        getWindow().setLayout(width, height);

        Intent it = getIntent();
        String jsonQuestion = it.getStringExtra("jsonQ");
        token = it.getStringExtra("token");

        broadcasts = getBroadcast(jsonQuestion);

        if(broadcasts.size()!=0)
        {
            String nb ="一般公告";
            String nbb = broadcasts.get(0).type;
            for (int i = 0; i < broadcasts.size(); i++)
            {
                Broadcast current=broadcasts.get(i);
                if(current.type.equals("一般公告"))
                {
                    normal.add(current);
                }
                if(current.type.equals("授課重點"))
                {key.add(current);}
                if(current.type.equals("補充教材"))
                {material.add(current);}
                if(current.type.equals("學生提問"))
                {question.add(current);}
            }

        }
        listAdapter_normal = new ArrayAdapter(PopBroadcast.this ,android.R.layout.simple_list_item_1,normal);
        listAdapter_key = new ArrayAdapter(PopBroadcast.this ,android.R.layout.simple_list_item_1,key);
        listAdapter_material = new ArrayAdapter(PopBroadcast.this ,android.R.layout.simple_list_item_1,material);
        listAdapter_question= new ArrayAdapter(PopBroadcast.this ,android.R.layout.simple_list_item_1,question);

        //default
        lv.setAdapter(listAdapter_normal);
        noramlbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllToGreen();
                noramlbtn.setBackgroundColor(Color.BLUE);
                lv.setAdapter(listAdapter_normal);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                         bid=normal.get(position).id;
                        String title =normal.get(position).title;
                        String content =normal.get(position).content;
                        goAnswer(bid,title,content);
                    }
                });
            }
        });

        keybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllToGreen();
                keybtn.setBackgroundColor(Color.BLUE);
                lv.setAdapter(listAdapter_key);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                         bid=key.get(position).id;
                        String title =key.get(position).title;
                        String content =key.get(position).content;
                        goAnswer(bid,title,content);
                    }
                });
            }
        });

        materialbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllToGreen();
                materialbtn.setBackgroundColor(Color.BLUE);
                lv.setAdapter(listAdapter_material);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                         bid =material.get(position).id;
                        String title =material.get(position).title;
                        String content =material.get(position).content;
                        goAnswer(bid,title,content);


                    }
                });
            }
        });

        questionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllToGreen();
                questionbtn.setBackgroundColor(Color.BLUE);
                lv.setAdapter(listAdapter_question);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                    {
                        bid= question.get(position).id;
                        String title =question.get(position).title;
                        String content =question.get(position).content;
                        goAnswer(bid,title,content);
                    }
                });
            }
        });

    }
    public List<Broadcast> getBroadcast(String jsonstring)
    {

        List<Broadcast> broadcasts=new ArrayList<Broadcast>();
        try{
            JSONArray jsonArray =new JSONArray(jsonstring);

            for(int i=0;i<jsonArray.length();i++)
            {
                Broadcast broadcast=new Broadcast();
                JSONObject broad =jsonArray.getJSONObject(i);
                broadcast.id=Integer.parseInt(broad.getString("id"));
                broadcast.title=broad.getString("title");
                broadcast.content=broad.getString("content");
                broadcast.type=broad.getString("type");

                broadcasts.add(broadcast);
            }

        }catch(JSONException e){
            e.printStackTrace();

        }


        return broadcasts;
    }
    public void goAnswer(int id,String title,String content)
    {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("瞭解",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid + "&content=2&token=" + token, "");
                            }
                        }
                )
                .setNeutralButton("知道",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid + "&content=1&token=" + token, "");
                            }
                        }
                )
                .setNegativeButton("忽略",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid
                                        + "&content=0&token=" + token, "");
                            }
                        }
                )
                .show();
    }
    void AllToGreen()
    {
        noramlbtn.setBackgroundColor(Color.CYAN);
        keybtn.setBackgroundColor(Color.CYAN);
        questionbtn.setBackgroundColor(Color.CYAN);
        materialbtn.setBackgroundColor(Color.CYAN);
    }
}
