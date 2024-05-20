package com.delta.mobileplatform.activity;


import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.adapter.SearchFeatureAdapter;
import com.delta.mobileplatform.adapter.SearchFeatureHistoryAdapter;
import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.BasePresenter;

import com.delta.mobileplatform.presenter.SearchFearchPresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.ui.SearchBarLayout;
import com.delta.mobileplatform.util.SharedPerformer;
import com.delta.mobileplatform.view.SearchFeatureView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFeatureActivity extends BaseActivity implements SearchFeatureView, ApiServiceObserver {

    private ApiClient apiClient;
    @Inject
    OIDCAuthenticator oidcAuthenticator;
    private ApiService apiService;
    private TabLayout tabLayout;
    private SearchFearchPresenter mSearchFearchPresenter;
    private List<FactoryBean> mFactoryBeans;
    private RecyclerView recyclerView, searchHistoryRecyclerView;
    private SearchBarLayout searchBarLayout;
    private TextView resultTv;
    private String currentFactoryId;
    private List<String> searchHistoryList;

    private Handler handler = new Handler();

    private Runnable searchRunnable;
    private List<FeatureBean> mFeatureBeanList;

    private List<FeatureBean> filteredList;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected BasePresenter initPresenter() {
        mSearchFearchPresenter = new SearchFearchPresenter(oidcAuthenticator);
        return mSearchFearchPresenter;
    }

    @Override
    protected void initData() {
        searchHistoryList = new ArrayList<>(SharedPerformer.getInstance().getSearchHistory());
        mFeatureBeanList = new ArrayList<>();
        mSearchFearchPresenter.getUserInfo();
        mSearchFearchPresenter.getAccountNavigation();
    }

    @Override
    protected void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        searchBarLayout = findViewById(R.id.search_bar);
        searchBarLayout.getEtSearch().setFocusable(true);
        searchBarLayout.getEtSearch().setFocusableInTouchMode(true);
        resultTv = findViewById(R.id.result_title_tv);
        recyclerView = findViewById(R.id.search_recycler_view);
        setUpRecyclerView();

    }

    @Override
    protected void initListener() {
        setupSearchBarBackListener();
        setupSearchTextChangeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiClient.getInstance().unregisterObserver(this);
        searchBarLayout.getEtSearch().setFocusable(false);
        searchBarLayout.getEtSearch().setFocusableInTouchMode(false);
    }


    private void setupSearchBarBackListener() {
        searchBarLayout.getmIvBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchBarLayout.getmIvClose().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBarLayout.getEtSearch().setText("");
            }
        });


    }

    private void setupSearchTextChangeListener() {
        searchBarLayout.setSearchTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterFeatures(editable.toString());

                if (searchBarLayout.getEtSearch().getText().toString().isEmpty()) {
                    resultTv.setText(R.string.recent_search);
                    recyclerView.setVisibility(View.GONE);
                    searchHistoryRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    resultTv.setText(R.string.search_result);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchHistoryRecyclerView.setVisibility(View.GONE);
                }
                String trimmedText = editable.toString().trim();
                if (!trimmedText.isEmpty()) {
                    handler.removeCallbacks(searchRunnable);
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String searchText = searchBarLayout.getEtSearch().getText().toString();
                            if (filteredList.size() != 0) {
                                SharedPerformer.getInstance().saveSearchRecord(searchText);
                                searchHistoryList = new ArrayList<>(SharedPerformer.getInstance().getSearchHistory());
                                searchHistoryRecyclerView.getAdapter().notifyDataSetChanged();

                            }
                        }
                    };
                    handler.postDelayed(searchRunnable, 2000);
                }
            }
        });
    }

    private void filterFeatures(String searchText) {

        if (TextUtils.isEmpty(searchText.trim())) {
            return;
        }
        filteredList = new ArrayList<>();
        for (FeatureBean feature : mFeatureBeanList) {
            if (feature.getFunctionName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(feature);
            }
        }
        SearchFeatureAdapter adapter = new SearchFeatureAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

    }

    private void populateRecyclerView(List<FeatureBean> featureBeans) {
        mFeatureBeanList.clear();

        String rootFuntionKey = null;

        for (FeatureBean featureBean : featureBeans) {
            if (featureBean.getFatherFunctionKey().isEmpty() || featureBean.getFunctionId().equals("ROOT")) {
                rootFuntionKey = featureBean.getFunctionKey();
            }
        }
        for (FeatureBean featureBean : featureBeans) {
            if (featureBean.getFatherFunctionKey().equals(rootFuntionKey) || featureBean.getFatherFunctionKey().isEmpty() || featureBean.getFunctionId().equals("ROOT")) {
                continue;
            }
            mFeatureBeanList.add(featureBean);
        }
        if (recyclerView.getAdapter() == null) {
            SearchFeatureAdapter adapter = new SearchFeatureAdapter(this, mFeatureBeanList);
            recyclerView.setAdapter(adapter);
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager searchHistoryLayoutManager = new LinearLayoutManager(this);
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view);
        searchHistoryRecyclerView.setHasFixedSize(true);
        searchHistoryRecyclerView.setLayoutManager(searchHistoryLayoutManager);
        SearchFeatureHistoryAdapter searchFeatureHistoryAdapter = new SearchFeatureHistoryAdapter(this, searchHistoryList);
        SearchFeatureHistoryAdapter.OnItemClickListener searchHistoryClickListener = new SearchFeatureHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String searchStr) {
                searchBarLayout.getEtSearch().setText(searchStr);
            }
        };
        searchFeatureHistoryAdapter.setOnItemClickListener(searchHistoryClickListener);
        searchHistoryRecyclerView.setAdapter(searchFeatureHistoryAdapter);
    }

    private void setTabLayout() {
        tabLayout.removeAllTabs();

        for (FactoryBean factoryBean : mFactoryBeans) {
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(factoryBean.getFactoryName());
            tab.setTag(factoryBean);
            tabLayout.addTab(tab);
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            FactoryBean factoryBean = (FactoryBean) tab.getTag();

            if (factoryBean.getFactoryId().equals(currentFactoryId)) {

                tabLayout.selectTab(tab);
                break;
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                FactoryBean selectedFactory = (FactoryBean) tab.getTag();
                mSearchFearchPresenter.changeFactory(selectedFactory.getFactoryId());
                searchBarLayout.getEtSearch().setText("");
                showMessage(selectedFactory.getFactoryId().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public void getUserInfoSuccess(UserInfoBean userInfoBean) {
        currentFactoryId = userInfoBean.getFactoryId();
        mSearchFearchPresenter.getFactories();
    }

    @Override
    public void getUserInfoFailed(String err) {
        showErrorMessage(err);
    }

    @Override
    public void getFactorySuccess(List<FactoryBean> factoryBeans) {
        mFactoryBeans = factoryBeans;
        setTabLayout();
    }

    @Override
    public void getFactoryFailed(String err) {
        showErrorMessage(err);
    }

    @Override
    public void getAccountNavationSuccess(List<FeatureBean> featureBean) {
        populateRecyclerView(featureBean);
    }

    @Override
    public void getAccountNavationFailed(String err) {
        showErrorMessage(err);
    }

    @Override
    public void changeFactorySuccess() {
        searchBarLayout.getEtSearch().setText("");
        mSearchFearchPresenter.getAccountNavigation();
    }

    @Override
    public void changeFactoryFailed(String err) {
        showErrorMessage(err);
    }

    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        this.apiService = apiService;
    }

}