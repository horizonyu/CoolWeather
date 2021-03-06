package com.example.horizon.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.horizon.coolweather.R;
import com.example.horizon.coolweather.gson.Forecast;
import com.example.horizon.coolweather.gson.Weather;
import com.example.horizon.coolweather.service.AutoUpdateService;
import com.example.horizon.coolweather.util.HttpCallbackListener;
import com.example.horizon.coolweather.util.HttpUtil;
import com.example.horizon.coolweather.util.Utility;

//TODO 使用okHttp进行网络请求
//TODO 同时存在小bug,在选择显示地区时，重复刷新页面两次，会显示上一次选择的地方。
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
    private ImageView iv_bing_pic_bg;
    private Button bt_change_location;
    public DrawerLayout drawerLayout;


    private TextView tv_date_item;
    private TextView tv_info_item;
    private TextView tv_max_item;
    private TextView tv_min_item;

    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实现背景与状态栏的融合
        combineBackStatus();
        setContentView(R.layout.activity_weather);
        //初始化控件
        initUI();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //从本地获取图片，设置为背景图片
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(iv_bing_pic_bg);
        } else {
            //本地不存在图片则从网络获取
            loadBingPic();
        }

        //获取天气数据
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null ) {
            //如果存在缓存则直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);

            weatherId = weather.basic.weatherId;

            showWeatherInfo(weather);
        } else {
            //没有缓存则直接从服务器中获取数据
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);

        }


        //下拉页面进行更新天气信息
        refreshWeatherData(weatherId);

        //更换天气地址
        changeLocation();
    }

    private void changeLocation() {
        bt_change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开滑动菜单
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
    }

    private void refreshWeatherData(final String weatherId) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                String weatherId = preferences.getString("weather_id", null);
                requestWeather(weatherId);
            }
        });
    }

    /**
     * 实现将背景与状态栏完美融合,并且
     */
    private void combineBackStatus() {
        //由于这一功能是在Android 5.0 及以上的系统才支持的，所以预先进行一个判断。
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //改变系统UI显示方法
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            //将状态栏设置成透明色
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            Toast.makeText(this, "fullsreen", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 根据天气id请求天气数据
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
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
                        if (weather != null && weather.status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            editor.putString("weather", response);
                            editor.apply();
                            showWeatherInfo(weather);

                            //自动更新天气信息
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });
        //并且每次在更新天气信息时，从网络获取新的背景图片
        loadBingPic();
    }

    /**
     * 加载背景图片(从必应官网获取)
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestBingPic, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("bing_pic", response);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Glide ，加载图片的类库，下面就是其使用方法
                        Glide.with(WeatherActivity.this).load(response).into(iv_bing_pic_bg);

                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
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
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout, false);

            tv_date_item = (TextView) view.findViewById(R.id.tv_date_item);
            tv_info_item = (TextView) view.findViewById(R.id.tv_info_item);
            tv_max_item = (TextView) view.findViewById(R.id.tv_max_item);
            tv_min_item = (TextView) view.findViewById(R.id.tv_min_item);

            tv_date_item.setText(forecast.date);
            tv_info_item.setText(forecast.more.info);
            tv_max_item.setText(forecast.temperature.max);
            tv_min_item.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if (weather.aqi != null) {
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

        //背景图片
        iv_bing_pic_bg = (ImageView) findViewById(R.id.iv_bing_pic_bg);

        //刷新更新天气
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);

        //更换地址
        bt_change_location = (Button) findViewById(R.id.bt_change_location);

        //隐藏的碎片
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    }
}
