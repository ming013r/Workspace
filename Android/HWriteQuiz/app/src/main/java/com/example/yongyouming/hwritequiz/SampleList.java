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

public class SampleList extends AppCompatActivity {
    private ArrayAdapter<String> listAdapter;

    WebApi webapi;
    int qid,cid;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);
        setParas();
        ListQuiz();

    }

    private void ToList(String jsonstring)
    {
        ListView lv= (ListView)findViewById(R.id.lv_sample);

        final List<answerModel> AnsL=new ArrayList<answerModel>();
        try{
            JSONArray jsonArray =new JSONArray(jsonstring);

            for(int i=0;i<jsonArray.length();i++)
            {
                answerModel ans=new answerModel();
                JSONObject answer =jsonArray.getJSONObject(i);
                ans.id=Integer.parseInt(answer.getString("id"));
                ans.Score=Integer.parseInt(answer.getString("Score"));
                ans.UsualMistake=answer.getString("UsualMistake");

                AnsL.add(ans);
            }
            listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,AnsL);
            lv.setAdapter(listAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
                {
                    CallDetail(AnsL.get(position).id);
                }
            });
        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(SampleList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void ListQuiz()
    {
        WebApi webApi=new WebApi();
        String jsonsrc=webApi.GET("QuizsApi/ListSample?qid="+qid);
        ToList(jsonsrc);
    }
    void CallDetail(int aid)
    {

        Intent it2=new Intent();
        it2.putExtra("aid",aid);
        it2.putExtra("qid",qid);
        it2.putExtra("cid",cid);
        it2.putExtra("token",token);
        it2.setClass(SampleList.this,SampleDisplay.class);
        startActivity(it2);
    }


    void setParas()
    {
        webapi=new WebApi();

        Intent it_prev=getIntent();
        qid=it_prev.getIntExtra("qid",-1);
        cid=it_prev.getIntExtra("cid",-1);
        token =it_prev.getStringExtra("token");
    }
}
