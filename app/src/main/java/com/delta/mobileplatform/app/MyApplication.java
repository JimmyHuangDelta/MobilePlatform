package com.delta.mobileplatform.app;

import android.app.Application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.delta.mobileplatform.util.SharedPerformer;
import com.delta.mobileplatform.util.TokenManager;

import java.util.Locale;


import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
public class MyApplication extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        SharedPerformer.initialize(context);
        TokenManager.initialize(context);

        Locale myLocale = new Locale("zh_CN");
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        Locale.setDefault(myLocale);
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);

    }

    public static Context getAppContext() {
        return context;
    }
}