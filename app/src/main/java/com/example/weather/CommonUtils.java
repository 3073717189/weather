package com.example.weather;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//自定义通用工具类
public class CommonUtils {

    //显示短消息
    public static void showShortMsg(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    //显示长消息
    public static void showLongMsg(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    //显示消息对话框
    public static void showDialogMsg(Context context,String message){
        new AlertDialog.Builder(context).
                setTitle("提示信息").
                setMessage(message).
                setPositiveButton("确定",null).
                setNegativeButton("取消",null).
                create().show();
    }

    //获取当前时间的字符串
    public static String getDateStrFromNow() {
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
