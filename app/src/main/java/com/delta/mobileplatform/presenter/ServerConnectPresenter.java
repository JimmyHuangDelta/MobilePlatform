package com.delta.mobileplatform.presenter;


import com.delta.mobileplatform.bean.HealthzBean;

import com.delta.mobileplatform.client.ApiServiceObserver;

import com.delta.mobileplatform.model.ServerConnectModel;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;

import com.delta.mobileplatform.view.ServerConnectView;

import javax.inject.Inject;


public class ServerConnectPresenter extends BasePresenter<ServerConnectView> implements ApiServiceObserver {

    private ServerConnectModel serverConnectModel;
    private ApiService apiService;

    @Inject
    public ServerConnectPresenter() {
        serverConnectModel = new ServerConnectModel();
    }

    @Override
    public void attachView(ServerConnectView view) {
        super.attachView(view);
    }


//    獲取健康度
    public void getHealthz() {

        getView().showLoading();

        serverConnectModel.getHealthz(new ModelCallback<HealthzBean>() {
            @Override
            public void onSuccess(HealthzBean result) {
                if (isViewAttached()) {
                    getView().getHealthSuccess();
                    getView().hideLoading();
                }

            }

            @Override
            public void onFailOrError(String err) {
                if (isViewAttached()) {
                    getView().getHealthFailed(err);
                    getView().hideLoading();
                }
            }
        });
    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }
}
