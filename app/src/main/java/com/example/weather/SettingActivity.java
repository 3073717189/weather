package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;
import com.qq.e.comm.util.AdError;

import java.util.Locale;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    Switch night_mode_switch;//夜间模式开关
    SharedPreferences night_mode;//夜间模式相关状态信息
    SharedPreferences view_state;//记录控件显示状态信息
    Switch air_switch,forecast_switch,wind_switch,life_switch,other_switch;
    private int start_hourOfDay, start_minute, end_hourOfDay, end_minute;

    String TAG;

    //以下是夜间模式时间段相关
    private TextView start_time;
    private TextView end_time;
    private TimePickerDialog start_timeDialog, end_timeDialog;
    private Switch night_time_switch;
    private Switch service_switch;

    private Button ad_button;//广告按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        night_mode = getSharedPreferences("night_mode", MODE_PRIVATE);//获取夜间模式相关状态信息
        view_state = getSharedPreferences("view_state", MODE_PRIVATE);//获取控件显示状态相关信息
InitSwitch();//初始化控件显示开关

//设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"，此活动有标题栏，将该属性放置标题栏
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);


        ad_button=(Button)findViewById(R.id.ad_Button);
        ad_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingActivity.this,ADActivity.class);
                startActivity(intent);//进入广告页
            }
        });

        night_mode_switch = (Switch) findViewById(R.id.night_mode_switch);//夜间模式开关
        night_mode_switch.setChecked(night_mode.getBoolean("switch_mode", false));//读取存储的夜间模式状态，确定此时夜间模式开关的状态

        night_time_switch = (Switch) findViewById(R.id.night_time_switch);//夜间模式开关
        night_time_switch.setChecked(night_mode.getBoolean("switch_time", false));//读取存储的夜间模式状态，确定此时夜间模式开关的状态
        start_time = (TextView) findViewById(R.id.night_start_time);//夜间模式起始时间文本框
        end_time = (TextView) findViewById(R.id.night_end_time);//夜间模式结束时间文本框


        start_time.setText(night_mode.getInt("start_hour", 0) + ":" + night_mode.getInt("start_minute", 0));//初始化夜间模式起始时间
        end_time.setText(night_mode.getInt("end_hour", 0) + ":" + night_mode.getInt("end_minute", 0));//初始化夜间模式结束时间

        //以下是起始时间和结束时间的对话框实例化以及点击事件
        start_timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                start_time.setText(hourOfDay + ":" + minute);
                night_mode.edit().putInt("start_hour", hourOfDay).commit();
                night_mode.edit().putInt("start_minute", minute).commit();
            }
        }, start_hourOfDay, start_minute, true); // 最后一个参数设置是否为24小时制

        end_timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                end_time.setText(hourOfDay + ":" + minute);
                night_mode.edit().putInt("end_hour", hourOfDay).commit();
                night_mode.edit().putInt("end_minute", minute).commit();
            }
        }, end_hourOfDay, end_minute, true); // 最后一个参数设置是否为24小时制
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_timeDialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end_timeDialog.show();
            }
        });
        //////

        night_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    night_mode.edit().putBoolean("switch_mode", true).commit();//将夜间模式开关状态记录为开启
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//启用夜间模式
                } else {
                    night_mode.edit().putBoolean("switch_mode", false).commit();////将夜间模式开关状态记录为关闭
                    night_mode.edit().putBoolean("switch_time", false).commit();////将夜间模式定时开关状态记录为关闭
                    night_time_switch.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//关闭夜间模式
                }
            }
        });

        night_time_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    night_mode.edit().putBoolean("switch_time", true).commit();//将夜间模式定时开关状态记录为开启

                } else {
                    night_mode.edit().putBoolean("switch_time", false).commit();////将夜间模式定时开关状态记录为关闭

                }
            }
        });
    }
public void InitSwitch(){//读取开关状态，当开关状态改变时记录开关状态
        air_switch=(Switch) findViewById(R.id.air_switch);
        forecast_switch=(Switch) findViewById(R.id.forecast_switch);
        wind_switch=(Switch) findViewById(R.id.wind_switch);
        life_switch=(Switch) findViewById(R.id.life_switch);
        other_switch=(Switch) findViewById(R.id.other_switch);
    service_switch=(Switch)findViewById(R.id.service_switch);

        air_switch.setChecked(view_state.getBoolean("air_state",true));
    air_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("air_state",b).commit();

        }
    });
    forecast_switch.setChecked(view_state.getBoolean("forecast_state",true));
    forecast_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("forecast_state",b).commit();

        }
    });
    wind_switch.setChecked(view_state.getBoolean("wind_state",true));
    wind_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("wind_state",b).commit();

        }
    });
    life_switch.setChecked(view_state.getBoolean("life_state",true));
    life_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("life_state",b).commit();

        }
    });
    other_switch.setChecked(view_state.getBoolean("other_state",true));
    other_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("other_state",b).commit();

        }

    });
    service_switch.setChecked(view_state.getBoolean("service_state",false));
    service_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("service_state",b).commit();
if(b){Intent service_intent = new Intent(getApplicationContext(), WeatherService.class);
            startService(service_intent);}//启动通知栏服务
            else {
    Intent service_intent = new Intent(getApplicationContext(), WeatherService.class);
    stopService(service_intent);
}
        }

    });

}
public void onBackPressed(){

    Intent intent=new Intent(SettingActivity.this,MainActivity.class);
    startActivity(intent);
    finish();
}

}