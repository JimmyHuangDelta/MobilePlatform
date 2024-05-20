package com.delta.mobileplatform.view;

import net.openid.appauth.TokenResponse;

public interface KeyCloakConnectView extends IBaseView {

    void getHealthSuccess();

    void getHealthFailed(String err);

    void loginSuccess(TokenResponse tokenResponse);

    void loginFailed(String errMsg);

    void introspectSuccess();

    void introspectFailed(String err);

    void refreshSuccess(String newAccessToken, String newRefreshToken);

    void refreshFailed(String err);

    void logoutSuccess();

    void logoutFailed(String err);

}