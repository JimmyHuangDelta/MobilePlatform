package com.delta.mobileplatform.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.client.ApiClient;
import com.delta.mobileplatform.client.ApiServiceObserver;
import com.delta.mobileplatform.web.controller.localStorage.DbRepository;
import com.delta.mobileplatform.web.controller.localStorage.LocalStorageDao;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.util.Permission;
import com.delta.mobileplatform.view.IBaseView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseActivity extends AppCompatActivity implements IBaseView, ApiServiceObserver {
    private ProgressDialog mProgressDialog;
    protected static ProgressBar mProgressBar;
    @Inject
    LocalStorageDao javaLocalStorageDao;
    @Inject
    DbRepository javaDbRepository;

    private Context context;
    private BasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        setupBase();
    }


    private void setupBase() {

        context = getBaseContext();
        //讀取bar
        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        mProgressBar.setIndeterminate(true);

        FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.setLayoutParams(layoutParams);

        mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
        frameLayout.addView(mProgressBar);
        ViewGroup contentView = findViewById(android.R.id.content);
        contentView.addView(frameLayout);
        //

//        初始化presenter
        mPresenter = initPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }

        initData();
        ApiClient.getInstance().registerObserver(this);
        initView();
        initListener();
        Permission.checkPermission(this);
    }

    //
    protected abstract BasePresenter initPresenter();
    protected abstract int getLayoutResourceId();

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initListener();

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Close the soft keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                // Clear focus from EditText
                currentFocus.clearFocus();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        ApiClient.getInstance().unregisterObserver(this);
    }


    //    顯示讀取框
    @Override
    public void showLoading() {
        if (mProgressBar != null && !mProgressBar.isShown()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);

                }
            });
        }

    }

    //    隱藏讀取框
    @Override
    public void hideLoading() {
        if (mProgressBar != null && mProgressBar.isShown()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    }


//    提示訊息
    protected void showMessage(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_text,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.custom_toast_text);
        text.setText(message);
        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

//    錯誤提示
    protected void showErrorMessage(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_error_text,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.custom_toast_text);
        text.setText(message);
        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}