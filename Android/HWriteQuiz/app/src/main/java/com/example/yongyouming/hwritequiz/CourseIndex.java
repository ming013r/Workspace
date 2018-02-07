package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CourseIndex extends AppCompatActivity {

    Intent it_prev;
    String token;
    int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_index);

        setParameters();

    }





    void setParameters()
    {
        it_prev=getIntent();

        token=it_prev.getStringExtra("token");
        cid=it_prev.getIntExtra("cid",-1);
    }
}
