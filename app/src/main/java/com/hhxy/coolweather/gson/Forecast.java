package com.hhxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/12.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{

        public String max;
        public String min;

        @Override
        public String toString() {
            return "Temperature{" +
                    "max='" + max + '\'' +
                    ", min='" + min + '\'' +
                    '}';
        }
    }

    public class More{
        @SerializedName("txt_d")
        public String info;

        @Override
        public String toString() {
            return "More{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", temperature=" + temperature +
                ", more=" + more +
                '}';
    }
}
