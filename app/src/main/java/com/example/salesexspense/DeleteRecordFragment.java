package com.example.salesexspense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.salesexspense.models.Expense;
import com.example.salesexspense.models.Sales;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
     * A simple {@link Fragment} subclass.
     */
    public class DeleteRecordFragment extends Fragment {

        Boolean hidedCal = false;

        Button btnDelete;

        Date dFromDate;
        Date dToDate;
        EditText etFromDate;
        EditText etToDate;
        String fromDate = "";
        String toDate = "";
        RadioGroup rgDeleteType;
        String rgTypeSelected = "";
        Calendar fromMyCalendar;
        Calendar toMyCalendar;
        private List<Sales> saleData;
        private List<Expense> expenseData;

        public DeleteRecordFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View view =  inflater.inflate(R.layout.fragment_delete_record, container,false);
            etFromDate = view.findViewById(R.id.etFromDate);
            etToDate = view.findViewById(R.id.etToDate);
            rgDeleteType = view.findViewById(R.id.rgDeleteType);
            btnDelete = view.findViewById(R.id.btDeleteRecords);
            fromMyCalendar = Calendar.getInstance();
            toMyCalendar = Calendar.getInstance();
            etFromDate.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.e("Date","Clicked");
                    hidedCal = true;
                    new DatePickerDialog(getActivity(), dateSetFromListener, fromMyCalendar
                            .get(Calendar.YEAR), fromMyCalendar.get(Calendar.MONTH),
                            fromMyCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            etFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus && hidedCal){
                        Log.e("FromDate","Clicked");
                        new DatePickerDialog(getActivity(), dateSetFromListener, fromMyCalendar
                                .get(Calendar.YEAR), fromMyCalendar.get(Calendar.MONTH),
                                fromMyCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        hidedCal = true;
                    }
                }
            });
            etToDate.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.e("Date","Clicked");
                    new DatePickerDialog(getActivity(), dateSetToListener, toMyCalendar
                            .get(Calendar.YEAR), toMyCalendar.get(Calendar.MONTH),
                            toMyCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            etToDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        Log.e("etToDate","Clicked");
                        new DatePickerDialog(getActivity(), dateSetToListener, toMyCalendar
                                .get(Calendar.YEAR), toMyCalendar.get(Calendar.MONTH),
                                toMyCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }
            });

            updateCurrentLabel();

            rgDeleteType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) view.findViewById(rgDeleteType.getCheckedRadioButtonId());
                    rgTypeSelected =radioButton.getText().toString();
                }
            });

            RadioButton radioButton = (RadioButton) view.findViewById(rgDeleteType.getCheckedRadioButtonId());
            rgTypeSelected =radioButton.getText().toString();

            saleData = MainActivity.myAppDatabase.myDao().getSales();
            expenseData = MainActivity.myAppDatabase.myDao().getExpenses();

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(rgTypeSelected.equals("Sales")){
                        deleteSales();
                    }else if(rgTypeSelected.equals("Expenses")){
                        deleteExpenses();
                    }
                }
            });

            return view;
        }

        private void deleteSales() {
            try {
                dFromDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
                dToDate = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);


                if (dFromDate.compareTo(dToDate) > 0) {
                    Toast.makeText(getActivity(),"dFromDate is after dToDate",Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();
                    MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSalesFragment()).addToBackStack(null).commit();
                } else if (dFromDate.compareTo(dToDate) < 0) {
                    System.out.println("dFromDate is before dToDate");
                    int count = 0;
                    for(Sales s: saleData)
                        synchronized (this) {
                        Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(s.getDate());
                        Log.e("dSaleDate:","//"+dSaleDate.toString());
                        if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                            int result = MainActivity.myAppDatabase.myDao().deleteSaleById(s.getId());
                            if (result == 1) {
                                Log.e("Removing:","*"+s.getDate()+"*"+s.getId()+"*");
                                count++;
                            }else if(result == 0){
                                Log.e("Failed:","*"+s.getDate()+"*"+s.getId()+"*");
                            }else{
                                Log.e("Failed Unkwon Reason:","*"+s.getDate()+"*"+s.getId()+"*");
                            }
                        }
                    }
                    Toast.makeText(getActivity(),"Removed SalesItems:"+count,Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();
                } else if (dFromDate.compareTo(dToDate) == 0) {
                    System.out.println("Date1 is equal to Date2");
                    int count = 0;
                    for(Sales s: saleData)
                        synchronized (this) {
                            Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(s.getDate());
                            Log.e("dSaleDate:","//"+dSaleDate.toString());
                            if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                                int result = MainActivity.myAppDatabase.myDao().deleteSaleById(s.getId());
                                if (result == 1) {
                                    Log.e("Removing:","*"+s.getDate()+"*"+s.getId()+"*");
                                    count++;
                                }else if(result == 0){
                                    Log.e("Failed:","*"+s.getDate()+"*"+s.getId()+"*");
                                }else{
                                    Log.e("Failed Unkwon Reason:","*"+s.getDate()+"*"+s.getId()+"*");
                                }
                            }
                        }
                    Toast.makeText(getActivity(),"Removed SalesItems:"+count,Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();
                } else {
                    System.out.println("How to get here?");
                }
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"Date parsing error",Toast.LENGTH_LONG).show();
                MainActivity.fragmentManager.popBackStack();
            }
        }

        private void deleteExpenses() {
            try {
                dFromDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
                dToDate = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);
                if (dFromDate.compareTo(dToDate) > 0) {
                    Toast.makeText(getActivity(),"dFromDate is after dToDate",Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();
                    MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSalesFragment()).addToBackStack(null).commit();
                } else if (dFromDate.compareTo(dToDate) < 0) {
                    System.out.println("dFromDate is before dToDate");
                    int count = 0;
                    for(Expense e: expenseData)
                        synchronized (this) {
                            Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(e.getDate());
                            Log.e("dSaleDate:","//"+dSaleDate.toString());
                            if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                                int result = MainActivity.myAppDatabase.myDao().deleteExpenseById(e.getId());
                                if (result == 1) {
                                    Log.e("Removing:","*"+e.getDate()+"*"+e.getId()+"*");
                                    count++;
                                }else if(result == 0){
                                    Log.e("Failed:","*"+e.getDate()+"*"+e.getId()+"*");
                                }else{
                                    Log.e("Failed Unkwon Reason:","*"+e.getDate()+"*"+e.getId()+"*");
                                }
                            }
                        }
                    Toast.makeText(getActivity(),"Removed Expenses:"+count,Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();

                } else if (dFromDate.compareTo(dToDate) == 0) {
                    System.out.println("Date1 is equal to Date2");
                    int count = 0;
                    for(Expense e: expenseData)
                        synchronized (this) {
                            Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(e.getDate());
                            Log.e("dSaleDate:","//"+dSaleDate.toString());
                            if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                                int result = MainActivity.myAppDatabase.myDao().deleteExpenseById(e.getId());
                                if (result == 1) {
                                    Log.e("Removing:","*"+e.getDate()+"*"+e.getId()+"*");
                                    count++;
                                }else if(result == 0){
                                    Log.e("Failed:","*"+e.getDate()+"*"+e.getId()+"*");
                                }else{
                                    Log.e("Failed Unkwon Reason:","*"+e.getDate()+"*"+e.getId()+"*");
                                }
                            }
                        }
                    Toast.makeText(getActivity(),"Removed Expenses:"+count,Toast.LENGTH_LONG).show();
                    MainActivity.fragmentManager.popBackStack();
                } else {
                    System.out.println("How to get here?");
                }
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"Date parsing error",Toast.LENGTH_LONG).show();
                MainActivity.fragmentManager.popBackStack();
            }
        }


        DatePickerDialog.OnDateSetListener dateSetFromListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromMyCalendar.set(Calendar.YEAR, year);
                fromMyCalendar.set(Calendar.MONTH, monthOfYear);
                fromMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFromDateLabel();
            }

        };

        DatePickerDialog.OnDateSetListener dateSetToListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toMyCalendar.set(Calendar.YEAR, year);
                toMyCalendar.set(Calendar.MONTH, monthOfYear);
                toMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateToDateLabel();
            }

        };

        private void updateFromDateLabel() {
            String myFormat = "dd-MM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            etFromDate.setText(sdf.format(fromMyCalendar.getTime()));
            fromDate = etFromDate.getText().toString();

        }
        private void updateToDateLabel() {
            String myFormat = "dd-MM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            etToDate.setText(sdf.format(toMyCalendar.getTime()));
            toDate = etToDate.getText().toString();
        }
        private void updateCurrentLabel() {
            String myFormat = "dd-MM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            etFromDate.setText(sdf.format(new Date()));
            etToDate.setText(sdf.format(new Date()));
            fromDate = etFromDate.getText().toString();
            toDate = etToDate.getText().toString();
        }

    }

