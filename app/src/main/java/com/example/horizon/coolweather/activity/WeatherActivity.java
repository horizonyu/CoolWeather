package com.example.horizon.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.*;
import android.widget.Toast;

import com.example.horizon.coolweather.R;
import com.example.horizon.coolweather.gson.Forecast;
import com.example.horizon.coolweather.gson.Weather;
import com.example.horizon.coolweather.util.HttpCallbackListener;
import com.example.horizon.coolweather.util.HttpUtil;
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


    private TextView tv_date_item;
    private TextView tv_info_item;
    private TextView tv_max_item;
    private TextView tv_min_item;

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
        }else {
            //没有缓存则直接从服务器中获取数据
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);

        }
    }

    /**
     * 根据天气id请求天气数据
     * @param weatherId
     */
    private void requestWeather(String weatherId) {
        //根据天气id以及和风天气的个人认证key拼接请求的链接
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=c90d37e0141f4845a7c5ee3cb378a2c2";

        //根据地址发送请求
        HttpUtil.sendHttpRequest(weatherUrl, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                final Weather weather = Utility.handleWeatherResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putString("weather",response);
                            editor.apply();
                            showWeatherInfo(weather);

                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
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

            tv_date_item = (TextView) view.findViewById(R.id.tv_date_item);
            tv_info_item = (TextView) view.findViewById(R.id.tv_info_item );
            tv_max_item = (TextView) view.findViewById(R.id.tv_max_item);
            tv_min_item = (TextView) view.findViewById(R.id.tv_min_item);

            tv_date_item.setText(forecast.date);
            tv_info_item.setText(forecast.more.info);
            tv_max_item.setText(forecast.temperature.max);
            tv_min_item.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null){
            tv_aqi.setText(weather.aqi.city.aqi);
            tv_pm25.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfortable.info;
        String carwash = "洗车指数：" + weather.suggestion.carwash.info;
        String sport = "运动指数：" + weather.suggestion.sport.info;
        tv_comfort.setText(comfort);
        tv_carwash.setText(carwash);
        tv_sport.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initUI() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        tv_title = (TextView) findViewById(R.id.tv_title_city);
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
