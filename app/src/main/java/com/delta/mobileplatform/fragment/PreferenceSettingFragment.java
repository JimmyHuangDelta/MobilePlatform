package com.delta.mobileplatform.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.adapter.SettingPageAdapter;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.presenter.SettingPagePresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.view.IBaseView;
import com.google.android.material.tabs.TabLayout;

public class PreferenceSettingFragment extends BaseFragment {
    private RadioGroup radioGroup;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_preference;
    }

    @Override
    protected void initView(View view) {
        radioGroup = view.findViewById(R.id.connect_rg);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        setupRGListener();
    }

    private void setupRGListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1:
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intent);
                        break;
                    case R.id.radioButton2:
                        break;
                }
            }
        });

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected IBaseView getBaseView() {
        return null;
    }

}
