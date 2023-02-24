package com.example.weather.hourly;

public class Hourly {
    private String temp;
    private String weather;
    private String wind_level;
  //  private String air_level;
    private String time;

    public String getTemp() {
        return temp;
    }

    public String getWeather() {
        return weather;
    }

    public String getWind_level() {
        return wind_level;
    }

  //  public String getAir_level() {
  //      return air_level;
  //  }

    public String getTime() {
        return time;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setWind_level(String wind_level) {
        this.wind_level = wind_level;
    }

   // public void setAir_level(String air_level) {
   //     this.air_level = air_level;
   // }

    public void setTime(String time) {
        this.time = time;
    }
    public Hourly(String temp,String weather,String wind_level,String time){
        this.temp=temp;
        this.weather=weather;
        this.wind_level=wind_level;
       // this.air_level=air_level;
        this.time=time;
    }
}
