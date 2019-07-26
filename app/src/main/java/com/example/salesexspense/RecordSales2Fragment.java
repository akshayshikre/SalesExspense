package com.example.salesexspense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.salesexspense.adapters.MenuItemAdapter;
import com.example.salesexspense.adapters.RecordSaleAdapter;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;
import com.example.salesexspense.models.Sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RecordSales2Fragment extends Fragment {

    private EditText date,quantity,totalPrice;
    private Spinner itemsSpinner;
    private Button saveSale;
    private GridView gridView;
    private String saleType = "";
    Calendar myCalendar = Calendar.getInstance();
    List<Item> itemsList;
    List<String> itemsCode;
    List<Sales> lastSale;
    List<Sales> allSales;
    MenuItemAdapter adapter;
    List<MenuCategory> menuCategories;
    ArrayList<RecordSalesFragment.RecordSaleDataItem> recordSaleDataItemsArrayList;
    RecyclerView recyclerRecordSale;

    public RecordSales2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_sales2, container, false);
        menuCategories = MainActivity.myAppDatabase.myDao().getMenuCategories();
        recyclerRecordSale = view.findViewById(R.id.recyclerRecordSale);
        date = view.findViewById(R.id.date);
        saveSale = view.findViewById(R.id.saveSale);
        RecordSaleAdapter recordSaleAdapter = new RecordSaleAdapter(getActivity().getBaseContext(),recyclerRecordSale,menuCategories);
        recyclerRecordSale.setAdapter(recordSaleAdapter);
        return view;
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
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(new Date()));
    }

    public void saveSale(){


    }

}
