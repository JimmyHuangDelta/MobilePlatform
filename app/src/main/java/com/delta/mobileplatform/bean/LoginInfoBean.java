package com.delta.mobileplatform.bean;

public class LoginInfoBean {

    private String access_token;
    private String tokenType;
    private int expiresIn;
    private String scope;
    private String idToken;
    private String refreshToken;

    public LoginInfoBean(String access_token, String tokenType, int expiresIn, String scope, String idToken, String refreshToken) {
        this.access_token = access_token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
    }

    // 其他可能的属性

    // 构造函数、getter 和 setter 方法
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

//    @Override
//    public String toString() {
//        return "LoginInfoBean{" +
//                "accessToken='" + access_token + '\'' +
//                ", tokenType='" + tokenType + '\'' +
//                ", expiresIn=" + expiresIn +
//                ", scope='" + scope + '\'' +
//                ", idToken='" + idToken + '\'' +
//                ", refreshToken='" + refreshToken + '\'' +
//                '}';
//    }
}