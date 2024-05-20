package com.delta.mobileplatform.ui;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.delta.mobileplatform.R;

public class SearchBarLayout extends RelativeLayout {
    private ImageView mIvBack;
    private ImageView mIvClose;
    private EditText mEtSearch;

    public SearchBarLayout(Context context) {
        super(context);
        init(context);
    }

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.search_bar_layout, this, true);
        mIvBack = this.findViewById(R.id.iv_back);
        mIvClose = this.findViewById(R.id.iv_close);
        mEtSearch = this.findViewById(R.id.et_search);

        mEtSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showSoftKeyboard(mEtSearch);
                }
            }
        });
    }

    public void setSearchHint(String hint) {
        mEtSearch.setHint(hint);
    }

    public void setSearchTextChangeListener(TextWatcher watcher) {
        mEtSearch.addTextChangedListener(watcher);
    }


    public EditText getEtSearch() {

        return mEtSearch;
    }

    public ImageView getmIvBack() {

        return mIvBack;
    }

    public ImageView getmIvClose() {

        return mIvClose;
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}