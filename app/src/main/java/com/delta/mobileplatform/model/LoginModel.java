package com.delta.mobileplatform.model;


import android.os.Handler;

import com.delta.mobileplatform.bean.LoginInfoBean;
import com.delta.mobileplatform.client.ApiClient;

import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.RequestBodyBuilder;
import com.delta.mobileplatform.util.TokenManager;
import com.google.gson.Gson;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginModel extends BaseModel<LoginInfoBean> {

    OIDCAuthenticator mOidcAuthenticator;
    @Inject
    ApiClient apiClient;

    private String clientId = Global.getClientId();

    private String clientSecret = Global.getClientSecret();

    public LoginModel(OIDCAuthenticator oidcAuthenticator) {
        mOidcAuthenticator = oidcAuthenticator;
    }

    public void login(String username, String password, final LoginCallback callback) {

        mOidcAuthenticator.getToken(username, password, new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException tokenException) {
                if (tokenResponse != null) {

                    callback.onLoginSuccess(tokenResponse);
                } else {
                    callback.onLoginFailed(tokenException.error);


                }
            }
        });
    }

    public interface LoginCallback {
        void onLoginSuccess(TokenResponse tokenResponse);

        void onLoginFailed(String errorMessage);
    }

}