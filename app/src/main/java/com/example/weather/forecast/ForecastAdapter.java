package com.example.weather.forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.WeatherUtil;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {
private List<Forecast>forecastList;//初始化
    public ForecastAdapter(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }//用于在活动中给适配器赋值
    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new ForecastAdapter.ViewHolder(view);
    }//绑定该rv对应的item

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapter.ViewHolder holder, int position) {
//在这里对rv中的控件进行操作以及设计点击事件
        Forecast forecast = forecastList.get(position);
        holder.date.setText(forecast.getDate());
        WeatherUtil.changeIcon(holder.weather,Integer.parseInt(forecast.getWeatherDay()));
        holder.temp.setText(forecast.getTemp());
        holder.forecastView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(view.getContext()).inflate(R.layout.diglog_forecast, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(v);

                AlertDialog dialog = builder.create();
                TextView text_date=v.findViewById(R.id.forecast_dialog_date);
                ImageView image_weatherDay=v.findViewById(R.id.forecast_dialog_weatherDay);
                TextView text_temp=v.findViewById(R.id.forecast_dialog_temp);
                ImageView image_weatherNight=v.findViewById(R.id.forecast_dialog_weatherNight);
                TextView text_sunrise=v.findViewById(R.id.forecast_dialog_sunrise);
                TextView text_sunset=v.findViewById(R.id.forecast_dialog_sunset);
                TextView text_moonRise=v.findViewById(R.id.forecast_dialog_moonRise);
                TextView text_moonSet=v.findViewById(R.id.forecast_dialog_moonSet);
                TextView text_moonPhase=v.findViewById(R.id.forecast_dialog_moonPhase);
                ImageView image_moonPhaseIcon=v.findViewById(R.id.forecast_dialog_moonPhaseIcon);
                TextView text_windDirDay=v.findViewById(R.id.forecast_dialog_windDirDay);
                TextView text_windDirNight=v.findViewById(R.id.forecast_dialog_windDirNight);
                TextView text_windScaleDay=v.findViewById(R.id.forecast_dialog_windScaleDay);
                TextView text_windScaleNight=v.findViewById(R.id.forecast_dialog_windScaleNight);
                TextView text_windSpeedDay=v.findViewById(R.id.forecast_dialog_windSpeedDay);
                TextView text_windSpeedNight=v.findViewById(R.id.forecast_dialog_windSpeedNight);
                TextView text_humidity=v.findViewById(R.id.forecast_dialog_humidity);
                TextView text_precip=v.findViewById(R.id.forecast_dialog_precip);
                TextView text_pressure=v.findViewById(R.id.forecast_dialog_pressure);
                TextView text_cloud=v.findViewById(R.id.forecast_dialog_could);
                TextView text_uvInDex=v.findViewById(R.id.forecast_dialog_uvInDex);
                TextView text_vis=v.findViewById(R.id.forecast_dialog_vis);

                text_date.setText(forecast.getDate());
                WeatherUtil.changeIcon(image_weatherDay,Integer.parseInt(forecast.getWeatherDay()));
                text_temp.setText(forecast.getTemp());
                WeatherUtil.changeIcon(image_weatherNight,Integer.parseInt(forecast.getWeatherNight()));
                text_sunrise.setText(forecast.getSunrise());
                text_sunset.setText(forecast.getSunset());
                text_moonRise.setText(forecast.getMoonRise());
                text_moonSet.setText(forecast.getMoonSet());
                text_moonPhase.setText(forecast.getMoonPhase());
                WeatherUtil.changeIcon(image_moonPhaseIcon,Integer.parseInt(forecast.getMoonPhaseIcon()));
                text_windDirDay.setText(forecast.getWindDirDay());
                text_windDirNight.setText(forecast.getWinDirNight());
                text_windScaleDay.setText(forecast.getWindScaleDay());
                text_windScaleNight.setText(forecast.getWindScaleNight());
                text_windSpeedDay.setText(forecast.getWindSpeedDay());
                text_windSpeedNight.setText(forecast.getWindSpeedNight());
                text_humidity.setText(forecast.getHumidity());
                text_precip.setText(forecast.getPrecip());
                text_pressure.setText(forecast.getPressure());
                text_cloud.setText(forecast.getCould());
                text_uvInDex.setText(forecast.getUvInDex());
                text_vis.setText(forecast.getVis());



                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View forecastView;
        TextView date;
        ImageView weather;
        TextView temp;
        //以上控件将会在onBindViewHolder方法中使用并设置相应的显示内容或者点击事件
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            forecastView = itemView;
            date = itemView.findViewById(R.id.forecast_date);
            weather = itemView.findViewById(R.id.forecast_weather);
            temp=itemView.findViewById(R.id.forecast_temp);
        }
    }
}

