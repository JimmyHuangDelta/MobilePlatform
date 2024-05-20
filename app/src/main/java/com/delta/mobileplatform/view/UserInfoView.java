package com.delta.mobileplatform.view;

import com.delta.mobileplatform.bean.UserInfoBean;

public interface UserInfoView extends IBaseView {
    void logoutSuccess();

    void logoutFailed(String err);

    void getUserInfoSuccess(UserInfoBean userInfoBean);

    void getUserInfoFailed(String err);
}