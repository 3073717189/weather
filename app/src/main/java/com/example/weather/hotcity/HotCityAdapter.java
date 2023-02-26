package com.example.weather.hotcity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.CityListActivity;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.citydb.City;
import com.example.weather.citydb.CityDBHelper;
import com.example.weather.searchResult.SearchCity;
import com.example.weather.searchResult.SearchResultAdapter;

import java.util.List;


public class HotCityAdapter extends RecyclerView.Adapter<HotCityAdapter.ViewHolder>{
    private Context mContext;
    private List<HotCity> hotCityList;
    final Handler handler = new Handler();
    SharedPreferences last_county;
    private Handler mHandler;
    //为了设计点击事件，所以要传入context参数，为了在子线程中跳转回主活动并销毁活动，传入handler
    public HotCityAdapter(Context context,List<HotCity>hotCityList,Handler handler){
        this.hotCityList=hotCityList;
        this.mContext=context;
        this.mHandler=handler;
    }//用于在活动中给适配器赋值


    @NonNull
    @Override
    public HotCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_city_item, parent, false);
        return new ViewHolder(view);
    }//绑定该rv对应的item

    @Override
    public void onBindViewHolder(@NonNull HotCityAdapter.ViewHolder holder, int position) {
HotCity hotCity=hotCityList.get(position);
holder.hot_cityName.setText(hotCity.getName());
last_county=holder.itemView.getContext().getSharedPreferences("last_county", Context.MODE_PRIVATE);
holder.hot_cityView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        int position=holder.getAdapterPosition();
        HotCity hotCity=hotCityList.get(position);
        last_county.edit().putString("id",hotCity.getId()).commit();
        //将选中的城市name和id存入数据库
        saveCity(new City(hotCity.getId(), hotCity.getName()));
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
        return hotCityList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
View hot_cityView;
TextView hot_cityName;
        //以上控件将会在onBindViewHolder方法中使用并设置相应的显示内容或者点击事件
        public ViewHolder(@NonNull View itemView) {
            //这里将rv中的每个控件绑定到对应的item的xml文件中设置的id
            super(itemView);
            hot_cityView=itemView;
            hot_cityName=itemView.findViewById(R.id.hot_city_name);
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
