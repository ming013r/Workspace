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


public class CourseEntry extends AppCompatActivity {

    ArrayAdapter<String> listadapter;
    List<CourseModel> CourseSrc;

    Intent it_prev;
    WebApi webapi;
    String token="";

    ////View declare
    ListView clist ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_entry);

        webapi =new WebApi();
        it_prev=getIntent();
        token= it_prev.getStringExtra("token");

        clist=(ListView)findViewById(R.id.lv_course);
        CourseSrc=ParseCourse(webapi.GET("QuizsApi/Course"));
        listadapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,CourseSrc);
        clist.setAdapter(listadapter);

        clist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                GoIndex( position);
            }
        });

    }

    public void GoIndex(int position)
    {

        Intent it_go = new Intent();
        it_go.putExtra("cid",CourseSrc.get(position).Id);
        it_go.putExtra("token",token);
        it_go.setClass(CourseEntry.this,CourseIndex.class);
        startActivity(it_go);
    }



    List<CourseModel> ParseCourse (String response)
    {
        List<CourseModel> courses =new ArrayList<CourseModel>();


        try{
            JSONArray jsonArray =new JSONArray(response);

            for(int i=0;i<jsonArray.length();i++)
            {
                CourseModel course=new CourseModel();
                JSONObject cour =jsonArray.getJSONObject(i);
                course.Id=Integer.parseInt(cour.getString("id"));
                course.Room=cour.getString("room");
                course.Name=cour.getString("name");

                courses.add(course);
            }

        }catch(JSONException e){
            e.printStackTrace();
            Toast.makeText(CourseEntry.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            return null;
        }

        return courses;
    }
}
