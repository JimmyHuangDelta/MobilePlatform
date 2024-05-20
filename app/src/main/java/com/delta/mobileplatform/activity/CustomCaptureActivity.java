package com.delta.mobileplatform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.Toast;


import com.journeyapps.barcodescanner.CaptureActivity;

/**
 * 客製相機掃描頁，原先的不支援條碼槍
 * hiddenEditText解決條碼槍沒有焦點及輸入位置問題
 */

public class CustomCaptureActivity extends CaptureActivity {
    private StringBuilder barcodeStringBuilder = new StringBuilder();
    private EditText hiddenEditText;
    private static final String SCAN_RESULT = "SCAN_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup content = findViewById(android.R.id.content);
        ViewGroup root = (ViewGroup) content.getChildAt(0);

        hiddenEditText = new EditText(this);
        hiddenEditText.setSingleLine();
        hiddenEditText.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        root.addView(hiddenEditText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        hiddenEditText.requestFocus();
        hiddenEditText.setAlpha(0);
    }

    private void handleBarcodeScan() {
        String scannedData = hiddenEditText.getText().toString();
        if (isValidBarcode(scannedData)) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(SCAN_RESULT, scannedData);
            setResult(RESULT_OK, resultIntent);
            hiddenEditText.setText("");

            finish();
        } else {
            hiddenEditText.setText("");
            Toast.makeText(this, "Invalid barcode", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hiddenEditText.clearFocus();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 66) {  //精聯的pda為66，其他目前不確定
            handleBarcodeScan();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    private boolean isValidBarcode(String barcode) {
        return !TextUtils.isEmpty(barcode);
    }
}