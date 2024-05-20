package com.delta.mobileplatform.presenter;


import com.delta.mobileplatform.bean.HealthzBean;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.model.KeyCloakConnectModel;
import com.delta.mobileplatform.model.LoginModel;
import com.delta.mobileplatform.model.ServerConnectModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.TokenManager;
import com.delta.mobileplatform.view.KeyCloakConnectView;
import com.delta.mobileplatform.view.ServerConnectView;

import net.openid.appauth.TokenResponse;

import javax.inject.Inject;

//for keycloak test
public class KeyCloakConnectPresenter extends BasePresenter<KeyCloakConnectView>implements ApiServiceObserver {

    private KeyCloakConnectModel keyCloakConnectModel;
    private ApiService apiService;

    @Inject
    public KeyCloakConnectPresenter(OIDCAuthenticator oidcAuthenticator) {
        keyCloakConnectModel = new KeyCloakConnectModel(oidcAuthenticator);
    }

    @Override
    public void attachView(KeyCloakConnectView view) {
        super.attachView(view);
    }
    public void getHealthz() {

        getView().showLoading();

        keyCloakConnectModel.getHealthz( new ModelCallback<HealthzBean>() {
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
    public void login(String username, String password){
        getView().showLoading();
        keyCloakConnectModel.login( username,  password, new KeyCloakConnectModel.LoginCallback() {
            @Override
            public void onLoginSuccess(TokenResponse tokenResponse) {
                if (isViewAttached()) {
                    getView().loginSuccess(tokenResponse);
                    getView().hideLoading();
                }
            }

            @Override
            public void onLoginFailed(String errorMessage) {
                if (isViewAttached()) {
                    getView().loginFailed(errorMessage);
                    getView().hideLoading();
                }
            }
        });

    }



    public void refresh(){
        getView().showLoading();
        keyCloakConnectModel.refresh( new TokenManager.TokenRefreshCallback() {
            @Override
            public void onTokenRefreshed(String newAccessToken, String newRefreshToken) {
                if (isViewAttached()) {
                    getView().refreshSuccess(newAccessToken, newRefreshToken);
                    getView().hideLoading();
                }
            }

            @Override
            public void onTokenRefreshFailed(String error) {
                if (isViewAttached()) {
                    getView().refreshFailed(error);
                    getView().hideLoading();
                }
            }

        });

    }


    public void introspect(){
        getView().showLoading();
        keyCloakConnectModel.introspect(new TokenManager.TokenIntrospectCallback() {
            @Override
            public void onTokenValid() {
                if (isViewAttached()) {
                    getView().introspectSuccess();
                    getView().hideLoading();
                }
            }

            @Override
            public void onTokenInvalid(String error) {
                if (isViewAttached()) {
                    getView().introspectFailed(error);
                    getView().hideLoading();
                }
            }
        } );

    }

    public void logout(){
        getView().showLoading();
        keyCloakConnectModel.logout(new TokenManager.TokenRevokeCallback() {
            @Override
            public void onTokenRevoke() {
                if (isViewAttached()) {
                    getView().logoutSuccess();
                    getView().hideLoading();
                }
            }

            @Override
            public void onTokenRevokeFailed(String error) {
                if (isViewAttached()) {
                    getView().logoutFailed(error);
                    getView().hideLoading();
                }
            }
        } );

    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }
}
