package com.delta.mobileplatform.bean;

public class FeatureOldBean {
    private String title;
    private int iconRes;
    private String sectionTitle;
    public FeatureOldBean(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;

    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }
}