package com.delta.mobileplatform.view;

import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;

import java.util.List;

public interface FeatureView extends IBaseView {
    void getUserInfoSuccess(UserInfoBean userInfoBean);

    void getUserInfoFailed(String err);

    void getFactorySuccess(List<FactoryBean> factoryBeans);

    void getFactoryFailed(String err);

    void getAccountNavationSuccess(List<FeatureBean> featureBean);

    void getAccountNavationFailed(String err);

    void changeFactorySuccess();

    void changeFactoryFailed(String err);

}