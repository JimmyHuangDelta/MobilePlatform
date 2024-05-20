package com.delta.mobileplatform.presenter;



import com.delta.mobileplatform.bean.UserInfoBean;

import com.delta.mobileplatform.model.UserInfoModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;

import com.delta.mobileplatform.util.TokenManager;

import com.delta.mobileplatform.view.UserInfoView;

public class UserInfoPresenter extends BasePresenter<UserInfoView> {
    private UserInfoModel userInfoModel;

    public UserInfoPresenter( OIDCAuthenticator oidcAuthenticator) {
        userInfoModel = new UserInfoModel( oidcAuthenticator);
    }

    @Override
    public void attachView(UserInfoView view) {
        super.attachView(view);
    }

//    登出
    public void logout() {

        getView().showLoading();
        userInfoModel.logout(new TokenManager.TokenRevokeCallback() {
            @Override
            public void onTokenRevoke() {
                if (isViewAttached()) {
                    getView().logoutSuccess();
                    getView().hideLoading();
                }
            }

            @Override
            public void onTokenRevokeFailed(String error) {
                if (isViewAttached()) {
                    getView().logoutFailed(error);
                    getView().hideLoading();
                }
            }
        });
    }


//    使用者資訊
    public void getUserInfo() {

        getView().showLoading();
        userInfoModel.getUserInfo(new TokenManager.UserInfoCallback() {
            @Override
            public void onUserInfoSuccess(UserInfoBean userInfoBean) {
                if (isViewAttached()) {
                    getView().getUserInfoSuccess(userInfoBean);
                    getView().hideLoading();
                }

            }

            @Override
            public void onUserInfoFailed(String error) {
                if (isViewAttached()) {
                    getView().getUserInfoFailed(error);
                    getView().hideLoading();
                }
            }
        });
    }

    public void onDestroy() {
        userInfoModel.onDestroy();
    }
}
