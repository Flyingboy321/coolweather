package com.hhxy.coolweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hhxy.coolweather.gson.Forecast;
import com.hhxy.coolweather.gson.Weather;
import com.hhxy.coolweather.util.HttpUtil;
import com.hhxy.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//        初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);

//        显示城市tv
        titleCity = (TextView) findViewById(R.id.title_city);
//        显示更新时间tv
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

//        显示今天天气气温now中的控件tv degree==温度，度数
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

//        显示未来天气状况的控件
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

//        空气质量布局中的控件
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);

//        建议suggest中的控件
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

//        获得文件的SharedPreferences 对象，后面要将天气的具体消息缓存到sp文件中
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        从文件中获得天气详情字符串，只用本地解析即可
        String weatherString = sharedPreferences.getString("weather",null);
        if (weatherString!=null){

        }else {
//            无缓存就从服务器中获取
//            首先获得前面activity中传来的天气id,注意在前一个activity  item点击事件中存入的键应该是weather—id
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);

        }

    }

    private void requestWeather(String weatherId) {
//        拼接URL
        String weatherUrl = "http://guolin.tech/api/weather?" +
                "cityid="+weatherId+"&key=03545b8d0e004ce582ca48f93dcb0315";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resopnseText = response.body().string();
//                解析数据得到具体天气对象
                final Weather weather = Utility.handleWeatherResponse(resopnseText);
                Log.e(TAG, "onResponse: "+weather.toString());
//                切换线程更新ui界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeatherInfo(weather);

                    }
                });


            }
        });

//        请求图片
        loadBingPic();
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                获得图片的UI地址
                final String bingPic = response.body().string();
//                将线程切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });

            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime;
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
//        将显示未来天气的布局清空
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
//            创建未来天气的子布局view对象，
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText("最高温"+forecast.temperature.max);
            minText.setText("最低温"+forecast.temperature.min);

            forecastLayout.addView(view);
            if (weather.aqi!=null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }

            String comfort = "舒适度："+weather.suggestion.comfort.info;
            String carWash = "洗车指数："+weather.suggestion.carWash.info;

            if (weather.suggestion.sprot!=null){
                String sport = "运动指数："+weather.suggestion.sprot.info;
                sportText.setText(sport);

            }



            comfortText.setText(comfort);
            carWashText.setText(carWash);



        }

    }
}
