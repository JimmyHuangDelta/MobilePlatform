package com.delta.mobileplatform.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.delta.mobileplatform.bean.UserInfoBean;

public class TokenManager {

    private static TokenManager instance;
    private static SharedPreferences sharedPreferences;
    private SharedPerformer sharedPerformer;
    private TokenRefreshCallback tokenRefreshCallback;

    private TokenIntrospectCallback tokenIntrospectCallback;

    private TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        sharedPerformer = SharedPerformer.getInstance(); // 初始化 SharedPerformer
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TokenManager is not initialized. Call initialize() first.");
        }
        return instance;
    }

//    public void saveAccessToken(String token) {
//
//        sharedPerformer.saveString(PreferencesKey.ACCESS_TOKEN, token);
//
//    }
//
//    public String getAccessToken() {
//        return sharedPerformer.getString(PreferencesKey.ACCESS_TOKEN);
//
//    }
//
//    public void saveRefreshToken(String token) {
//
//        sharedPerformer.saveString(PreferencesKey.REFRESH_TOKEN, token);
//
//    }
//
//    public void saveTokens(String accessToken, String refreshToken) {
//        sharedPerformer.saveString(PreferencesKey.ACCESS_TOKEN, accessToken);
//        sharedPerformer.saveString(PreferencesKey.REFRESH_TOKEN, refreshToken);
//    }
//
//    public String getRefreshToken() {
//        return sharedPerformer.getString(PreferencesKey.REFRESH_TOKEN);
//
//    }
//
//    public void clearToken() {
//        sharedPerformer.removeValue(PreferencesKey.ACCESS_TOKEN); // 使用 SharedPerformer 清除令牌
//        sharedPerformer.removeValue(PreferencesKey.REFRESH_TOKEN); // 使用 SharedPerformer 清除令牌
//    }
//
//    public void setTokenRefreshCallback(TokenRefreshCallback callback) {
//        this.tokenRefreshCallback = callback;
//    }
//
//    public void setTokenIntrospectCallback(TokenIntrospectCallback callback) {
//        this.tokenIntrospectCallback = callback;
//    }

    public interface TokenRefreshCallback {
        void onTokenRefreshed(String newAccessToken, String newRefreshToken);

        void onTokenRefreshFailed(String error);
    }

    public interface TokenIntrospectCallback {
        void onTokenValid();

        void onTokenInvalid(String error);
    }
    public interface TokenRevokeCallback {
        void onTokenRevoke();

        void onTokenRevokeFailed(String error);
    }
    public interface UserInfoCallback {
        void onUserInfoSuccess(UserInfoBean userInfoBean);

        void onUserInfoFailed(String error);
    }

    private void checkInitialized() {
        if (sharedPreferences == null) {
            throw new IllegalStateException("TokenManager not initialized. Call initialize() first.");
        }
    }
}