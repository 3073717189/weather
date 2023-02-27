package com.example.weather.hourly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.WeatherUtil;
import com.example.weather.searchResult.SearchResultAdapter;

import java.time.temporal.Temporal;
import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.ViewHolder> {

    private List<Hourly> hourlyList;//初始化

    public HourlyAdapter(List<Hourly> hourlyList) {
        this.hourlyList = hourlyList;
    }//用于在活动中给适配器赋值

    @NonNull
    @Override
    public HourlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_item, parent, false);
        return new ViewHolder(view);
    }//绑定该rv对应的item

    @Override
    public void onBindViewHolder(@NonNull HourlyAdapter.ViewHolder holder, int position) {
        //在这里对rv中的控件进行操作以及设计点击事件
        Hourly hourly = hourlyList.get(position);
holder.temp.setText(hourly.getTemp());
        WeatherUtil.changeIcon(holder.weather,Integer.parseInt(hourly.getWeather()));
//holder.weather.setText(hourly.getWeather());
holder.wind_level.setText(hourly.getWind_level());
//holder.air_level.setText(hourly.getAir_level());
holder.time.setText(hourly.getTime());

    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View hourlyView;
        TextView temp;
        ImageView weather;
        TextView wind_level;
        TextView time;
        //以上控件将会在onBindViewHolder方法中使用并设置相应的显示内容或者点击事件

        public ViewHolder(@NonNull View itemView) {
            //这里将rv中的每个控件绑定到对应的item的xml文件中设置的id
            super(itemView);
            hourlyView=itemView;
            temp=itemView.findViewById(R.id.hourly_temp);
            weather=itemView.findViewById(R.id.hourly_weather);
            wind_level=itemView.findViewById(R.id.hourly_wind_level);
            time=itemView.findViewById(R.id.hourly_time);
        }

    }
}
