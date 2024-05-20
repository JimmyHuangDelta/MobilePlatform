package com.delta.mobileplatform.activity;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.delta.mobileplatform.R;

import com.delta.mobileplatform.fragment.FeatureFragment;
import com.delta.mobileplatform.fragment.SettingPageFragment;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.presenter.HomePresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.view.HomeView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity implements HomeView {
    private BottomNavigationView bottomNavigationView;
    private String CURRENT_TAG = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String currentTag = savedInstanceState.getString("currentTag");
            if (!currentTag.isEmpty()) {
                CURRENT_TAG = currentTag;
            }
            initView();
        } else {
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentTag", CURRENT_TAG);
    }

    @Override
    protected void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        String featureFragmentTag = FeatureFragment.class.getSimpleName();
        String settingFragmentTag = SettingPageFragment.class.getSimpleName();
        if (CURRENT_TAG.isEmpty()) {
            replaceFragment(new FeatureFragment());
        } else if (CURRENT_TAG.equals(featureFragmentTag)) {
            replaceFragment(new FeatureFragment());
        } else if (CURRENT_TAG.equals(settingFragmentTag)) {
            replaceFragment(new SettingPageFragment());
        }
    }


    @Override
    protected void initListener() {
        setupBottomNavigationListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    //region setup listener
    private void setupBottomNavigationListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_home:
                        replaceFragment(new FeatureFragment());
                        return true;
                    case R.id.menu_settings:
                        replaceFragment(new SettingPageFragment());
                        return true;

                }
                return false;
            }
        });
        bottomNavigationView.setItemHorizontalTranslationEnabled(false);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment existingFragment : fragmentManager.getFragments()) {
            transaction.hide(existingFragment);
        }
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());
        }

        transaction.commit();
        CURRENT_TAG = fragment.getClass().getSimpleName();
    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {

    }
    //endregion
}