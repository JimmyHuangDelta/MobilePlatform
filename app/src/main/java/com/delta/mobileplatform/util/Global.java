package com.delta.mobileplatform.util;

/**
 * 全域變數
 */
public class Global {

    private static String clientId = "DIAWorks";
    private static String clientSecret = "918b82cc-f86c-43e0-b9ec-afb4707216c6";

    private static String wellknownUrl = "/oidc/.well-known/openid-configuration";

//    private static String clientId = "demo";
//    private static String clientSecret = "zUAP9din1iWdkhGtDfwIotP9y1tC2qWi";


    // 測試keyCloak用 記得拔掉
    public static void setWellKnownURL(String url) {
        SharedPerformer.getInstance().saveString("wellknown", url);
    }

    public static String getGetWellKnownUrl() {
//        return clientId;
        return SharedPerformer.getInstance().getString("wellknown");
    }

    // 測試keyCloak用 記得拔掉 是否加密
    public static void setIsOIDC(String isOIDC) {
        SharedPerformer.getInstance().saveString("isOIDC", isOIDC);
    }

    public static String getIsOIDC() {
//        return clientId;
        return SharedPerformer.getInstance().getString("isOIDC");
    }

    // 測試keyCloak用 記得拔掉
    public static void setClientId(String clientId) {
        SharedPerformer.getInstance().saveString("clientId", clientId);
    }

    public static String getClientId() {
//        return clientId;
        return SharedPerformer.getInstance().getString("clientId");
    }

    // 測試keyCloak用 記得拔掉
    public static void setClientSecret(String clientSecret) {
        SharedPerformer.getInstance().saveString("clientSecret", clientSecret);
    }

    public static String getClientSecret() {
        return SharedPerformer.getInstance().getString("clientSecret");
    }

    public static String getDomain() {
        return SharedPerformer.getInstance().getString("domain");
    }

    public static void setDomain(String newDomain) {
        SharedPerformer.getInstance().saveString("domain", newDomain);
    }

    public static String getMqtt() {
        return SharedPerformer.getInstance().getString("mqtt");
    }

    public static void setMqtt(String newMqtt) {
        SharedPerformer.getInstance().saveString("mqtt", newMqtt);
    }

    public static String getAccessToken() {
        return SharedPerformer.getInstance().getString("access_token");
    }

    public static void setAccessToken(String accessToken) {
        SharedPerformer.getInstance().saveString("access_token", accessToken);
    }

    public static String getRefreshToken() {
        return SharedPerformer.getInstance().getString("refresh_token");
    }

    public static void setRefreshToken(String RefreshToken) {
        SharedPerformer.getInstance().saveString("refresh_token", RefreshToken);
    }


    public static String getIntrospectEndpoint() {
        return SharedPerformer.getInstance().getString("introspect_endpoint");
    }

    public static void setIntrospectEndpoint(String introspectEndpoint) {
        SharedPerformer.getInstance().saveString("introspect_endpoint", introspectEndpoint);
    }


    public static String getUserInfoEndpoint() {
        return SharedPerformer.getInstance().getString("userinfo_endpoint");
    }

    public static void setUserInfoEndpoint(String userInfoEndpoint) {
        SharedPerformer.getInstance().saveString("userinfo_endpoint", userInfoEndpoint);
    }


    public static String getRevokeEndpoint() {
        return SharedPerformer.getInstance().getString("revoke_endpoint");
    }

    public static void setRevokeEndpoint(String revokeEndpoint) {
        SharedPerformer.getInstance().saveString("revoke_endpoint", revokeEndpoint);
    }

    public static String getAuthorizationEndpoint() {
        return SharedPerformer.getInstance().getString("authorization_endpoint");
    }

    public static void setAuthorizationEndpoint(String authorizationEndpoint) {
        SharedPerformer.getInstance().saveString("authorization_endpoint", authorizationEndpoint);
    }

    public static String getTokenEndpoint() {
        return SharedPerformer.getInstance().getString("token_endpoint");
    }

    public static void setTokenEndpoint(String tokenEndpoint) {
        SharedPerformer.getInstance().saveString("token_endpoint", tokenEndpoint);
    }

    public static String getEndSessionEndpoint() {
        return SharedPerformer.getInstance().getString("end_session_endpoint");
    }

    public static void setEndSessionEndpoint(String endSessionEndpoint) {
        SharedPerformer.getInstance().saveString("end_session_endpoint", endSessionEndpoint);
    }


}
