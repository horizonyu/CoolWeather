package com.example.horizon.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by horizon on 4/7/2017.
 * 实况天气
 * "now":{
             "cond":{"code":"104","txt":"阴"},
             "fl":"18",
             "hum":"81",
             "pcpn":"0.1",
             "pres":"1013",
             "tmp":"17",
             "vis":"7",
             "wind":{"deg":"58","dir":"东北风","sc":"3-4","spd":"11"}
        }
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;          //温度

    @SerializedName("cond")
    public More more;

    public class More{                  //天气情况
        @SerializedName("txt")
        public String info;             //天气情况描述
    }

}
