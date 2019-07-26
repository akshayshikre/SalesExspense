package com.example.salesexspense.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.salesexspense.MainActivity;
import com.example.salesexspense.R;
import com.example.salesexspense.SalesReportFragment;
import com.example.salesexspense.models.Item;

import java.util.ArrayList;
import java.util.List;

public class SaleItemEditAdapter extends RecyclerView.Adapter<SaleItemEditAdapter.SaleItemEditViewHolder> {
    ArrayList<SalesReportFragment.SaleItem> itemArrayList;
    List<Item> itemFullDataList;
    private Context ctx;
    private  EditItemOuantity editItemQuantityCallback;

    public SaleItemEditAdapter(Context ctx , ArrayList<SalesReportFragment.SaleItem> itemArrayList) {
        this.ctx = ctx;
        this.itemArrayList = itemArrayList;
        this.itemFullDataList = MainActivity.myAppDatabase.myDao().getItems();
        for(SalesReportFragment.SaleItem si: itemArrayList){
            System.out.println(si.toString());
        }
    }
    public interface EditItemOuantity {
        void editItemQuantityOnChanged(View v, int position, SalesReportFragment.SaleItem saleItem);
        void deleteItemOnClick(View v, int position, SalesReportFragment.SaleItem saleItem);
    }

    public void setEditItemQuantityCallback(EditItemOuantity listener) {
        this.editItemQuantityCallback = listener;
    }

    @NonNull
    @Override
    public SaleItemEditAdapter.SaleItemEditViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.dialogue_saleitem_row_edit, null);
        return new SaleItemEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SaleItemEditAdapter.SaleItemEditViewHolder saleItemEditViewHolder, final int i) {
        final int price = Integer.parseInt(itemArrayList.get(i).getItemAmount())/Integer.parseInt(itemArrayList.get(i).getItemQuantity());
        Item item = null;
        for(Item mItem:itemFullDataList){
            if(itemArrayList.get(i).getItemName().equals(mItem.getName()) ){
                item = mItem;
                break;
            }
        }
        saleItemEditViewHolder.tvItemName.setText(itemArrayList.get(i).getItemName());
        saleItemEditViewHolder.tvItemAmount.setText(itemArrayList.get(i).getItemAmount());
        saleItemEditViewHolder.etQuntity.setText(itemArrayList.get(i).getItemQuantity());
        saleItemEditViewHolder.tvItemPrice.setText(""+price);
        saleItemEditViewHolder.tvItemCode.setText(item.getCode());

        saleItemEditViewHolder.btnItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleItemEditViewHolder.etQuntity.setText(""+(Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString())+1));
                saleItemEditViewHolder.tvItemAmount.setText(""+(Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString())*price));
                if(editItemQuantityCallback!=null){
                    editItemQuantityCallback.editItemQuantityOnChanged(v, i, itemArrayList.get(i));
                }
            }
        });

        saleItemEditViewHolder.btnItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editItemQuantityCallback != null) {
                    if(Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString())>1) {
                        saleItemEditViewHolder.etQuntity.setText("" + (Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString()) - 1));
                        saleItemEditViewHolder.tvItemAmount.setText(""+(Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString())*price));
                        if(editItemQuantityCallback!=null){
                            editItemQuantityCallback.editItemQuantityOnChanged(v, i, itemArrayList.get(i));
                        }
                    }
                }
            }
        });
        saleItemEditViewHolder.etQuntity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Integer.parseInt(""+s)>1 ) {
                    saleItemEditViewHolder.tvItemAmount.setText(""+(Integer.parseInt(saleItemEditViewHolder.etQuntity.getText().toString())*price));
                    if(editItemQuantityCallback!=null){
                        editItemQuantityCallback.editItemQuantityOnChanged(saleItemEditViewHolder.etQuntity, i, itemArrayList.get(i));
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        saleItemEditViewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editItemQuantityCallback!=null){
                    editItemQuantityCallback.deleteItemOnClick(v, i, itemArrayList.get(i));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public class SaleItemEditViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemName;
        TextView tvItemCode;
        TextView tvItemPrice;
        public TextView tvItemAmount ;
        public EditText etQuntity;
        Button deleteItem, btnItemAdd, btnItemSub;
        public SaleItemEditViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            etQuntity = itemView.findViewById(R.id.etQuntity);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            btnItemAdd = itemView.findViewById(R.id.btnItemAdd);
            btnItemSub = itemView.findViewById(R.id.btnItemSub);
        }
    }
}
