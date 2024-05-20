package com.delta.mobileplatform.presenter;


import com.delta.mobileplatform.view.IBaseView;

import java.lang.ref.WeakReference;

public class BasePresenter<V extends IBaseView> {
    private WeakReference<V> mViewRef;

    public void attachView(V baseView) {
        mViewRef = new WeakReference<>(baseView);
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public V getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }

}