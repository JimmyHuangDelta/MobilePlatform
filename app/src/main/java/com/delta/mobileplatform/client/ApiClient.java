package com.delta.mobileplatform.client;

import com.delta.mobileplatform.web.controller.localStorage.DbRepository;

import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.OAuth2Interceptor;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static volatile ApiClient instance;
    private ApiService apiService;
    private OkHttpClient okHttpClient;
    private static String BASE_URL = "https://example.com/";
    private DbRepository repository;

    private Retrofit retrofit;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    private List<ApiServiceObserver> observers = new ArrayList<>();

    private ApiClient() {
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }

    public synchronized ApiService initialize(DbRepository repository, OIDCAuthenticator oidcAuthenticator, String baseUrl) {
        apiService = null;
        if (apiService == null) {
            this.repository = repository;
            this.oidcAuthenticator = oidcAuthenticator;
            createApiService(baseUrl);
        }
        return apiService;
    }


    private void createApiService(String baseUrl) {
        provideOkHttpClient(oidcAuthenticator, new OkHttpClientCallback() {
            @Override
            public void onOkHttpClientReady(OkHttpClient client) {
                okHttpClient = client;
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();
                apiService = retrofit.create(ApiService.class);
                notifyObservers(apiService);
            }
        });
    }


    public synchronized ApiService setBaseUrl(String baseUrl) {
        createApiService(baseUrl);
        return apiService;
    }

    private static void provideOkHttpClient(OIDCAuthenticator oidcAuthenticator, OkHttpClientCallback callback) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        String accessToken = Global.getAccessToken();
        String refreshToken = Global.getRefreshToken();
        if (!accessToken.isEmpty() && !refreshToken.isEmpty()) {
            httpClientBuilder.addInterceptor(new OAuth2Interceptor(oidcAuthenticator));
        }

        httpClientBuilder.addInterceptor(chain -> {

            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
//                    .header("Authorization", "Bearer " + "your_token_here")
                    .header("User-Agent", "PostmanRuntime/7.37.0")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive");

            if (original.method().equalsIgnoreCase("POST")) {
                requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        OkHttpClient httpClient = httpClientBuilder.build();
        callback.onOkHttpClientReady(httpClient);
    }


    interface OkHttpClientCallback {
        void onOkHttpClientReady(OkHttpClient client);
    }

    public ApiService getApiService() {
        if (apiService == null) {
            recreateApiService();
        }
        return apiService;
    }

    public synchronized void recreateApiService() {
        apiService = null;

        if (Global.getDomain().isEmpty()) {
            createApiService(BASE_URL);
        } else {
            createApiService(Global.getDomain());
        }
    }


    public synchronized void registerObserver(ApiServiceObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void unregisterObserver(ApiServiceObserver observer) {
        observers.remove(observer);
    }

    private synchronized void notifyObservers(ApiService apiService) {
        for (ApiServiceObserver observer : observers) {
            observer.onApiServiceUpdated(apiService);
        }
    }

}