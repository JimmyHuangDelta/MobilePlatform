package com.delta.mobileplatform.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.activity.WebActivity;
import com.delta.mobileplatform.bean.FeatureBean;

import java.util.List;

import android.widget.Toast;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemLabel;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemLabel = (TextView) itemView.findViewById(R.id.item_label);
        }
    }

    private Context context;
    private List<FeatureBean> featureBeans;
    public ItemRecyclerViewAdapter(Context context, List<FeatureBean> featureBeans) {
        this.context = context;
        this.featureBeans = featureBeans;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_row_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        FeatureBean featureBean = featureBeans.get(position);
        holder.itemLabel.setText(featureBean.getFunctionName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", featureBean.getMenuUrl());
                intent.putExtra("title", featureBean.getFunctionName());
//                intent.putExtra("url", "http://10.139.32.254:8022");
                        context.startActivity(intent);
                Toast.makeText(context, "網址:" + featureBean.getMenuUrl(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return featureBeans.size();
    }

}