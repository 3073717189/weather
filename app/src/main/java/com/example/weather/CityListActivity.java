package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CityListActivity extends AppCompatActivity {
    SharedPreferences last_county;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);


    }
    public void onBackPressed(){
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);

        //如果是用户第一次打开尚未定位时，在城市列表选择了返回，那么认为用户是想退出软件，以下代码关闭软件
        if(last_county.getString("id",null)==null)
        {
            finish();
        }//若用户在此前已经选择过城市，那么认为用户是不想更改定位，为其返回至主页
        else {
        Intent intent=new Intent(CityListActivity.this,MainActivity.class);
        startActivity(intent);
        finish();}
    }
}