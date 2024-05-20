package com.delta.mobileplatform.model;


import com.delta.mobileplatform.bean.HealthzBean;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.service.ModelCallback;
import com.delta.mobileplatform.util.TokenManager;
import com.google.gson.Gson;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class KeyCloakConnectModel<T> extends BaseModel<T> implements ApiServiceObserver {

    private ApiService apiService;
    OIDCAuthenticator mOidcAuthenticator;

    @Inject
    public KeyCloakConnectModel(OIDCAuthenticator oidcAuthenticator) {
        this.apiService = ApiClient.getInstance().getApiService();
        ApiClient.getInstance().registerObserver(this);
        mOidcAuthenticator = oidcAuthenticator;
    }

    public void getHealthz(ModelCallback<HealthzBean> modelCallback) {
        Call<ResponseBody> call = apiService.getHealthz();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String json = null;
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Gson gson = new Gson();
                    HealthzBean healthzBean = gson.fromJson(json, HealthzBean.class);
//                    if (healthzBean.getStatus().equals("Healthy")) {
                    modelCallback.onSuccess(healthzBean);
//                    } else {
//                        modelCallback.onFailOrError(healthzBean.getStatus());
//                    }
                } else {
                    modelCallback.onFailOrError(response.message().toString());

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                modelCallback.onFailOrError(t.toString());

            }
        });
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

    public void introspect(final TokenManager.TokenIntrospectCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOidcAuthenticator.introspectToken(new TokenManager.TokenIntrospectCallback() {
                    @Override
                    public void onTokenValid() {

                        callback.onTokenValid();
                    }

                    @Override
                    public void onTokenInvalid(String error) {
                        callback.onTokenInvalid(error);
                    }
                });
            }
        }).start();
    }


    public void refresh(final TokenManager.TokenRefreshCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOidcAuthenticator.refreshToken(new TokenManager.TokenRefreshCallback() {
                    @Override
                    public void onTokenRefreshed(String newAccessToken, String newRefreshToken) {
                        callback.onTokenRefreshed(newAccessToken, newRefreshToken);
                    }

                    @Override
                    public void onTokenRefreshFailed(String error) {
                        callback.onTokenRefreshFailed(error);
                    }
                });
            }
        }).start();
    }


    public void logout(final TokenManager.TokenRevokeCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mOidcAuthenticator.performLogout(new TokenManager.TokenRevokeCallback() {
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

    public interface LoginCallback {
        void onLoginSuccess(TokenResponse tokenResponse);

        void onLoginFailed(String errorMessage);


    }


    public interface RefreshCallback {

        void onRefreshSuccess(TokenResponse tokenResponse);

        void onRefreshFailed(String errorMessage);

    }


    public interface IntrospectkCallback {

        void onIntrospectSuccess(TokenResponse tokenResponse);

        void onIntrospectFailed(String errorMessage);

    }


    public interface LogoutCallback {
        void onLogoutSuccess(TokenResponse tokenResponse);

        void onLogoutFailed(String errorMessage);

    }


}
