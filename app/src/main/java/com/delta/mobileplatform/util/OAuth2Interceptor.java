package com.delta.mobileplatform.util;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.delta.mobileplatform.activity.LoginActivity;
import com.delta.mobileplatform.app.MyApplication;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import javax.inject.Inject;

public class OAuth2Interceptor implements Interceptor {
    private final Object lock = new Object();
//    private String accessToken;
//    private String refreshToken;
    private boolean isRefreshingToken = false;
    private boolean isValidToken = false;
    private OIDCAuthenticator oidcAuthenticator;

    public OAuth2Interceptor( OIDCAuthenticator oidcAuthenticator) {
        this.oidcAuthenticator = oidcAuthenticator;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        synchronized (lock) {
            introspectToken(chain);
            if (!isValidToken && !isRefreshingToken) {
                refreshAccessToken(chain);
            }
        }
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + Global.getAccessToken())
                .build();
        return chain.proceed(request);
    }

    private void refreshAccessToken(final Chain chain) {
        isRefreshingToken = true;
        oidcAuthenticator.refreshToken(new TokenManager.TokenRefreshCallback() {
            @Override
            public void onTokenRefreshed(String newAccessToken, String newRefreshToken) {
                synchronized (lock) {
                    isRefreshingToken = false;
                    isValidToken = true;
                }
                try {
                    Request newRequest = chain.request().newBuilder()
                            .header("Authorization", "Bearer " + newAccessToken)
                            .build();
                    chain.proceed(newRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenRefreshFailed(String error) {
                synchronized (lock) {
                    isRefreshingToken = false;
                }
                redirectToLoginPage();
            }
        });
    }

    private void introspectToken(final Chain chain) {
        oidcAuthenticator.introspectToken(new TokenManager.TokenIntrospectCallback() {
            @Override
            public void onTokenValid() {
                synchronized (lock) {
                    isValidToken = true;
                }
            }
            @Override
            public void onTokenInvalid(String error) {
                synchronized (lock) {
                    isValidToken = false;
                }
            }
        });
    }

    private void redirectToLoginPage() {
        Context context = MyApplication.getAppContext();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}


