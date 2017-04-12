package com.hhxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

//    注意下面的一个变量是一个集合
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @Override
    public String toString() {
        return "Weather{" +
                "aqi=" + aqi +
                ", status='" + status + '\'' +
                ", basic=" + basic +
                ", now=" + now +
                ", suggestion=" + suggestion +
                ", forecastList=" + forecastList +
                '}';
    }
}
