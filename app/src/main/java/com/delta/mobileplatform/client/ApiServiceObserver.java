package com.delta.mobileplatform.client;

import com.delta.mobileplatform.service.ApiService;

public interface ApiServiceObserver {
    void onApiServiceUpdated(ApiService apiService);
}