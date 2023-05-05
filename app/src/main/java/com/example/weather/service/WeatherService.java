package com.example.weather.service;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.List;

//通知栏常驻服务，从网络获取数据，计划定时启动服务刷新数据
public class WeatherService extends Service {
    SharedPreferences weather_today;
    final Handler handler = new Handler();
    SharedPreferences last_county;
    public String TAG;

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("WeatherService", "onCreate executed");
    }
    public WeatherService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager AutoUpdate = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 60* 60 * 1000;//每小时刷新一次，后面的数字为刷新间隔的毫秒数
        Intent service_intent = new Intent(this, WeatherService.class);
        PendingIntent pi_update = PendingIntent.getService(this, 0, service_intent, 0);

        last_county = getSharedPreferences("last_county", MODE_PRIVATE);//读取当前城市id
        weather_today = getSharedPreferences("weather_today", MODE_PRIVATE);//加载今日天气部分数据尝试展示在通知栏
        Intent main_intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, main_intent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel("myChannelId", "channelname"
                    , NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，通知才能正常弹出
            manager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myChannelId");
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pi);
        builder.setOngoing(true);
        QWeather.getWeatherNow(WeatherService.this, last_county.getString("id", null),
                Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherNowListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(WeatherNowBean weatherBean) {


                        if (Code.OK == weatherBean.getCode()) {
                            WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//接下来开始从网络获取对应的数据来更新显示
                                    builder.setContentTitle(now.getTemp() + "℃   " + now.getText());
                                    notificationManager.notify(1, builder.build());

                                }
                            });
                        } else {
                            //在此查看返回数据失败的原因
                            Code code = weatherBean.getCode();
                            Log.i(TAG, "failed code: " + code);

                        }
                    }
                });

        QWeather.getWeather7D(WeatherService.this, last_county.getString("id", null),
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

                                    builder.setContentText(dailyBeanList.get(0).getTempMin() + "-"
                                            + dailyBeanList.get(0).getTempMax() + "℃");
                                    notificationManager.notify(1, builder.build());


                                }
                            });
                        }
                    }


                });


        AutoUpdate.cancel(pi_update);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0
            AutoUpdate.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi_update);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//  4.4
            AutoUpdate.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi_update);
        } else {
            AutoUpdate.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi_update);
        }//根据安卓版本来设置通知栏刷新方法
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}