package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class QuizDetail extends AppCompatActivity {
    WebApi webapi;
    int qid,cid;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        setParas();

        quizModel quizmodel=new quizModel();
        answerModel ansmodel=new answerModel();
        try{
            String JsonSourceQuiz =webapi.GET("QuizsApi/GetQuiz?id="+qid);
            JSONObject qui =new JSONObject(JsonSourceQuiz);

            quizmodel.id=Integer.parseInt(qui.getString("id"));
            quizmodel.DateTime=qui.getString("Time_close");
            quizmodel.Image=qui.getString("image");
            quizmodel.Name=qui.getString("Name");

            String JsonSourceAns = webapi.GET("QuizsApi/GetAnswer?token="+token+"&qid="+qid);
            JSONObject ans = new JSONObject(JsonSourceAns);
            ansmodel.Image=ans.getString("file");
            ansmodel.Score=Integer.parseInt(ans.getString("Score"));
            ansmodel.UsualMistake=ans.getString("UsualMistake");



        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(QuizDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        TextView tv= (TextView)findViewById(R.id.pastQuizName);
        tv.setText("Quiz Name : "+quizmodel.Name);
        TextView score =(TextView)findViewById(R.id.tv_myScore);
        score.setText("Score : "+ansmodel.Score);
        TextView mistake =(TextView)findViewById(R.id.tv_myMistake);
        mistake.setText("常犯錯誤 : "+ansmodel.UsualMistake);



        final ImageView myans =(ImageView)findViewById(R.id.img_myans);
        Glide.with(this).load(Constants.dURL+ansmodel.Image).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                myans.setImageBitmap(resource);
            }
        });
        //zoom
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(myans);
        pAttacher.update();





        Button btn = (Button)findViewById(R.id.btn_goSample);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSample();
            }
        });

    }
    void gotoSample()
    {
        Intent it =new Intent();
        it.putExtra("qid",qid);
        it.putExtra("cid",cid);
        it.putExtra("token",cid);

        it.setClass(QuizDetail.this,SampleList.class);
        startActivity(it);
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
