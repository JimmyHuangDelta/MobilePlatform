package com.delta.mobileplatform.fragment;

import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.presenter.BasePresenter;
import com.delta.mobileplatform.view.IBaseView;

public abstract class BaseFragment<P extends BasePresenter, V extends IBaseView> extends Fragment implements IBaseView {
    protected Context mContext;
    protected View mView;
    protected P mPresenter;
    private ProgressBar mProgressBar;
    private static final String KEY_PRESENTER_STATE = "presenter_state";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setRetainInstance(false);
        mPresenter = initPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(getLayoutRes(), container, false);

        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        frameLayout.setLayoutParams(layoutParams);
        mProgressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
        frameLayout.addView(mProgressBar);
        ViewGroup contentView = getActivity().findViewById(android.R.id.content);
        contentView.addView(frameLayout);
//        ApiClient.getInstance().registerObserver(this);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initData();
        initView(mView);
        initListener();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void showLoading() {

        if (mProgressBar != null && !mProgressBar.isShown()) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void hideLoading() {

        if (mProgressBar != null && mProgressBar.isShown()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    protected void showMessage(String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_text,
                        requireActivity().findViewById(R.id.custom_toast_container));

                TextView text = layout.findViewById(R.id.custom_toast_text);
                text.setText(message);

                Toast toast = new Toast(requireContext());
//        toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

    }

    protected void showErrorMessage(String message) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_error_text,
                        requireActivity().findViewById(R.id.custom_toast_container));

                TextView text = layout.findViewById(R.id.custom_toast_text);
                text.setText(message);

                Toast toast = new Toast(requireContext());
//        toast.setGravity(Gravity.BOTTOM, 0, 100);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

    }

    protected abstract int getLayoutRes();

    protected abstract P initPresenter();

    protected abstract void initView(View view);

    protected abstract void initData();

    protected abstract void initListener();

    protected abstract V getBaseView();
}