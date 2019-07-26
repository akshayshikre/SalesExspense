package com.example.salesexspense;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salesexspense.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordExpenseFragment extends Fragment implements View.OnClickListener {

    private EditText date,description,amount;
    private Spinner categorySpinner;
    private Button addExpense;
    Calendar myCalendar = Calendar.getInstance();

    public RecordExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_expense, container, false);

        date = view.findViewById(R.id.date);
        description = view.findViewById(R.id.description);
        amount = view.findViewById(R.id.amount);

        categorySpinner = view.findViewById(R.id.categorySpinner);

        addExpense = view.findViewById(R.id.addExpense);

        addExpense.setOnClickListener(this);
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

        List<String> categoriesList = MainActivity.myAppDatabase.myDao().getCategories();

        Log.e("List",""+categoriesList);


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriesList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySpinner.setAdapter(dataAdapter);

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

            case R.id.addExpense :

                String edate = date.getText().toString();
                String edesc = description.getText().toString();
                String eamonut = amount.getText().toString();
                String ecat = categorySpinner.getSelectedItem().toString();

                Expense expense = new Expense();
                expense.setDate(edate);
                expense.setDescription(edesc);
                expense.setAmount(eamonut);
                expense.setCategory(ecat);

                MainActivity.myAppDatabase.myDao().addExpense(expense);
                Toast.makeText(getActivity(),"Expense added Successfully ",Toast.LENGTH_SHORT).show();

                date.setText("");
                description.setText("");
                amount.setText("");

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


}
