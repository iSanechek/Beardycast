package com.isanechek.beardycast.data.network;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by isanechek on 11.04.16.
 */
public class OkHelper {

    public static String getBody(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = OkHttp
                    .getOkHttpClient()
                    .newCall(request)
                    .execute();
            return  response.body().string();
        } catch (IOException e) {
            msg(e.getMessage());
        }
        return null;
    }

    public static InputStream getStream(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;

        try {
            response = OkHttp
                    .getOkHttpClient()
                    .newCall(request)
                    .execute();
            return response.body().byteStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void msg(String text) {
//        Timber.tag("OkHelper");
//        Timber.e(text);
        Log.e("OkHelper", text);
    }
}
