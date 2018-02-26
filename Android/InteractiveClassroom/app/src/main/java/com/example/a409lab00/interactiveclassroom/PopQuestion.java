package com.example.a409lab00.interactiveclassroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 409LAB00 on 2017/11/11.
 */

public class PopQuestion extends Activity {
    List<classQuestion> QuestionQueue;
    WebSocket websocket =new WebSocket();
    WebApi webapi =new WebApi();
    List<Answer> currentAnswer;
    classQuestion currentQuestion;
    private Handler handler = new Handler();

    int currentIndex=0;
    String token;
    Boolean Achecked=false;
    Boolean Bchecked =false;
    Boolean Cchecked=false;
    Boolean Dchecked=false;
    int type= 1;///1 單選 2複選
    Button itemA,itemB,itemC,itemD;
    int itemCount=0;
    int way=-1;
    int gid=-1;
    String stop="";
    int secLast=0;
    long difftime;
    long hr;
    long min;
    long sec;


    TextView explain;
    Button explain_url;
    Button confirm;
    Button prev;
    Button next;
    Button getScore;

    TextView TV_content;
    TextView CountDown;
    TextView reminder;

    TextView TV_answerA;
    TextView TV_answerB;
    TextView TV_answerC;
    TextView TV_answerD;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popquestion);
        TV_content=(TextView)findViewById(R.id.Qcontent);
        TV_answerA =(TextView)findViewById(R.id.QAnswerA);
        TV_answerB =(TextView)findViewById(R.id.QAnswerB);
        TV_answerC =(TextView)findViewById(R.id.QAnswerC);
        TV_answerD =(TextView)findViewById(R.id.QAnswerD);
        reminder=(TextView)findViewById(R.id.remindCnt);
        CountDown =(TextView)findViewById(R.id.Timer);

        itemA =(Button)findViewById(R.id.ItemA);
        itemB =(Button)findViewById(R.id.ItemB);
        itemC =(Button)findViewById(R.id.ItemC);
        itemD =(Button)findViewById(R.id.ItemD);

        explain =(TextView)findViewById(R.id.explain);
        explain_url=(Button)findViewById(R.id.explain_url);
        confirm =(Button)findViewById(R.id.confirmAnswer);

        prev=(Button)findViewById(R.id.prev);
        next=(Button)findViewById(R.id.next);
        getScore =(Button)findViewById(R.id.getScore);
        prev.setBackgroundResource(R.drawable.ic_arrow_back_black_24px);
        next.setBackgroundResource(R.drawable.ic_arrow_forward_black_24px);
        getScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sco = "您的答題正確率為 : "+webapi.GET("QuestionAPI/getScore?gid="+gid+"&token="+token);
                Toast.makeText(PopQuestion.this, sco , Toast.LENGTH_SHORT).show();
            }
        });




        //離開
        Button exit =(Button)findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //DisplayMetrics dm =new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(dm);
        //int width =(int)(dm.widthPixels*0.8);
        //int height=(int)(dm.heightPixels*0.8);
        //getWindow().setLayout(width,height);


        currentQuestion=new classQuestion();

        Intent it =getIntent();
        String jsonQuestion=it.getStringExtra("jsonQ");
        String Gtitle=it.getStringExtra("Gtitle");
        token =it.getStringExtra("token");
        way = it.getIntExtra("way",-1);
        gid=it.getIntExtra("gid",-1);
        stop=webapi.GET("QuestionAPI/getGroupStop?gid="+gid);
        Date nowTime =new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("\"yyyy/MM/dd HH:mm\"");
            Date stopTime = sdf.parse(stop);
             difftime = stopTime.getTime() - nowTime.getTime();


        }catch(ParseException e){
            e.printStackTrace();
        }

        if(way!=2)
        {
            CountDown.setVisibility(View.VISIBLE);
            ///////////
            Runnable myRunnable =Timer();
            handler.removeCallbacks(myRunnable);
            // 設定間隔的時間
            handler.postDelayed(myRunnable, 1000);
            //////////
        }


        QuestionQueue=websocket.JoinNewGroup(jsonQuestion,way);


        TextView TV_title =(TextView)findViewById(R.id.GTitle);
        TV_title.setText(Gtitle);

        explain_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webapi.GET("LogAPI/SaveLog?name=點擊解釋教材"+"&content=點擊"+"&token="+token);
                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse(QuestionQueue.get(currentIndex).explain_url));
                startActivity(intent);
            }
        });



        if(QuestionQueue.size()==0)
        {
            finish();
        }
        ///設定第一題
        currentQuestion=QuestionQueue.get(0);
        type=currentQuestion.type;
        explain.setText(currentQuestion.explain);
        itemCount=currentQuestion.itemCount;
        TV_content.setText(currentQuestion.content);
        AnswerText(currentQuestion);
        reminder.setText("第"+(currentIndex+1)+"題(共"+QuestionQueue.size()+"題)");

        SetToDefault();
        uncheck();
        ///

        //確認作答
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    QuestionQueue.get(currentIndex).answer=assembleAnswer();
                    String selected=Achecked.toString()+","+Bchecked.toString()+","+Cchecked.toString()+","+Dchecked.toString();
                    String result=webapi.POST("QuestionAPI/AnswerQuestion","token="+token+"&selectedItem="+selected+"&qid="+QuestionQueue.get(currentIndex).id+"&gid="+gid);
                    String chk =result;
                Toast.makeText(PopQuestion.this,"答案已交",Toast.LENGTH_SHORT).show();
                confirm.setBackgroundColor(Color.WHITE);
            }
        });
        if(QuestionQueue.size()==1)
        {
            next.setVisibility(View.INVISIBLE);
        }
        prev.setVisibility(View.INVISIBLE);
        //上一題
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            prevQuestion();
            }
        });
        //下一題
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            nextQuestion();
            }
        });

        itemA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1)
                {
                    uncheck();
                    SetToDefault();
                    Achecked=true;
                    itemA.setBackgroundColor(Color.RED);
                }
                else
                {
                    if(Achecked==true)
                    {
                        Achecked=false;
                        itemA.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        Achecked=true;
                        itemA.setBackgroundColor(Color.RED);
                    }
                }


            }
        });
        itemB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1)
                {
                    uncheck();
                    SetToDefault();
                    Bchecked=true;
                    itemB.setBackgroundColor(Color.RED);
                }
                else
                {
                    if(Bchecked==true)
                    {
                        Bchecked=false;
                        itemB.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        Bchecked=true;
                        itemB.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        itemC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1)
                {
                    uncheck();
                    SetToDefault();
                    Cchecked=true;
                    itemC.setBackgroundColor(Color.RED);
                }
                else
                {
                    if(Cchecked==true)
                    {
                        Cchecked=false;
                        itemC.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        Cchecked=true;
                        itemC.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
        itemD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==1)
                {
                    uncheck();
                    SetToDefault();
                    Dchecked=true;
                    itemD.setBackgroundColor(Color.RED);
                }
                else
                {
                    if(Dchecked==true)
                    {
                        Dchecked=false;
                        itemD.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        Dchecked=true;
                        itemD.setBackgroundColor(Color.RED);
                    }

                }
            }
        });

    }
    private Runnable Timer()
    {

        Runnable aRunnable = new Runnable(){

            public void run(){
                if(way!=2)
                {
                    hr= difftime / (60 * 60 * 1000) % 24;
                    min=difftime / (60 * 1000) % 60;
                    sec=difftime / 1000 % 60;

                    if(hr<=0&&min<=0&&sec<=0)
                    {
                        finish();
                    }
                    CountDown.setTextColor(Color.RED);
                    CountDown.setText("剩餘時間："+hr+"時"+min+"分"+sec+"秒");

                    difftime-=1000;
                    handler.postDelayed(this, 1000);/////by api better
                }


            }
        };

        return aRunnable;
    };
    void nextQuestion()
    {
        currentIndex++;
        if(currentIndex>=QuestionQueue.size()-1)
            next.setVisibility(View.INVISIBLE);
        reminder.setText("第"+(currentIndex+1)+"題(共"+QuestionQueue.size()+"題)");

        prev.setVisibility(View.VISIBLE);

        currentQuestion=QuestionQueue.get(currentIndex);
        TV_content.setText(currentQuestion.content);
        type= currentQuestion.type;
        explain.setText(QuestionQueue.get(currentIndex).explain);
        itemCount=QuestionQueue.get(currentIndex).itemCount;

        AnswerText(currentQuestion);


        uncheck();//set all to false
        SetToDefault();

        recheck(currentQuestion);
    }
    void prevQuestion()
    {
        currentIndex--;
        if(currentIndex<=0)
            prev.setVisibility(View.INVISIBLE);

        reminder.setText("第"+(currentIndex+1)+"題(共"+QuestionQueue.size()+"題)");
        next.setVisibility(View.VISIBLE);

        currentQuestion=QuestionQueue.get(currentIndex);

        TV_content.setText(currentQuestion.content);
        type= currentQuestion.type;
        explain.setText(QuestionQueue.get(currentIndex).explain);
        itemCount=QuestionQueue.get(currentIndex).itemCount;

        AnswerText(currentQuestion);

        recheck(currentQuestion);

        uncheck();///set all to false
        SetToDefault();
        recheck(currentQuestion);

    }
    void uncheck()
    {

        Achecked=false;
        Bchecked=false;
        Cchecked=false;
        Dchecked=false;
    }
    void AnswerText(classQuestion ques)
    {
        currentAnswer=ParseAns(ques);
        String text="";
        String myans =webapi.POST("QuestionAPI/MyAnswer","token="+token+"&id="+ques.id+"&gid="+gid);
        String[] myansArr=new String[4];
        myansArr=myans.split(",");

        for (int i=0;i==currentAnswer.size()-1;i++) {
            if(way==2)//歷屆試卷模
            {
                switch (i){
                    case 0:
                        if(currentAnswer.get(i).chked.equals("true"))
                        {
                            TV_answerA.setText(currentAnswer.get(i).text+"*");
                        }
                        else
                        {
                            TV_answerA.setText(currentAnswer.get(i).text);
                        }
                        break;
                    case 1:
                        if(currentAnswer.get(i).chked.equals("true"))
                        {
                            TV_answerA.setText(currentAnswer.get(i).text+"*");
                        }
                        else
                        {
                            TV_answerA.setText(currentAnswer.get(i).text);
                        }
                        break;
                    case 2:
                        if(currentAnswer.get(i).chked.equals("true"))
                        {
                            TV_answerA.setText(currentAnswer.get(i).text+"*");
                        }
                        else
                        {
                            TV_answerA.setText(currentAnswer.get(i).text);
                        }
                        break;
                    case 3:
                        if(currentAnswer.get(i).chked.equals("true"))
                        {
                            TV_answerA.setText(currentAnswer.get(i).text+"*");
                        }
                        else
                        {
                            TV_answerA.setText(currentAnswer.get(i).text);
                        }
                        break;
                }

            }
            else//作答模式
            {
                switch (i){
                    case 0:
                        TV_answerA.setText(currentAnswer.get(i).text);
                        break;
                    case 1:
                        TV_answerB.setText(currentAnswer.get(i).text);
                        break;
                    case 2:
                        TV_answerC.setText(currentAnswer.get(i).text);
                        break;
                    case 3:
                        TV_answerD.setText(currentAnswer.get(i).text);
                        break;
                }
            }
            switch (i)
            {
                case 0:
                    if(myansArr.equals("true"))
                    {TV_answerA.setTextColor(Color.RED);}
                    break;
                case 1:
                    if(myansArr.equals("true"))
                    {TV_answerB.setTextColor(Color.RED);}
                    break;
                case 2:
                    if(myansArr.equals("true"))
                    {TV_answerC.setTextColor(Color.RED);}
                    break;
                case 3:
                    if(myansArr.equals("true"))
                    {TV_answerD.setTextColor(Color.RED);}
                    break;
            }
        }
    }
    String assembleAnswer()
    {
        String ansString="";
        if(Achecked)
        {
            ansString+="A,";
        }
        if(Bchecked)
        {
            ansString+="B,";
        }
        if(Cchecked)
        {
            ansString+="C,";
        }
        if(Dchecked)
        {
            ansString+="D,";
        }

        return  ansString;
    }
    void recheck(classQuestion question)
    {

        if(question.answer!=null)
        {
            String[] ans=question.answer.split(",");
            for (String item :ans) {

                if(item.equals("A"))
                {
                    Achecked=true;
                    itemA.setBackgroundColor(Color.RED);
                }
                if(item.equals("B"))
                {
                    Bchecked=true;
                    itemB.setBackgroundColor(Color.RED);
                }
                if(item.equals("C"))
                {
                    Cchecked=true;
                    itemC.setBackgroundColor(Color.RED);
                }
                if(item.equals("D"))
                {
                    itemD.setBackgroundColor(Color.RED);
                    Dchecked=true;
                }
            }
        }

    }
    void HideItem()
    {
        itemA.setVisibility(View.INVISIBLE);
        itemB.setVisibility(View.INVISIBLE);
        itemC.setVisibility(View.INVISIBLE);
        itemD.setVisibility(View.INVISIBLE);

    }
    void SetToDefault()
    {
        itemA.setBackgroundColor(Color.GREEN);
        itemB.setBackgroundColor(Color.GREEN);
        itemC.setBackgroundColor(Color.GREEN);
        itemD.setBackgroundColor(Color.GREEN);

        if(way==2)
        {
            explain.setVisibility(View.VISIBLE);
            explain_url.setVisibility(View.VISIBLE);
            getScore.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.INVISIBLE);

        }

        itemA.setVisibility(View.VISIBLE);
        itemB.setVisibility(View.VISIBLE);
        itemC.setVisibility(View.VISIBLE);
        itemD.setVisibility(View.VISIBLE);
        if(way==2)
        {
            HideItem();
        }

        switch (itemCount)
        {
            case 0:
                itemA.setVisibility(View.INVISIBLE);
                itemB.setVisibility(View.INVISIBLE);
                itemC.setVisibility(View.INVISIBLE);
                itemD.setVisibility(View.INVISIBLE);
            case 1:

                itemB.setVisibility(View.INVISIBLE);
                itemC.setVisibility(View.INVISIBLE);
                itemD.setVisibility(View.INVISIBLE);
            case 2:

                itemC.setVisibility(View.INVISIBLE);
                itemD.setVisibility(View.INVISIBLE);
            case 3:
                itemD.setVisibility(View.INVISIBLE);
            case 4:
                //do nothiing
        }
    }
    List<Answer> ParseAns(classQuestion question)
    {
        List<Answer> answers =new ArrayList<Answer>();
        String jsonAnswer=question.items;
        try{
            JSONArray jsonArray =new JSONArray(jsonAnswer);
            for(int i=0;i<jsonArray.length();i++)
            {
                Answer answer=new Answer();
                JSONObject ans =jsonArray.getJSONObject(i);

                answer.item=ans.getString("item");
                answer.text=ans.getString("text");
                answer.chked=ans.getString("chked");
                answers.add(answer);
            }



        }catch (JSONException e)
        {
            e.getMessage();
            e.printStackTrace();
        }

        return answers;
    }
}
