package com.example.weather.forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.weather.R;
import com.example.weather.WeatherUtil;

import java.util.List;

public class ForecastAdapter extends ArrayAdapter<Forecast> {
    public ForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects){
        super(context,resource,objects);
    }
    //每个子项被滚动到屏幕内的时候会被调用
@NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        Forecast forecast=getItem(position);//得到当前项的Forecast实例
    //为每一个子项加载设定的布局
        View view= LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,parent,false);
    //分别获取textview的实例
    TextView date=view.findViewById(R.id.forecast_date);
    ImageView weather=view.findViewById(R.id.forecast_weather);
    TextView temp=view.findViewById(R.id.forecast_temp);
    //设置要显示的文字
    date.setText(forecast.getDate());
    WeatherUtil.changeIcon(weather,Integer.parseInt(forecast.getWeather()));
   // weather.setText(forecast.getWeather());
    temp.setText(forecast.getTemp());
    return view;
}
}

