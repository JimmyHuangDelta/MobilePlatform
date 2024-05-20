package com.delta.mobileplatform.view;

import android.widget.Toast;

import com.delta.mobileplatform.bean.LoginInfoBean;

import net.openid.appauth.TokenResponse;

public interface LoginInfoView extends IBaseView {
    void loginSuccess(TokenResponse tokenResponse);

    void loginFailed(String errMsg);

}