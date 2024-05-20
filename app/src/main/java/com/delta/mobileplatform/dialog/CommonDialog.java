package com.delta.mobileplatform.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.util.LanguageUtil;

public class CommonDialog {

    public static void showLanguageDialog(Context context, LanguageUtil languageUtilInstance, final LanguageDialogListener listener) {
        String language = languageUtilInstance.getPrefLanguage();
        LanguageUtil.LanguageObj currentLanguage = LanguageUtil.LanguageObj.getByCode(language);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.language_dialog, null);
        builder.setView(dialogView);

        TextView tvCurrentLanguage = dialogView.findViewById(R.id.tv_current_language);
        TextView btnChinese = dialogView.findViewById(R.id.btn_language_chinese);
        TextView btnTraditional = dialogView.findViewById(R.id.btn_language_traditional);
        TextView btnEnglish = dialogView.findViewById(R.id.btn_language_english);

        tvCurrentLanguage.setText(currentLanguage.getName());

        setOptionColor(context, btnChinese, currentLanguage);
        setOptionColor(context, btnTraditional, currentLanguage);
        setOptionColor(context, btnEnglish, currentLanguage);
        final AlertDialog dialog = builder.create();
        dialog.show();

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLanguageSelected("zh_cn");
                }
                dialog.dismiss();
            }
        });

        btnTraditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLanguageSelected("zh_tw");
                }
                dialog.dismiss();
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLanguageSelected("en");
                }
                dialog.dismiss();
            }
        });
    }

    public interface LanguageDialogListener {
        void onLanguageSelected(String languageCode);
    }

    private static void setOptionColor(Context context, TextView textView, LanguageUtil.LanguageObj currentLanguage) {
        if (textView.getText().toString().equals(currentLanguage.getName())) {
            int color = ContextCompat.getColor(context, R.color.night_text_primary_blue_color);
            textView.setTextColor(color);
        }
    }
}
