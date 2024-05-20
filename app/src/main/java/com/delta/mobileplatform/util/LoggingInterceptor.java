package com.delta.mobileplatform.util;

import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Headers;

import java.io.IOException;

public class LoggingInterceptor implements Interceptor {

    private static final String TAG = "LoggingInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Log.d(TAG, "Request Headers:");
        Log.d(TAG, "================");
        for (String name : request.headers().names()) {
            Log.d(TAG, name + ": " + request.header(name));
        }
        Log.d(TAG, "================");

        // 执行请求
        Response response = chain.proceed(request);

        return response;
    }
}