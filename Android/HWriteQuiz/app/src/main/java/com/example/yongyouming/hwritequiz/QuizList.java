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

public class QuizList extends AppCompatActivity {
    ListView lv_quiz;


    ArrayAdapter<String> listAdapter;
    WebApi webapi;
    List<QuizesModel> QuizList;
    Intent it_prev;
    int cid;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        lv_quiz = (ListView) findViewById(R.id.lv_quiz);
        setParas();
        webapi = new WebApi();

        QuizList = new ArrayList<QuizesModel>();
        AddtoList(webapi.GET("QuizsApi/GetQuizPart?status=1&qid=&token="));

        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,QuizList);
        lv_quiz.setAdapter(listAdapter);
        lv_quiz.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                startQuiz(QuizList.get(position).id);
            }
        });
    }

    void startQuiz(int qid)
    {
        Intent it_go=new Intent();
        it_go.putExtra("qid",qid);
        it_go.putExtra("cid",cid);
        it_go.putExtra("token",token);

        it_go.setClass(QuizList.this,OnQuiz.class);
        startActivity(it_go);
    }

    void AddtoList(String jsonstring) {
        try {
            JSONArray jsonArray = new JSONArray(jsonstring);

            for (int i = 0; i < jsonArray.length(); i++) {
                QuizesModel qui = new QuizesModel();
                JSONObject quiz = jsonArray.getJSONObject(i);
                qui.id = Integer.parseInt(quiz.getString("id"));
                qui.Name = quiz.getString("Name");
                qui.DateTime = quiz.getString("DateTime");
                qui.remark = quiz.getString("remark");
                QuizList.add(qui);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(QuizList.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void setParas()
    {
        it_prev=getIntent();
        cid=it_prev.getIntExtra("cid",-1);
        token=it_prev.getStringExtra("token");
    }


}
