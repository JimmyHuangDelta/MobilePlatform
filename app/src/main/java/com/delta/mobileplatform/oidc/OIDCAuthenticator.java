package com.delta.mobileplatform.oidc;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.delta.mobileplatform.activity.LoginActivity;
import com.delta.mobileplatform.app.MyApplication;
import com.delta.mobileplatform.bean.UserInfoBean;

import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.JavaLocalStorage.DbRepositoryCaller;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.util.Global;
import com.delta.mobileplatform.util.MyConnectionBuilder;

import com.delta.mobileplatform.util.TokenManager;
import com.google.gson.Gson;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.connectivity.ConnectionBuilder;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;


public class OIDCAuthenticator {
    private AuthorizationService mAuthService;
    private AppAuthConfiguration appAuthConfiguration;
    private ConnectionBuilder connectionBuilder;
    private String domain,wellknownUrl ,client_id, clientSecret ;
    private String accessToken, refreshToken, refreshTokenEndpoint,introspectEndpoint, revokeEndpoint, userInfoEndPoint;
    private Uri UriIntrospectEndpoint, UriRevokeEndpoint;
    private DbRepositoryCaller dbRepositoryCaller;
    private DbRepository repository;

    @Inject
    public OIDCAuthenticator(DbRepository repository) {
        connectionBuilder = new MyConnectionBuilder();
        appAuthConfiguration = new AppAuthConfiguration.Builder().setConnectionBuilder(connectionBuilder).setSkipIssuerHttpsCheck(true).build();
        mAuthService = new AuthorizationService(MyApplication.getAppContext(), appAuthConfiguration);
        this.repository = repository;
        dbRepositoryCaller = new DbRepositoryCaller(repository);
    }

    public void getToken(String username, String password, AuthorizationService.TokenResponseCallback callback) {
        domain = Global.getDomain();
        wellknownUrl = Global.getGetWellKnownUrl();
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("username", username);
        additionalParams.put("password", password);
        additionalParams.put("client_secret", Global.getClientSecret());

        AuthorizationServiceConfiguration.fetchFromUrl(
                Uri.parse(domain + wellknownUrl),

                (configuration, ex) -> {
                    CustomAuthorizationServiceConfiguration modifiedConfiguration = createModifiedConfiguration(configuration);
                    if (modifiedConfiguration != null) {

                        String authorizationEndpoint = modifiedConfiguration.discoveryDoc.getAuthorizationEndpoint().toString();
                        String tokenEndpoint = modifiedConfiguration.discoveryDoc.getTokenEndpoint().toString();
                        String endSessionEndpoint = modifiedConfiguration.discoveryDoc.getEndSessionEndpoint().toString();

                        String userinfoEndpoint = modifiedConfiguration.discoveryDoc.getUserinfoEndpoint().toString();
                        String scope = modifiedConfiguration.discoveryDoc.getScopesSupported().toString();

                        String introspectionEndpoint = modifiedConfiguration.getIntrospectionEndpoint().toString();
                        String revokeEndpoint = modifiedConfiguration.getRevokeEndpoint().toString();

                        Global.setAuthorizationEndpoint(authorizationEndpoint);
                        Global.setTokenEndpoint(tokenEndpoint);
                        Global.setEndSessionEndpoint(endSessionEndpoint);
                        Global.setIntrospectEndpoint(introspectionEndpoint);
                        Global.setUserInfoEndpoint(userinfoEndpoint);
                        Global.setRevokeEndpoint(revokeEndpoint);

                        TokenRequest.Builder tokenRequestBuilder = new TokenRequest.Builder(
                                modifiedConfiguration,
                                Global.getClientId()
                        )
                                .setGrantType("password")
                                .setClientId(Global.getClientId())
                                .setAdditionalParameters(additionalParams)
                                .setScope("openid profile offline_access");

                        TokenRequest tokenRequest = tokenRequestBuilder.build();

                        mAuthService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException tokenException) {
                                if (tokenResponse != null) {
                                    dbRepositoryCaller.callSetMethod(Global.getDomain(), "access_token", tokenResponse.accessToken);
                                    dbRepositoryCaller.callSetMethod(Global.getDomain(), "refresh_token", tokenResponse.refreshToken);
                                    Global.setAccessToken(tokenResponse.accessToken);
                                    Global.setRefreshToken(tokenResponse.refreshToken);

                                    callback.onTokenRequestCompleted(tokenResponse, tokenException);
                                } else {
                                    callback.onTokenRequestCompleted(tokenResponse, tokenException);
                                }
                            }
                        });

                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(MyApplication.getAppContext(), "Failed to fetch configuration: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                        redirectToLoginPage();
                        Log.e("OIDCManager", "Failed to fetch configuration: " + ex.getMessage());
                    }
                },
                connectionBuilder
        );
    }

    //刷新
    public void refreshToken(TokenManager.TokenRefreshCallback callback) {

        domain = Global.getDomain();
        wellknownUrl = Global.getGetWellKnownUrl();
        refreshToken = Global.getRefreshToken();
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("client_secret", Global.getClientSecret());

        AuthorizationServiceConfiguration.fetchFromUrl(
                Uri.parse(Global.getDomain() + wellknownUrl),
//                Uri.parse(domain + "/realms/master/.well-known/openid-configuration"),
                (configuration, ex) -> {
                    CustomAuthorizationServiceConfiguration modifiedConfiguration = createModifiedConfiguration(configuration);
                    if (modifiedConfiguration != null) {
                        TokenRequest.Builder tokenRequestBuilder = new TokenRequest.Builder(
                                modifiedConfiguration,
                                Global.getClientId()
                        )

                                .setGrantType("refresh_token")
                                .setScope("openid profile offline_access")
                                .setRefreshToken(refreshToken)
                                .setAdditionalParameters(additionalParams);


                        TokenRequest tokenRequest = tokenRequestBuilder.build();

                        mAuthService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
                            @Override
                            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException tokenException) {
                                if (tokenResponse != null) {
                                    Global.setAccessToken(tokenResponse.accessToken);
                                    Global.setRefreshToken(tokenResponse.refreshToken);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dbRepositoryCaller.callSetMethod(Global.getDomain(), "access_token", tokenResponse.accessToken);
                                            dbRepositoryCaller.callSetMethod(Global.getDomain(), "refresh_token", tokenResponse.refreshToken);
                                        }
                                    }).start();


                                    ApiClient.getInstance().recreateApiService();

                                    callback.onTokenRefreshed(tokenResponse.accessToken, tokenResponse.refreshToken);
                                } else {
                                    Log.e("OIDCAuthenticator", "Token refresh failed: " + tokenException.error);
                                    callback.onTokenRefreshFailed(tokenException.error);
                                }
                            }
                        });
                    } else {
                        Log.e("OIDCManager", "Failed to fetch configuration: " + ex.getMessage());
                    }
                },
                connectionBuilder
        );

    }

    //    驗證
    public void introspectToken(TokenManager.TokenIntrospectCallback callback) {
        domain = Global.getDomain();
        accessToken = Global.getAccessToken();
        introspectEndpoint = Global.getIntrospectEndpoint();
        client_id = Global.getClientId();
        clientSecret = Global.getClientSecret();
        if (!introspectEndpoint.isEmpty()) {
            try {
                URL url = new URL(introspectEndpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setDoOutput(true);

                Map<String, String> requestBodyMap = new HashMap<>();
                requestBodyMap.put("token", accessToken);
                requestBodyMap.put("token_type_hint", "access_token");

                requestBodyMap.put("client_id", client_id);
                requestBodyMap.put("client_secret", clientSecret);

                StringBuilder requestBodyBuilder = new StringBuilder();
                for (Map.Entry<String, String> entry : requestBodyMap.entrySet()) {
                    if (requestBodyBuilder.length() > 0) {
                        requestBodyBuilder.append("&");
                    }
                    requestBodyBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }

                String requestBody = requestBodyBuilder.toString();

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onTokenValid();
                } else {
                    callback.onTokenInvalid(connection.getResponseMessage());
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                redirectToLoginPage();
            }
        } else {
            Log.e("OIDCManager", "IntrospectionEndpoint is null.");
            redirectToLoginPage();
        }
    }

    public void getUserInfo(TokenManager.UserInfoCallback callback) {

        domain = Global.getDomain();
        accessToken = Global.getAccessToken();
        userInfoEndPoint = Global.getUserInfoEndpoint();
        client_id = Global.getClientId();
        clientSecret = Global.getClientSecret();
        if (!userInfoEndPoint.isEmpty()) {
            try {
                URL url = new URL(userInfoEndPoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);
                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                connection.setRequestProperty("Connection", "keep-alive");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();

                    Gson gson = new Gson();
                    UserInfoBean userInfoBean = gson.fromJson(response.toString(), UserInfoBean.class);
                    callback.onUserInfoSuccess(userInfoBean);
                } else {
                    callback.onUserInfoFailed(connection.getResponseMessage());
                }
                connection.disconnect();
            } catch (IOException e) {
                    Toast.makeText(MyApplication.getAppContext(), "Failed to connect: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("OIDCManager", "IntrospectionEndpoint is null.");
            redirectToLoginPage();

        }

    }


    public void performLogout(TokenManager.TokenRevokeCallback callback) {

        domain = Global.getDomain();
        accessToken = Global.getAccessToken();
        revokeEndpoint = Global.getRevokeEndpoint();
        client_id = Global.getClientId();
        clientSecret = Global.getClientSecret();
        if (!revokeEndpoint.isEmpty()) {
            try {
                URL url = new URL(revokeEndpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
                connection.setRequestProperty("Accept", "*/*");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                connection.setRequestProperty("Connection", "keep-alive");

                Map<String, String> requestBodyMap = new HashMap<>();
                requestBodyMap.put("token", accessToken);
                requestBodyMap.put("token_type_hint", "access_token");

                requestBodyMap.put("client_id", client_id);
                requestBodyMap.put("client_secret", clientSecret);

                StringBuilder requestBodyBuilder = new StringBuilder();
                for (Map.Entry<String, String> entry : requestBodyMap.entrySet()) {
                    if (requestBodyBuilder.length() > 0) {
                        requestBodyBuilder.append("&");
                    }
                    requestBodyBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }

                String requestBody = requestBodyBuilder.toString();

                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dbRepositoryCaller.callSetMethod(Global.getDomain(), "access_token", "");
                    dbRepositoryCaller.callSetMethod(Global.getDomain(), "refresh_token", "");
                    Global.setAccessToken("");
                    Global.setRefreshToken("");
                    callback.onTokenRevoke();
                } else {
                    callback.onTokenRevokeFailed(connection.getResponseMessage());
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                callback.onTokenRevokeFailed(e.getMessage());
            }
        } else {
            redirectToLoginPage();
        }

    }


    private CustomAuthorizationServiceConfiguration createModifiedConfiguration(AuthorizationServiceConfiguration configuration) {
        if (configuration != null) {
            try {
                this.UriIntrospectEndpoint = Uri.parse(configuration.discoveryDoc.docJson.getString("introspection_endpoint"));
                this.UriRevokeEndpoint = Uri.parse(configuration.discoveryDoc.docJson.getString("revocation_endpoint"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return new CustomAuthorizationServiceConfiguration(

                    configuration.discoveryDoc,
                    UriIntrospectEndpoint, UriRevokeEndpoint
            );
        } else {
            return null;
        }
    }


    public String getEndpointFromJSON(JSONObject json, String endpointName) {
        try {
            if (json.has(endpointName)) {
                return json.getString(endpointName);
            } else {
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = json.get(key);
                    if (value instanceof JSONObject) {
                        String result = getEndpointFromJSON((JSONObject) value, endpointName);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void redirectToLoginPage() {
        Context context = MyApplication.getAppContext();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void dispose() {
        if (mAuthService != null) {
            mAuthService.dispose();
        }
    }
}