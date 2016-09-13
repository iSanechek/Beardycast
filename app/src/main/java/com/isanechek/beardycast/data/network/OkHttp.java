package com.isanechek.beardycast.data.network;

import com.isanechek.beardycast.Constants;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by isanechek on 11.04.16.
 */
public class OkHttp {
    private static OkHttpClient client;

    public static void init() {

        client = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT_OKHTTP, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.TIMEOUT_OKHTTP, TimeUnit.MILLISECONDS)
                .build();
        
    }

    public static OkHttpClient getOkHttpClient() {
        return client;
    }

}
