package com.example.a409lab00.interactiveclassroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BroadNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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


    Button btn_nor;
    Button btn_material;
    Button btn_que;
    Button btn_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        normal=new ArrayList<>();
        key=new ArrayList<>();
        material=new ArrayList<>();
        question=new ArrayList<>();

        btn_key=(Button)findViewById(R.id.b_imp);
        btn_material=(Button)findViewById(R.id.b_material);
        btn_que=(Button)findViewById(R.id.b_que);
        btn_nor=(Button)findViewById(R.id.b_normal);

        lv=(ListView)findViewById(R.id.broadList);

        setTitle("隨堂互動系統2.0");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);














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
        listAdapter_normal = new ArrayAdapter(BroadNav.this ,android.R.layout.simple_list_item_1,normal);
        listAdapter_key = new ArrayAdapter(BroadNav.this ,android.R.layout.simple_list_item_1,key);
        listAdapter_material = new ArrayAdapter(BroadNav.this ,android.R.layout.simple_list_item_1,material);
        listAdapter_question= new ArrayAdapter(BroadNav.this ,android.R.layout.simple_list_item_1,question);

        //default
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
                .setPositiveButton("知道",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid + "&content=知道&token=" + token, "");
                            }
                        }
                )
                .setNeutralButton("瞭解",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid + "&content=瞭解&token=" + token, "");
                            }
                        }
                )
                .setNegativeButton("忽略",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                webapi.POST("Broadcast_reply/PostBroadcast_reply?broadcast_id=" +bid
                                        + "&content=忽略&token=" + token, "");
                            }
                        }
                )
                .show();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.b_normal) {
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
        } else if (id == R.id.b_imp) {
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
        } else if (id == R.id.b_material) {
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
        } else if (id == R.id.b_que) {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
