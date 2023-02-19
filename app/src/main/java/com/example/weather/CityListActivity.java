package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class CityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
    }
    public void onBackPressed(){

        Intent intent=new Intent(CityListActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}