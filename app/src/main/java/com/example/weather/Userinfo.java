package com.example.weather;

import java.io.Serializable;

public class Userinfo implements Serializable {
    private int id;//用户id
    private String userName;//用户名
    private String userPass;//用户密码
    private String createDate;//注册时间

    public Userinfo(){}

    public Userinfo(int id, String userName, String userPass, String createDate) {
        this.id = id;
        this.userName = userName;
        this.userPass = userPass;
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
