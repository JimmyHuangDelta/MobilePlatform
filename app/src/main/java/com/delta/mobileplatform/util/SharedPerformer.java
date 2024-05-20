package com.delta.mobileplatform.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SharedPerformer {

    private SharedPreferences sharedPreferences;

    private static final String SEARCH_HISTORY_KEY = "search_history";
    private static final int MAX_SEARCH_HISTORY_SIZE = 7;
    private SharedPreferences.Editor editor;
    private static SharedPerformer instance;

    public SharedPerformer(Context context) {
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new SharedPerformer(context.getApplicationContext());
        }
    }

    public static synchronized SharedPerformer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharedPerformer is not initialized. Call initialize() first.");
        }
        return instance;
    }

    // 存储字符串值
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void saveStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public Set getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<>());
    }

    public void removeValue(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void saveSearchRecord(String searchItem) {

        Set<String> list = sharedPreferences.getStringSet(SEARCH_HISTORY_KEY, null);
        Set<String> searchHistory = new TreeSet<String>();
        if (searchItem != "") {
            if (list != null) {
                for (String each : list) {
                    searchHistory.add(each);
                }
            }
        }
        searchHistory.add(searchItem);
        editor.putStringSet(SEARCH_HISTORY_KEY, searchHistory);

        editor.apply();
    }


    public Set<String> getSearchHistory() {
        return sharedPreferences.getStringSet(SEARCH_HISTORY_KEY, new HashSet<>());
    }

    public void clearSearchHistory() {
        editor.remove(SEARCH_HISTORY_KEY);
        editor.apply();
    }
}