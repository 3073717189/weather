package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.example.weather.citydb.CityDBHelper;
import com.example.weather.citylist.CityList;
import com.example.weather.forecast.Forecast;
import com.example.weather.forecast.ForecastAdapter;
import com.example.weather.hourly.Hourly;
import com.example.weather.hourly.HourlyAdapter;
import com.example.weather.service.WeatherService;
import com.google.gson.Gson;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView_hourly;//初始化逐小时显示天气的rv
    private HourlyAdapter hourlyAdapter;//上述rv的适配器
    // 以下是夜间模式相关
    SharedPreferences night_mode;
    Boolean night_switch_state;
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
    private TextView feel_temp_now_textview;

    private TextView aqi_primary, aqi_category, aqi_pm2p5;//空气质量相关

    ImageButton button_city_list;//城市列表按钮
    ImageButton button_setting;//设置按钮

    private TextView spt, cw, drsg, comf;//生活建议

    RecyclerView recyclerView_forecast;
    private TextView county_name;
    SharedPreferences last_county;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        LocationClient.setAgreePrivacy(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HeConfig.init("HE2301031356041355", "210e7e865a58471ca4dee69484f722ff");//初始化key
        HeConfig.switchToDevService();//切换为免费版

        night_switch_state = false;

        view_state = getSharedPreferences("view_state", MODE_PRIVATE);//读取控件显示状态，初始化控件
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);

        if (view_state.getBoolean("service_state", false)) {
            Intent service_intent = new Intent(this, WeatherService.class);
            startService(service_intent);
        }//启动通知栏服务
        GetData();//从网络获取数据更新视图
        InitLayout();//初始化布局

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//下拉刷新重载页面
            @Override
            public void onRefresh() {
                // 显示下拉刷新动画
                swipeRefresh.setRefreshing(true);
                Toast.makeText(getApplicationContext(), "刷新中", Toast.LENGTH_SHORT).show();
                // 获取数据
                GetData();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "刷新完成！", Toast.LENGTH_SHORT).show();
            }
        });

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
        button_city_list = (ImageButton) findViewById(R.id.city_list);
        button_city_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入选择城市页面

                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //初始化，如果用户第一次打开尚未定位，会启用以下代码跳转至选择城市页面
        if (last_county.getString("id", null) == null) {
            Intent intent_init = new Intent(MainActivity.this, CityListActivity.class);
            startActivity(intent_init);
            finish();
        }

        county_name = (TextView) findViewById(R.id.county_name);//城市名称显示文本框
        county_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> cityList;
//遍历数据库，将存储的城市展示在下拉框菜单中
                CityDBHelper dbHelper = new CityDBHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] projection = {"id", "name"};
                String sortOrder = "name ASC";
                Cursor cursor = db.query("city", projection, null, null, null, null, sortOrder);
                cityList = new ArrayList<>();
                cityList.add("请选择城市");
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    cityList.add(name);
                }
                cursor.close();
                // 在Activity或Fragment中获取Spinner对象
                Spinner citySpinner = findViewById(R.id.city_spinner);
                citySpinner.setSelection(0);
                citySpinner.performClick();
// 创建ArrayAdapter对象，用于将数据源绑定到Spinner上
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, cityList);

// 设置下拉列表的样式
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// 将adapter添加到spinner中
                citySpinner.setAdapter(adapter);
// 给Spinner设置选择监听器
                citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // 获取选中的城市
                        String selectedCity = (String) parent.getItemAtPosition(position);
                        if (selectedCity.equals("请选择城市")) {
                            // 如果选中了提示文本，则不做任何处理
                            return;
                        }
                        // 查询对应城市的id
                        String countyId = dbHelper.queryCountyId(selectedCity);
                        dbHelper.close();
                        // TODO: 处理选中城市的逻辑
                        last_county.edit().putString("id", countyId).commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // 未选择任何项
                    }
                });

            }
        });
    }

    private void InitLayout() {
        //设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

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
        feel_temp_now_textview = (TextView) findViewById(R.id.feel_temp_now);//当前体感温度
        aqi_primary = (TextView) findViewById(R.id.aqi_primary);
        aqi_category = (TextView) findViewById(R.id.aqi_category);
        aqi_pm2p5 = (TextView) findViewById(R.id.aqi_pm2_5);//空气质量相关显示框

        spt = (TextView) findViewById(R.id.spt);
        cw = (TextView) findViewById(R.id.cw);
        drsg = (TextView) findViewById(R.id.drsg);
        comf = (TextView) findViewById(R.id.comf);//生活建议相关显示框

        LinearLayout air = (LinearLayout) findViewById(R.id.air);
        LinearLayout forecast = (LinearLayout) findViewById(R.id.forecast);
        LinearLayout wind = (LinearLayout) findViewById(R.id.wind);
        LinearLayout life = (LinearLayout) findViewById(R.id.life);
        LinearLayout other = (LinearLayout) findViewById(R.id.other);
        LinearLayout hourly = (LinearLayout) findViewById(R.id.hourly);
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
        if (!view_state.getBoolean("hourly_state", true))
            hourly.setVisibility(View.GONE);
        life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //生活建议区域点击事件，跳转至生活建议详情页
                Intent intent = new Intent(MainActivity.this, LifeActivity.class);
                startActivity(intent);
            }
        });
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
            night_switch_state = true;

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//关闭夜间模式
        }


    }

    private void GetData() {
        //初始化逐小时显示天气的rv
        recyclerView_hourly = findViewById(R.id.rv_hourly);
        LinearLayoutManager hourlyLinearLayoutManager = new LinearLayoutManager(this);
        hourlyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);//布局朝向为水平
        recyclerView_hourly.setLayoutManager(hourlyLinearLayoutManager);
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);//获取存储的城市id
        QWeather.getWeather24Hourly(getApplicationContext(),
                last_county.getString("id", null), new QWeather.OnResultWeatherHourlyListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                        if (Code.OK == weatherHourlyBean.getCode()) {
                            List<WeatherHourlyBean.HourlyBean> hourlyDate = weatherHourlyBean.getHourly();
                            List<Hourly> hourlyList = new ArrayList<>();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    int i;
                                    for (i = 0; i < hourlyDate.size(); i++) {
                                        Hourly hourly = new Hourly(hourlyDate.get(i).getTemp() + "℃", hourlyDate.get(i).getIcon(),
                                                hourlyDate.get(i).getWindScale() + "级", hourlyDate.get(i).getFxTime().substring(11, 16));
                                        //上述的getFxTime返回的数据过长，只截取第12-16的内容，其中包含逐小时预报天气的小时和分钟信息
                                        hourlyList.add(hourly);
                                    }
                                    hourlyAdapter = new HourlyAdapter(hourlyList);
                                    hourlyAdapter.notifyDataSetChanged();
                                    recyclerView_hourly.setAdapter(hourlyAdapter);
                                }
                            });
                        }

                    }
                });
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
                                    feel_temp_now_textview.setText(now.getFeelsLike() + "℃");
                                    //如果是夜间模式，将背景设置为夜间模式图片，否则根据天气设置图片
                                    if (night_switch_state) {
                                        getWindow().setBackgroundDrawableResource(R.drawable.background_night);
                                    } else {
                                        WeatherUtil.changeBackground(MainActivity.this, Integer.parseInt(now.getIcon()));
                                    }
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
                            Calendar calendar = Calendar.getInstance();
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
        recyclerView_forecast = findViewById(R.id.forecast_rv);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView_forecast.setLayoutManager(linearLayoutManager);
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
                                        Forecast forecast = new Forecast(dailyBeanList.get(i).getFxDate().substring(5,10),
                                                dailyBeanList.get(i).getIconDay(),dailyBeanList.get(i).getTempMin()
                                                + "-" + dailyBeanList.get(i).getTempMax() + "℃",
                                                dailyBeanList.get(i).getIconNight(), dailyBeanList.get(i).getSunrise(), dailyBeanList.get(i).getSunset(),
                                                dailyBeanList.get(i).getMoonRise(), dailyBeanList.get(i).getMoonSet(), dailyBeanList.get(i).getMoonPhase(),
                                                dailyBeanList.get(i).getMoonPhaseIcon(), dailyBeanList.get(i).getWindDirDay()
                                                , dailyBeanList.get(i).getWindDirNight(), dailyBeanList.get(i).getWindScaleDay()+"级", dailyBeanList.get(i).
                                                getWindScaleNight()+"级", dailyBeanList.get(i).getWindSpeedDay()+"公里/小时", dailyBeanList.get(i).getWindSpeedNight()+"公里/小时",
                                                dailyBeanList.get(i).getHumidity()+"%", dailyBeanList.get(i).getPrecip()+"mm", dailyBeanList.get(i).getPressure()+"hPa",
                                                dailyBeanList.get(i).getCloud(), dailyBeanList.get(i).getUvIndex(), dailyBeanList.get(i).getVis()+"公里"
                                                );
                                        //其中的日期参数只截取月份和日期
                                        forecastList.add(forecast);
                                    }
                                    ForecastAdapter adapter = new ForecastAdapter(forecastList);
                                    adapter.notifyDataSetChanged();
                                    recyclerView_forecast.setAdapter(adapter);
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
    public void onBackPressed(){
        //在首页点击返回时，直接关闭软件
        finishAffinity();
    }
}