package com.delta.mobileplatform.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.delta.mobileplatform.R;

import com.delta.mobileplatform.fragment.PreferenceSettingFragment;

import com.delta.mobileplatform.fragment.UserInfoFragment;

public class SettingPageAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public SettingPageAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserInfoFragment();
            case 1:
                return new PreferenceSettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }



//    hardcode?
    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.accountInfo);
            case 1:
                return mContext.getResources().getString(R.string.preference);
            default:
                return null;
        }
    }
}