package com.isanechek.beardycast.data.network;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Created by isanechek on 02.07.16.
 */

public class NetworkManager {

    public NetworkManager() {
    }

    private String getContent(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = OkHttp
                    .getOkHttpClient()
                    .newCall(request)
                    .execute();

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Observable<String> getBody(String url) {
        return Async.start(() -> getContent(url), Schedulers.io());
    }
}
