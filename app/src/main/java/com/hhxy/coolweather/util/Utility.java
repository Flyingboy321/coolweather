package com.hhxy.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hhxy.coolweather.db.City;
import com.hhxy.coolweather.db.County;
import com.hhxy.coolweather.db.Province;
import com.hhxy.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/4/11.
 */

public class Utility {
    private static final String TAG = "Utility";
//    处理省级数据
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
//                    操作数据将数据库存入到数据库中
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
//    处理了市级数据
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i=0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();

                    city.setProvinceId(provinceId);
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
//    处理县级数据
    public static boolean handleCountyResponse(String response ,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountry = new JSONArray(response);
                for (int i=0;i<allCountry.length();i++){
                    Log.e(TAG, "handleCountyResponse: 什么鬼");

                    JSONObject countyObject = allCountry.getJSONObject(i);
                    Log.e(TAG, "handleCountyResponse:json对象 "+countyObject.toString());
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    Log.e(TAG, "handleCountyResponse:这个是在handlecounty中打印的 "+county.toString());
                    county.save();


                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
//    解析具体天气数据，返回天气对象
//{
//    HeWeather: [
//    {
//        aqi: {},
//        basic: {},
//        daily_forecast: [],
//        hourly_forecast: [],
//        now: {},
//        status: "ok",
//        suggestion: {}
//    }
//    ]
//}
    public static Weather handleWeatherResponse(String response){
        try {
//            首先得到对象
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.get(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        如果解析失败，返回null
        return null;

    }
}
