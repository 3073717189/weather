package com.example.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText et_userName;//用户名编辑框
    private EditText et_password;//密码编辑框

    private Handler mainHandler;//主线程

    private UserDao dao;//用户数据库操作类

    SharedPreferences sp_userName;//当前登录用户名

    private Button btn_login;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"，此活动有标题栏，将该属性放置标题栏
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏深色字体

        et_userName =(EditText) findViewById(R.id.login_et_userName);
        et_password =(EditText) findViewById(R.id.login_et_password);

        btn_login =(Button) findViewById(R.id.login_btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
        dao=new UserDao();
        mainHandler=new Handler(getMainLooper());//获取主线程

        btn_register =(Button) findViewById(R.id.login_btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至注册页面
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //执行登录操作
    private void doLogin() {
        final String userName=et_userName.getText().toString().trim();
        final String userPass=et_password.getText().toString().trim();
        if(TextUtils.isEmpty(userName)){
            CommonUtils.showShortMsg(this,"请输入用户名");
            et_userName.requestFocus();
        }else if(TextUtils.isEmpty(userPass)){
            CommonUtils.showShortMsg(this,"请输入用户密码");
            et_password.requestFocus();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Userinfo item =dao.getUserByUserNameAndUserPass(userName,userPass);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(item.getUserName()==null){
                                CommonUtils.showDialogMsg(LoginActivity.this,"用户名或密码错误");
                            }
                            else {
                                CommonUtils.showShortMsg(LoginActivity.this,"登录成功");
                                sp_userName=getSharedPreferences("user_name",MODE_PRIVATE);//获取当前登录用户名信息
                                sp_userName.edit().putString("name",item.getUserName()).commit();
                                Intent intent =new Intent(getApplicationContext(),SettingActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }).start();
        }
    }
    }
