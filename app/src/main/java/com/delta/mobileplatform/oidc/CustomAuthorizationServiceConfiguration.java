package com.delta.mobileplatform.oidc;


import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.AuthorizationServiceDiscovery;

public class CustomAuthorizationServiceConfiguration extends AuthorizationServiceConfiguration {
    private final Uri introspectionEndpoint;
    private final Uri revokeEndpoint;

    public CustomAuthorizationServiceConfiguration(
            @NonNull AuthorizationServiceDiscovery discovery,
            @Nullable Uri introspectionEndpoint,
            @Nullable Uri revokeEndpoint) {
        super(discovery);
        this.introspectionEndpoint = introspectionEndpoint;
        this.revokeEndpoint = revokeEndpoint;
    }

    public Uri getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }

    public Uri getRevokeEndpoint() {
        return revokeEndpoint;
    }

}
