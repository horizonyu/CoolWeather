package com.example.horizon.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.*;

import com.example.horizon.coolweather.R;
import com.example.horizon.coolweather.gson.Forecast;
import com.example.horizon.coolweather.gson.Weather;
import com.example.horizon.coolweather.util.Utility;

/**
 * Created by horizon on 4/7/2017.
 */

public class WeatherActivity extends Activity {
    private ScrollView weatherLayout;
    private TextView tv_title;
    private TextView tv_update_time;
    private TextView tv_degree;
    private TextView tv_weather_info;
    private LinearLayout forecastLayout;
    private TextView tv_aqi;
    private TextView tv_pm25;
    private TextView tv_comfort;
    private TextView tv_carwash;
    private TextView tv_sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化控件
        initUI();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null){
            //如果存在缓存则直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }


    }

    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        //控件的初始化
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°c";
        String weatherInfo = weather.now.more.info;
        tv_title.setText(cityName);
        tv_update_time.setText(updateTime);
        tv_degree.setText(degree);
        tv_weather_info.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout, false);
            TextView tv_date = (TextView) findViewById(R.id.tv_date);
            TextView tv_info = (TextView) findViewById(R.id.tv_info);
            TextView tv_max = (TextView) findViewById(R.id.tv_max);
            TextView tv_min = (TextView) findViewById(R.id.tv_min);

            tv_date.setText(forecast.date);
            tv_info.setText(forecast.more.info);
            tv_max.setText(forecast.temperature.max);
            tv_min.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null){
            tv_aqi.setText(weather.aqi.city.aqi);
            tv_pm25.setText(weather.aqi.city.pm25);
        }
    }

    private void initUI() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_update_time = (TextView) findViewById(R.id.tv_update_time);
        tv_degree = (TextView) findViewById(R.id.tv_degree);
        tv_weather_info = (TextView) findViewById(R.id.tv_weather_info);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_pm25 = (TextView) findViewById(R.id.tv_pm25);
        tv_comfort = (TextView) findViewById(R.id.tv_comfort);
        tv_carwash = (TextView) findViewById(R.id.tv_washcar);
        tv_sport = (TextView) findViewById(R.id.tv_sport);
    }
}
