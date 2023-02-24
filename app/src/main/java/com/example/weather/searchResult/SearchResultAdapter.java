package com.example.weather.searchResult;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.CityListActivity;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.citydb.City;
import com.example.weather.citydb.CityDBHelper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private Context mContext;
    private List<SearchCity> cities;
    final Handler handler = new Handler();
SharedPreferences last_county;
    private Handler mHandler;
    public SearchResultAdapter(Context context,List<SearchCity> cities,Handler handler) {
        this.cities = cities;
        this.mContext=context;
        this.mHandler=handler;
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
                last_county.edit().putString("id",searchCity.getId()).commit();

                //将选中的城市name和id存入数据库
                saveCity(new City(searchCity.getId(), searchCity.getName()));
handler.post(new Runnable() {
    @Override
    public void run() {
        Intent intent=new Intent(view.getContext(), MainActivity.class);
        view.getContext().startActivity(intent);
        Message msg = mHandler.obtainMessage();
        msg.what = CityListActivity.MSG_FINISH_ACTIVITY;
        mHandler.sendMessage(msg);
        // 当用户点击列表项跳转到另一个活动时，Handler会在UI线程中接收到消息，然后在处理消息的函数中执行销毁活动的操作，避免出现黑屏
    }
});

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
    public void saveCity(City city) {
        CityDBHelper dbHelper = new CityDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from city where id=?", new String[]{city.getId()});
        if (cursor.moveToFirst()) {
            // 数据库中已经存在该城市，执行更新操作
            ContentValues values = new ContentValues();
            values.put("name", city.getName());
            db.update("city", values, "id=?", new String[]{city.getId()});
        } else {
            // 数据库中不存在该城市，执行插入操作
            ContentValues values = new ContentValues();
            values.put("id", city.getId());
            values.put("name", city.getName());
            db.insert("city", null, values);
        }
        cursor.close();
    }
}
