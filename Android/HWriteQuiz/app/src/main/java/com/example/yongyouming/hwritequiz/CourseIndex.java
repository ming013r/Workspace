package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CourseIndex extends AppCompatActivity {

    Intent it_prev;
    String token;
    int cid;


    ////View objects
    Button btn_video,btn_pastquiz,btn_quizlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        setParameters();
        btn_pastquiz=(Button)findViewById(R.id.pastQuiz);
        btn_video=(Button)findViewById(R.id.videos);
        btn_quizlist=(Button)findViewById(R.id.qlist_btn);

        setButtonListener();



    }


    void setButtonListener()
    {
        btn_pastquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(0);
            }
        });
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(1);
            }
        });
        btn_quizlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goFeature(2);
            }
        });
    }
    void goFeature(int feature)
    {
        Intent it_go=new Intent();
        it_go.putExtra("token",token);
        it_go.putExtra("cid",cid);

        switch (feature)
        {
            case 0:
                break;
            case 1:
                it_go.setClass(CourseIndex.this,VideoList.class);
                startActivity(it_go);
                break;
            case 2:
                break;
        }
    }

    void setParameters()
    {
        it_prev=getIntent();

        token=it_prev.getStringExtra("token");
        cid=it_prev.getIntExtra("cid",-1);

    }
}
