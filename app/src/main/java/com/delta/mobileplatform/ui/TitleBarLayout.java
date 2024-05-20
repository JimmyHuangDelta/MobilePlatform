package com.delta.mobileplatform.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.delta.mobileplatform.R;

import java.util.List;

public class TitleBarLayout extends RelativeLayout {
    private TextView mTitleTextView, mcloseTv;
    private ImageView mLeftIcon1, mLeftIcon2, mRightIcon1, mRightIcon2;

    public TitleBarLayout(Context context) {
        super(context);
        init(context);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.title_bar_layout, this, true);
        mTitleTextView = findViewById(R.id.title_text_view);
        mRightIcon1 = findViewById(R.id.right_first_icon);
        mRightIcon2 = findViewById(R.id.right_second_icon);
        mLeftIcon1 = findViewById(R.id.left_first_icon);
        mLeftIcon2 = findViewById(R.id.left_second_icon);
        mcloseTv = findViewById(R.id.close_tv);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int title) {
        mTitleTextView.setText(title);
    }


    public void setCloseTv(String title, OnClickListener listener) {
        if (mcloseTv != null) {
            mcloseTv.setText(title);
            mcloseTv.setOnClickListener(listener);
        }
    }

    public TextView getCloseTv() {
        return mcloseTv;
    }

    public void setLeftIcon1(int resId, OnClickListener listener) {
        if (mLeftIcon1 != null) {
            mLeftIcon1.setImageResource(resId);
            mLeftIcon1.setOnClickListener(listener);
        }
    }

    public ImageView getLeftIcon1() {
        return mLeftIcon1;
    }

    public void setLeftIcon2(int resId, OnClickListener listener) {
        if (mLeftIcon2 != null) {
            mLeftIcon2.setImageResource(resId);
            mLeftIcon2.setOnClickListener(listener);
        }
    }

    public ImageView getLeftIcon2() {
        return mLeftIcon2;
    }


    public void setRightIcon1(int resId, OnClickListener listener) {
        if (mRightIcon1 != null) {
            mRightIcon1.setImageResource(resId);
            mRightIcon1.setOnClickListener(listener);
        }
    }

    public ImageView getRightIcon1() {
        return mRightIcon1;
    }

    public void setRightIcon2(int resId, OnClickListener listener) {
        if (mRightIcon2 != null) {
            mRightIcon2.setImageResource(resId);
            mRightIcon2.setOnClickListener(listener);
        }
    }

    public ImageView getRightIcon2() {
        return mRightIcon2;
    }


}