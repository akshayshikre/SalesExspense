package com.example.salesexspense.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.salesexspense.R;
import com.example.salesexspense.models.MenuCategory;

import java.util.List;

public class RecordSaleAdapter extends RecyclerView.Adapter<RecordSaleAdapter.RecordSaleViewHolder> {
    private static final int UNSELECTED = -1;
    private List<MenuCategory> menuCategories;
    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;

    private Context ctx;

    public RecordSaleAdapter(Context ctx, RecyclerView recyclerView, List<MenuCategory> menuCategories) {
        this.ctx = ctx;
        this.recyclerView = recyclerView;
        this.menuCategories = menuCategories;
    }



    @NonNull
    @Override
    public RecordSaleAdapter.RecordSaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.record_sales_row, null);
        return new RecordSaleAdapter.RecordSaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordSaleAdapter.RecordSaleViewHolder recordSaleViewHolder, final int i) {
        recordSaleViewHolder.bind();
    }


    @Override
    public int getItemCount() {
        return menuCategories.size();
    }

    public class RecordSaleViewHolder extends RecyclerView.ViewHolder  {
//        ExpandableLayout expandableLayout;
        TextView expandButton;

        public RecordSaleViewHolder(View itemView) {
            super(itemView);
//            expandableLayout = itemView.findViewById(R.id.expandable_layout);
//            expandableLayout.setInterpolator(new OvershootInterpolator());
//            expandableLayout.setOnExpansionUpdateListener(this);
            expandButton = itemView.findViewById(R.id.expand_button);
//            expandButton.setOnClickListener(this);
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
            expandButton.setText(menuCategories.get(position).getName() + ". Tap to expand");
            expandButton.setSelected(isSelected);
//            expandableLayout.setExpanded(isSelected, false);
        }


    }
}
