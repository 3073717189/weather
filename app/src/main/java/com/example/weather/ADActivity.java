package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;

import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.util.AdError;

import java.util.Locale;
import java.util.Map;

public class ADActivity extends AppCompatActivity {


    Boolean adLoaded,videoCached=false;
    String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adactivity);
        GDTAdSdk.init(getApplicationContext(),"201826896");
         RewardVideoAD rewardVideoAD = new RewardVideoAD(this, "5015403540275419", new RewardVideoADListener() {
             @Override
             public void onADLoad() {
                 adLoaded = true;
                 String msg = "load ad success ! ";
                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
             }

             @Override
             public void onVideoCached() {
                 videoCached = true;
                 Log.i(TAG, "onVideoCached");
             }

             @Override
             public void onADShow() {
                 Log.i(TAG, "onADShow");
             }

             @Override
             public void onADExpose() {
                 Log.i(TAG, "onADExpose");
             }

             @Override
             public void onReward(Map<String, Object> map) {
                 Log.i(TAG, "onReward " + map.get(ServerSideVerificationOptions.TRANS_ID));  // 获取服务端验证的唯一 ID
             }

             @Override
             public void onADClick() {
                 Log.i(TAG, "onADClick");
             }

             @Override
             public void onVideoComplete() {
                 Log.i(TAG, "onVideoComplete");
             }

             @Override
             public void onADClose() {
                 Log.i(TAG, "onADClose");
             }

             @Override
             public void onError(AdError adError) {
                 String msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
                         adError.getErrorCode(), adError.getErrorMsg());
                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
             }
         }); // 有声播放
        if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
            //广告展示检查3：展示广告前判断广告数据未过期
            rewardVideoAD.showAD();
        } else {
            Toast.makeText(getApplicationContext(), "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
        }
        rewardVideoAD.loadAD();

     /*   Button rw;
        rw=(Button) findViewById(R.id.button_rw);
        rw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
                    //广告展示检查3：展示广告前判断广告数据未过期
                    rewardVideoAD.showAD();
                } else {
                    Toast.makeText(getApplicationContext(), "此条广告已经展示过，请再次请求广告后进行广告展示！", Toast.LENGTH_LONG).show();
                }
            }
        });*/


    }


}