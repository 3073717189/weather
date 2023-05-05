package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.weather.citydb.CityDBHelper;
import com.example.weather.citylist.CityList;
import com.example.weather.service.WeatherService;
import com.qq.e.ads.ADActivity;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    Switch night_mode_switch;//夜间模式开关
    SharedPreferences night_mode;//夜间模式相关状态信息
    SharedPreferences view_state;//记录控件显示状态信息
    SharedPreferences sp_userName;//当前登录用户名
    SharedPreferences last_county;//当前城市信息

    Switch air_switch,forecast_switch,wind_switch,life_switch,other_switch,hourly_switch;
    private int start_hourOfDay, start_minute, end_hourOfDay, end_minute;

    private UserDataDao userDataDao;//用户数据数据操作类实例
    private UserCityDao userCityDao;//城市数据数据操作类实例

    String TAG;

    //以下是夜间模式时间段相关
    private TextView start_time;
    private TextView end_time;
    private TimePickerDialog start_timeDialog, end_timeDialog;
    private Switch night_time_switch;
    private Switch service_switch;


    private Handler mainHandler;

    //账户相关
    private TextView textView_login;
    private TextView textView_upload;
    private TextView textView_userName;
    private TextView textView_download;
    private TextView textView_exitLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        userDataDao=new UserDataDao();
        userCityDao=new UserCityDao();
        mainHandler=new Handler(getMainLooper());

        night_mode = getSharedPreferences("night_mode", MODE_PRIVATE);//获取夜间模式相关状态信息
        view_state = getSharedPreferences("view_state", MODE_PRIVATE);//获取控件显示状态相关信息
        sp_userName=getSharedPreferences("user_name",MODE_PRIVATE);//获取当前登录用户名信息
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);//获取当前存储的默认城市信息
         InitSwitch();//初始化控件显示开关

//设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"，此活动有标题栏，将该属性放置标题栏
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);



        night_mode_switch = (Switch) findViewById(R.id.night_mode_switch);//夜间模式开关
        night_mode_switch.setChecked(night_mode.getBoolean("switch_mode", false));//读取存储的夜间模式状态，确定此时夜间模式开关的状态

        night_time_switch = (Switch) findViewById(R.id.night_time_switch);//夜间模式开关
        night_time_switch.setChecked(night_mode.getBoolean("switch_time", false));//读取存储的定时夜间模式状态，确定此时夜间模式开关的状态
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
        //账户相关的控件实例化以及点击事件
        textView_login=findViewById(R.id.setting_login);
        if(!TextUtils.isEmpty(sp_userName.getString("name",null)))
        {textView_login.setText("切换账户");}
        textView_upload=findViewById(R.id.setting_upload);

        textView_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(sp_userName.getString("name",null))){
                    CommonUtils.showDialogMsg(getApplicationContext(),"请先登录账户");
                }
                else {
                doUpLoad();}
            }
        });
        textView_userName=findViewById(R.id.setting_userName);
        if(TextUtils.isEmpty(sp_userName.getString("name",null)))
        {
            textView_userName.setText("未登录,请先登录");
        }else {textView_userName.setText("当前用户  :  "+sp_userName.getString("name",null));}
        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        textView_download=findViewById(R.id.setting_download);
        textView_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(sp_userName.getString("name",null))){
                    CommonUtils.showDialogMsg(getApplicationContext(),"请先登录账户");
                }
                else {
                    doDownload();}
            }

            private void doDownload() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //查找数据库中是否存在此用户
                        final UserDatainfo searchItem=userDataDao.getUserByUserName(sp_userName.getString("name",null));
                        if(searchItem==null){
                            //未查找到，提示用户数据库中不存在相关信息
                            CommonUtils.showDialogMsg(getApplicationContext(),"数据库中未上传过此账户的信息");
                        }
//                查找到，执行下载操作
                        else {
                            //将数据库中存储的sp数据下载到本地，比如控件开关等
                            last_county.edit().putString("id",searchItem.getLast_county()).commit();
                            night_mode.edit().putInt("start_hour",searchItem.getStart_hour()).commit();
                            night_mode.edit().putInt("start_minute",searchItem.getStart_minute()).commit();
                            night_mode.edit().putInt("end_hour",searchItem.getEnd_hour()).commit();
                            night_mode.edit().putInt("end_minute",searchItem.getEnd_minute()).commit();
                            night_mode.edit().putBoolean("switch_mode",searchItem.isSwitch_mode()).commit();
                            night_mode.edit().putBoolean("switch_time",searchItem.isSwitch_time()).commit();
                            view_state.edit().putBoolean("air_state",searchItem.isAir_state()).commit();
                            view_state.edit().putBoolean("forecast_state",searchItem.isForecast_state()).commit();
                            view_state.edit().putBoolean("wind_state",searchItem.isWind_state()).commit();
                            view_state.edit().putBoolean("life_state",searchItem.isLife_state()).commit();
                            view_state.edit().putBoolean("other_state",searchItem.isOther_state()).commit();
                            view_state.edit().putBoolean("hourly_state",searchItem.isHourly_state()).commit();
                            view_state.edit().putBoolean("service_state",searchItem.isService_state()).commit();

                            //将数据库中存储的城市信息下载到本地，首先要删除本地存储的城市信息
                            CityDBHelper dbHelper = new CityDBHelper(getApplicationContext());
                            dbHelper.deleteAllCities();
                            //下面开始将数据库中的城市信息下载到本地数据库

                            final List<Cityinfo> cityList=userCityDao.getCityByUserName(sp_userName.getString("name",null));
                            for (Cityinfo city : cityList) {
                                // 将从数据库中返回的集合遍历并储存在本地数据库
                                System.out.println(city.getCity_name());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("id", city.getCity_id());
                                values.put("name", city.getCity_name());
                                db.insert("city", null, values);
                            }
                            dbHelper.close();

                        }
                    }
                }).start();
                //下载数据成功，返回主页并销毁当前活动
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textView_exitLogin=findViewById(R.id.setting_exitLogin);
        textView_exitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp_userName.edit().putString("name",null).commit();
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void doUpLoad() {




        final UserDatainfo item=new UserDatainfo();

        item.setLast_county(last_county.getString("id", null));
        item.setUser_name(sp_userName.getString("name",null));
        item.setStart_hour(night_mode.getInt("start_hour", 0));
        item.setStart_minute(night_mode.getInt("start_minute", 0));
        item.setEnd_hour(night_mode.getInt("end_hour", 0));
        item.setEnd_minute(night_mode.getInt("end_minute", 0));
        item.setSwitch_mode(night_mode.getBoolean("switch_mode", false));
        item.setSwitch_time(night_mode.getBoolean("switch_time", false));
        item.setAir_state(view_state.getBoolean("air_state",true));
        item.setForecast_state(view_state.getBoolean("forecast_state",true));
        item.setWind_state(view_state.getBoolean("wind_state",true));
        item.setLife_state(view_state.getBoolean("life_state",true));
        item.setOther_state(view_state.getBoolean("other_state",true));
        item.setHourly_state(view_state.getBoolean("hourly_state",true));
        item.setService_state(view_state.getBoolean("service_state",false));
        new Thread(new Runnable() {
            @Override
            public void run() {
                //查找数据库中是否已存在同个用户上传的数据
                final UserDatainfo searchItem=userDataDao.getUserByUserName(sp_userName.getString("name",null));
                //先删除此用户已存储的城市列表信息
                userCityDao.delCity(sp_userName.getString("name",null));

                if(searchItem==null){
                    //未查找到，执行添加操作
                    final int iRow=userDataDao.addUser(item);
                }
//                查找到，执行修改操作
                else {final int iRow=userDataDao.editUser(item);}

                //以下开始遍历本地数据库中的城市并添加到MySQL数据库中
                CityDBHelper dbHelper = new CityDBHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] projection = {"id", "name"};
                String sortOrder = "name ASC";
                Cursor cursor = db.query("city", projection, null, null, null, null, sortOrder);
//遍历数据库

                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    final Cityinfo item=new Cityinfo();
                    item.setCity_name(name);
                    item.setCity_id(id);
                    item.setUser_name(sp_userName.getString("name",null));
                    final int iRow=userCityDao.addCity(item);
                }
                cursor.close();


                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setResult(1);//使用参数表示当前界面操作成功
                    }
                });
            }
        }).start();

        CommonUtils.showShortMsg(this,"上传成功");
    }


    public void InitSwitch(){//读取开关状态，当开关状态改变时记录开关状态
        air_switch=(Switch) findViewById(R.id.air_switch);
        forecast_switch=(Switch) findViewById(R.id.forecast_switch);
        wind_switch=(Switch) findViewById(R.id.wind_switch);
        life_switch=(Switch) findViewById(R.id.life_switch);
        other_switch=(Switch) findViewById(R.id.other_switch);
    service_switch=(Switch)findViewById(R.id.service_switch);
    hourly_switch=(Switch)findViewById(R.id.hourly_switch);

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
    hourly_switch.setChecked(view_state.getBoolean("hourly_state",true));
    hourly_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            view_state.edit().putBoolean("hourly_state",b).commit();

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