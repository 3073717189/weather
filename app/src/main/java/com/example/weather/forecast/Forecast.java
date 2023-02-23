package com.example.weather;

public class Forecast {
    private String date;
    private String weather;
    private String temp;

    public String getDate() {
        return date;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
    public Forecast(String date,String weather,String temp){
        this.date=date;
        this.weather=weather;
        this.temp=temp;
    }
}
