package com.example.weather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.weather.citydb.City;
import com.example.weather.citydb.CityDBHelper;
import com.example.weather.location.LocationCallback;
import com.example.weather.location.MyLocationListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.List;

public class CityListActivity extends AppCompatActivity implements LocationCallback {
    SharedPreferences last_county;
    public String TAG;
    FloatingActionButton city_list_search;
    ImageButton city_list_back;
    //以下定位相关
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    //权限数组
    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;
    double longitude;//经度
    double latitude;//纬度
    ImageButton GPSButton;//定位按钮
    //以上定位相关

    private FloatingActionButton search_start;//查询按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        registerIntent();//初始化定位
        LocationClient.setAgreePrivacy(true);//同意隐私保护

        initLocation();//初始化定位
        requestPermission();//初始化定位
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);
        initLayout();

    }

    private void initLayout() {
        city_list_search = (FloatingActionButton) findViewById(R.id.city_list_search);
        city_list_search.setOnClickListener(new View.OnClickListener() {
            //点击查询按钮跳转至搜索城市页面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityListActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        city_list_back = (ImageButton) findViewById(R.id.city_list_back);
        city_list_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击返回按钮时间，其实这个图片按钮和返回按键是一样的功能
                //如果是用户第一次打开尚未定位时，在城市列表选择了返回，那么认为用户是想退出软件，以下代码关闭软件
                if (last_county.getString("id", null) == null) {
                    finish();
                }//若用户在此前已经选择过城市，那么认为用户是不想更改定位，为其返回至主页
                else {
                    Intent intent = new Intent(CityListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        GPSButton = (ImageButton) findViewById(R.id.city_list_gps);
        GPSButton.setOnClickListener(new View.OnClickListener() {
            //编辑定位按钮点击事件
            @Override
            public void onClick(View view) {
                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
                } else {
                    QWeather.getGeoCityLookup(getApplicationContext(), longitude + "," + latitude, new QWeather.OnResultGeoListener() {
                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "getWeather onError: " + e);
                        }

                        @Override
                        public void onSuccess(GeoBean geoBean) {
                            if (Code.OK == geoBean.getCode()) {
                                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                                last_county.edit().putString("id", locationBean.get(0).getId()).commit();
                                //将定位到的城市id和名字存入数据库
                                saveCity(new City(locationBean.get(0).getId(), locationBean.get(0).getAdm1() + "," +
                                        locationBean.get(0).getAdm2() + "市," + locationBean.get(0).getName()));


                                Intent intent = new Intent(CityListActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    public void saveCity(City city) {
        CityDBHelper dbHelper = new CityDBHelper(getApplicationContext());
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

    public void onBackPressed() {


        //如果是用户第一次打开尚未定位时，在城市列表选择了返回，那么认为用户是想退出软件，以下代码关闭软件
        if (last_county.getString("id", null) == null) {
            finish();
        }//若用户在此前已经选择过城市，那么认为用户是不想更改定位，为其返回至主页
        else {
            Intent intent = new Intent(CityListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    //以下是定位相关
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        latitude = bdLocation.getLatitude();    //获取纬度信息
        longitude = bdLocation.getLongitude();    //获取经度信息
        float radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
        String coorType = bdLocation.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
        int errorCode = bdLocation.getLocType();//161  表示网络定位结果
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
        String addr = bdLocation.getAddrStr();    //获取详细地址信息
        String country = bdLocation.getCountry();    //获取国家
        String province = bdLocation.getProvince();    //获取省份
        String city = bdLocation.getCity();    //获取城市
        String district = bdLocation.getDistrict();    //获取区县
        String street = bdLocation.getStreet();    //获取街道信息
        String locationDescribe = bdLocation.getLocationDescribe();    //获取位置描述信息
    }

    private void registerIntent() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            //    boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));

            if (fineLocation) {
                //权限已经获取到，开始定位
                try {
                    startLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //开始定位
    private void startLocation() throws Exception {
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();

        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
        option.setNeedNewVersionRgc(true);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(option);
        //启动定位
        mLocationClient.start();

    }

    private void initLocation() {//初始化定位
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            myListener.setCallback((LocationCallback) this);
            //注册定位监听
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            //如果开发者需要获得当前点的地址信息，此处必须为true
            option.setIsNeedAddress(true);
            //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
            option.setNeedNewVersionRgc(true);
            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            mLocationClient.setLocOption(option);
        }
    }

    private void requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        //开始定位
        try {
            startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}