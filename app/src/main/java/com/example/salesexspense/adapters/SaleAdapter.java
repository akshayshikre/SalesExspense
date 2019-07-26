package com.example.salesexspense.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.salesexspense.R;
import com.example.salesexspense.SalesReportFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {
    HashMap<String, SalesReportFragment.SaleData> saleDataHashMap;
    ArrayList<String> saleDataHashMapKeys;
    private Context ctx;
    private EditItemClick editItemClickCallback;

    public SaleAdapter(Context ctx ,HashMap<String,SalesReportFragment.SaleData> saleDataHashMap, ArrayList<String> saleDataHashMapKeys) {
        this.ctx = ctx;
        this.saleDataHashMap = saleDataHashMap;
        this.saleDataHashMapKeys = saleDataHashMapKeys;
    }

    public interface EditItemClick {
        void editItemClickOnClick(View v, int position,SalesReportFragment.SaleData saleData);
    }



    public void setEditItemClickListener(EditItemClick listener) {
        this.editItemClickCallback = listener;
    }

    @NonNull
    @Override
    public SaleAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.sales_report_row, null);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleAdapter.SaleViewHolder saleViewHolder, final int i) {

        saleViewHolder.saleId.setText(saleDataHashMap.get(saleDataHashMapKeys.get(i)).getSaleId());
        saleViewHolder.saleDate.setText(saleDataHashMap.get(saleDataHashMapKeys.get(i)).getSaleDate());
        saleViewHolder.saleType.setText(saleDataHashMap.get(saleDataHashMapKeys.get(i)).getSaleType());
        saleViewHolder.saleAmount.setText(saleDataHashMap.get(saleDataHashMapKeys.get(i)).getSaleAmount());
        String saleData = "";
        int k = 0;
        ArrayList<SalesReportFragment.SaleItem> saleItemsArray = saleDataHashMap.get(saleDataHashMapKeys.get(i)).getSaleItemsArray();
        for(int j = 0; saleItemsArray.size()>j;j++){
                if(saleItemsArray.size()-1 == j){
                    saleData += ""+(k+1)+") Name: "+saleItemsArray.get(j).getItemName() + "\n"
                            + " Quantity: "+saleItemsArray.get(j).getItemQuantity()
                            + "     Amount: "+saleItemsArray.get(j).getItemAmount() +".\n";
                    ++k;
                }else{
                    saleData += ""+(k+1)+") Name: "+saleItemsArray.get(j).getItemName() + "\n"
                            + " Quantity: "+saleItemsArray.get(j).getItemQuantity()
                            + " Amount: "+saleItemsArray.get(j).getItemAmount() +".\n\n";
                    ++k;
                }
        }

        saleViewHolder.saleData.setText(saleData);
        saleViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editItemClickCallback != null) {
                    editItemClickCallback.editItemClickOnClick(v, i,saleDataHashMap.get(saleDataHashMapKeys.get(i)));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return saleDataHashMapKeys.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {

        TextView saleId,saleDate , saleType ,saleAmount, saleData ;
        Button btnEdit;
        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            saleId = itemView.findViewById(R.id.tvSaleId);
            saleDate = itemView.findViewById(R.id.tvSaleDate);
            saleType = itemView.findViewById(R.id.tvSaleType);
            saleAmount = itemView.findViewById(R.id.tvSaleAmount);
            saleData = itemView.findViewById(R.id.tvSaleData);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
