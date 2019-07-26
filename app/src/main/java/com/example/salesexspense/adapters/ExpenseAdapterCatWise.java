package com.example.salesexspense.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.salesexspense.R;
import com.example.salesexspense.models.Expense;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseAdapterCatWise extends RecyclerView.Adapter<ExpenseAdapterCatWise.ExpenseViewHolder> {
    HashMap<String,ArrayList<Expense>> expenseDataHashMap;
    ArrayList<String> expenseDataHashMapKeys;
    private Context ctx;

    public ExpenseAdapterCatWise(Context ctx , HashMap<String,ArrayList<Expense>>  expenseDataHashMap, ArrayList<String> expenseDataHashMapKeys) {
        this.ctx = ctx;
        this.expenseDataHashMap = expenseDataHashMap;
        this.expenseDataHashMapKeys = expenseDataHashMapKeys;
    }

    @NonNull
    @Override
    public ExpenseAdapterCatWise.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.expense_report_row_catwise, null);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapterCatWise.ExpenseViewHolder expenseViewHolder, int i) {
        expenseViewHolder.tvCatName.setText(expenseDataHashMapKeys.get(i));
        String data = "";
        int k = 0;
        int amount = 0;
        for(Expense e: expenseDataHashMap.get(expenseDataHashMapKeys.get(i))){
            if(k == expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).size()-1){
                data += ""+(++k)+") "+"Date: "+ e.getDate() + " Amount: " + e.getAmount() + "\n"
                        + "Description: "+e.getDescription() + "\n";
            }else{
                data += ""+(++k)+") "+"Date: "+ e.getDate() + " Amount: " + e.getAmount() + "\n"
                        + "Description: "+e.getDescription() + "\n\n";
            }
            amount += Integer.parseInt(e.getAmount());
        }
        expenseViewHolder.tvExpenseItemsData.setText(data);
        expenseViewHolder.tvCatAmount.setText(""+amount);

    }

    @Override
    public int getItemCount() {
        return expenseDataHashMap.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvCatName , tvExpenseItemsData ,tvCatAmount ;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            tvExpenseItemsData = itemView.findViewById(R.id.tvExpenseItemsData);
            tvCatAmount = itemView.findViewById(R.id.tvCatAmount);
        }
    }
}
