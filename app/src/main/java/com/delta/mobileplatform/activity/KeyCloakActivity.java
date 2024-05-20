package com.delta.mobileplatform.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.JavaLocalStorage.DbRepositoryCaller;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.presenter.KeyCloakConnectPresenter;
import com.delta.mobileplatform.presenter.ServerConnectPresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.view.KeyCloakConnectView;
import com.delta.mobileplatform.view.ServerConnectView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


//for test
@AndroidEntryPoint
public class KeyCloakActivity extends BaseActivity implements KeyCloakConnectView, ApiServiceObserver {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    private static final String SCAN_RESULT = "SCAN_RESULT";

    private static final int REQUEST_BARCODE_SCAN = 201;  // 自定義的結果碼
    @Inject
    DbRepository dbRepository;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    ApiClient apiClient;
    ApiService apiService;
    private static String defaultDomain = "http://example";
    private EditText etHealthy, etAccount, etPassword;
    private TextView tvResponse;

    private AppCompatButton btHealthy, btLogin, btIntrospect, btRefresh, btLogout;
    private DbRepositoryCaller dbRepositoryCaller;

    private KeyCloakConnectPresenter mKeyCloakConnectPresenter;


    @Override
    protected BasePresenter initPresenter() {
        mKeyCloakConnectPresenter = new KeyCloakConnectPresenter(oidcAuthenticator);
        return mKeyCloakConnectPresenter;
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_keycloak;
    }
//    @Override
//    protected void initPresenter() {
//        apiService = ApiClient.getInstance().getApiService();
//        mServerConnectPresenter = new ServerConnectPresenter(apiService);
//    }

    @Override
    public void initData() {
        ApiClient.getInstance().initialize(dbRepository, oidcAuthenticator, defaultDomain);
        ApiClient.getInstance().registerObserver(this);
        dbRepositoryCaller = new DbRepositoryCaller(dbRepository);
    }
    @Override
    public void initView() {

        etHealthy = findViewById(R.id.et_healthy);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);

        btHealthy = findViewById(R.id.bt_healthy);
        btLogin = findViewById(R.id.bt_login);
        btIntrospect = findViewById(R.id.bt_introspect);
        btRefresh = findViewById(R.id.bt_refresh);
        btLogout = findViewById(R.id.bt_logout);
        tvResponse = findViewById(R.id.articleContent);
    }

    @Override
    protected void initListener() {
        setupConnectListener();


    }



    private void setupConnectListener() {
        btHealthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etHealthy.getText().toString().isEmpty()){
                    showErrorMessage("URL不得為空");
                    return;
                }

                Global.setDomain(etHealthy.getText().toString());
                ApiClient.getInstance().recreateApiService();
                mKeyCloakConnectPresenter.getHealthz();
            }
        });


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etAccount.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()){
                    showErrorMessage("不得為空");
                    return;
                }

                mKeyCloakConnectPresenter.login(etAccount.getText().toString(), etPassword.getText().toString());
            }
        });

        btIntrospect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyCloakConnectPresenter.introspect();
            }
        });




        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                mKeyCloakConnectPresenter.refresh();
            }
        });




        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                mKeyCloakConnectPresenter.logout();
            }
        });










    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().unregisterObserver(this);
    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }

    //region Api callback
    @Override
    public void getHealthSuccess() {
        tvResponse.setText("伺服器正常");
    }

    @Override
    public void getHealthFailed(String err) {
        tvResponse.setText(err);

    }

    @Override
    public void loginSuccess(TokenResponse tokenResponse) {
        ApiClient.getInstance().recreateApiService();
        tvResponse.setText("AccessToken:"+tokenResponse.accessToken+ "\n" + "RefreshToken:" + tokenResponse.refreshToken);
    }

    @Override
    public void loginFailed(String errMsg) {
        tvResponse.setText(errMsg);
    }

    @Override
    public void introspectSuccess() {
        tvResponse.setText("token有效");
    }

    @Override
    public void introspectFailed(String err) {
        tvResponse.setText(err);
    }

    @Override
    public void refreshSuccess(String newAccessToken, String newRefreshToken) {
        tvResponse.setText("AccessToken:"+newAccessToken+ "\n" + "RefreshToken:" + newRefreshToken);
    }

    @Override
    public void refreshFailed(String err) {
        tvResponse.setText(err);
    }

    @Override
    public void logoutSuccess() {
        tvResponse.setText("登出");
    }

    @Override
    public void logoutFailed(String err) {
        tvResponse.setText(err);
    }


}