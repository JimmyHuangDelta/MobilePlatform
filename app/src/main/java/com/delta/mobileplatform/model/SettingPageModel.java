package com.delta.mobileplatform.model;

import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.service.ApiService;

import javax.inject.Inject;

public class SettingPageModel<T>  extends BaseModel<T>  implements ApiServiceObserver {
    private ApiService apiService;;
    public SettingPageModel() {
        ApiClient.getInstance().registerObserver(this);
    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }
}