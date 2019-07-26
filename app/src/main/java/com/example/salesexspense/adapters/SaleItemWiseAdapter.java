package com.example.salesexspense.adapters;


        import android.content.Context;
        import android.support.annotation.NonNull;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.example.salesexspense.R;
        import com.example.salesexspense.SalesReportFragment;

        import java.util.ArrayList;
        import java.util.HashMap;

public class SaleItemWiseAdapter extends RecyclerView.Adapter<SaleItemWiseAdapter.SaleViewHolder> {
    HashMap<String, SalesReportFragment.SaleDataItemWise> saleDataItemWiseHashMap;
    ArrayList<String> saleDataItemWiseHashMapKeys;
    private Context ctx;

    public SaleItemWiseAdapter(Context ctx ,HashMap<String,SalesReportFragment.SaleDataItemWise> saleDataItemWiseHashMap, ArrayList<String> saleDataItemWiseHashMapKeys) {
        this.ctx = ctx;
        this.saleDataItemWiseHashMap = saleDataItemWiseHashMap;
        this.saleDataItemWiseHashMapKeys = saleDataItemWiseHashMapKeys;
    }

    @NonNull
    @Override
    public SaleItemWiseAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.sales_report_row_itemwise, null);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder saleViewHolder, int i) {
        saleViewHolder.tvItemName.setText(saleDataItemWiseHashMap.get(saleDataItemWiseHashMapKeys.get(i)).getItemName());
        saleViewHolder.tvItemCode.setText(saleDataItemWiseHashMap.get(saleDataItemWiseHashMapKeys.get(i)).getItemCode());
        saleViewHolder.tvItemPrice.setText(saleDataItemWiseHashMap.get(saleDataItemWiseHashMapKeys.get(i)).getItemPrice());
        saleViewHolder.tvSaleItemsAmount.setText(saleDataItemWiseHashMap.get(saleDataItemWiseHashMapKeys.get(i)).getTotalItemSaleAmount());
        String saleData = "";
        int k = 0;
        ArrayList<SalesReportFragment.SaleItemItemWise> saleItemsArray = saleDataItemWiseHashMap.get(saleDataItemWiseHashMapKeys.get(i)).getSaleItemsArray();
        for(int j = 0; saleItemsArray.size()>j;j++){
            if(saleItemsArray.size()-1 == j){
                saleData += ""+(k+1)+") Sale Id: "+saleItemsArray.get(j).getSaleId() + " "+saleItemsArray.get(j).getSaleDate() + " Type: "+saleItemsArray.get(j).getSaleType() + "\n"
                        + "Quantity: "+saleItemsArray.get(j).getItemQuantity()
                        + "    Amount: "+saleItemsArray.get(j).getItemAmount() +".\n";
                ++k;
            }else{
                saleData += ""+(k+1)+") Sale Id: "+saleItemsArray.get(j).getSaleId() + " "+saleItemsArray.get(j).getSaleDate() + " Type: "+saleItemsArray.get(j).getSaleType() + "\n"
                        + "Quantity: "+saleItemsArray.get(j).getItemQuantity()
                        + "    Amount: "+saleItemsArray.get(j).getItemAmount() +".\n\n";
                ++k;
            }
        }

        saleViewHolder.tvSaleItemsData.setText(saleData);
    }



    @Override
    public int getItemCount() {
        return saleDataItemWiseHashMap.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemName,tvItemCode , tvItemPrice ,tvSaleItemsAmount, tvSaleItemsData ;
        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvSaleItemsAmount = itemView.findViewById(R.id.tvSaleItemsAmount);
            tvSaleItemsData = itemView.findViewById(R.id.tvSaleItemsData);
        }
    }
}
