package com.delta.mobileplatform.presenter;

import com.delta.mobileplatform.model.LoginModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;

import com.delta.mobileplatform.view.LoginInfoView;

import net.openid.appauth.TokenResponse;


public class LoginPresenter extends BasePresenter<LoginInfoView> {

    private LoginModel loginModel;

    public LoginPresenter(OIDCAuthenticator oidcAuthenticator) {
        loginModel = new LoginModel(oidcAuthenticator);
    }
    @Override
    public void attachView(LoginInfoView view) {
        super.attachView(view);
    }


//    登入
    public void login(String username, String password){
        getView().showLoading();
        loginModel.login(username, password, new LoginModel.LoginCallback() {
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

}
