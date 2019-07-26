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
import com.example.salesexspense.models.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    HashMap<String, Expense> expenseDataHashMap;
    ArrayList<String> expenseDataHashMapKeys;
    List<String> distinctCategoriesList;

    private Context ctx;

    public ExpenseAdapter(Context ctx ,HashMap<String,Expense> expenseDataHashMap, ArrayList<String> expenseDataHashMapKeys, List<String> distinctCategoriesList) {
        this.ctx = ctx;
        this.expenseDataHashMap = expenseDataHashMap;
        this.expenseDataHashMapKeys = expenseDataHashMapKeys;
        this.distinctCategoriesList = distinctCategoriesList;
    }

    public interface EditItemClick {
        void editItemClickOnClick(View v, int position,Expense expense,/* HashMap<String,Expense> expenseDataHashMap, ArrayList<String> expenseDataHashMapKeys,*/ List<String> categories);
    }

    private EditItemClick editItemClickCallback;

    public void setEditItemClickListener(EditItemClick listener) {
        this.editItemClickCallback = listener;
    }


    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.expense_report_row, null);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder expenseViewHolder, final int i) {
        expenseViewHolder.tvExpenseId.setText(""+expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).getId());
        expenseViewHolder.tvExpenseDate.setText(expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).getDate());
        expenseViewHolder.tvExpenseCategory.setText(expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).getCategory());
        expenseViewHolder.tvExpenseAmount.setText(expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).getAmount());
        expenseViewHolder.tvExpenseData.setText(expenseDataHashMap.get(expenseDataHashMapKeys.get(i)).getDescription());

        expenseViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editItemClickCallback != null) {
                    editItemClickCallback.editItemClickOnClick(v, i,expenseDataHashMap.get(expenseDataHashMapKeys.get(i)),distinctCategoriesList  );
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenseDataHashMap.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvExpenseId, tvExpenseDate , tvExpenseCategory ,tvExpenseAmount, tvExpenseData ;
        Button btnEdit;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseId = itemView.findViewById(R.id.tvExpenseId);
            tvExpenseDate = itemView.findViewById(R.id.tvExpenseDate);
            tvExpenseCategory = itemView.findViewById(R.id.tvExpenseCategory);
            tvExpenseAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvExpenseData = itemView.findViewById(R.id.tvExpenseData);
            btnEdit = itemView.findViewById(R.id.btnEdit);

        }
    }
}
