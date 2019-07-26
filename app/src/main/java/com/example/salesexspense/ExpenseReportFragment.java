package com.example.salesexspense;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesexspense.adapters.ExpenseAdapter;
import com.example.salesexspense.adapters.ExpenseAdapterCatWise;
import com.example.salesexspense.models.Category;
import com.example.salesexspense.models.Expense;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseReportFragment extends Fragment implements ExpenseAdapter.EditItemClick {


    Boolean hidedCal = false;
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private  RecyclerView recyclerView;
    Button btnExport;
    Calendar fromMyCalendar = Calendar.getInstance();
    Calendar toMyCalendar = Calendar.getInstance();
    List<Expense> expenseData;
    ArrayList<Expense> dateWiseExpenseData = new ArrayList<Expense>();
    List<Category> categoriesList;
    List<String> mCategoriesList;
    HashMap<String, Expense> expenseDataHashMap = new HashMap<String, Expense>();
    ArrayList<String> expenseDataHashMapKeysArrayList = new ArrayList<String>();
    HashMap<String, Expense> refreshedExpenseDataHashMap = new HashMap<String,Expense>();
    ArrayList<String> refreshedExpenseDataHashMapKeysArrayList = new ArrayList<String>();

    //CatWise
    ArrayList<String> expenseDataHashMapCatWiseKeys = new ArrayList<String>();
    HashMap<String, ArrayList<Expense>> expenseDataHashMapCatWise = new HashMap<String,  ArrayList<Expense>>();

    boolean categorywise = false;
    int saleFinalAmount;
    Date dFromDate;
    Date dToDate;
    LayoutInflater inflater;
    View alertLayout;
    EditText etFromDate;
    EditText etToDate;
    CheckBox cbCatwise;
    String fromDate = "";
    String toDate = "";
    private TextView tvFinalSaleAmount;

    public ExpenseReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_expense_report, container,false);
        inflater = getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.dialogue_select_expense_type, null);
        etFromDate = alertLayout.findViewById(R.id.etFromDate);
        etToDate = alertLayout.findViewById(R.id.etToDate);
        cbCatwise = alertLayout.findViewById(R.id.cbcatwise);
        recyclerView = view.findViewById(R.id.recyclerViewExpense);
        btnExport = view.findViewById(R.id.btnExpenseExport);
        tvFinalSaleAmount = view.findViewById(R.id.tvFinalExpenseAmount);
        recyclerView.setHasFixedSize(true);

        expenseData = MainActivity.myAppDatabase.myDao().getExpenses();
        categoriesList = MainActivity.myAppDatabase.myDao().getAllCategories();
        mCategoriesList = MainActivity.myAppDatabase.myDao().getCategories();
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
                    if (cbCatwise.isChecked()) {
                        exportCatWiseEmailInCSV();
                    } else {
                        exportEmailInCSV();
                    }

//               }catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(),"Can't export",Toast.LENGTH_LONG).show();
//                }
            }
        });


        //For Date wise
        for(Expense e: expenseData){
            expenseDataHashMap.put(String.valueOf( e.getId()), new Expense( e.getId(), e.getDate(), e.getCategory(), e.getDescription(), e.getAmount()));
        }

        for(Expense e :expenseDataHashMap.values()){
            Log.e("hashmap data :",""+e.toString());
        }
        for(String key :expenseDataHashMap.keySet()){
            expenseDataHashMapKeysArrayList.add(key);
        }

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        selectReportType();

        return view;
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

    }
    private void updateToDateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etToDate.setText(sdf.format(toMyCalendar.getTime()));

    }

    private void updateCurrentLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etFromDate.setText(sdf.format(new Date()));
        etToDate.setText(sdf.format(new Date()));
    }

    public void selectReportType(){

        cbCatwise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                categorywise = isChecked;
            }
        });

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


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Select Expense Report Options");
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

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                categorywise = cbCatwise.isChecked();
                fromDate = etFromDate.getText().toString();
                toDate = etToDate.getText().toString();
                if(cbCatwise.isChecked()){
                    refreshDataByDateForCatWise();
                }else {
                    refreshData();
                }
            }
        });

        AlertDialog dialog = alert.create();

        dialog.show();
        updateCurrentLabel();

    }

    public void refreshData(){
        try {
            dFromDate=new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
            dToDate=new SimpleDateFormat("dd-MM-yyyy").parse(toDate);

            if (dFromDate.compareTo(dToDate) > 0) {
                Toast.makeText(getActivity(),"dFromDate is after dToDate",Toast.LENGTH_LONG).show();
                MainActivity.fragmentManager.popBackStack();
            } else if (dFromDate.compareTo(dToDate) < 0) {
                System.out.println("dFromDate is before dToDate");
                saleFinalAmount = 0;
                for(Map.Entry entry:expenseDataHashMap.entrySet()){
                    Expense e = (Expense)entry.getValue();
                    Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(e.getDate());
                    Log.e("dSaleDate:","//"+dSaleDate.toString());
                    if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                        Log.e("Added:","***"+e.getDate());
                        refreshedExpenseDataHashMap.put((String)entry.getKey(),e);
                        saleFinalAmount += Integer.parseInt(e.getAmount());
                    }
                }

                for(Expense e :refreshedExpenseDataHashMap.values()){
                    Log.e("refreshedExpenseData:",""+e.toString());
                }
                for(String key :refreshedExpenseDataHashMap.keySet()){
                    refreshedExpenseDataHashMapKeysArrayList.add(key);
                }

                adapter = new ExpenseAdapter(getActivity().getBaseContext(),refreshedExpenseDataHashMap,refreshedExpenseDataHashMapKeysArrayList, mCategoriesList);
                ((ExpenseAdapter) adapter).setEditItemClickListener(this);
                recyclerView.setAdapter(adapter);
                tvFinalSaleAmount.setText("Total Expense Amount: "+saleFinalAmount);

            } else if (dFromDate.compareTo(dToDate) == 0) {
                System.out.println("Date1 is equal to Date2");
                saleFinalAmount = 0;
                for(Map.Entry entry:expenseDataHashMap.entrySet()){
                    Expense e = (Expense) entry.getValue();
                    if( e.getDate().equals(fromDate)){
                        Log.e("Added:","***"+e.getDate());
                        refreshedExpenseDataHashMap.put((String)entry.getKey(),e);
                        saleFinalAmount += Integer.parseInt(e.getAmount());
                    }
                }

                for(Expense e :refreshedExpenseDataHashMap.values()){
                    Log.e("refreshedExpenseData:",""+e.toString());
                }
                for(String key :refreshedExpenseDataHashMap.keySet()){
                    refreshedExpenseDataHashMapKeysArrayList.add(key);
                }

                adapter = new ExpenseAdapter(getActivity().getBaseContext(),refreshedExpenseDataHashMap,refreshedExpenseDataHashMapKeysArrayList, mCategoriesList);
                ((ExpenseAdapter) adapter).setEditItemClickListener(this);
                recyclerView.setAdapter(adapter);
                tvFinalSaleAmount.setText("Total Expense Amount: "+saleFinalAmount);

            } else {
                System.out.println("How to get here?");
            }


        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Date parsing error",Toast.LENGTH_LONG).show();
            MainActivity.fragmentManager.popBackStack();
       }
    }

    public void refreshDataByDateForCatWise(){
        //dateWiseSaleData
        try {
            dFromDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
            dToDate = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);

            if (dFromDate.compareTo(dToDate) > 0) {
                Toast.makeText(getActivity(),"dFromDate is after dToDate",Toast.LENGTH_LONG).show();
                MainActivity.fragmentManager.popBackStack();
           } else if (dFromDate.compareTo(dToDate) < 0) {
                for(Expense e: expenseData){
                    Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(e.getDate());
                    if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0)  ) {
                        dateWiseExpenseData.add(e);
                    }
                }
            } else if (dFromDate.compareTo(dToDate) == 0) {
                for(Expense e: expenseData){
                    Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(e.getDate());
                    if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0)  ) {
                        dateWiseExpenseData.add(e);
                    }
                }
            } else {
                System.out.println("How to get here?");
            }

        }catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Date parsing error",Toast.LENGTH_LONG).show();
            MainActivity.fragmentManager.popBackStack();
        }
        // For Categorywise

        ArrayList<String> categoriesArrayList = new ArrayList<String>();
        for(Expense e: dateWiseExpenseData){
            categoriesArrayList.add(e.getCategory());
        }
        List<String> distinctCategoriesList = new ArrayList<String>(new HashSet<String>(categoriesArrayList));

        int FinalSaleAmount = 0;
        for(String cat: distinctCategoriesList){
            ArrayList<Expense> expenseArrayList = new ArrayList<Expense>();
            for(Expense e: dateWiseExpenseData){
                if(cat.equals(e.getCategory())) {
                    expenseArrayList.add(new Expense(e.getId(), e.getDate(), e.getCategory(), e.getDescription(), e.getAmount()));
                    FinalSaleAmount += Integer.parseInt(e.getAmount());
                }
            }
            expenseDataHashMapCatWise.put(cat,expenseArrayList);
            Log.e("expenseDataHashMapCat", "cat:"+cat+" expenseArrayList:"+expenseArrayList.toString());
        }


        for(String key :expenseDataHashMapCatWise.keySet()){
            expenseDataHashMapCatWiseKeys.add(key);
        }

        adapter = new ExpenseAdapterCatWise(getActivity().getBaseContext(),expenseDataHashMapCatWise,expenseDataHashMapCatWiseKeys);
        recyclerView.setAdapter(adapter);
        tvFinalSaleAmount.setText("Total Expense Amount: "+FinalSaleAmount);
    }

    private void exportCatWiseEmailInCSV() {


        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("" + var);


        final String filename = folder.toString() + "/" + "ExportExpenseDataCatWise.csv";

        Log.e("exportEmailInCSV#",filename);
        final ProgressDialog progDailog = ProgressDialog.show(
                getActivity(), "Export In CSV", "Exporting...",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        new Thread() {
            public void run() {
                try {
                    FileWriter fw = new FileWriter(filename);
                    fw.append("No");fw.append(',');
                    fw.append("Expense Category");fw.append(',');
                    fw.append("Expense Category Bill");fw.append(',');
                    fw.append("Expense Id");fw.append(',');
                    fw.append("Expense Date");fw.append(',');
                    fw.append("Expense Amount");fw.append(',');
                    fw.append("Expense Description");fw.append(',');
                    fw.append('\n');

                    int row = 1;
                    int totalAmount = 0;
                    for(Map.Entry entry:expenseDataHashMapCatWise.entrySet()){
                        ArrayList<Expense> eList = (ArrayList<Expense>) entry.getValue();
                        int bill = 0;
                        for(Expense e:eList){
                            bill += Integer.parseInt(e.getAmount());
                        }
                        for(Expense e:eList){
                            Log.e("date:::",e.getDate());
                            fw.append(""+row);fw.append(',');
                            fw.append(""+e.getCategory());fw.append(',');
                            fw.append(""+bill);fw.append(',');
                            fw.append(""+e.getId());fw.append(',');
                            fw.append(" "+e.getDate());fw.append(',');
                            fw.append(""+e.getAmount());fw.append(',');
                            fw.append(""+e.getDescription());fw.append(',');
                            fw.append('\n');
                            row++;
                            totalAmount += Integer.parseInt(e.getAmount());
                        }
                    }
                    fw.append('\n');
                    fw.append('\n');
                    fw.append("FromDate");fw.append(',');
                    fw.append("ToDate");fw.append(',');
                    fw.append("Total Expenses");fw.append(',');fw.append('\n');
                    fw.append(fromDate);fw.append(',');
                    fw.append(toDate);fw.append(',');
                    fw.append(""+totalAmount);fw.append(',');
                    fw.close();

                } catch (Exception e) {
                }
                finally {
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();

                }


            }
        }.start();
        try {
            //File Root= Environment.getExternalStorageDirectory();
            //String filelocation=Root.getAbsolutePath() + folder_name + "/" + file_name;
            String filelocation = filename;
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            String message="File to be shared is " + "ExportExpenseDataDateWise.csv" + ".";
            intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Expense Data");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setData(Uri.parse("mailto:"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } catch(Exception e)  {
            System.out.println("is exception raises during sending mail"+e);
        }

    }
    private void exportEmailInCSV() {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Folder");

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdir();

        System.out.println("" + var);


        final String filename = folder.toString() + "/" + "ExportExpenseDataDateWise.csv";

        Log.e("exportEmailInCSV#",filename);
        final ProgressDialog progDailog = ProgressDialog.show(
                getActivity(), "Export In CSV", "Exporting...",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        new Thread() {
            public void run() {
                try {
                    FileWriter fw = new FileWriter(filename);
                    fw.append("No");fw.append(',');
                    fw.append("Expense Id");fw.append(',');
                    fw.append("Expense Date");fw.append(',');
                    fw.append("Expense Category");fw.append(',');
                    fw.append("Expense Amount");fw.append(',');
                    fw.append("Expense Description");fw.append(',');
                    fw.append('\n');

                    int row = 1;
                    int totalAmount = 0;
                    for(Map.Entry entry:refreshedExpenseDataHashMap.entrySet()){
                        Expense e = (Expense) entry.getValue();
                            Log.e("date:::",e.getDate());
                            fw.append(""+row);fw.append(',');
                            fw.append(""+e.getId());fw.append(',');
                            fw.append(" "+e.getDate());fw.append(',');
                            fw.append(e.getCategory());fw.append(',');
                            fw.append(e.getAmount());fw.append(',');
                            fw.append(e.getDescription());fw.append(',');
                            fw.append('\n');
                            row++;
                        totalAmount += Integer.parseInt(e.getAmount());

                    }
                    fw.append('\n');
                    fw.append('\n');
                    fw.append("FromDate");fw.append(',');
                    fw.append("ToDate");fw.append(',');
                    fw.append("Total Expenses");fw.append(',');fw.append('\n');
                    fw.append(fromDate);fw.append(',');
                    fw.append(toDate);fw.append(',');
                    fw.append(""+totalAmount);fw.append(',');
                    fw.close();

                } catch (Exception e) {
                }
                finally {
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();

                }


            }
        }.start();
        try {
            //File Root= Environment.getExternalStorageDirectory();
            //String filelocation=Root.getAbsolutePath() + folder_name + "/" + file_name;
            String filelocation = filename;
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            String message="File to be shared is " + "ExportExpenseDataDateWise.csv" + ".";
            intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Expense Data");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setData(Uri.parse("mailto:"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        } catch(Exception e)  {
            System.out.println("is exception raises during sending mail"+e);
        }

    }
    Boolean newhidedCal = true;
    @Override
    public void editItemClickOnClick(View v, final int position, final Expense expense, /*final HashMap<String,Expense> expenseDataHashMap, final ArrayList<String> expenseDataHashMapKeys,*/ List<String> categories) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialogue_expense_row_edit, null);
        final TextView expenseId = alertLayout.findViewById(R.id.expenseId);
        final EditText etDate = alertLayout.findViewById(R.id.etDate);
        final EditText etAmount = alertLayout.findViewById(R.id.etAmount);
        final EditText etDescription = alertLayout.findViewById(R.id.etDescription);
        final Spinner categorySpinner = alertLayout.findViewById(R.id.categorySpinner);
        final Button deleteExpense = alertLayout.findViewById(R.id.deleteExpense);
        final Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy", Locale.US);
        try {
            mCalendar.setTime(sdf.parse(expense.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                etDate.setText(sdf.format(mCalendar.getTime()));
            }

        };

        etDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.e("Date","Clicked");
                //newhidedCal = true;
                new DatePickerDialog(getActivity(), dateSetListener, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                newhidedCal = false;
            }
        });
        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !newhidedCal ){
                    Log.e("Date","Clicked");
                    new DatePickerDialog(getActivity(), dateSetListener, mCalendar
                            .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    newhidedCal = false;
                }
            }

        });
        expenseId.setText("Expense Id: "+ expense.getId());
        etDate.setText(expense.getDate());
        etAmount.setText(expense.getAmount());
        etDescription.setText(expense.getDescription());
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,categories);
        categorySpinner.setAdapter(mAdapter);
        int selectedCatId = 0;
        for(String cat:categories){
            if(expense.getCategory().equals(cat)){
                break;
            }else{
                selectedCatId++;
            }
        }
        categorySpinner.setSelection(selectedCatId);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Edit Expense");
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

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = MainActivity.myAppDatabase.myDao().updateExpense(expense.getId(),etDate.getText().toString(),
                        categorySpinner.getSelectedItem().toString(),etDescription.getText().toString(),""+etAmount.getText());
                if(result>0){
                    refreshedExpenseDataHashMap.put(""+expense.getId(),new Expense(expense.getId(), etDate.getText().toString(),
                            categorySpinner.getSelectedItem().toString(), etDescription.getText().toString(), etAmount.getText().toString()));
//                    adapter = new ExpenseAdapter()
                    adapter.notifyItemChanged(position);
                    adapter.notifyItemRangeChanged(position, refreshedExpenseDataHashMapKeysArrayList.size());
                    //recyclerView.invalidate();
                    int sum = 0;
                            for(Expense e:refreshedExpenseDataHashMap.values()){
                                sum += Integer.parseInt(e.getAmount());
                            }
                    tvFinalSaleAmount.setText("Total Expense Amount: "+sum);
                    Toast.makeText(getActivity().getBaseContext(), "Result updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity().getBaseContext(), "Result NOT updated", Toast.LENGTH_SHORT).show();
                }

            }
        });
        final AlertDialog dialog = alert.create();
        deleteExpense.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int result = MainActivity.myAppDatabase.myDao().deleteExpenseById(expense.getId());
                if(result>0){
                    refreshedExpenseDataHashMap.remove(""+expense.getId());
                    refreshedExpenseDataHashMapKeysArrayList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, refreshedExpenseDataHashMapKeysArrayList.size());
                    //adapter.notifyDataSetChanged();
                    //recyclerView.invalidate();
                    int sum = 0;
                    for(Expense e:refreshedExpenseDataHashMap.values()){
                        sum += Integer.parseInt(e.getAmount());
                    }
                    tvFinalSaleAmount.setText("Total Expense Amount: "+sum);
                    dialog.dismiss();
                    Toast.makeText(getActivity().getBaseContext(), "Result deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity().getBaseContext(), "Result NOT deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                newhidedCal = true;
            }
        });
        dialog.show();
    }


}
