package com.example.weather.citylist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.CityListActivity;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.citydb.CityDBHelper;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {
    SharedPreferences last_county;//初始化sp，当点击rv中的子项时根据点击的城市更新存储的城市id
    Context mContext;//获取传入的当前context，用于编写点击事件
    private Handler mHandler;
private List<CityList>cityLists;
public CityListAdapter(List<CityList>cityLists,Context mContext,Handler handler){
    this.cityLists=cityLists;
    this.mContext=mContext;
this.mHandler=handler;
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
                Message msg = mHandler.obtainMessage();
                msg.what = CityListActivity.MSG_FINISH_ACTIVITY;
                mHandler.sendMessage(msg);
                // 当用户点击列表项跳转到另一个活动时，Handler会在UI线程中接收到消息，然后在处理消息的函数中执行销毁活动的操作，避免出现黑屏
            }
        });
        holder.name_text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 获取当前选中的城市数据
                CityList city = cityLists.get(holder.getAdapterPosition());
                // 弹出删除确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("删除城市");
                builder.setMessage("是否删除 " + city.getName() + " ?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CityDBHelper dbHelper = new CityDBHelper(mContext);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String selection = "id = ?";
                        String[] selectionArgs = {String.valueOf(city.getId())};
                        db.delete("city", selection, selectionArgs);
                        cityLists.remove(position);
                        //调用 notifyDataSetChanged() 方法只是通知适配器数据集有变化，但是它并不知道具体哪些数据被删除或者添加了，
                        // 所以需要在适配器内部调用 remove() 方法删除对应的数据，这样适配器才能正确的更新列表。
                        Toast.makeText(mContext.getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true; // 告诉系统该事件已被处理
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
