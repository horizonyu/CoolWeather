package com.example.horizon.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by horizon on 4/7/2017.
 * 天气预报
 * "daily_forecast":
 * [
        {
         "cond":{"code_d":"104","code_n":"101","txt_d":"阴","txt_n":"多云"},
         "tmp":{"max":"19","min":"13"},
        }

    ]

 daily_forecast包含的是一个数组，数组中的每一项代表着未来一天的天气信息
 所以这里只是定义了，单日天气的实体类，在声明实体类引用的时候使用集合类型进行声明。
 */

public class Forecast {

    public String date;

    @SerializedName("cond")                 //天气情况
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }

    @SerializedName("tmp")                  //一天的温度情况
    public Temperature temperature;
    public class Temperature{

        public String max;                  //最高温和最低温
        public String min;
    }

}
