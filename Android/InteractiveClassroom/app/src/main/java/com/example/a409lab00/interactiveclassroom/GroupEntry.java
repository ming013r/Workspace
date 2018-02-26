package com.example.a409lab00.interactiveclassroom;

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

public class GroupEntry extends AppCompatActivity {
    public  String token;
    public  int cid;
    public ArrayAdapter<QGroup> GroupAdapter;
    List<QGroup>  GroupList=new ArrayList<QGroup>();
    WebSocket websocket;
    WebApi webApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_entry);
        websocket =new WebSocket();
        webApi=new WebApi();
        Intent it =getIntent();
        token = it.getStringExtra("token");
        cid=it.getIntExtra("cid",-1);



        GroupList=currentQuestions(cid);
        ListView LV_GroupList =(ListView)findViewById(R.id.gListView);
        GroupAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,GroupList);
        LV_GroupList.setAdapter(GroupAdapter);
        LV_GroupList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {

                String jsonQuestion=webApi.GET("QuestionAPI/GetQuestion?gid="+GroupList.get(position).id);

                List<classQuestion> QuestionQueue=websocket.JoinNewGroup(jsonQuestion,2);

                if(QuestionQueue.size()==0)
                {
                    Toast.makeText(GroupEntry.this,"沒有題目",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent ITquestion=new Intent();
                    ITquestion.setClass(GroupEntry.this,PopQuestion.class);
                    ITquestion.putExtra("stop",GroupList.get(position).id);
                    ITquestion.putExtra("gid",GroupList.get(position).id);
                    ITquestion.putExtra("jsonQ",jsonQuestion);
                    ITquestion.putExtra("way",1);
                    ITquestion.putExtra("Gtitle",websocket.getTitle(jsonQuestion));
                    ITquestion.putExtra("token",token);
                    startActivity(ITquestion);
                }



            }
        });
    }











    List<QGroup> currentQuestions(int cid)
    {
        WebApi webapi =new WebApi();



        String  jsonGroups=webapi.GET("QuestionAPI/GetQuestionGroup?cid="+cid);

        List<QGroup> groups =new ArrayList<QGroup>();
        try{
            JSONArray jsonArray =new JSONArray(jsonGroups);
            for(int i=0;i<jsonArray.length();i++)
            {
                QGroup group=new QGroup();
                JSONObject que =jsonArray.getJSONObject(i);

                group.id=Integer.parseInt(que.getString("id"));
                group.title=que.getString("title");
                group.stop=que.getString("stop");

                groups.add(group);
            }

        }catch(JSONException e){
            e.printStackTrace();
        }


        return groups;
    }
    class QGroup
    {
        public int id;
        public String title;
        public  String stop;
        @Override
       public String toString()
        {
            return "["+title+"]"+"　　結束日期："+stop;
        }
    }
}
