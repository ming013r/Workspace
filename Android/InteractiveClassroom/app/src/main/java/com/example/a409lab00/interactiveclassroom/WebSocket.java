package com.example.a409lab00.interactiveclassroom;

import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 409LAB00 on 2017/11/11.
 */

public class WebSocket {
    public String getTitle(String JsonQuestionGroup)
    {
        String title="";
        try{
            JSONObject groupJsonObj =new JSONObject(JsonQuestionGroup);
            title=groupJsonObj.getString("title");
        }catch (JSONException e)
        {
            e.getMessage();
        }
        return title;
    }
    public int getCid(String JsonQuestionGroup)
    {
        int cid=-1;
        try{
            JSONObject groupJsonObj =new JSONObject(JsonQuestionGroup);
            cid=Integer.parseInt(groupJsonObj.getString("cid"));
        }catch (JSONException e)
        {
            e.getMessage();
        }

        return cid;
    }
    public List<classQuestion> JoinNewGroup(String JsonQuestionGroup,int way)//way 0push  1pull  2review
    {
        List<classQuestion> queue= new ArrayList<>();
        try{
            QuestionGroup questionGroup =new QuestionGroup();
            JSONObject groupJsonObj =new JSONObject(JsonQuestionGroup);
            questionGroup.id=Integer.parseInt(groupJsonObj.getString("id"));
            questionGroup.cid=Integer.parseInt(groupJsonObj.getString("cid"));
            questionGroup.title=groupJsonObj.getString("title");




            if(way==0)
            {
                questionGroup.questions=groupJsonObj.getString("questions");
                int k =2;
            }
            else
            {
                questionGroup.questions=groupJsonObj.getString("questionList");
            }
            List<classQuestion> questionList=ParseQuestion(questionGroup.questions);

                if(queue.size()==0)
                {
                    for (classQuestion item:questionList) {
                        queue.add(item);
                    }
                }


        }catch (JSONException e)
        {
            e.getMessage();
            String k="";
        }
        return  queue;


    }
    public List<classQuestion> ParseQuestion(String JsonQuestion)
    {

            List<classQuestion> questionList=new ArrayList<classQuestion>();
            try{
                JSONArray jsonArray =new JSONArray(JsonQuestion);
                for(int i=0;i<jsonArray.length();i++)
                {
                    classQuestion question=new classQuestion();
                    JSONObject que =jsonArray.getJSONObject(i);

                    question.id=Integer.parseInt(que.getString("id"));
                    question.content=que.getString("content");
                    question.gid=Integer.parseInt(que.getString("gid"));
                    question.items=que.getString("items");
                    question.type=Integer.parseInt(que.getString("type"));
                    question.itemCount=Integer.parseInt(que.getString("itemCount"));

                    question.explain=que.getString("explain");
                    question.explain_url=que.getString("explain_url");

                    questionList.add(question);
                }

            }catch(JSONException e){
                e.getMessage();
                e.printStackTrace();
            }
            return questionList;

    }
    public SocketBroadcastModel getBroadcast(String Json)
    {
        SocketBroadcastModel broadcast =new SocketBroadcastModel();
        try
        {
            JSONObject broadcastObj =new JSONObject(Json);
            broadcast.title=broadcastObj.getString("title");
            broadcast.cid=Integer.parseInt(broadcastObj.getString("cid"));
            broadcast.content=broadcastObj.getString("content");
            broadcast.type=broadcastObj.getString("type");
        }catch (JSONException e)
        {
            e.getMessage();
        }

        return broadcast;
    }
    class SocketBroadcastModel
    {
        public int cid;
        public String title;
        public  String content;
        public String type;
    }
}
