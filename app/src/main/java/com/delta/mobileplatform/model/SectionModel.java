package com.delta.mobileplatform.model;

import com.delta.mobileplatform.bean.FeatureBean;

import java.util.List;

public class SectionModel {
    private String title;
    private List<FeatureBean> items;

    public SectionModel(String title, List<FeatureBean> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public List<FeatureBean> getItems() {
        return items;
    }
}