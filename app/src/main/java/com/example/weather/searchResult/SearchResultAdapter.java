package com.example.weather.searchResult;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.MainActivity;
import com.example.weather.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<SearchCity> cities;

SharedPreferences last_county;

    public SearchResultAdapter(List<SearchCity> cities) {
        this.cities = cities;
    }//用于在活动中给适配器赋值

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(view);
    }//绑定该rv对应的item

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //在这里对rv中的控件进行操作以及设计点击事件
        SearchCity searchCity = cities.get(position);
        holder.cityName.setText(searchCity.getName());

        last_county = holder.itemView.getContext().getSharedPreferences("last_county", Context.MODE_PRIVATE);
        holder.cityView.setOnClickListener(new View.OnClickListener() {
            //编辑rv中控件的点击事件，选中城市后将对应的城市id记录并启动主页面以获取天气信息
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                SearchCity searchCity=cities.get(position);
                Toast.makeText(holder.itemView.getContext(), searchCity.getName(),Toast.LENGTH_SHORT).show();
                last_county.edit().putString("id",searchCity.getId()).commit();
                Intent intent=new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        View cityView;
        TextView cityName;
        //以上控件将会在onBindViewHolder方法中使用并设置相应的显示内容或者点击事件

        public ViewHolder(@NonNull View itemView) {
            //这里将rv中的每个控件绑定到对应的item的xml文件中设置的id
            super(itemView);
            cityView=itemView;
            cityName = itemView.findViewById(R.id.search_city_name);
        }
    }
}
