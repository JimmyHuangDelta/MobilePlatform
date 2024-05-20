package com.delta.mobileplatform.web.controller.localStorage.JavaLocalStorage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.delta.mobileplatform.web.controller.localStorage.DBRepositoryImpl;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.entity.DeltaLocalStorage;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.entity.DeltaLocalStorage;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class DbRepositoryCaller {

    private DbRepository dbRepository;

    @Inject
    public DbRepositoryCaller(DbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    public void callSetMethod(String domain, String key, String value) {
        Continuation<Object> continuation = new Continuation<Object>() {
            @Override
            public void resumeWith(@NonNull Object o) {

            }
            public void resume(Object result) {

                if (result instanceof DeltaLocalStorage) {
                    DeltaLocalStorage deltaLocalStorage = (DeltaLocalStorage) result;
                } else {
                }
            }


            public void resumeWithException(Throwable throwable) {
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        };

        dbRepository.set(domain, key, value, continuation);
    }
    public void callGetMethod(String domain, String key, GetMethodCallback callback) {
        Continuation<DeltaLocalStorage> continuation = new Continuation<DeltaLocalStorage>() {
            @Override
            public void resumeWith(@NonNull Object o) {
                if (o instanceof DeltaLocalStorage) {
                    DeltaLocalStorage deltaLocalStorage = (DeltaLocalStorage) o;
                    callback.onResult(deltaLocalStorage);
                } else {
                }
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        };

        dbRepository.get(domain, key, continuation);
    }



    public void callGetAllMethod(String domain) {
        Continuation<? super List<? extends DeltaLocalStorage>> continuation = new Continuation<List<? extends DeltaLocalStorage>>() {
            @Override
            public void resumeWith(@NonNull Object o) {

            }


            public void resumeWith(@NonNull List<? extends DeltaLocalStorage> resultList) {
            }


            public void resume(List<? extends DeltaLocalStorage> resultList) {
            }


            public void resumeWithException(Throwable throwable) {
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        };

        dbRepository.getAll(domain, continuation);
    }

    public void callClearMethod(String domain) {
        Continuation<Object> continuation = new Continuation<Object>() {
            @Override
            public void resumeWith(@NonNull Object o) {
            }


            public void resume(Object o) {
            }


            public void resumeWithException(Throwable throwable) {
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        };

        dbRepository.clear(domain, continuation);
    }

    public void callRemoveMethod(String domain, String key) {
        Continuation<Object> continuation = new Continuation<Object>() {
            @Override
            public void resumeWith(@NonNull Object o) {
                // 移除成功
            }


            public void resume(Object o) {
            }


            public void resumeWithException(Throwable throwable) {
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        };

        dbRepository.remove(domain, key, continuation);
    }



    public interface GetMethodCallback {
        void onResult(DeltaLocalStorage result);

        void onResult(List<DeltaLocalStorage> result);
    }

    // 可以继续实现其他方法
}
