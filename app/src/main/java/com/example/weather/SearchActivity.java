package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.weather.hotcity.HotCity;
import com.example.weather.hotcity.HotCityAdapter;
import com.example.weather.searchResult.SearchCity;
import com.example.weather.searchResult.SearchResultAdapter;
import com.qweather.sdk.bean.base.Code;

import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView_search_result;
    private RecyclerView recyclerView_hot_city;
    private SearchResultAdapter searchResultAdapter;
    public String TAG;
    ImageButton search_button;
    EditText search_edit;
    SharedPreferences last_county;
    final Handler handler = new Handler();
    public static final int MSG_FINISH_ACTIVITY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        setContentView(R.layout.activity_search);

        //设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏深色字体

        recyclerView_search_result = findViewById(R.id.search_result_list);
        recyclerView_search_result.setLayoutManager(new LinearLayoutManager(this));//初始化rv
recyclerView_hot_city=findViewById(R.id.hot_city_list);
        StaggeredGridLayoutManager staggered_grid_layoutManager = new
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
recyclerView_hot_city.setLayoutManager(staggered_grid_layoutManager);
        QWeather.getGeoTopCity(SearchActivity.this,20, Range.CN, Lang.ZH_HANS, new QWeather.OnResultGeoListener() {
            //以下获取热门城市并用rv展示
            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"getWeather onError: " + e);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
if(Code.OK==geoBean.getCode()){
    //如果查询到了城市，获取该城市id和名字，将城市列表用rv展示
    List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
    List<HotCity>hotCityList=new ArrayList<>();

    handler.post(new Runnable() {
        @Override
        public void run() {
            int i;
            for(i=0;i<locationBean.size();i++){
                HotCity hotCity=new HotCity(locationBean.get(i).getId()
                        ,locationBean.get(i).getName());
                hotCityList.add(hotCity);
            }
            HotCityAdapter hotCityAdapter = new HotCityAdapter(getApplicationContext(),hotCityList,mHandler);
            recyclerView_hot_city.setAdapter(hotCityAdapter);
        }
    });
}
            }
        });


        search_button=(ImageButton) findViewById(R.id.search_button);
        search_edit=(EditText)findViewById(R.id.search_edit);//绑定控件

        search_button.setOnClickListener(new View.OnClickListener() {
            //通过输入的文字查询相关城市
            @Override
            public void onClick(View view) {
                QWeather.getGeoCityLookup(SearchActivity.this,search_edit.getText().toString(), new QWeather.OnResultGeoListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
handler.post(new Runnable() {
    @Override
    public void run() {
        Toast.makeText(getApplicationContext(),"查询的地区可能不存在，重新输入试试？",Toast.LENGTH_SHORT).show();
    }
});
                    }

                    @Override
                    public void onSuccess(GeoBean geoBean) {
                        if (Code.OK == geoBean.getCode()) {
//如果查询到了城市，获取该城市id和名字，将城市列表用rv展示
                            List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                            List<SearchCity>searchCities=new ArrayList<>();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView_hot_city.setVisibility(View.GONE);//隐藏热点城市的rv，避免它们重叠以及影响用户体验
                                    int i;
                                    for(i=0;i<locationBean.size();i++){
                                        SearchCity searchCity=new SearchCity(locationBean.get(i).getAdm1()+","+
                                                locationBean.get(i).getAdm2()+"市,"+locationBean.get(i).getName()
                                                ,locationBean.get(i).getId());
                                        searchCities.add(searchCity);
                                    }
                                    searchResultAdapter = new SearchResultAdapter(getApplicationContext(),searchCities,mHandler);
                                    recyclerView_search_result.setAdapter(searchResultAdapter);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FINISH_ACTIVITY:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    public void onBackPressed(){
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);

        //如果是用户第一次打开尚未定位时，在查询页面选择了返回，那么认为用户是想返回上一级
        if(last_county.getString("id",null)==null)
        {
            Intent intent=new Intent(SearchActivity.this,CityListActivity.class);
            startActivity(intent);
            finish();
        }//若用户在此前已经选择过城市，那么认为用户是不想更改定位，为其返回至主页
        else {
            Intent intent=new Intent(SearchActivity.this,MainActivity.class);
            startActivity(intent);
            finish();}
    }
}