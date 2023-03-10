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

    private RecyclerView recyclerView_hourly;//?????????????????????????????????rv
    private HourlyAdapter hourlyAdapter;//??????rv????????????
    // ???????????????????????????
    SharedPreferences night_mode;
    Boolean night_switch_state;
    int start, now, end;

    SharedPreferences view_state;

    SwipeRefreshLayout swipeRefresh;//????????????
    public String TAG;
    private TextView temp_today;//????????????
    private TextView temp_now_textview;//????????????
    private TextView weather_now_textview;//????????????
    private TextView winddir_now_textview;//????????????
    private TextView windscale_now_textview;//??????????????????
    private TextView windspeed_now_textview;//????????????
    private TextView humidity_now_textview;//??????????????????
    private TextView precip_now_textview;//?????????
    private TextView pressure_now_textview;//????????????
    private TextView vis_now_textview;//?????????
    private TextView dew_now_textview;//????????????
    private TextView feel_temp_now_textview;

    private TextView aqi_primary, aqi_category, aqi_pm2p5;//??????????????????

    ImageButton button_city_list;//??????????????????
    ImageButton button_setting;//????????????

    private TextView spt, cw, drsg, comf;//????????????

    RecyclerView recyclerView_forecast;
    private TextView county_name;
    SharedPreferences last_county;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//???????????????
        LocationClient.setAgreePrivacy(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HeConfig.init("HE2301031356041355", "210e7e865a58471ca4dee69484f722ff");//?????????key
        HeConfig.switchToDevService();//??????????????????

        night_switch_state = false;

        view_state = getSharedPreferences("view_state", MODE_PRIVATE);//??????????????????????????????????????????
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);

        if (view_state.getBoolean("service_state", false)) {
            Intent service_intent = new Intent(this, WeatherService.class);
            startService(service_intent);
        }//?????????????????????
        GetData();//?????????????????????????????????
        InitLayout();//???????????????

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//????????????????????????
            @Override
            public void onRefresh() {
                // ????????????????????????
                swipeRefresh.setRefreshing(true);
                Toast.makeText(getApplicationContext(), "?????????", Toast.LENGTH_SHORT).show();
                // ????????????
                GetData();
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
            }
        });

        button_setting = (ImageButton) findViewById(R.id.setting);
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????????????????????????????????????????

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();

            }
        });
        button_city_list = (ImageButton) findViewById(R.id.city_list);
        button_city_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????????????????????

                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //??????????????????????????????????????????????????????????????????????????????????????????????????????
        if (last_county.getString("id", null) == null) {
            Intent intent_init = new Intent(MainActivity.this, CityListActivity.class);
            startActivity(intent_init);
            finish();
        }

        county_name = (TextView) findViewById(R.id.county_name);//???????????????????????????
        county_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> cityList;
//???????????????????????????????????????????????????????????????
                CityDBHelper dbHelper = new CityDBHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String[] projection = {"id", "name"};
                String sortOrder = "name ASC";
                Cursor cursor = db.query("city", projection, null, null, null, null, sortOrder);
                cityList = new ArrayList<>();
                cityList.add("???????????????");
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    cityList.add(name);
                }
                cursor.close();
                // ???Activity???Fragment?????????Spinner??????
                Spinner citySpinner = findViewById(R.id.city_spinner);
                citySpinner.setSelection(0);
                citySpinner.performClick();
// ??????ArrayAdapter????????????????????????????????????Spinner???
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, cityList);

// ???????????????????????????
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// ???adapter?????????spinner???
                citySpinner.setAdapter(adapter);
// ???Spinner?????????????????????
                citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // ?????????????????????
                        String selectedCity = (String) parent.getItemAtPosition(position);
                        if (selectedCity.equals("???????????????")) {
                            // ???????????????????????????????????????????????????
                            return;
                        }
                        // ?????????????????????id
                        String countyId = dbHelper.queryCountyId(selectedCity);
                        dbHelper.close();
                        // TODO: ???????????????????????????
                        last_county.edit().putString("id", countyId).commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // ??????????????????
                    }
                });

            }
        });
    }

    private void InitLayout() {
        //??????????????????????????????xml?????????????????????android:fitsSystemWindows="true"
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        temp_today = (TextView) findViewById(R.id.temp_today);//???????????????????????????
        weather_now_textview = (TextView) findViewById(R.id.weather_now);//???????????????????????????
        temp_now_textview = (TextView) findViewById(R.id.temperature_now);//???????????????????????????
        winddir_now_textview = (TextView) findViewById(R.id.winddir_now);//???????????????????????????
        windscale_now_textview = (TextView) findViewById(R.id.windscale_now);//?????????????????????????????????
        windspeed_now_textview = (TextView) findViewById(R.id.windspeed_now);//???????????????????????????
        humidity_now_textview = (TextView) findViewById(R.id.humidity_now);//???????????????????????????
        precip_now_textview = (TextView) findViewById(R.id.precip_now);//????????????????????????
        pressure_now_textview = (TextView) findViewById(R.id.pressure_now);//???????????????????????????
        vis_now_textview = (TextView) findViewById(R.id.vis_now);//????????????????????????
        dew_now_textview = (TextView) findViewById(R.id.dew_now);//???????????????????????????
        feel_temp_now_textview = (TextView) findViewById(R.id.feel_temp_now);//??????????????????
        aqi_primary = (TextView) findViewById(R.id.aqi_primary);
        aqi_category = (TextView) findViewById(R.id.aqi_category);
        aqi_pm2p5 = (TextView) findViewById(R.id.aqi_pm2_5);//???????????????????????????

        spt = (TextView) findViewById(R.id.spt);
        cw = (TextView) findViewById(R.id.cw);
        drsg = (TextView) findViewById(R.id.drsg);
        comf = (TextView) findViewById(R.id.comf);//???????????????????????????

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
                //???????????????????????????????????????????????????????????????
                Intent intent = new Intent(MainActivity.this, LifeActivity.class);
                startActivity(intent);
            }
        });
        night_mode = getSharedPreferences("night_mode", MODE_PRIVATE);//????????????????????????????????????
        start = night_mode.getInt("start_hour", 0) * 60 + night_mode.getInt("start_minute", 0);
        end = night_mode.getInt("end_hour", 0) * 60 + night_mode.getInt("end_minute", 0);
        Calendar calendar = Calendar.getInstance();
        now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.
                get(Calendar.MINUTE);//????????????????????????????????????????????????????????????????????????????????????
        if (night_mode.getBoolean("switch_time", false)) {//????????????????????????????????????
            if ((start <= now && now < end) || (end < start && start <= now) || (now < end && end < start)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//??????????????????
                night_mode.edit().putBoolean("switch_mode", true).commit();//??????????????????????????????????????????
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//??????????????????
                night_mode.edit().putBoolean("switch_mode", false).commit();////??????????????????????????????????????????
            }
        }
        if (night_mode.getBoolean("switch_mode", false)) {//??????????????????????????????
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//??????????????????
            night_switch_state = true;

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//??????????????????
        }


    }

    private void GetData() {
        //?????????????????????????????????rv
        recyclerView_hourly = findViewById(R.id.rv_hourly);
        LinearLayoutManager hourlyLinearLayoutManager = new LinearLayoutManager(this);
        hourlyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);//?????????????????????
        recyclerView_hourly.setLayoutManager(hourlyLinearLayoutManager);
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);//?????????????????????id
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
                                        Hourly hourly = new Hourly(hourlyDate.get(i).getTemp() + "???", hourlyDate.get(i).getIcon(),
                                                hourlyDate.get(i).getWindScale() + "???", hourlyDate.get(i).getFxTime().substring(11, 16));
                                        //?????????getFxTime????????????????????????????????????12-16?????????????????????????????????????????????????????????????????????
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
                        //??????????????????status??????????????????status???????????????????????????status?????????????????????status?????????Code???????????????

                        if (Code.OK == weatherBean.getCode()) {
                            WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//????????????????????????????????????????????????????????????

                                    weather_now_textview.setText(now.getText() + " ");
                                    temp_now_textview.setText(now.getTemp() + "???");
                                    winddir_now_textview.setText(now.getWindDir());
                                    windscale_now_textview.setText(now.getWindScale() + "???");
                                    windspeed_now_textview.setText(now.getWindSpeed() + "??????/??????");
                                    humidity_now_textview.setText(now.getHumidity());
                                    precip_now_textview.setText(now.getPrecip() + "mm");
                                    pressure_now_textview.setText(now.getPressure() + "hPa");
                                    vis_now_textview.setText(now.getVis() + "??????");
                                    dew_now_textview.setText(now.getDew());
                                    feel_temp_now_textview.setText(now.getFeelsLike() + "???");
                                    //?????????????????????????????????????????????????????????????????????????????????????????????
                                    if (night_switch_state) {
                                        getWindow().setBackgroundDrawableResource(R.drawable.background_night);
                                    } else {
                                        WeatherUtil.changeBackground(MainActivity.this, Integer.parseInt(now.getIcon()));
                                    }
                                }
                            });
                        } else {
                            //???????????????????????????????????????
                            Code code = weatherBean.getCode();
                            Log.i(TAG, "failed code: " + code);
                        }
                    }
                });
        QWeather.getGeoCityLookup(MainActivity.this, last_county.getString("id", null),
                new QWeather.OnResultGeoListener() {    //????????????id?????????????????????
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(GeoBean geoBean) {
                        if (Code.OK == geoBean.getCode()) {
                            Calendar calendar = Calendar.getInstance();
                            List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                            handler.post((new Runnable() {      //????????????id?????????????????????????????????????????????????????????
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
//????????????????????????????????????????????????????????????
                                    List<Forecast> forecastList = new ArrayList<>();
                                    temp_today.setText(dailyBeanList.get(0).getTempMin() +
                                            "-" + dailyBeanList.get(0).getTempMax() + "???");
                                    for (int i = 0; i < dailyBeanList.toArray().length; i++) {
                                        Forecast forecast = new Forecast(dailyBeanList.get(i).getFxDate().substring(5,10),
                                                dailyBeanList.get(i).getIconDay(),dailyBeanList.get(i).getTempMin()
                                                + "-" + dailyBeanList.get(i).getTempMax() + "???",
                                                dailyBeanList.get(i).getIconNight(), dailyBeanList.get(i).getSunrise(), dailyBeanList.get(i).getSunset(),
                                                dailyBeanList.get(i).getMoonRise(), dailyBeanList.get(i).getMoonSet(), dailyBeanList.get(i).getMoonPhase(),
                                                dailyBeanList.get(i).getMoonPhaseIcon(), dailyBeanList.get(i).getWindDirDay()
                                                , dailyBeanList.get(i).getWindDirNight(), dailyBeanList.get(i).getWindScaleDay()+"???", dailyBeanList.get(i).
                                                getWindScaleNight()+"???", dailyBeanList.get(i).getWindSpeedDay()+"??????/??????", dailyBeanList.get(i).getWindSpeedNight()+"??????/??????",
                                                dailyBeanList.get(i).getHumidity()+"%", dailyBeanList.get(i).getPrecip()+"mm", dailyBeanList.get(i).getPressure()+"hPa",
                                                dailyBeanList.get(i).getCloud(), dailyBeanList.get(i).getUvIndex(), dailyBeanList.get(i).getVis()+"??????"
                                                );
                                        //?????????????????????????????????????????????
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
                                    aqi_primary.setText("????????????:" + now.getPrimary());
                                    aqi_category.setText("????????????" + now.getCategory() + " " + now.getAqi());
                                    aqi_pm2p5.setText("pm2.5:" + now.getPm2p5());
                                }
                            });
                        }
                    }
                });

        //??????????????????
        ArrayList<IndicesType> life = new ArrayList<>();
        life.add(IndicesType.SPT);//????????????
        life.add(IndicesType.CW);//????????????
        life.add(IndicesType.DRSG);//????????????
        life.add(IndicesType.COMF);//???????????????

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
                                    spt.setText("???????????????" + life.get(0).getText());
                                    cw.setText("???????????????" + life.get(1).getText());
                                    drsg.setText("???????????????" + life.get(2).getText());
                                    comf.setText("????????????" + life.get(3).getText());
                                }
                            });
                        }
                    }
                });

    }

}