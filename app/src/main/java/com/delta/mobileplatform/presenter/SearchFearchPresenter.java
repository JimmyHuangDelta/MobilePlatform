package com.delta.mobileplatform.presenter;

import android.util.Log;

import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.model.SearchFeatureModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;

import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.TokenManager;
import com.delta.mobileplatform.view.SearchFeatureView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class SearchFearchPresenter extends BasePresenter<SearchFeatureView> {

    private SearchFeatureModel searchFeatureModel;

    public SearchFearchPresenter(OIDCAuthenticator oidcAuthenticator) {
        searchFeatureModel = new SearchFeatureModel(oidcAuthenticator);
    }

    @Override
    public void attachView(SearchFeatureView view) {
        super.attachView(view);
    }

//    使用者資訊
    public void getUserInfo() {
        getView().showLoading();
        searchFeatureModel.getUserInfo(new TokenManager.UserInfoCallback() {
            @Override
            public void onUserInfoSuccess(UserInfoBean userInfoBean) {
                if (isViewAttached()) {
                    getView().getUserInfoSuccess(userInfoBean);
                    getView().hideLoading();
                } else {
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

//    使用者權限
    public void getAccountNavigation() {
        getView().showLoading();
        searchFeatureModel.getAccountNavigation(new ModelCallback<List<FeatureBean>>() {
            @Override
            public void onSuccess(List<FeatureBean> result) {
                if (isViewAttached()) {
                    getView().getAccountNavationSuccess(result);
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailOrError(String err) {
                if (isViewAttached()) {
                    getView().getAccountNavationFailed(err);
                    getView().hideLoading();
                }
            }

        });
    }

//    所有廠別
    public void getFactories() {
        getView().showLoading();
        searchFeatureModel.getFactories(new ModelCallback<List<FactoryBean>>() {
            @Override
            public void onSuccess(List<FactoryBean> result) {
                if (isViewAttached()) {
                    getView().getFactorySuccess(result);
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailOrError(String err) {
                if (isViewAttached()) {
                    getView().getFactoryFailed(err);
                    getView().hideLoading();
                }
            }
        });
    }

//切換廠別
    public void changeFactory(String factoryId) {
        getView().showLoading();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("factoryId", factoryId);
        String jsonBody = new Gson().toJson(jsonObject);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);
        searchFeatureModel.changeFactory(requestBody, new ModelCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody result) {
                if (isViewAttached()) {
                    getView().changeFactorySuccess();
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailOrError(String err) {
                if (isViewAttached()) {
                    getView().changeFactoryFailed(err);
                    getView().hideLoading();
                }
            }
        });
    }

    public void onDestroy() {
        searchFeatureModel.onDestroy();
    }
}