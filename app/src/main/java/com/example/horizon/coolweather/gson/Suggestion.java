package com.example.horizon.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by horizon on 4/7/2017.
 * 根据天气情况给出建议
 *         "suggestion": { //生活指数，仅限国内城市
                             "comf": { //舒适度指数
                                         "brf": "较不舒适",  //简介
                                         "txt": "白天天气多云，同时会感到有些热，不很舒适。" //详细描述
                             },
                             "cw": { //洗车指数
                                         "brf": "较适宜",
                                         "txt": "较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"
                             },

                             "sport": { //运动指数
                                         "brf": "较适宜",
                                         "txt": "天气较好，户外运动请注意防晒。推荐您进行室内运动。"
                             },

                        }
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfortable comfortable;     //舒适度指数
    public class Comfortable{
        @SerializedName("txt")
        public String info;             //舒适度描述
    }

    @SerializedName("cw")               //洗车指数
    public Carwash carwash;
    public class Carwash{
        @SerializedName("txt")
        public String info;             //具体建议
    }


    public Sport sport;                 //运动指数
    public class Sport{
        @SerializedName("txt")
        public String info;             //具体建议
    }
}
