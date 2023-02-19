package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.example.weather.adapter.ForecastAdapter;
import com.google.gson.Gson;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 以下是夜间模式相关
    SharedPreferences night_mode;
    int start, now, end;

    SharedPreferences view_state;

    SwipeRefreshLayout swipeRefresh;//下拉刷新
    public String TAG;
    private TextView temp_today;//今日温度
    private TextView temp_now_textview;//当前温度
    private TextView weather_now_textview;//当前天气
    private TextView winddir_now_textview;//当前风向
    private TextView windscale_now_textview;//当前风力等级
    private TextView windspeed_now_textview;//当前风速
    private TextView humidity_now_textview;//当前相对湿度
    private TextView precip_now_textview;//降水量
    private TextView pressure_now_textview;//大气压强
    private TextView vis_now_textview;//能见度
    private TextView dew_now_textview;//当前云量

    private TextView aqi_primary, aqi_category, aqi_pm2p5;//空气质量相关

    ImageButton button_city_list;//城市列表按钮
    ImageButton button_setting;//设置按钮

    private TextView spt, cw, drsg, comf;//生活建议

    MyListView listView;
    String weather_id;
    private TextView county_name;
    SharedPreferences last_county;
    SharedPreferences weather_today;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        LocationClient.setAgreePrivacy(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


     //   Intent update_intent=new Intent(this,AutoUpdateService.class);
     //   startService(update_intent);
      //  weather_today = getSharedPreferences("weather_today", MODE_PRIVATE);//存储今日天气部分数据，尝试在通知栏展示
        view_state = getSharedPreferences("view_state", MODE_PRIVATE);//读取控件显示状态，初始化控件

        if(view_state.getBoolean("service_state",false)){
            Intent service_intent = new Intent(this, WeatherService.class);
            startService(service_intent);}//启动通知栏服务

        InitLayout();
        night_mode = getSharedPreferences("night_mode", MODE_PRIVATE);//获取夜间模式相关状态信息
        start = night_mode.getInt("start_hour", 0) * 60 + night_mode.getInt("start_minute", 0);
        end = night_mode.getInt("end_hour", 0) * 60 + night_mode.getInt("end_minute", 0);
        Calendar calendar = Calendar.getInstance();
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.
                get(Calendar.MINUTE);//将夜间模式起始时间，当前时间，结束时间的值转换，比较大小
        if (night_mode.getBoolean("switch_time", false)) {//检测夜间模式定时开关状态
            if ((start <= now && now < end) || (end < start && start <= now) || (now < end && end < start)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//启用夜间模式
                night_mode.edit().putBoolean("switch_mode", true).commit();//将夜间模式开关状态记录为开启
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//关闭夜间模式
                night_mode.edit().putBoolean("switch_mode", false).commit();////将夜间模式开关状态记录为关闭
            }
        }
        if (night_mode.getBoolean("switch_mode", false)) {//检测夜间模式开关状态
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//启用夜间模式
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//关闭夜间模式
        }


        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//下拉刷新重载页面
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "刷新中", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.forecast_lv);
        button_city_list = (ImageButton) findViewById(R.id.city_list);
        button_setting = (ImageButton) findViewById(R.id.setting);
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //编写设置的代码，进入设置页面

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();

            }
        });
        button_city_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入选择城市页面

                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        last_county = getSharedPreferences("last_county", MODE_PRIVATE);
        Intent intent = getIntent();
        weather_id = intent.getStringExtra("weather_id");//获取传入的id

        if (weather_id != null) {
            SharedPreferences.Editor editor = last_county.edit();
            editor.putString("id", weather_id);
            editor.commit();

        }

        temp_today = (TextView) findViewById(R.id.temp_today);//今日温度文本显示框
        weather_now_textview = (TextView) findViewById(R.id.weather_now);//当前天气显示文本框
        temp_now_textview = (TextView) findViewById(R.id.temperature_now);//当前温度显示文本框
        winddir_now_textview = (TextView) findViewById(R.id.winddir_now);//当前风向文本显示框
        windscale_now_textview = (TextView) findViewById(R.id.windscale_now);//当前风力等级显示文本框
        windspeed_now_textview = (TextView) findViewById(R.id.windspeed_now);//当前风速显示文本框
        humidity_now_textview = (TextView) findViewById(R.id.humidity_now);//相对湿度显示文本框
        precip_now_textview = (TextView) findViewById(R.id.precip_now);//降水量显示文本框
        pressure_now_textview = (TextView) findViewById(R.id.pressure_now);//大气压强显示文本框
        vis_now_textview = (TextView) findViewById(R.id.vis_now);//能见度显示文本框
        dew_now_textview = (TextView) findViewById(R.id.dew_now);//当前云量显示文本框


        aqi_primary = (TextView) findViewById(R.id.aqi_primary);
        aqi_category = (TextView) findViewById(R.id.aqi_category);
        aqi_pm2p5 = (TextView) findViewById(R.id.aqi_pm2_5);//空气质量相关显示框

        spt = (TextView) findViewById(R.id.spt);
        cw = (TextView) findViewById(R.id.cw);
        drsg = (TextView) findViewById(R.id.drsg);
        comf = (TextView) findViewById(R.id.comf);//生活建议相关显示框

        county_name = (TextView) findViewById(R.id.county_name);//城市名称显示文本框


        HeConfig.init("HE2301031356041355", "210e7e865a58471ca4dee69484f722ff");//初始化key
        HeConfig.switchToDevService();//切换为免费版
        QWeather.getWeatherNow(MainActivity.this, last_county.getString("id", null),
                Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherNowBean weatherBean) {
                        Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                        //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因

                        if (Code.OK == weatherBean.getCode()) {
                            WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//接下来开始从网络获取对应的数据来更新显示

                                    weather_now_textview.setText(now.getText() + " ");
                                    temp_now_textview.setText(now.getTemp() + "℃");
                                    winddir_now_textview.setText(now.getWindDir());
                                    windscale_now_textview.setText(now.getWindScale() + "级");
                                    windspeed_now_textview.setText(now.getWindSpeed() + "公里/小时");
                                    humidity_now_textview.setText(now.getHumidity());
                                    precip_now_textview.setText(now.getPrecip() + "mm");
                                    pressure_now_textview.setText(now.getPressure() + "hPa");
                                    vis_now_textview.setText(now.getVis() + "公里");
                                    dew_now_textview.setText(now.getDew());

                                }
                            });
                        } else {
                            //在此查看返回数据失败的原因
                            Code code = weatherBean.getCode();
                            Log.i(TAG, "failed code: " + code);
                        }
                    }
                });
        QWeather.getGeoCityLookup(MainActivity.this, last_county.getString("id", null),
                new QWeather.OnResultGeoListener() {    //通过城市id来确定城市信息
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(GeoBean geoBean) {
                        if (Code.OK == geoBean.getCode()) {
                            List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                            handler.post((new Runnable() {      //通过城市id确定城市名字并将城市名称文本显示框更新
                                @Override
                                public void run() {
                                    county_name.setText(locationBean.get(0).getName() +
                                            "    " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                                }
                            }));
                        }
                    }
                });

        QWeather.getWeather7D(MainActivity.this, last_county.getString("id", null),
                new QWeather.OnResultWeatherDailyListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherDailyBean dailyBean) {
                        if (Code.OK == dailyBean.getCode()) {
                            List<WeatherDailyBean.DailyBean> dailyBeanList = dailyBean.getDaily();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//接下来开始从网络获取对应的数据来更新显示
                                    List<Forecast> forecastList = new ArrayList<>();
                                    temp_today.setText(dailyBeanList.get(0).getTempMin() +
                                            "-" + dailyBeanList.get(0).getTempMax() + "℃");
                                    for (int i = 0; i < dailyBeanList.toArray().length; i++) {
                                        Forecast forecast = new Forecast(dailyBeanList.get(i).getFxDate(),
                                                dailyBeanList.get(i).getTextDay(), dailyBeanList.get(i).getTempMin()
                                                + "-" + dailyBeanList.get(i).getTempMax() + "℃");
                                        forecastList.add(forecast);
                                    }
                                    ForecastAdapter adapter = new ForecastAdapter(MainActivity.this,
                                            R.layout.forecast_item, forecastList);
                                    listView.setAdapter(adapter);
                                }
                            });
                        }
                    }


                });
        QWeather.getAirNow(MainActivity.this, last_county.getString("id", null),
                Lang.ZH_HANS, new QWeather.OnResultAirNowListener() {

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if (Code.OK == airNowBean.getCode()) {
                    AirNowBean.NowBean now = airNowBean.getNow();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            aqi_primary.setText("主要污染:" + now.getPrimary());
                            aqi_category.setText("空气质量" + now.getCategory() + " " + now.getAqi());
                            aqi_pm2p5.setText("pm2.5:" + now.getPm2p5());
                        }
                    });
                }
            }
        });

        //生活指数相关
        ArrayList<IndicesType> life = new ArrayList<>();
        life.add(IndicesType.SPT);//运动指数
        life.add(IndicesType.CW);//洗车指数
        life.add(IndicesType.DRSG);//穿衣指数
        life.add(IndicesType.COMF);//舒适度指数

        QWeather.getIndices1D(MainActivity.this, last_county.getString("id", null),
                Lang.ZH_HANS, life, new QWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                if (Code.OK == indicesBean.getCode()) {
                    List<IndicesBean.DailyBean> life = indicesBean.getDailyList();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            spt.setText("运动建议：" + life.get(0).getText());
                            cw.setText("洗车建议：" + life.get(1).getText());
                            drsg.setText("穿衣建议：" + life.get(2).getText());
                            comf.setText("舒适度：" + life.get(3).getText());
                        }
                    });
                }
            }
        });

    }

    private void InitLayout() {
        LinearLayout air = (LinearLayout) findViewById(R.id.air);
        LinearLayout forecast = (LinearLayout) findViewById(R.id.forecast);
        LinearLayout wind = (LinearLayout) findViewById(R.id.wind);
        LinearLayout life = (LinearLayout) findViewById(R.id.life);
        LinearLayout other = (LinearLayout) findViewById(R.id.other);
        if (!view_state.getBoolean("air_state", true))
            air.setVisibility(View.GONE);
        if (!view_state.getBoolean("forecast_state", true))
            forecast.setVisibility(View.GONE);
        if (!view_state.getBoolean("wind_state", true))
            wind.setVisibility(View.GONE);
        if (!view_state.getBoolean("life_state", true))
            life.setVisibility(View.GONE);
        if (!view_state.getBoolean("other_state", true))
            other.setVisibility(View.GONE);
    }


}