package com.example.horizon.coolweather.util;

import android.text.TextUtils;

import com.example.horizon.coolweather.db.CoolWeatherDB;
import com.example.horizon.coolweather.gson.Weather;
import com.example.horizon.coolweather.model.City;
import com.example.horizon.coolweather.model.County;
import com.example.horizon.coolweather.model.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by horizon on 4/6/2017.
 * <p>
 * 由于从服务器返回的省市县级的数据都是下面的格式，所以对数据进行如下处理
 * <p>
 * [{"id":1,"name":"北京"},{"id":2,"name":"上海"},{"id":3,"name":"天津"},
 * {"id":4,"name":"重庆"},{"id":5,"name":"香港"},{"id":6,"name":"澳门"}]
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    Province province = new Province();
                    province.setProvinceCode(String.valueOf(id));
                    province.setProvinceName(name);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 服务器中的数据存储格式为
     * [{"id":113,"name":"南京"},{"id":114,"name":"无锡"},{"id":115,"name":"镇江"},
     * {"id":116,"name":"苏州"},{"id":117,"name":"南通"},{"id":118,"name":"扬州"}]
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                List<JSONObject> list = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    City city = new City();
                    city.setCityCode(String.valueOf(id));
                    city.setCityName(name);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 服务器县级数据的存储格式为
     * [{"id":937,"name":"苏州","weather_id":"CN101190401"},{"id":938,"name":"常熟","weather_id":"CN101190402"},
     * {"id":939,"name":"张家港","weather_id":"CN101190403"},{"id":940,"name":"昆山","weather_id":"CN101190404"}]
     * 解析并处理从服务器返回的县级数据
     */
    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                List<JSONObject> list = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String weather_id = jsonObject.getString("weather_id");
                    County county = new County();
                    county.setCountyCode(String.valueOf(id));
                    county.setCountyName(name);
                    county.setWeatherId(weather_id);
                    county.setCityId(cityId);

                    coolWeatherDB.saveCounty(county);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的Json数据解析成Weather实体类
     */
    public synchronized static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();

            //上面两步将天气数据主体内容解析出来，下面将Json数据转化成Weather对象
            return new Gson().fromJson(weatherContent,Weather.class);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解析和处理服务器返回的省级数据
     */
/*
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);

                    //将解析出来的数据存储到Province表中
                    coolWeatherDB.saveProvince(province);

                }
                return true;
            }
        }
        return false;

    }
*/

    /**
     * 解析和处理服务器返回的市级数据
     */
/*
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] arrary = c.split("\\|");
                    City city = new City();
                    city.setCityCode(arrary[0]);
                    city.setCityName(arrary[1]);
                    city.setProvinceId(provinceId);

                    //将解析出来的数据存储到City表中
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;

    }
*/

    /**
     * 解析并处理从服务器返回的县级数据
     */
 /*   public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);

                    //将解析的数据保存到County表中
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
*/

}
