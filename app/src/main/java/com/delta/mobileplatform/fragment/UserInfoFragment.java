package com.delta.mobileplatform.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.activity.MainActivity;
import com.delta.mobileplatform.adapter.SettingPageAdapter;
import com.delta.mobileplatform.bean.UserInfoBean;
//import com.delta.mobileplatform.domain.localStorage.JavaLocalStorage.JavaDbRepository;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.SettingPagePresenter;
import com.delta.mobileplatform.presenter.UserInfoPresenter;
import com.delta.mobileplatform.service.ApiService;

import com.delta.mobileplatform.view.UserInfoView;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserInfoFragment extends BaseFragment<UserInfoPresenter, UserInfoView> implements UserInfoView {

    @Inject
    DbRepository javaDbRepository;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    private AppCompatButton button;
    private TextView tvUsername, tvFactory, tvUsercode, tvEmail;
    private ApiService mApiService;
    private UserInfoBean mUserInfoBean;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_userinfo;
    }

    @Override
    protected UserInfoPresenter initPresenter() {
//        ApiClient.getInstance().registerObserver(this);
//        mApiService = ApiClient.getInstance().getApiService();
        return new UserInfoPresenter(oidcAuthenticator);
    }

    @Override
    protected void initData() {
        mPresenter.getUserInfo();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvFactory = view.findViewById(R.id.tv_factory);
        tvUsercode = view.findViewById(R.id.tv_usercode);
        tvEmail = view.findViewById(R.id.tv_email);
        button = view.findViewById(R.id.bt_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter != null) {
                    mPresenter.logout();
                }
            }
        });
    }

    @Override
    protected UserInfoView getBaseView() {
        return this;
    }

    @Override
    public void logoutSuccess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage("登出");
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    @Override
    public void logoutFailed(String err) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorMessage(err);
            }
        });
    }

    @Override
    public void getUserInfoSuccess(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在這裡更新 UI 元素
                tvUsername.setText(userInfoBean.getUserName());
                tvEmail.setText(userInfoBean.getEmail());

            }
        });

    }

    @Override
    public void getUserInfoFailed(String err) {
        showErrorMessage(err);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
