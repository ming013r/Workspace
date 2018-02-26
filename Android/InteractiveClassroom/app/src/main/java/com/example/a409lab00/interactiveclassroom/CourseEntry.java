package com.example.a409lab00.interactiveclassroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseEntry extends AppCompatActivity {
    public ArrayAdapter<Course> courseAdapter;
    WebApi webapi=new WebApi();
    String token="";
    List<Course>  CourseList=new ArrayList<Course>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_entry);
        Intent it =getIntent();
         token = it.getStringExtra("token");

        setCourseList();


    }
    public void setCourseList()
    {

        CourseList=getCourse(token);

        ListView LV_courseList =(ListView)findViewById(R.id.CourseList);
        courseAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,CourseList);
        LV_courseList.setAdapter(courseAdapter);
        LV_courseList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent it =new Intent();
                it.setClass(CourseEntry.this,MainView.class);
                it.putExtra("cid",CourseList.get(position).id);
                it.putExtra("token",token);
                startActivity(it);


            }
        });
    }
    public List<Course> getCourse(String token)
    {
        List<Course> Courses=new ArrayList<Course>();
        String jsonString = webapi.POST("Broadcast_reply/GetCourse","token="+token);
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i =0;i<jsonArray.length();i++)
            {
                Course course =new Course();
                JSONObject eve = jsonArray.getJSONObject(i);
                course.id=Integer.parseInt(eve.getString("id"));
                course.name=eve.getString("name");
                Courses.add(course);
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return Courses;
    }
}
