package com.delta.mobileplatform.presenter;

import android.util.Log;

import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.model.FeatureModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.TokenManager;
import com.delta.mobileplatform.view.FeatureView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class FeaturePresenter extends BasePresenter<FeatureView> {
    private FeatureModel featureModel;

    public FeaturePresenter(OIDCAuthenticator oidcAuthenticator) {
        featureModel = new FeatureModel(oidcAuthenticator);
    }

    @Override
    public void attachView(FeatureView view) {
        super.attachView(view);
    }


    //user資訊
    public void getUserInfo() {

        getView().showLoading();
        featureModel.getUserInfo(new TokenManager.UserInfoCallback() {
            @Override
            public void onUserInfoSuccess(UserInfoBean userInfoBean) {
                if (isViewAttached()) {
                    getView().getUserInfoSuccess(userInfoBean);
                    getView().hideLoading();
                }
            }
            @Override
            public void onUserInfoFailed(String error) {
                if (isViewAttached()) {
                    getView().getUserInfoFailed(error);
                    getView().hideLoading();
                }
            }
        });
    }

    //帳號權限
    public void getAccountNavigation() {
        getView().showLoading();
        featureModel.getAccountNavigation(new ModelCallback<List<FeatureBean>>() {
            @Override
            public void onSuccess(List<FeatureBean> result) {
                if (isViewAttached()) {
                    getView().getAccountNavationSuccess(result);
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailOrError(String result) {
                if (isViewAttached()) {
                    getView().getAccountNavationFailed(result);
                    getView().hideLoading();
                }
            }

        });
    }

//    所有廠別
    public void getFactories() {
        getView().showLoading();
        featureModel.getFactories(new ModelCallback<List<FactoryBean>>() {
            @Override
            public void onSuccess(List<FactoryBean> result) {
                if (isViewAttached()) {
                    getView().getFactorySuccess(result);
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailOrError(String result) {
                if (isViewAttached()) {
                    getView().getFactoryFailed(result);
                    getView().hideLoading();
                }
            }
        });
    }

//    切換廠別
    public void changeFactory(String factoryId) {
        getView().showLoading();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("factoryId", factoryId);
        String jsonBody = new Gson().toJson(jsonObject);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        featureModel.changeFactory(requestBody, new ModelCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                if (isViewAttached()) {
                    getView().changeFactorySuccess();
                    getView().hideLoading();
                }
            }
            @Override
            public void onFailOrError(String result) {
                if (isViewAttached()) {
                    getView().changeFactoryFailed(result);
                    getView().hideLoading();
                }
            }
        });
    }

    public void onDestroy() {
        featureModel.onDestroy();
    }

}