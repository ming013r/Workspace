package com.example.yongyouming.hwritequiz;

/**
 * Created by yongyouming on 2018/2/7.
 */

public class Models {
}
class CourseModel
{
    public int Id;
    public String Room;
    public String Name;

    @Override
    public String toString(){
        return Name;
    }
}
class VideoModel
{
    public int id ;
    public String Name;
    public String URL;
    @Override
    public String toString()
    {
        return Name  ;
    }
}