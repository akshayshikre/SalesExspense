package com.example.salesexspense;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesexspense.adapters.MenuItemAdapter;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;
import com.example.salesexspense.models.Sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordSalesFragment extends Fragment implements View.OnClickListener, MenuItemAdapter.MenuItemClick {

    private EditText date,quantity,totalPrice;
    private Spinner itemsSpinner;
    private Button addSales;
    private GridView gridView;
    private String saleType = "";
    Calendar myCalendar = Calendar.getInstance();
    List<Item> itemsList;
    List<String> itemsCode;
    List<Sales> lastSale;
    List<Sales> allSales;
    MenuItemAdapter adapter;
    ArrayList<RecordSaleDataItem> recordSaleDataItemsArrayList;
    String myFormat = "dd-MM-yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);



    public RecordSalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_sales, container, false);
        recordSaleDataItemsArrayList=new ArrayList<RecordSaleDataItem>();
        itemsList = MainActivity.myAppDatabase.myDao().getItems();
        itemsCode = new ArrayList<String>();
        for(Item item : itemsList){
            Log.e("itemsList","code:" + item.getCode()+"  price:"+item.getPrice()+"    name:"+item.getName());
            itemsCode.add(item.getCode());
            recordSaleDataItemsArrayList.add(new RecordSaleDataItem(item,-1,-1,item.getCode(),null));
        }
        gridView = view.findViewById(R.id.gridView);
        date = view.findViewById(R.id.date);
        addSales = view.findViewById(R.id.saveSale);
        adapter = new MenuItemAdapter(getActivity(),itemsList);
        adapter.setMenuItemClickListener(this);
        gridView.setAdapter(adapter);
        addSales.setOnClickListener(this);
        date.setOnClickListener(this);
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Log.e("Date","Clicked");
                    new DatePickerDialog(getActivity(), dateSetListener, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        updateCurrentLabel();
        selectSaleType();
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date :
                Log.e("Date","Clicked");
                new DatePickerDialog(getActivity(), dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.saveSale :
                saveSale();
                break;
        }

    }


    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateCurrentLabel() {

        date.setText(sdf.format(new Date()));
    }

    private  int calcToatalSaleAmount(ArrayList<RecordSaleDataItem> mRecordSaleDataItemsArrayList){
        int sum = 0;
        for(RecordSaleDataItem rec : mRecordSaleDataItemsArrayList){
            if(rec.getQuantity()>0 && rec.getAmount()>0 && rec.getMenuCategory() != null){
                sum += rec.getAmount();
            }
        }
        return sum;
    }

    public void saveSale(){
        final int totalSale = calcToatalSaleAmount(recordSaleDataItemsArrayList);

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialogue_save_sale, null);
        final TextView tvDialSaleData = alertLayout.findViewById(R.id.tvDialSaleData);
        final TextView tvDialSaleAmount = alertLayout.findViewById(R.id.tvDialSaleAmount);
        final TextView tvDialSaleType = alertLayout.findViewById(R.id.tvDialSaleType);
        tvDialSaleType.setText("Sale Type: "+saleType);
        tvDialSaleAmount.setText("Total Amount: "+totalSale+" Rs");
        String saleData = "";

        for(int i = 0, j = 0; recordSaleDataItemsArrayList.size()>i;i++){
            if(recordSaleDataItemsArrayList.get(i).getAmount()>0 && recordSaleDataItemsArrayList.get(i).getQuantity()>0 && recordSaleDataItemsArrayList.get(i).getMenuCategory() != null){
                saleData += ""+(j+1)+") Name: "+recordSaleDataItemsArrayList.get(i).getItem().getName() + "\n"
                        + " Quantity: "+recordSaleDataItemsArrayList.get(i).getQuantity()
                        + " Amount: "+recordSaleDataItemsArrayList.get(i).getAmount() +".\n\n";
                ++j;
            }
        }

        tvDialSaleData.setText(saleData);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("View Orders");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.fragmentManager.popBackStack();
           }
        });


        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                lastSale = MainActivity.myAppDatabase.myDao().getLastSale();

                if(lastSale.isEmpty()){
                    int lastiSaleId = 0;
                    for(int i = 0; recordSaleDataItemsArrayList.size()>i;i++){
                        if(recordSaleDataItemsArrayList.get(i).getAmount()>0 && recordSaleDataItemsArrayList.get(i).getQuantity()>0 && recordSaleDataItemsArrayList.get(i).getMenuCategory() != null){
                            MainActivity.myAppDatabase.myDao().addSales(new Sales(date.getText().toString(),
                                    ""+recordSaleDataItemsArrayList.get(i).getItem().getId(),
                                    ""+recordSaleDataItemsArrayList.get(i).getQuantity(),
                                    ""+recordSaleDataItemsArrayList.get(i).getAmount(),
                                    ""+String.valueOf(lastiSaleId),
                                    ""+String.valueOf(totalSale),
                                    ""+saleType,
                                    ""+sdf.format(new Date())));

                        }
                    }

                }else{
                    Log.e("lastSale","last id: "+lastSale.get(0).getSale_id()+"  data:"+lastSale.get(0).toString());
                    int lastiSaleId = Integer.parseInt(lastSale.get(0).getSale_id());
                    for(int i = 0; recordSaleDataItemsArrayList.size()>i;i++){
                        if(recordSaleDataItemsArrayList.get(i).getAmount()>0 && recordSaleDataItemsArrayList.get(i).getQuantity()>0 && recordSaleDataItemsArrayList.get(i).getMenuCategory() != null) {
                            MainActivity.myAppDatabase.myDao().addSales(new Sales(date.getText().toString(),
                                    "" + recordSaleDataItemsArrayList.get(i).getItem().getId(),
                                    "" + recordSaleDataItemsArrayList.get(i).getQuantity(),
                                    "" + recordSaleDataItemsArrayList.get(i).getAmount(),
                                    ""+String.valueOf(lastiSaleId+1),
                                    ""+String.valueOf(totalSale),
                                    ""+saleType,
                                    ""+sdf.format(new Date())));
                        }
                    }
                }
                allSales = MainActivity.myAppDatabase.myDao().getSales();
                String listString = "";
                for (Sales s : allSales)
                {
                    listString += s.getId() + " \t " + s.getItem() + " \t " + s.getDate() + " \t " + s.getAmount() + " \t " + s.getQuantity() + " \t " + s.getSale_id() + " \t "
                            + s.getSale_amount() + "\t" + s.getSale_type() + "** \n ";
                }
                Log.e("allSales",listString);
                MainActivity.fragmentManager.popBackStack();
           }
        });

        AlertDialog dialog = alert.create();
        dialog.show();

    }

    @Override
    public void menuItemClickOnClick(View v, final int position, final Item item, final MenuCategory menuCat, final TextView tv) {
        final int[] calcAmount = new int[1];
        calcAmount[0] = 0;
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialogue_add_item, null);
        final TextView tvDialItemTitle = alertLayout.findViewById(R.id.tvDialItemTitle);
        final TextView tvDialItemCat = alertLayout.findViewById(R.id.tvDialItemCat);
        final TextView tvItemPrice = alertLayout.findViewById(R.id.tvItemPrice);
        final TextView tvDialCalcAmount = alertLayout.findViewById(R.id.tvDialCalcAmount);
        final EditText etDialQuant = alertLayout.findViewById(R.id.etDialQuant);
        final Button btnDialItemAdd = alertLayout.findViewById(R.id.btnDialItemAdd);
        final Button btnDialItemSub = alertLayout.findViewById(R.id.btnDialItemSub);

        if(recordSaleDataItemsArrayList.get(position).getAmount()==-1 &&
                recordSaleDataItemsArrayList.get(position).getQuantity()==-1 &&
                recordSaleDataItemsArrayList.get(position).getMenuCategory()==null){
            etDialQuant.setText("0");
        }else{
            etDialQuant.setText(""+recordSaleDataItemsArrayList.get(position).getQuantity());
            tvDialCalcAmount.setText("Amount: "+recordSaleDataItemsArrayList.get(position).getAmount()+ " Rs.");
            calcAmount[0] = recordSaleDataItemsArrayList.get(position).getAmount();
        }


        etDialQuant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if(Integer.parseInt(""+s)>0){
                    if(Integer.parseInt(""+item.getPrice())>0){
                        tvDialCalcAmount.setText("Amount: "+(Integer.parseInt(""+item.getPrice()) * Integer.parseInt(""+s))+ " Rs.");
                        calcAmount[0] = Integer.parseInt(""+item.getPrice()) * Integer.parseInt(""+s);
                    }

                }
            }
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnDialItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etDialQuant.getText().toString().isEmpty()){
                    etDialQuant.setText("1");
                }else{
                    if(Integer.parseInt(etDialQuant.getText().toString())>=0){
                        etDialQuant.setText(""+(Integer.parseInt(etDialQuant.getText().toString())+1));
                        tvDialCalcAmount.setText("Amount: "+(Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString()))+ " Rs.");
                        calcAmount[0] = Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString());
                    }
                }
            }
        });
        btnDialItemSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(etDialQuant.getText().toString())>0){
                    etDialQuant.setText(""+(Integer.parseInt(etDialQuant.getText().toString())-1));
                    tvDialCalcAmount.setText("Amount: "+(Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString()))+ " Rs.");
                    calcAmount[0] = Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString());

                }
            }
        });
        tvDialItemTitle.setText("Menu Item Name:"+item.getName());
        tvDialItemCat.setText("Menu Category Name:"+menuCat.getName());
        tvItemPrice.setText("Menu Item Price:"+item.getPrice());
        if(etDialQuant.getText().toString().isEmpty()) {
            tvDialCalcAmount.setText("Amount: 0 Rs.");
            calcAmount[0] = 0;
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Add Menu Item Quntity");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();

            }
        });


        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Integer.parseInt(etDialQuant.getText().toString())>0) {
                    calcAmount[0] = Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString());
                    recordSaleDataItemsArrayList.set(position,new RecordSaleDataItem(item,Integer.parseInt(etDialQuant.getText().toString()),
                            Integer.parseInt(""+item.getPrice()) * Integer.parseInt(etDialQuant.getText().toString()),
                            item.getCode(),menuCat));
                    Log.e("recordSaleDataItems...",""+recordSaleDataItemsArrayList.toString());
                    if(recordSaleDataItemsArrayList.get(position).getQuantity() == -1)  tv.setText("0");
                    else tv.setText(""+recordSaleDataItemsArrayList.get(position).getQuantity());
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void selectSaleType(){

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.dialogue_select_sale_type, null);
        final RadioGroup rgSaleType = alertLayout.findViewById(R.id.rgSaleType);
        rgSaleType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) alertLayout.findViewById(checkedId);
                saleType =radioButton.getText().toString();
            }
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Select Sale Type");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.fragmentManager.popBackStack();
            }
        });


        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int btnid = rgSaleType.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) alertLayout.findViewById(btnid);
                saleType =radioButton.getText().toString();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }

    public class RecordSaleDataItem {
        private Item item;
        private String itemCode;
        private int quantity;
        private int amount;


        private MenuCategory menuCategory;

        public RecordSaleDataItem(Item item, int quantity, int amount, String itemCode, MenuCategory menuCategory) {
            this.item = item;
            this.quantity = quantity;
            this.amount = amount;
            this.itemCode = itemCode;
            this.menuCategory = menuCategory;
        }

        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public MenuCategory getMenuCategory() {
            return menuCategory;
        }

        public void setMenuCategory(MenuCategory menuCategory) {
            this.menuCategory = menuCategory;
        }

    }
}
