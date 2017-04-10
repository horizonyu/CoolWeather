package com.example.horizon.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by horizon on 4/7/2017.
 * 一些基本信息
 * "basic":{
                 "city":"苏州",
                 "cnty":"中国",
                 "id":"CN101190401",
                 "lat":"31.299379",
                 "lon":"120.619585",
                 "update":{
                             "loc":"2017-04-07 14:51",
                             "utc":"2017-04-07 06:51"
                            }
             }
 */

public class Basic {
    /**
     * 由于Json中的一些字段不适合作为Java字段来命名，因此这里使用了@SerializedName注解的方式来
     * 让Json字段和Java字段建立映射关系
     */
    @SerializedName("city")
    public String cityName;          //城市名称

    @SerializedName("id")
    public String weatherId;         //天气id

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;   //更新时的当地时间
    }


}
