package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;



import com.example.weather.searchResult.SearchCity;
import com.example.weather.searchResult.SearchResultAdapter;
import com.qweather.sdk.bean.base.Code;

import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchResultAdapter searchResultAdapter;
    public String TAG;
    ImageButton search_button;
    EditText search_edit;
    SharedPreferences last_county;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_result_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//初始化rv


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
                                    int i;
                                    for(i=0;i<locationBean.size();i++){
                                        SearchCity searchCity=new SearchCity(locationBean.get(i).getAdm1()+","+
                                                locationBean.get(i).getAdm2()+"市,"+locationBean.get(i).getName()
                                                ,locationBean.get(i).getId());
                                        searchCities.add(searchCity);
                                    }
                                    searchResultAdapter = new SearchResultAdapter(searchCities);
                                    recyclerView.setAdapter(searchResultAdapter);
                                }
                            });
                        }
                    }
                });
            }
        });
    }
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