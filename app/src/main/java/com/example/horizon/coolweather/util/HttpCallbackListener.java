package com.example.horizon.coolweather.util;

/**
 * Created by horizon on 4/6/2017.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
