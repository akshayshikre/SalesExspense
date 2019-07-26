package com.example.salesexspense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button addCategory,addMenuCategory,addItem,recordExpense,recordSales,salesReport,expenseReport,deleteRecords,recordSales2;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addCategory = view.findViewById(R.id.addCategory);
        addCategory.setOnClickListener(this);

        addMenuCategory = view.findViewById(R.id.addMenuCategory);
        addMenuCategory.setOnClickListener(this);

        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(this);

        recordExpense = view.findViewById(R.id.recordExpense);
        recordExpense.setOnClickListener(this);

        recordSales = view.findViewById(R.id.recordSales);
        recordSales2 = view.findViewById(R.id.recordSales2);
        recordSales.setOnClickListener(this);
        recordSales2.setOnClickListener(this);

        salesReport = view.findViewById(R.id.salesReport);
        expenseReport = view.findViewById(R.id.expenseReport);
        deleteRecords = view.findViewById(R.id.deleteRecords);
        salesReport.setOnClickListener(this);
        expenseReport.setOnClickListener(this);
        deleteRecords.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addCategory :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new AddCategoryFragment()).addToBackStack(null).commit();
                break;

            case R.id.addMenuCategory :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new AddMenuCategoryFragment()).addToBackStack(null).commit();
                break;

            case R.id.addItem :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new AddItemFragment()).addToBackStack(null).commit();
                break;

            case R.id.recordExpense :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordExpenseFragment()).addToBackStack(null).commit();
                break;

            case R.id.recordSales :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSalesFragment()).addToBackStack(null).commit();
                break;

            case R.id.recordSales2 :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSales2Fragment()).addToBackStack(null).commit();
                break;

            case R.id.salesReport :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new SalesReportFragment()).addToBackStack(null).commit();
                break;


            case R.id.expenseReport :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new ExpenseReportFragment()).addToBackStack(null).commit();
                break;

            case R.id.deleteRecords :
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new DeleteRecordFragment()).addToBackStack(null).commit();
                break;


        }
    }
}
