package com.example.a409lab00.interactiveclassroom;

/**
 * Created by 409LAB00 on 2017/11/11.
 */

public class Models {
}
 class Course
{
    public int id;
    public String name;
    @Override
    public String toString() {
        return name;
    }
}
class QuestionGroup
{
    public int id;
    public String title;
    public int cid;
    public String questions;

}
class classQuestion {
    public int id;
    public String content;
    public int gid;
    public String items;
    public int type; //1 單選  2 多選
    public int itemCount;
    public String explain;
    public String explain_url;

    public String answer;
}
class Answer
{
    public String item;
    public String text;
    public String chked;
}
class Broadcast
{
    public int id;
    public String title;
    public String content;
    public String type;
    @Override
    public String toString() {
        if(title.length()>=25)
        {
            String out = title.substring(0,24)+"...";
            return  out;
        }
        else
        {
            return title;
        }

    }
}
class Device {
    public int id;
    public String Name;
    public String remark;
    public String UUID;
    public int TXPower;
    public int RSSI;
    public int major;
    public int minor;
    public String mac;
    public int timeout;

    @Override
    public String toString()
    {
        switch (major)
        {
            case 33: remark="[CMA0406教室(講台)]"; break;
            case 65534: remark="[CMA0406教室(講台)]"; break;
            default : remark="BluetoothBeacon";
        }
        return  this.remark + "  ;  " +"\n"+ "[訊號強度(rssi) :" + this.RSSI + "]";
    }


}
