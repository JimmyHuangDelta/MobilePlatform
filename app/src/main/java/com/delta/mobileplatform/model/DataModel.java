package com.delta.mobileplatform.model;


/**
 * 该类用于创建具体的业务Model
 */
public class DataModel {
    public static BaseModel createModel(Class clazz) {
        BaseModel model = null;
        try {
            model = (BaseModel) clazz.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return model;
    }
}