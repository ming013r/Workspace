package com.example.yongyouming.hwritequiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SampleDisplay extends AppCompatActivity {
    WebApi webapi;
    int qid,cid,aid;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_display);
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

            String JsonSourceAns = webapi.GET("QuizsApi/GetSample?ansid="+aid);
            JSONObject ans = new JSONObject(JsonSourceAns);
            ansmodel.Image=ans.getString("file");
            ansmodel.Score=Integer.parseInt(ans.getString("Score"));
            ansmodel.UsualMistake=ans.getString("UsualMistake");



        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(SampleDisplay.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        TextView tv= (TextView)findViewById(R.id.sampleQuizName);
        tv.setText("Quiz Name : "+quizmodel.Name);
        TextView score =(TextView)findViewById(R.id.tv_sampleScore);
        score.setText("Score : "+ansmodel.Score);
        TextView mistake =(TextView)findViewById(R.id.tv_sampleMistake);
        mistake.setText("常犯錯誤 : "+ansmodel.UsualMistake);



        final ImageView myans =(ImageView)findViewById(R.id.img_otherans);
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



    }


    void setParas()
    {
        webapi=new WebApi();

        Intent it_prev=getIntent();
        aid=it_prev.getIntExtra("aid",-1);
        qid=it_prev.getIntExtra("qid",-1);
        cid=it_prev.getIntExtra("cid",-1);
        token =it_prev.getStringExtra("token");
    }
}
