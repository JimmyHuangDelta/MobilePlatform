package com.delta.mobileplatform.fragment;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.adapter.SettingPageAdapter;
import com.delta.mobileplatform.presenter.SettingPagePresenter;
import com.delta.mobileplatform.ui.TitleBarLayout;
import com.delta.mobileplatform.view.SettingPageView;
import com.google.android.material.tabs.TabLayout;

public class SettingPageFragment extends BaseFragment<SettingPagePresenter, SettingPageView> implements SettingPageView {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TitleBarLayout titleBarLayout;
    private SettingPageAdapter pagerAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_setting_page;
    }

    @Override
    protected void initView(View view) {
        tabLayout = view.findViewById(R.id.tab_Layout);
        viewPager = view.findViewById(R.id.viewPager);
        titleBarLayout = view.findViewById(R.id.edit_title_bar);
        titleBarLayout.setTitle(R.string.settings);
        titleBarLayout.getRightIcon1().setVisibility(View.VISIBLE);

        titleBarLayout.setRightIcon1(R.drawable.ic_notification, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pagerAdapter = new SettingPageAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected SettingPagePresenter initPresenter() {
        return new SettingPagePresenter();
    }

    @Override
    protected SettingPageView getBaseView() {
        return this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
