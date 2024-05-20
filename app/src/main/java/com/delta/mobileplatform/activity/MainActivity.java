package com.delta.mobileplatform.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.LanguageUtil;
import com.delta.mobileplatform.util.Permission;
import com.delta.mobileplatform.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    DbRepository repository;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    private String domain;
    private String accessToken;
    private String refreshToken;
    private LanguageUtil languageUtil;
    private String prefLanguage;
    private static String defaultDomain = "http://example";

    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 200;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageUtil = new LanguageUtil(this);
        prefLanguage = languageUtil.getPrefLanguage();
        languageUtil.applyLanguage(prefLanguage);
        domain = Global.getDomain();
        accessToken = Global.getAccessToken();
        refreshToken = Global.getRefreshToken();
        checkDomainAndToken();
        Permission.checkPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkDomainAndToken() {
        if (domain.isEmpty()) {
            ApiClient.getInstance().initialize(repository, oidcAuthenticator, defaultDomain); //創建實例用
            goToServerActivity();
            return;
        }

        ApiClient.getInstance().initialize(repository, oidcAuthenticator, domain);

        if (accessToken.isEmpty()) {
            goToLoginActivity();
        } else {
            introspectToken();
        }
    }

    private void introspectToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                oidcAuthenticator.introspectToken(new TokenManager.TokenIntrospectCallback() {
                    @Override
                    public void onTokenValid() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goToHomeActivity();
                            }
                        });
                    }

                    @Override
                    public void onTokenInvalid(String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshToken();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void refreshToken() {
        oidcAuthenticator.refreshToken(new TokenManager.TokenRefreshCallback() {
            @Override
            public void onTokenRefreshed(String newAccessToken, String newRefreshToken) {
                goToHomeActivity();
            }
            @Override
            public void onTokenRefreshFailed(String error) {
                goToLoginActivity();
            }
        });
    }

    private void goToHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void goToServerActivity() {
        startActivity(new Intent(this, ServerConnectActivity.class));
        finish();
    }

}