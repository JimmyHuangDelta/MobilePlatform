package com.delta.mobileplatform.model;

import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;

import com.delta.mobileplatform.util.TokenManager;

import javax.inject.Inject;


public class UserInfoModel<T> extends BaseModel<T>  implements ApiServiceObserver {
     OIDCAuthenticator oidcAuthenticator;
    ApiService apiService;
    @Inject
    public UserInfoModel( OIDCAuthenticator oidcAuthenticator) {
        this.apiService = ApiClient.getInstance().getApiService();
        this.oidcAuthenticator = oidcAuthenticator;
        ApiClient.getInstance().registerObserver(this);
    }

//  使用者資訊
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


//登出
    public void logout(final TokenManager.TokenRevokeCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                oidcAuthenticator.performLogout(new TokenManager.TokenRevokeCallback() {
                    @Override
                    public void onTokenRevoke() {
                        callback.onTokenRevoke();
                    }

                    @Override
                    public void onTokenRevokeFailed(String error) {
                        callback.onTokenRevokeFailed(error);
                    }
                });
            }
        }).start();
    }
    public void onDestroy() {
        ApiClient.getInstance().unregisterObserver(this);
    }
    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }
}
