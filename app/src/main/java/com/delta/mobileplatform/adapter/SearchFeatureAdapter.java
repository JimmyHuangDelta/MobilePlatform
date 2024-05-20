package com.delta.mobileplatform.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.activity.WebActivity;
import com.delta.mobileplatform.bean.FeatureBean;

import java.util.List;

public class SearchFeatureAdapter extends RecyclerView.Adapter<SearchFeatureAdapter.SearchFeatureViewHolder> {
    private List<FeatureBean> mDataList;
    private Context mContext;

    public SearchFeatureAdapter( Context context, List<FeatureBean> dataList) {
        mDataList = dataList;
        this.mContext = context;
    }
    @NonNull
    @Override
    public SearchFeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_search_row_layout, parent, false);
        return new SearchFeatureViewHolder(view);
    }

    // 绑定数据到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull SearchFeatureViewHolder holder, int position) {
        FeatureBean item = mDataList.get(position);
        holder.textView.setText(item.getFunctionName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("url", item.getMenuUrl());
                mContext.startActivity(intent);
                Toast.makeText(mContext, "網址:" + item.getMenuUrl(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    public static class SearchFeatureViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public SearchFeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_label);
            imageView = itemView.findViewById(R.id.imageView_icon);
        }
    }
}