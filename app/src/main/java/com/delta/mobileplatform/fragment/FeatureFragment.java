package com.delta.mobileplatform.fragment;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delta.mobileplatform.R;
//import com.delta.mobileplatform.adapter.FeatureAdapter;
import com.delta.mobileplatform.activity.SearchFeatureActivity;
import com.delta.mobileplatform.adapter.SectionRecyclerViewAdapter;
import com.delta.mobileplatform.bean.FactoryBean;
import com.delta.mobileplatform.bean.FeatureBean;
import com.delta.mobileplatform.bean.UserInfoBean;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.model.SectionModel;
import com.delta.mobileplatform.oidc.OIDCAuthenticator;
import com.delta.mobileplatform.presenter.FeaturePresenter;
import com.delta.mobileplatform.service.ApiService;
import com.delta.mobileplatform.ui.TitleBarLayout;
import com.delta.mobileplatform.view.FeatureView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FeatureFragment extends BaseFragment<FeaturePresenter, FeatureView> implements FeatureView, ApiServiceObserver {

    @Inject
    OIDCAuthenticator oidcAuthenticator;
    private TabLayout tabLayout;
    private ApiService mApiService;
    private RecyclerView recyclerView;
    private TitleBarLayout titleBarLayout;
    private String currentFactoryId;

    private SectionRecyclerViewAdapter SectionAdapter;
    private ArrayList<SectionModel> mSectionModelArrayList;
    private List<FactoryBean> mFactoryBeansList;
    private FeaturePresenter mPresenter;


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_feature;
    }

    @Override
    protected FeaturePresenter initPresenter() {
        ApiClient.getInstance().registerObserver(this);
//        mApiService = ApiClient.getInstance().getApiService();
        if (mPresenter == null) {
            mPresenter = new FeaturePresenter(oidcAuthenticator);
        }
        return mPresenter;
    }

    @Override
    protected void initData() {
        mSectionModelArrayList = new ArrayList<>();
        mPresenter.getUserInfo();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.sectioned_recycler_view);
        tabLayout = view.findViewById(R.id.tab_layout);
        setUpRecyclerView(view);
        titleBarLayout = view.findViewById(R.id.edit_title_bar);
        titleBarLayout.setTitle(R.string.feature);
        titleBarLayout.getRightIcon1().setVisibility(View.VISIBLE);
        titleBarLayout.getRightIcon2().setVisibility(View.VISIBLE);
        titleBarLayout.setRightIcon1(R.drawable.ic_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchFeatureActivity.class));
            }
        });
        titleBarLayout.setRightIcon2(R.drawable.ic_notification, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected FeatureView getBaseView() {
        return this;
    }

    //setup recycler view
    private void setUpRecyclerView(View view) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        SectionAdapter = new SectionRecyclerViewAdapter(requireContext(), mSectionModelArrayList);
        recyclerView.setAdapter(SectionAdapter);
    }

    private void populateRecyclerView(List<FeatureBean> featureBeans) {

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

            FeatureBean parentFeature = null;
            for (FeatureBean parentBean : featureBeans) {
                if (parentBean.getFunctionKey().equals(featureBean.getFatherFunctionKey())) {
                    parentFeature = parentBean;
                    break;
                }
            }

            if (parentFeature == null) {
                continue;
            }

            SectionModel section = null;
            for (SectionModel existingSection : mSectionModelArrayList) {
                if (existingSection.getTitle().equals(parentFeature.getFunctionName())) {
                    section = existingSection;
                    break;
                }
            }

            if (section == null) {
                section = new SectionModel(parentFeature.getFunctionName(), new ArrayList<>());
                mSectionModelArrayList.add(section);
            }

            section.getItems().add(featureBean);
        }

        SectionAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void getUserInfoSuccess(UserInfoBean userInfoBean) {
        currentFactoryId = userInfoBean.getFactoryId();
        mPresenter.getFactories();
    }

    @Override
    public void getUserInfoFailed(String err) {
        showErrorMessage(err);
    }

    @Override
    public void getFactorySuccess(List<FactoryBean> factoryBeans) {
        mFactoryBeansList = factoryBeans;
        setTabLayout();
        mPresenter.getAccountNavigation();

    }

    @Override
    public void getFactoryFailed(String err) {
        showErrorMessage(err);
    }


    private void setTabLayout() {
        tabLayout.removeAllTabs();

        for (FactoryBean factoryBean : mFactoryBeansList) {
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
                mPresenter.changeFactory(selectedFactory.getFactoryId());
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
    public void getAccountNavationSuccess(List<FeatureBean> featureBean) {
        populateRecyclerView(featureBean);
    }

    @Override
    public void getAccountNavationFailed(String err) {
        showErrorMessage(err);
    }


    @Override
    public void changeFactorySuccess() {
        mSectionModelArrayList.clear();
        mPresenter.getAccountNavigation();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void changeFactoryFailed(String err) {
        showErrorMessage(err);
    }


    @Override
    public void onApiServiceUpdated(ApiService apiService) {
        mApiService = apiService;
    }
}