package com.hhxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/12.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sprot sprot;

    public class Comfort{
        @SerializedName("txt")
        public String info;

        @Override
        public String toString() {
            return "Comfort{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;

        @Override
        public String toString() {
            return "CarWash{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }
    public class Sprot{
        @SerializedName("txt")
        public String info;

        @Override
        public String toString() {
            return "Sprot{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "carWash=" + carWash +
                ", comfort=" + comfort +
                ", sprot=" + sprot +
                '}';
    }
}
