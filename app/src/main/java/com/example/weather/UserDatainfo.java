package com.example.weather;

import java.io.Serializable;

public class UserDatainfo implements Serializable {
    private String user_name;//用户名
    private String last_county;//当前展示城市id
    private int start_hour;//夜间模式起始小时
    private int start_minute;//夜间模式起始分钟
    private int end_hour;//夜间模式结束小时
    private int end_minute;//夜间模式结束分钟
    private boolean switch_mode;//夜间模式开关
    private boolean switch_time;//定时夜间模式开关
    private boolean air_state;//空气信息开关
    private boolean forecast_state;//预报信息开关
    private boolean wind_state;//风向信息开关
    private boolean life_state;//生活建议开关
    private boolean other_state;//其他信息开关
    private boolean hourly_state;//逐小时显示开关
    private boolean service_state;//通知服务开关

    public UserDatainfo(){}

    public UserDatainfo( String user_name, String last_county,
                        int start_hour, int start_minute, int end_hour,
                        int end_minute, boolean switch_mode, boolean switch_time,
                        boolean air_state, boolean forecast_state, boolean wind_state,
                        boolean life_state, boolean other_state, boolean hourly_state, boolean service_state) {
        this.user_name = user_name;
        this.last_county = last_county;
        this.start_hour = start_hour;
        this.start_minute = start_minute;
        this.end_hour = end_hour;
        this.end_minute = end_minute;
        this.switch_mode = switch_mode;
        this.switch_time = switch_time;
        this.air_state = air_state;
        this.forecast_state = forecast_state;
        this.wind_state = wind_state;
        this.life_state = life_state;
        this.other_state = other_state;
        this.hourly_state = hourly_state;
        this.service_state = service_state;
    }


    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLast_county() {
        return last_county;
    }

    public void setLast_county(String last_county) {
        this.last_county = last_county;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public int getStart_minute() {
        return start_minute;
    }

    public void setStart_minute(int start_minute) {
        this.start_minute = start_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public void setEnd_minute(int end_minute) {
        this.end_minute = end_minute;
    }

    public boolean isSwitch_mode() {
        return switch_mode;
    }

    public void setSwitch_mode(boolean switch_mode) {
        this.switch_mode = switch_mode;
    }

    public boolean isSwitch_time() {
        return switch_time;
    }

    public void setSwitch_time(boolean switch_time) {
        this.switch_time = switch_time;
    }

    public boolean isAir_state() {
        return air_state;
    }

    public void setAir_state(boolean air_state) {
        this.air_state = air_state;
    }

    public boolean isForecast_state() {
        return forecast_state;
    }

    public void setForecast_state(boolean forecast_state) {
        this.forecast_state = forecast_state;
    }

    public boolean isWind_state() {
        return wind_state;
    }

    public void setWind_state(boolean wind_state) {
        this.wind_state = wind_state;
    }

    public boolean isLife_state() {
        return life_state;
    }

    public void setLife_state(boolean life_state) {
        this.life_state = life_state;
    }

    public boolean isOther_state() {
        return other_state;
    }

    public void setOther_state(boolean other_state) {
        this.other_state = other_state;
    }

    public boolean isHourly_state() {
        return hourly_state;
    }

    public void setHourly_state(boolean hourly_state) {
        this.hourly_state = hourly_state;
    }

    public boolean isService_state() {
        return service_state;
    }

    public void setService_state(boolean service_state) {
        this.service_state = service_state;
    }
}
