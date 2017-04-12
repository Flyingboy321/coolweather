package com.hhxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/12.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
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
        return "Now{" +
                "more=" + more +
                ", temperature='" + temperature + '\'' +
                '}';
    }
}
