package com.delta.mobileplatform.activity;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.delta.mobileplatform.R;

import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.JavaLocalStorage.DbRepositoryCaller;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.presenter.ServerConnectPresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.view.ServerConnectView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ServerConnectActivity extends BaseActivity implements ServerConnectView, ApiServiceObserver {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    private static final String SCAN_RESULT = "SCAN_RESULT";

    private static final int REQUEST_BARCODE_SCAN = 201;  // 自定義的結果碼
    @Inject
    DbRepository dbRepository;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    ApiClient apiClient;
    ApiService apiService;

    private EditText etWebServerUrl, etMQTTServerUrl;
    private TextView webServerTitleTv, mqttServerTitleTv, connectTv, qrcodeTv, tvIgnore;
    private DbRepositoryCaller dbRepositoryCaller;
    private String webServerUrlHistory;
    private ServerConnectPresenter mServerConnectPresenter;
    private Drawable icWebUrl, icMqtt;
    private Spinner spOidc;

    private String selectedSpinnerItem = "請選擇登入機制";

    String[] spinnerItems = new String[]{"請選擇登入機制", "OIDC", "KeyCloak"};

    @Override
    protected BasePresenter initPresenter() {
        mServerConnectPresenter = new ServerConnectPresenter();
        return mServerConnectPresenter;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_server_connect;
    }

    @Override
    public void initData() {
        ApiClient.getInstance().registerObserver(this);
        dbRepositoryCaller = new DbRepositoryCaller(dbRepository);
    }

    @Override
    public void initView() {
        webServerTitleTv = findViewById(R.id.tv_web_server_title);
        mqttServerTitleTv = findViewById(R.id.tv_mqtt_server_title);
        etWebServerUrl = findViewById(R.id.et_web_server);
        etMQTTServerUrl = findViewById(R.id.et_mqtt_server);
        connectTv = findViewById(R.id.connect_tv);
        qrcodeTv = findViewById(R.id.qrcode_tv);
        tvIgnore = findViewById(R.id.tv_ignore);
        webServerUrlHistory = Global.getDomain();
        spOidc = findViewById(R.id.oidc_spinner);
        if (!webServerUrlHistory.isEmpty()) {
            etWebServerUrl.setText(webServerUrlHistory);
        }

        icWebUrl = getResources().getDrawable(R.drawable.ic_webserverurl);
        icWebUrl.setBounds(0, 0, icWebUrl.getIntrinsicWidth(), icWebUrl.getIntrinsicHeight());

        icMqtt = getResources().getDrawable(R.drawable.ic_mqtt);
        icMqtt.setBounds(0, 0, icMqtt.getIntrinsicWidth(), icMqtt.getIntrinsicHeight());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spOidc.setAdapter(adapter);

    }

    @Override
    protected void initListener() {
        setupIgnoreListener();
        setupQRcodeListener();
        setupConnectListener();
        setupEtWebServerListener();
        setupEtMqttServerListener();
        setupSpinnerOIDCListener();
    }

    private void setupIgnoreListener() {
        tvIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServerConnectActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupConnectListener() {
        connectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etWebServerUrl.getText().toString().isEmpty() || etMQTTServerUrl.getText().toString().isEmpty()) {
                    showErrorMessage("URL不得為空");
                    return;
                }

                if (!isValidDomainUrl(etWebServerUrl.getText().toString())) {
                    showErrorMessage("請輸入正確URL");
                    return;
                }

                if (selectedSpinnerItem.equals("請選擇登入機制")) {
                    showErrorMessage("請選擇登入機制");
                    return;
                }
                Global.setDomain(etWebServerUrl.getText().toString());
                Global.setMqtt(etMQTTServerUrl.getText().toString());
                ApiClient.getInstance().recreateApiService();
                mServerConnectPresenter.getHealthz();
            }
        });
    }

    private void setupQRcodeListener() {
        qrcodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ServerConnectActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ServerConnectActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    startQRScanner();
                }
            }
        });
    }

    private void setupEtWebServerListener() {

        etWebServerUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etWebServerUrl.setCompoundDrawables(icWebUrl, null, null, null);
                } else {
                    etWebServerUrl.setCompoundDrawables(null, null, null, null);
                }
                if (!editable.toString().isEmpty() && !etMQTTServerUrl.getText().toString().isEmpty()) {
                    connectTv.setEnabled(true);
                } else {
                    connectTv.setEnabled(false);
                }
            }
        });
    }

    private void setupEtMqttServerListener() {
        etMQTTServerUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().isEmpty()) {
                    etMQTTServerUrl.setCompoundDrawables(icMqtt, null, null, null);

                } else {
                    etMQTTServerUrl.setCompoundDrawables(null, null, null, null);
                }
                if (!editable.toString().isEmpty() && !etWebServerUrl.getText().toString().isEmpty()) {

                    connectTv.setEnabled(true);
                } else {
                    connectTv.setEnabled(false);
                }

            }
        });
    }


    private void setupSpinnerOIDCListener() {
        spOidc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                selectedSpinnerItem = selectedItem;
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        Global.setClientId("OIDC");
                        Global.setClientSecret("1fa5e9e7-49cf-4fee-a58c-e7ab8a891678");
                        Global.setIsOIDC("1");
                        Global.setWellKnownURL("/oidc/.well-known/openid-configuration");
                        etWebServerUrl.setText("http://twtpecimdocker3.deltaos.corp:8065");
                        etMQTTServerUrl.setText("tcp://10.0.0.0");

                        break;
                    case 2:
                        Global.setClientId("demo");
                        Global.setClientSecret("OTdC9lfD3eKaKWSCHk6LTQydBQJzBSQg");
                        Global.setIsOIDC("0");
                        Global.setWellKnownURL("/realms/master/.well-known/openid-configuration");
                        etWebServerUrl.setText("http://10.139.33.147:8088");
                        etMQTTServerUrl.setText("tcp://10.0.0.0");
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 當沒有選擇時的操作
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
        dbRepositoryCaller.callSetMethod("global", "serverIp", etWebServerUrl.getText().toString());
        ApiClient.getInstance().initialize(dbRepository, oidcAuthenticator, Global.getDomain());
        startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));

        finish();

    }

    @Override
    public void getHealthFailed(String err) {
        Global.setDomain("");
        webServerTitleTv.setTextColor(getColor(R.color.logout_button_orange));
        mqttServerTitleTv.setTextColor(getColor(R.color.logout_button_orange));
        showErrorMessage(err);

    }


    //region CAMERA
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                showErrorMessage("Camera permission is required to scan QR codes");
                finish();
            }
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CustomCaptureActivity.class);
        integrator.setPrompt("Scan a QR Code");
        integrator.setBeepEnabled(false);
        integrator.setRequestCode(REQUEST_BARCODE_SCAN);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mServerConnectPresenter.attachView(this);
        if (requestCode == REQUEST_BARCODE_SCAN && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(SCAN_RESULT)) {
                String scannedData = data.getStringExtra(SCAN_RESULT);

                if (isValidJson(scannedData)) {
                    apiService = ApiClient.getInstance().setBaseUrl(Global.getDomain());
                    mServerConnectPresenter.getHealthz();
                } else {
                    showErrorMessage("Scanning failed");
                }
            }
        }
    }

    private boolean isCameraScan(IntentResult result) {
        return result.getContents() != null && result.getContents().contains(":");
    }

    private void handleCameraScanResult(IntentResult result) {
//        showMessage("Camera scanned: " + result.getContents());
        startHealthCheckWithUrl(Global.getDomain());
    }

    private StringBuilder barcodeBuilder = new StringBuilder();

    private void startHealthCheckWithUrl(String url) {
        mServerConnectPresenter.attachView(this);
        apiService = ApiClient.getInstance().setBaseUrl(url);
        mServerConnectPresenter.getHealthz();
    }

    private boolean isValidJson(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);

            return isValidElement(jsonObject);
        } catch (JSONException e) {
            return false;
        }
    }

    private boolean isValidDomainUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private boolean isValidMqttUrl(String url) {

        return url != null && url.startsWith("tcp://");
    }


    private boolean isValidElement(JSONObject jsonObject) {
        try {
            if (jsonObject.has("serverUrl") && jsonObject.getString("serverUrl").startsWith("http://")) {
                Global.setDomain(jsonObject.getString("serverUrl"));

            } else {
                return false;
            }
            if (jsonObject.has("mqtt") && jsonObject.getString("mqtt").startsWith("tcp://")) {
                Global.setMqtt(jsonObject.getString("mqtt"));
            } else {
                return false;
            }

            return true;
        } catch (JSONException e) {
            return false;
        }
    }

}