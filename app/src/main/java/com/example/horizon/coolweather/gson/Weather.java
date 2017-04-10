package com.example.horizon.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by horizon on 4/7/2017.
 * 总的实例类，引用刚刚创建的各个实体类。
 */

public class Weather {
    public String status;               //接口状态
    public Basic basic;                 //基本情况
    public AQI aqi;                     //空气质量
    public Now now;                     //天气的实时情况
    public Suggestion suggestion;       //生活建议
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList; //预测的天数列表
}
