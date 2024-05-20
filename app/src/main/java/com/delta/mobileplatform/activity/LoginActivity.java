package com.delta.mobileplatform.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.dialog.CommonDialog;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.presenter.LoginPresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.util.CryptoUtil;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.LanguageUtil;

import com.delta.mobileplatform.util.Permission;
import com.delta.mobileplatform.view.LoginInfoView;

import net.openid.appauth.TokenResponse;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends BaseActivity implements LoginInfoView {
    private EditText etUsername, etPassword;
    private TextView tvLogin, tvChange, tvWebServer, tv_language;
    private LoginPresenter mLoginPresenter;
    private String username, password, cryptoPassword;
    ApiService mApiService;
    private LanguageUtil languageUtil;
    private String currentLanguage, currentLanguageTranslate;
    private Drawable icUserName, icPassword, icPasswordHide, icPasswordDisplay;
    private boolean visable = false;
    @Inject
    OIDCAuthenticator oidcAuthenticator;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected BasePresenter initPresenter() {
        mApiService = ApiClient.getInstance().getApiService();
        mLoginPresenter = new LoginPresenter(oidcAuthenticator);
        return mLoginPresenter;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvLogin = findViewById(R.id.tv_login);
        tvChange = findViewById(R.id.tv_change);
        tvWebServer = findViewById(R.id.tv_web_server);
        tv_language = findViewById(R.id.tv_language);
        languageUtil = new LanguageUtil(this);
        currentLanguage = languageUtil.getPrefLanguage();
        currentLanguageTranslate = LanguageUtil.LanguageObj.getByCode(currentLanguage).getName();
        tv_language.setText(currentLanguageTranslate);
        tvWebServer.setText(Global.getDomain());

        icUserName = getResources().getDrawable(R.drawable.ic_username);
        icPassword = getResources().getDrawable(R.drawable.ic_password);
        icPasswordHide = getResources().getDrawable(R.drawable.ic_hide);
        icPasswordDisplay = getResources().getDrawable(R.drawable.ic_display);
        icUserName.setBounds(0, 0, icUserName.getIntrinsicWidth(), icUserName.getIntrinsicHeight());
        icPassword.setBounds(0, 0, icPassword.getIntrinsicWidth(), icPassword.getIntrinsicHeight());
        icPasswordHide.setBounds(0, 0, icPasswordHide.getIntrinsicWidth(), icPasswordHide.getIntrinsicHeight());
        icPasswordDisplay.setBounds(0, 0, icPasswordDisplay.getIntrinsicWidth(), icPasswordDisplay.getIntrinsicHeight());
    }

    @Override
    protected void initListener() {
        setupLoginClickListener();
        setupLanguageClickListener();
        setupChangeClickListener();
        setupEtUserNameWatcher();
        setupEtPasswordWatcher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().unregisterObserver(this);
    }


    // region listener
    private void setupLoginClickListener() {
        tvLogin.setOnClickListener(view -> {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                showErrorMessage("Please enter username and password.");
                return;
            }
            try {
                cryptoPassword = CryptoUtil.encrypt(password);

//                測試keycloak用 記得拔 是否加密
                if (Global.getIsOIDC().equals("0")) {

                    mLoginPresenter.login(username, password);
                } else {
                    mLoginPresenter.login(username, cryptoPassword);
                }

//                mLoginPresenter.login(username, cryptoPassword);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setupChangeClickListener() {
        tvChange.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, ServerConnectActivity.class));
            finish();
        });
    }

    private void setupLanguageClickListener() {
        tv_language.setOnClickListener(view -> {
            CommonDialog.showLanguageDialog(LoginActivity.this, languageUtil, languageCode -> {
                languageUtil.applyLanguage(languageCode);
                restartApp();
            });
        });
    }


    private void setupEtUserNameWatcher() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etUsername.setCompoundDrawables(icUserName, null, null, null);
                } else {
                    etUsername.setCompoundDrawables(null, null, null, null);
                }

                if (!editable.toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
                    tvLogin.setEnabled(true);
                } else {
                    tvLogin.setEnabled(false);
                }
            }
        });
    }

    private void setupEtPasswordWatcher() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etPassword.setCompoundDrawables(icPassword, null, null, null);
                } else {
                    if (!visable) {
                        etPassword.setCompoundDrawables(null, null, icPasswordDisplay, null);
                    } else {
                        etPassword.setCompoundDrawables(null, null, icPasswordHide, null);
                    }

                }


                if (!editable.toString().isEmpty() && !etUsername.getText().toString().isEmpty()) {
                    tvLogin.setEnabled(true);
                } else {
                    tvLogin.setEnabled(false);
                }

            }
        });
    }
    //endregion


    //region api callback
    @Override
    public void loginSuccess(TokenResponse tokenResponse) {
        ApiClient.getInstance().recreateApiService();
        showMessage("登入成功");
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void loginFailed(String errMsg) {
        showErrorMessage(errMsg);

    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        mApiService = apiService;
    }
    //endregion

    public void restartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}