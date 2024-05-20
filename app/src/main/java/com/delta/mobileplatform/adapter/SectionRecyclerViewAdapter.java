package com.delta.mobileplatform.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delta.mobileplatform.R;
import com.delta.mobileplatform.model.SectionModel;

import java.util.ArrayList;


public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {
    private Context context;
    private ArrayList<SectionModel> sectionModelArrayList;
    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionLabel, showAllButton;
        private RecyclerView itemRecyclerView;
        private boolean isContentVisible = true;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
//            showAllButton = (TextView) itemView.findViewById(R.id.section_show_all_button);
            itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);

            sectionLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isContentVisible = !isContentVisible;
                    itemRecyclerView.setVisibility(isContentVisible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    public SectionRecyclerViewAdapter(Context context, ArrayList<SectionModel> sectionModelArrayList) {
        this.context = context;
//        this.recyclerViewType = recyclerViewType;
        this.sectionModelArrayList = sectionModelArrayList;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_layout, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        final SectionModel sectionModel = sectionModelArrayList.get(position);
        holder.sectionLabel.setText(sectionModel.getTitle());
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        holder.itemRecyclerView.setVisibility(holder.isContentVisible ? View.VISIBLE : View.GONE);

        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        holder.itemRecyclerView.setLayoutManager(gridLayoutManager);

        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context, sectionModel.getItems());
        holder.itemRecyclerView.setAdapter(adapter);


        //show toast on click of show all button
//        holder.showAllButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "You clicked on Show All of : " + sectionModel.getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return sectionModelArrayList.size();
    }


}