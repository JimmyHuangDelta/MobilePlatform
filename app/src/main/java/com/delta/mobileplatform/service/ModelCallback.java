package com.delta.mobileplatform.service;



public interface ModelCallback<T> {
    void onSuccess(T result);

    void onFailOrError(String result);

}