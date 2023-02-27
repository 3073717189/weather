package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;

public class LifeActivity extends AppCompatActivity {
    //本活动使用的代码较为简单，会少作注释
TextView spt,cw,drsg,fis,uv,tra,ag,comf,flu,ap,ac,gl,mu,dc,ptfc,spi;
public String TAG;
SharedPreferences last_county;
    final Handler handler = new Handler();//子线程设置控件内容
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//删除标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life);
        //设置透明状态栏，对应xml文件中添加属性android:fitsSystemWindows="true"
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//状态栏深色字体
InitText();
    }

    private void InitText() {
spt=(TextView) findViewById(R.id.life_spt);
cw=(TextView) findViewById(R.id.life_cw);
drsg=(TextView) findViewById(R.id.life_drsg);
fis=(TextView) findViewById(R.id.life_fis);
uv=(TextView) findViewById(R.id.life_uv);
tra=(TextView) findViewById(R.id.life_tra);
ag=(TextView) findViewById(R.id.life_ag);
comf=(TextView) findViewById(R.id.life_comf);
flu=(TextView) findViewById(R.id.life_flu);
ap=(TextView) findViewById(R.id.life_ap);
ac=(TextView) findViewById(R.id.life_ac);
gl=(TextView) findViewById(R.id.life_gl);
mu=(TextView) findViewById(R.id.life_mu);
dc=(TextView) findViewById(R.id.life_dc);
ptfc=(TextView) findViewById(R.id.life_ptfc);
spi=(TextView) findViewById(R.id.life_spi);
        ArrayList<IndicesType> life = new ArrayList<>();
        life.add(IndicesType.ALL);//获取全部类型生活建议
        last_county = getSharedPreferences("last_county", MODE_PRIVATE);
        QWeather.getIndices1D(LifeActivity.this, last_county.getString("id", null),
                Lang.ZH_HANS, life, new QWeather.OnResultIndicesListener() {
                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getWeather onError: " + e);
                    }

                    @Override
                    public void onSuccess(IndicesBean indicesBean) {
                        if (Code.OK == indicesBean.getCode()) {
                            List<IndicesBean.DailyBean> life = indicesBean.getDailyList();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    spt.setText("运动建议：" + life.get(0).getText());
                                    cw.setText("洗车建议：" + life.get(1).getText());
                                    drsg.setText("穿衣建议：" + life.get(2).getText());
                                    fis.setText("钓鱼建议："+life.get(3).getText());
                                    uv.setText("紫外线指数："+life.get(4).getText());
                                    tra.setText("旅游建议："+life.get(5).getText());
                                    ag.setText("花粉过敏指数："+life.get(6).getText());
                                    comf.setText("舒适度指数：" + life.get(7).getText());
                                    flu.setText("感冒指数："+life.get(8).getText());
                                    ap.setText("空气污染扩散条件指数："+life.get(9).getText());
                                    ac.setText("空调开启建议："+life.get(10).getText());
                                    gl.setText("太阳镜建议："+life.get(11).getText());
                                    mu.setText("化妆建议："+life.get(12).getText());
                                    dc.setText("晾晒建议："+life.get(13).getText());
                                    ptfc.setText("交通建议："+life.get(14).getText());
                                    spi.setText("防晒建议："+life.get(15).getText());
                                }
                            });
                        }
                    }
                });

    }
}