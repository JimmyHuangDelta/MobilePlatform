//package com.delta.mobileplatform.oidc;
//
//
//import android.net.Uri;
//import android.util.Log;
//
//import com.delta.mobileplatform.app.MyApplication;
//import com.delta.mobileplatform.bean.UserInfoBean;
//import com.delta.mobileplatform.domain.localStorage.DbRepository;
//import com.delta.mobileplatform.util.Global;
//import com.delta.mobileplatform.util.MyConnectionBuilder;
//import com.delta.mobileplatform.util.TokenManager;
//import com.google.gson.Gson;
//
//import net.openid.appauth.AppAuthConfiguration;
//import net.openid.appauth.AuthorizationException;
//import net.openid.appauth.AuthorizationService;
//import net.openid.appauth.AuthorizationServiceConfiguration;
//import net.openid.appauth.TokenRequest;
//import net.openid.appauth.TokenResponse;
//import net.openid.appauth.connectivity.ConnectionBuilder;
//
//import org.json.JSONException;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//
//public class OIDCAuthenticatorSample {
//    private AuthorizationService mAuthService;
//    private AppAuthConfiguration appAuthConfiguration;
//    private ConnectionBuilder connectionBuilder;
//    private String domain, accessToken, refreshToken, introspectEndpoint, revokeEndpoint, userInfoEndPoint;
//    private Uri UriIntrospectEndpoint, UriRevokeEndpoint;
//
//
//    public OIDCAuthenticatorSample() {
//        connectionBuilder = new MyConnectionBuilder();
//        appAuthConfiguration = new AppAuthConfiguration.Builder().setConnectionBuilder(connectionBuilder).setSkipIssuerHttpsCheck(true).build();
//        mAuthService = new AuthorizationService(MyApplication.getAppContext(), appAuthConfiguration);
//    }
//
//    public void getToken(String username, String password, AuthorizationService.TokenResponseCallback callback) {
//        Map<String, String> additionalParams = new HashMap<>();  //加入其他參數
//        additionalParams.put("username", username);
//        additionalParams.put("password", password);
//        additionalParams.put("client_secret", Global.getClientSecret());  //client_secret套件預設沒有，需自己加入
//
//        AuthorizationServiceConfiguration.fetchFromUrl(
//                Uri.parse(domain + "/oidc/.well-known/openid-configuration"), //標準格式，有需求可自訂
//                (configuration, ex) -> { //configuration回傳
//                    CustomAuthorizationServiceConfiguration modifiedConfiguration = createModifiedConfiguration(configuration);
//                    if (modifiedConfiguration != null) {
//
////                        即可獲取 各endpoint位置，依照需求可做取用
//                        String authorizationEndpoint = modifiedConfiguration.discoveryDoc.getAuthorizationEndpoint().toString();
//                        String tokenEndpoint = modifiedConfiguration.discoveryDoc.getTokenEndpoint().toString();
//                        String endSessionEndpoint = modifiedConfiguration.discoveryDoc.getEndSessionEndpoint().toString();
//
//                        String userinfoEndpoint = modifiedConfiguration.discoveryDoc.getUserinfoEndpoint().toString();
//                        String scope = modifiedConfiguration.discoveryDoc.getScopesSupported().toString();
//
//                        String introspectionEndpoint = modifiedConfiguration.getIntrospectionEndpoint().toString();
//                        String revokeEndpoint = modifiedConfiguration.getRevokeEndpoint().toString();
//
//
//                        //Token Request
//                        TokenRequest.Builder tokenRequestBuilder = new TokenRequest.Builder(
//                                modifiedConfiguration, //傳入上面獲取的設置
//                                Global.getClientId() //傳入clientId
//                        )
//                                .setGrantType("password") //設置grantType
//                                .setClientId(Global.getClientId()) //設置clientId
//                                .setAdditionalParameters(additionalParams)//設置其他需傳入參數
//                                .setScope("openid profile offline_access"); //設置scope
//
//
//                        //建立Token Request
//                        TokenRequest tokenRequest = tokenRequestBuilder.build();
//
//
//                        //執行, 無須處理tokenEndpoint, 套件內會完成獲取及使用
//                        mAuthService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
//                            @Override
//                            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException tokenException) {
//                                if (tokenResponse != null) {
//                                    String accessToken = tokenResponse.accessToken; //獲取accessToken
//                                    String refreshToken = tokenResponse.refreshToken;//獲取refreshToken
//                                }
//                            }
//                        });
//                    } else {
//                    }
//                },
//                connectionBuilder //建立連接
//        );
//    }
//
//    private AuthorizationServiceConfiguration modifiedConfiguration;
//
//    public void refreshToken(TokenManager.TokenRefreshCallback callback) {
//
//
//        refreshToken = Global.getRefreshToken();  //登入獲取之refreshToken
//
//        Map<String, String> additionalParams = new HashMap<>();
//        additionalParams.put("client_secret", Global.getClientSecret()); //設置client_secret
//
//                    if (modifiedConfiguration != null) {
//                        TokenRequest.Builder tokenRequestBuilder = new TokenRequest.Builder(
//                                modifiedConfiguration,
//                                Global.getClientId()
//                        )
//
//                                .setGrantType("refresh_token") //設置grantType為refresh_token
//                                .setScope("openid profile offline_access") //設置scope
//                                .setRefreshToken(refreshToken) //登入獲取之refreshToken
//                                .setClientId(Global.getClientId()) //設置clientId
//                                .setAdditionalParameters(additionalParams); //設置其他額外參數
//
//                        //建立tokenRequest
//                        TokenRequest tokenRequest = tokenRequestBuilder.build();
//
//                        //執行tokenRequest
//                        mAuthService.performTokenRequest(tokenRequest, new AuthorizationService.TokenResponseCallback() {
//                            @Override
//                            public void onTokenRequestCompleted(TokenResponse tokenResponse, AuthorizationException tokenException) {
//                                if (tokenResponse != null) {
//                                    Global.setAccessToken(tokenResponse.accessToken); //交換後獲取到的新accessToken
//                                    Global.setRefreshToken(tokenResponse.refreshToken);//交換後獲取到的新refreshToken
//                                }
//                            }
//                        });
//                    } else {
//                        Log.e("OIDCManager", "Failed to fetch configuration: " );
//                    }
//
//
//    }
//
//    //    驗證
//    public void introspectToken(TokenManager.TokenIntrospectCallback callback) {
//
//        domain = Global.getDomain();
//        accessToken = Global.getAccessToken();
//        introspectEndpoint = Global.getIntrospectEndpoint();
//        if (!introspectEndpoint.isEmpty()) {
//            try {
//                URL url = new URL(introspectEndpoint);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
//                connection.setRequestProperty("Accept", "*/*");
//                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//                connection.setRequestProperty("Connection", "keep-alive");
//                connection.setDoOutput(true);
//
//                Map<String, String> requestBodyMap = new HashMap<>();
//                requestBodyMap.put("token", accessToken);
//                requestBodyMap.put("token_type_hint", "access_token");
//
//                StringBuilder requestBodyBuilder = new StringBuilder();
//                for (Map.Entry<String, String> entry : requestBodyMap.entrySet()) {
//                    if (requestBodyBuilder.length() > 0) {
//                        requestBodyBuilder.append("&");
//                    }
//                    requestBodyBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
//                            .append("=")
//                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//                }
//
//                String requestBody = requestBodyBuilder.toString();
//
//                OutputStream outputStream = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//                writer.write(requestBody);
//                writer.flush();
//                writer.close();
//                outputStream.close();
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    callback.onTokenValid();
//
//                } else {
//                    callback.onTokenInvalid(connection.getResponseMessage());
//                }
//                connection.disconnect();
//            } catch (IOException e) {
//                // 处理异常
//                e.printStackTrace();
//            }
//        } else {
//            Log.e("OIDCManager", "IntrospectionEndpoint is null.");
//        }
//
//    }
//
//    public void getUserInfo(TokenManager.UserInfoCallback callback) {
//
//        domain = Global.getDomain();
//        accessToken = Global.getAccessToken();
//        userInfoEndPoint = Global.getUserInfoEndpoint();
//        if (!userInfoEndPoint.isEmpty()) {
//            try {
//                URL url = new URL(userInfoEndPoint);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Authorization", "Bearer " + accessToken); // 设置 Authorization 头部
//                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
//                connection.setRequestProperty("Accept", "*/*");
//                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//                connection.setRequestProperty("Connection", "keep-alive");
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    InputStream inputStream = connection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    reader.close();
//                    inputStream.close();
//
//                    Gson gson = new Gson();
//                    UserInfoBean userInfoBean = gson.fromJson(response.toString(), UserInfoBean.class);
//                    callback.onUserInfoSuccess(userInfoBean);
//                } else {
//                    callback.onUserInfoFailed(connection.getResponseMessage());
//                }
//                connection.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.e("OIDCManager", "IntrospectionEndpoint is null.");
//        }
//
//    }
//
//
//    public void performLogout(TokenManager.TokenRevokeCallback callback) {
//
//        domain = Global.getDomain();
//        accessToken = Global.getAccessToken();
//        revokeEndpoint = Global.getRevokeEndpoint();
//        if (!revokeEndpoint.isEmpty()) {
//            try {
//                URL url = new URL(revokeEndpoint);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//
//                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("User-Agent", "PostmanRuntime/7.37.0");
//                connection.setRequestProperty("Accept", "*/*");
//                connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//                connection.setRequestProperty("Connection", "keep-alive");
//
//                Map<String, String> requestBodyMap = new HashMap<>();
//                requestBodyMap.put("token", accessToken);
//                requestBodyMap.put("token_type_hint", "access_token");
//
//                StringBuilder requestBodyBuilder = new StringBuilder();
//                for (Map.Entry<String, String> entry : requestBodyMap.entrySet()) {
//                    if (requestBodyBuilder.length() > 0) {
//                        requestBodyBuilder.append("&");
//                    }
//                    requestBodyBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
//                            .append("=")
//                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//                }
//
//                String requestBody = requestBodyBuilder.toString();
//
//                connection.setDoOutput(true);
//                OutputStream outputStream = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//                writer.write(requestBody);
//                writer.flush();
//                writer.close();
//                outputStream.close();
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Global.setAccessToken("");
//                    Global.setRefreshToken("");
//                    callback.onTokenRevoke();
//                } else {
//                    callback.onTokenRevokeFailed(connection.getResponseMessage());
//                }
//                connection.disconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//                callback.onTokenRevokeFailed(e.getMessage());
//            }
//        } else {
//            Log.e("OIDCManager", "EndSessionEndpoint is null.");
//            callback.onTokenRevokeFailed("EndSessionEndpoint is null.");
//        }
//
//    }
//
//
//    private CustomAuthorizationServiceConfiguration createModifiedConfiguration(AuthorizationServiceConfiguration configuration) {
//        if (configuration != null) {
//            try {
//                this.UriIntrospectEndpoint = Uri.parse(configuration.discoveryDoc.docJson.getString("introspect"));
//                this.UriRevokeEndpoint = Uri.parse(configuration.discoveryDoc.docJson.getString("revoke"));
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            return new CustomAuthorizationServiceConfiguration(
//
//                    configuration.discoveryDoc,
//                    UriIntrospectEndpoint, UriRevokeEndpoint
//            );
//        } else {
//            return null;
//        }
//    }
//
//    public void dispose() {
//        if (mAuthService != null) {
//            mAuthService.dispose();
//        }
//    }
//}