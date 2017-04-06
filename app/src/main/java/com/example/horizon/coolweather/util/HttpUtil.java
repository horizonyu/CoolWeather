package com.example.horizon.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by horizon on 4/6/2017.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(){
            @Override
            public void run() {
                HttpURLConnection connection = null;
                super.run();
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");                         //设置请求方法
                    connection.setConnectTimeout(8000);                         //设置请求超时时长
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();               //返回数据流实例
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null){
                        //回调onFinish方法
                        listener.onFinish(response.toString());
                    }


                } catch (Exception e) {
                    if (listener != null){
                        //回调onErro方法
                        listener.onError(e);
                    }

                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }

            }
        }.start();
    }
}
