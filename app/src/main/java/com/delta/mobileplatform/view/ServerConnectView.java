package com.delta.mobileplatform.view;

public interface ServerConnectView extends IBaseView {
    void getHealthSuccess();
    void getHealthFailed(String err);

}