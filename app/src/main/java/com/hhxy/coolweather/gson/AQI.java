package com.hhxy.coolweather.gson;

/**
 * Created by Administrator on 2017/4/12.
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;

        @Override
        public String toString() {
            return "AQICity{" +
                    "aqi='" + aqi + '\'' +
                    ", pm25='" + pm25 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AQI{" +
                "city=" + city +
                '}';
    }
}
