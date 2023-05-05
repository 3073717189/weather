package com.example.weather;

import java.io.Serializable;

public class Cityinfo implements Serializable {
    private int id;//自动生成的主键id
    private String user_name;//用户名
    private String city_id;//城市id
    private String city_name;//城市名字
    public Cityinfo(){}

    public Cityinfo(int id, String user_name, String city_id, String city_name) {
        this.id = id;
        this.user_name = user_name;
        this.city_id = city_id;
        this.city_name = city_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
}
