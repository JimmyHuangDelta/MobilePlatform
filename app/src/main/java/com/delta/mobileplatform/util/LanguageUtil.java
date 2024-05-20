package com.delta.mobileplatform.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * 語言切換
 */
public class LanguageUtil {

    private static final String PREF_LANGUAGE = "pref_language";
    private Context mContext;

    private static LanguageUtil instance;
    private SharedPreferences sharedPreferences;

    public LanguageUtil(Context context) {
        mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_LANGUAGE, Context.MODE_PRIVATE);
    }

    public void saveLanguage(String languageCode) {
        SharedPerformer.getInstance().saveString(PREF_LANGUAGE, languageCode);
    }

    public String getPrefLanguage() {

        if (SharedPerformer.getInstance().getString(PREF_LANGUAGE).isEmpty()) {
            return "zh_tw";
        } else {
            return SharedPerformer.getInstance().getString(PREF_LANGUAGE);
        }
    }


    public void applyLanguage(String languageCode) {
        Locale locale;
        switch (languageCode) {
            case "en":
                locale = new Locale("en");
                break;
            case "zh_tw":
                locale = new Locale("zh", "tw");
                break;
            case "zh_cn":
                locale = new Locale("zh", "en");
                break;
            default:
                locale = new Locale("zh", "tw");
        }

        Locale.setDefault(locale);
        saveLanguage(languageCode);
        Resources resources = mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public enum LanguageObj {
        ZH_CN("zh_cn", "简体中文"),
        ZH_TW("zh_tw", "繁體中文"),
        EN("en", "ENGLISH");

        private final String code;
        private final String name;

        LanguageObj(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static LanguageObj getByCode(String code) {
            for (LanguageObj language : LanguageObj.values()) {
                if (language.code.equals(code)) {
                    return language;
                }
            }
            return null;
        }
    }


}
