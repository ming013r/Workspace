package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoList extends AppCompatActivity {
    WebApi webapi;
    String token;
    int cid;
    Intent it_prev;
    ArrayAdapter<String> listadapter;

    //View objects
    ListView vlist;

    List<VideoModel> videoListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        webapi = new WebApi();
        setParas();
        videoListModel=getVideoList();

        vlist=(ListView)findViewById(R.id.lv_video);

        listadapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,videoListModel);
        vlist.setAdapter(listadapter);

        vlist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                GoIndex( position);
            }
        });

    }

    void GoIndex(int position)
    {
        Intent it_go = new Intent();
        it_go.putExtra("Name",String.valueOf(videoListModel.get(position).Name));
        it_go.putExtra("URL",videoListModel.get(position).URL);
        it_go.putExtra("token",token);
        it_go.putExtra("cid",cid);
        it_go.setClass(VideoList.this,WatchVideo.class);
        startActivity(it_go);
    }

    void setParas()
    {
        it_prev=getIntent();

        token=it_prev.getStringExtra("token");
        cid=it_prev.getIntExtra("cid",-1);
    }


    List<VideoModel> getVideoList()
    {
        List<VideoModel> videos =new ArrayList<VideoModel>();
        try{
            JSONArray jsonArray =new JSONArray(webapi.GET("QuizsApi/getVideo?cid="+cid));

            for(int i=0;i<jsonArray.length();i++)
            {
                VideoModel video=new VideoModel();
                JSONObject cour =jsonArray.getJSONObject(i);
                video.id=Integer.parseInt(cour.getString("id"));
                video.Name=cour.getString("name");
                video.URL=cour.getString("URL");
                videos.add(video);

            }

        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(VideoList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return videos;
    }
}
