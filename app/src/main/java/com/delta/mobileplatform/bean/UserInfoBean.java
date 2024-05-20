package com.delta.mobileplatform.bean;

import java.util.List;

public class UserInfoBean {


    private String factoryId;
    private String userId;
    private String userName;
    private List<String> roles;
    private List<String> scopes;
    private String email;
    private String avatar;
    private OtherProperties otherProperties;

    public UserInfoBean(String factoryId, String userId, String userName, List<String> roles, List<String> scopes, String email, String avatar, OtherProperties otherProperties) {
        this.factoryId = factoryId;
        this.userId = userId;
        this.userName = userName;
        this.roles = roles;
        this.scopes = scopes;
        this.email = email;
        this.avatar = avatar;
        this.otherProperties = otherProperties;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public OtherProperties getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(OtherProperties otherProperties) {
        this.otherProperties = otherProperties;
    }

    public static class OtherProperties {
        private String jti;
        private String iss;
        private String iat;
        private String exp;
        private String client_id;
        private String smp_source_from;

        public OtherProperties(String jti, String iss, String iat, String exp, String client_id, String smp_source_from) {
            this.jti = jti;
            this.iss = iss;
            this.iat = iat;
            this.exp = exp;
            this.client_id = client_id;
            this.smp_source_from = smp_source_from;
        }

        public String getJti() {
            return jti;
        }

        public void setJti(String jti) {
            this.jti = jti;
        }

        public String getIss() {
            return iss;
        }

        public void setIss(String iss) {
            this.iss = iss;
        }

        public String getIat() {
            return iat;
        }

        public void setIat(String iat) {
            this.iat = iat;
        }

        public String getExp() {
            return exp;
        }

        public void setExp(String exp) {
            this.exp = exp;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        public String getSmp_source_from() {
            return smp_source_from;
        }

        public void setSmp_source_from(String smp_source_from) {
            this.smp_source_from = smp_source_from;
        }
    }
}