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
class QuizesModel
{
    public int id;
    public String Name;
    public String DateTime;
    public String remark;
    @Override
    public String toString()
    {
        return "["+Name+"]"+"備註 ："+remark;
    }
}


class quizModel
{
    public int id;
    public String Name;
    public String DateTime;
    public String Image;
}
class answerModel
{
    public int id;
    public String Image;
    public int Score;
    public String  UsualMistake;
    @Override
    public String toString(){return "Answer Sample : ["+id+"]"+"分數 : "+Score;}
}
