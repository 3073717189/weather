package com.example.weather;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

public class WeatherUtil {
    /**
     * 跟随当前切换背景图片
     * @param code 天气状况代码
     * @param activity 传入当前活动
     */
    public static void changeBackground(Activity activity, int code){
        switch (code){
            case 100:
            case 150:
                //晴
               activity.getWindow().setBackgroundDrawableResource(R.drawable.background_sunny);
                break;
            case 101:
            case 151:
            case 102:
            case 152:
            case 103:
            case 153:
                // 多云
               activity.getWindow().setBackgroundDrawableResource(R.drawable.background_cloudy);
                break;
            case 300:
            case 301:
            case 302:
            case 303:
            case 305:
            case 306:
            case 307:
            case 308:
            case 309:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 315:
            case 316:
            case 317:
            case 318:
            case 319:
            case 320:
            case 324:
            case 350:
            case 351:
            case 399:

                // 雨
                activity.getWindow().setBackgroundDrawableResource(R.drawable.background_rainy);
                break;

            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            case 405:
            case 406:
            case 407:
            case 408:
            case 409:
            case 410:
            case 456:
            case 457:
            case 499:
                //雪
                activity.getWindow().setBackgroundDrawableResource(R.drawable.background_snowy);
                break;

            case 104:
                activity.getWindow().setBackgroundDrawableResource(R.drawable.background_overcast);
                break;
            default:
                activity.getWindow().setBackgroundDrawableResource(R.drawable.background_cloudy);
        }
    }
    /**
     * 更改天气状态图标
     * @param weatherStateIcon 显示天气状态的ImageView
     * @param code 天气状况代码
     */
    public static void changeIcon(ImageView weatherStateIcon, int code) {
        switch (code) {
            case 100:
            case 150:
                // 晴
                weatherStateIcon.setImageResource(R.drawable.weather_icon_100);
                break;
            case 101:
            case 151:
                // 多云
                weatherStateIcon.setImageResource(R.drawable.weather_icon_101);
                break;
            case 102:
            case 152:
                // 少云
                weatherStateIcon.setImageResource(R.drawable.weather_icon_102);
                break;
            case 103:
            case 153:
                // 晴间多云
                weatherStateIcon.setImageResource(R.drawable.weather_icon_103);
                break;
            case 104:
                // 阴
                weatherStateIcon.setImageResource(R.drawable.weather_icon_104);
                break;
            case 300:
                // 阵雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_300);
                break;
            case 301:
                // 强阵雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_301);
                break;
            case 302:
                // 雷阵雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_302);
                break;
            case 303:
                // 强雷阵雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_303);
                break;
            case 304:
                // 雷阵雨伴有冰雹
                weatherStateIcon.setImageResource(R.drawable.weather_icon_304);
                break;
            case 305:
            case 309:
                // 小雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_305);
                break;
            case 306:
            case 315:
                // 中雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_306);
                break;
            case 307:
            case 316:
                // 大雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_307);
                break;
            case 308:
            case 318:
                // 极端降雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_308);
                break;
            case 310:
            case 311:
            case 312:
                // 暴雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_310);
                break;
            case 313:
                // 冻雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_313);
                break;
            case 314:
            case 324:
                // 小到中雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_314);
                break;
            case 317:
            case 319:
            case 320:
                // 大暴雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_317);
                break;

            case 350:
            case 351:
// 阵雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_350);
                break;
            case 399:
// 雨
                weatherStateIcon.setImageResource(R.drawable.weather_icon_399);
                break;
            case 400:
// 小雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_400);
                break;
            case 401:
// 中雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_401);
                break;
            case 402:
// 大雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_402);
                break;
            case 403:
// 暴雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_403);
                break;
            case 404:
// 雨夹雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_404);
                break;
            case 405:
// 雨雪天气
                weatherStateIcon.setImageResource(R.drawable.weather_icon_405);
                break;
            case 406:
// 阵雨夹雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_406);
                break;
            case 407:
// 阵雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_407);
                break;
            case 408:
            case 409:
            case 410:
// 大到暴雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_408);
                break;
            case 456:
            case 457:
// 阵雨夹雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_456);
                break;
            case 499:
// 雪
                weatherStateIcon.setImageResource(R.drawable.weather_icon_499);
                break;
            case 500:
// 薄雾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_500);
                break;
            case 501:
// 雾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_501);
                break;
            case 502:
// 霾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_502);
                break;
            case 503:
// 扬沙
                weatherStateIcon.setImageResource(R.drawable.weather_icon_503);
                break;
            case 504:
// 浮尘
                weatherStateIcon.setImageResource(R.drawable.weather_icon_504);
                break;
            case 507:
            case 508:
// 沙尘暴
                weatherStateIcon.setImageResource(R.drawable.weather_icon_507);
                break;
            case 509:
            case 510:
// 浓雾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_509);
                break;
            case 511:
// 中度霾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_511);
                break;
            case 512:
            case 513:
// 重度霾和严重霾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_512);
                break;
            case 514:
// 大雾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_514);
                break;
            case 515:
// 特强浓雾
                weatherStateIcon.setImageResource(R.drawable.weather_icon_515);
                break;
            case 900:
// 热
                weatherStateIcon.setImageResource(R.drawable.weather_icon_900);
                break;
            case 901:
                //冷
                weatherStateIcon.setImageResource(R.drawable.weather_icon_901);
                break;
            case 999:
                //未知
                weatherStateIcon.setImageResource(R.drawable.weather_icon_999);
                break;
            case 800:
                //新月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_800);
                break;
            case 801:
                //蛾眉月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_801);
                break;
                case 802:
                //上弦月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_802);
                break;
                case 803:
                //盈凸月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_803);
                break;
                case 804:
                //满月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_804);
                break;
                case 805:
                //亏凸月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_805);
                break;
                case 806:
                //下弦月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_806);
                break;
                case 807:
                //残月
                weatherStateIcon.setImageResource(R.drawable.weather_icon_807);
                break;
        }}}