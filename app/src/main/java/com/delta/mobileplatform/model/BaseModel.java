package com.delta.mobileplatform.model;

import com.delta.mobileplatform.service.ModelCallback;

import java.util.Map;

/**
 * 该类抽象出不同业务获取数据的通用方法
 */
public abstract class BaseModel<T> {
    //数据请求参数
    protected String[] mParams;

    /**
     * 设置数据请求参数
     *
     * @param args 参数数组
     */
    public BaseModel params(String... args) {
        mParams = args;
        return this;
    }

    /**
     * 添加Callback并执行数据请求，具体的数据请求由子类实现
     */
//    public abstract void execute(ModelCallback<T> modelCallback);

    /**
     * 执行Get网络请求，此类看需求由自己选择写与不写
     */
    public void requestGetApi(String url, ModelCallback<T> modelCallback) {
        //这里写具体的网络请求
    }

    //执行Post网络请求，此类看需求由自己选择写与不写
    public void requestPostApi(String url, Map params, ModelCallback<T> modelCallback) {
        //这里写具体的网络请求
    }
}