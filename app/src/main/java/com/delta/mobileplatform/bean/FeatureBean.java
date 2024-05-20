package com.delta.mobileplatform.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeatureBean {
    private String fatherFunctionKey;
    private String functionKey;
    private String functionId;
    private String functionName;
    private String menuUrl;
    private String offlineMode;
    private QueryParamsBean queryParams;

    // Getter and Setter methods for all properties

    public String getFatherFunctionKey() {
        return fatherFunctionKey;
    }

    public void setFatherFunctionKey(String fatherFunctionKey) {
        this.fatherFunctionKey = fatherFunctionKey;
    }

    public String getFunctionKey() {
        return functionKey;
    }

    public void setFunctionKey(String functionKey) {
        this.functionKey = functionKey;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getOfflineMode() {
        return offlineMode;
    }

    public void setOfflineMode(String offlineMode) {
        this.offlineMode = offlineMode;
    }

    public QueryParamsBean getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(QueryParamsBean queryParams) {
        this.queryParams = queryParams;
    }

    // Inner class for queryParams
    public static class QueryParamsBean {
        private String emptyLayout;
        private String fullScreen;
        private String functionId;

        // Getter and Setter methods for queryParams properties

        public String getEmptyLayout() {
            return emptyLayout;
        }

        public void setEmptyLayout(String emptyLayout) {
            this.emptyLayout = emptyLayout;
        }

        public String getFullScreen() {
            return fullScreen;
        }

        public void setFullScreen(String fullScreen) {
            this.fullScreen = fullScreen;
        }

        public String getFunctionId() {
            return functionId;
        }

        public void setFunctionId(String functionId) {
            this.functionId = functionId;
        }
    }


    public static List<FeatureBean> parseJSON(String jsonString) {
        List<FeatureBean> featureBeans = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FeatureBean functionItem = new FeatureBean();
                functionItem.setFatherFunctionKey(jsonObject.optString("fatherFunctionKey"));
                functionItem.setFunctionKey(jsonObject.optString("functionKey"));
                functionItem.setFunctionId(jsonObject.optString("functionId"));
                functionItem.setFunctionName(jsonObject.optString("functionName"));
                functionItem.setMenuUrl(jsonObject.optString("menuUrl"));
                functionItem.setOfflineMode(jsonObject.optString("offlineMode"));

                JSONObject queryParamsObject = jsonObject.optJSONObject("queryParams");
                if (queryParamsObject != null) {
                    QueryParamsBean queryParams = new QueryParamsBean();
                    queryParams.setEmptyLayout(queryParamsObject.optString("emptyLayout"));
                    queryParams.setFullScreen(queryParamsObject.optString("fullScreen"));
                    queryParams.setFunctionId(queryParamsObject.optString("functionId"));
                    functionItem.setQueryParams(queryParams);
                }

                featureBeans.add(functionItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return featureBeans;
    }
}