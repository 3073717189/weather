package com.example.weather.citylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.MainActivity;
import com.example.weather.R;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {
    SharedPreferences last_county;//初始化sp，当点击rv中的子项时根据点击的城市更新存储的城市id
    Context mContext;//获取传入的当前context，用于编写点击事件
private List<CityList>cityLists;
public CityListAdapter(List<CityList>cityLists,Context mContext){
    this.cityLists=cityLists;
    this.mContext=mContext;
}//用于在活动中给适配器赋值

    @NonNull
    @Override
    public CityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item,parent,false);
      return new ViewHolder(view);
    }//绑定该rv对应的item

    @Override
    public void onBindViewHolder(@NonNull CityListAdapter.ViewHolder holder, int position) {
//在这里对rv中的控件进行操作以及设计点击事件
        CityList cityList=cityLists.get(position);
        holder.name_text.setText(cityList.getName());
        holder.name_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将选中城市的id传入sp，并跳转至主页
                last_county =mContext.getSharedPreferences("last_county", mContext.MODE_PRIVATE);
                last_county.edit().putString("id",cityList.getId()).commit();
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityLists.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name_text;
        //以上控件将会在onBindViewHolder方法中使用并设置相应的显示内容或者点击事件

        public ViewHolder(@NonNull View itemView) {
            //这里将rv中的每个控件绑定到对应的item的xml文件中设置的id
            super(itemView);


            name_text=itemView.findViewById(R.id.city_list_item);
        }

    }
}
