package com.delta.mobileplatform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.delta.mobileplatform.R;
import java.util.List;


public class SearchFeatureHistoryAdapter extends RecyclerView.Adapter<SearchFeatureHistoryAdapter.SearchFeatureHistoryViewHolder> {
    private List<String> mDataList;
    private Context mContext;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String searchText);
    }

    public SearchFeatureHistoryAdapter(Context context, List<String> dataList) {
        mDataList = dataList;
        mContext = context;
    }

    @NonNull
    @Override
    public SearchFeatureHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_search_history_row_layout, parent, false);
        return new SearchFeatureHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFeatureHistoryViewHolder holder, int position) {
        String item = mDataList.get(position);
        holder.textView.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class SearchFeatureHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public SearchFeatureHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_label);
            imageView = itemView.findViewById(R.id.imageView_icon);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}