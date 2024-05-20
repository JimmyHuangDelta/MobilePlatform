package com.delta.mobileplatform.service;

import com.delta.mobileplatform.util.Global;

import net.openid.appauth.AuthorizationService;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

@Singleton
public interface ApiService {


//    測試keycloak用
    @GET("health")
    Call<ResponseBody> getHealth();

    @GET("healthz")
    Call<ResponseBody> getHealthz();

    @GET("api/v2/account/navigation")
    Call<ResponseBody> getAccountNavigation(@Query("mobile") String mobile);

    @GET("api/v2/factories")
    Call<ResponseBody> getFactories();

    @POST("api/v2/account/navigation/switch-factory")
    Call<ResponseBody> changeFactory(@Body RequestBody body);

    

}