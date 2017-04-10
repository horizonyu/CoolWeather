package com.example.horizon.coolweather.gson;

/**
 * Created by horizon on 4/7/2017.
 * 空气质量，仅限国内城市
 * "aqi":{
             "city":{
                     "aqi":"92",
                     "co":"1",
                     "no2":"57",
                     "o3":"37",
                     "pm10":"83",
                     "pm25":"68",
                     "qlty":"良",
                     "so2":"10"
                    }

        }
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;          //空气质量指数
        public String pm25;         //pm2.5的值
    }
}
