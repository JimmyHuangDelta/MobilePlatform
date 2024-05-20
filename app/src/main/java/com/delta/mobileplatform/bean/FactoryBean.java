package com.delta.mobileplatform.bean;

public class FactoryBean {
    private String factoryId;
    private String factoryName;
    private String factoryDesc;

    public FactoryBean(String factoryId, String factoryName,String factoryDesc ) {
        this.factoryId = factoryId;
        this.factoryName = factoryName;
        this.factoryDesc = factoryDesc;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public String getFactoryDesc() {
        return factoryDesc;
    }
}