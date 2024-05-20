package com.delta.mobileplatform.model;


import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.TokenManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.lang.reflect.Type;

import javax.inject.Inject;

public class FeatureModel<T> extends BaseModel<T> implements ApiServiceObserver {

    ApiClient apiClient;
    private ApiService mApiService;
    OIDCAuthenticator oidcAuthenticator;
    @Inject
    public FeatureModel(  OIDCAuthenticator oidcAuthenticator) {
       this.mApiService = ApiClient.getInstance().getApiService();
        this.oidcAuthenticator = oidcAuthenticator;
        ApiClient.getInstance().registerObserver(this);
    }
    public void getUserInfo(final TokenManager.UserInfoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                oidcAuthenticator.getUserInfo(new TokenManager.UserInfoCallback() {
                    @Override
                    public void onUserInfoSuccess(UserInfoBean userInfoBean) {
                        callback.onUserInfoSuccess(userInfoBean);
                    }

                    @Override
                    public void onUserInfoFailed(String error) {
                        callback.onUserInfoFailed(error);
                    }
                });
            }
        }).start();
    }

    // 执行登录请求的方法
    public void getAccountNavigation(ModelCallback<List<FeatureBean>> modelCallback) {

        Call<ResponseBody> call = mApiService.getAccountNavigation("Y");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String json = null;
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Type type = new TypeToken<List<FeatureBean>>() {}.getType();
                    List<FeatureBean> featureBeans = FeatureBean.parseJSON(json);

                    modelCallback.onSuccess(featureBeans);

                } else {
                    modelCallback.onFailOrError(response.message().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                modelCallback.onFailOrError(t.toString());
            }
        });
    }


    public void getFactories(ModelCallback<List<FactoryBean>> modelCallback) {

        Call<ResponseBody> call = mApiService.getFactories();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responseBody = response.body().string();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<FactoryBean>>(){}.getType();
                        List<FactoryBean> factoryBeans = gson.fromJson(responseBody, listType);
                        modelCallback.onSuccess(factoryBeans);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    modelCallback.onFailOrError(response.message().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 处理请求失败
                modelCallback.onFailOrError(t.toString());
            }
        });
    }





    public void changeFactory(RequestBody requestBody, ModelCallback<ResponseBody> modelCallback) {

        Call<ResponseBody> call = mApiService.changeFactory(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String newToken = response.headers().get("Authorization");
                    if (newToken != null) {
                       Global.setAccessToken(newToken);
                      ApiClient.getInstance().recreateApiService();
                    }
                    modelCallback.onSuccess(response.body());
                } else {
                    modelCallback.onFailOrError(response.message().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 处理请求失败
                modelCallback.onFailOrError(t.toString());
            }
        });
    }
    public void onDestroy() {
        ApiClient.getInstance().unregisterObserver(this);
    }
    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        mApiService = apiService;
    }
}
