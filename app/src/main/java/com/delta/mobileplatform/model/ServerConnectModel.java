package com.delta.mobileplatform.model;


import com.delta.mobileplatform.bean.HealthzBean;

import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;

import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;

import com.delta.mobileplatform.util.Global;
import com.google.gson.Gson;
import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ServerConnectModel<T> extends BaseModel<T> implements ApiServiceObserver {

    private ApiService apiService;

    @Inject
    public ServerConnectModel() {
        this.apiService = ApiClient.getInstance().getApiService();
        ApiClient.getInstance().registerObserver(this);
    }

    public void getHealthz(ModelCallback<HealthzBean> modelCallback) {

//        Call<ResponseBody> call = apiService.getHealthz();

        //        測試keycloak用 記得拔掉
        Call<ResponseBody> call;
        if( Global.getIsOIDC().equals("1")){
             call = apiService.getHealthz();
        }else{
           call = apiService.getHealth();
        }
//        測試keycloak用 記得拔掉



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

                    Gson gson = new Gson();
                    HealthzBean healthzBean = gson.fromJson(json, HealthzBean.class);
//                    if (healthzBean.getStatus().equals("Healthy")) {
                        modelCallback.onSuccess(healthzBean);
//                    } else {
//                        modelCallback.onFailOrError(healthzBean.getStatus());
//                    }
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

    public void onDestroy() {
        ApiClient.getInstance().unregisterObserver(this);
    }
    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }
}
