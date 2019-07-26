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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.salesexspense.adapters.SaleAdapter;
import com.example.salesexspense.adapters.SaleItemEditAdapter;
import com.example.salesexspense.adapters.SaleItemWiseAdapter;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.Sales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
public class SalesReportFragment extends Fragment implements SaleAdapter.EditItemClick, SaleItemEditAdapter.EditItemOuantity{

    Boolean hidedCal = false;
    Boolean newhidedCal = true;
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private  RecyclerView recyclerView;
    Button btnExport;
    Calendar fromMyCalendar = Calendar.getInstance();
    Calendar toMyCalendar = Calendar.getInstance();
    List<Sales> saleData;
    ArrayList<Sales> dateWiseSaleData = new ArrayList<Sales>();
    List<Item> itemsList;
    HashMap<String,SaleData> refreshedSaleDataHashMap = new HashMap<String, SaleData>();
    ArrayList<String> refreshedSaleDataHashMapKeys = new ArrayList<String>();
    HashMap<String,SaleData> saleDataHashMap = new HashMap<String, SaleData>();
    HashMap <String,ArrayList<SaleItem>> itemsHashMap = new HashMap<String,ArrayList<SaleItem>>();
    ArrayList<String> saleDataHashMapKeys = new ArrayList<String>();

    //Itemwise
    HashMap <String,ArrayList<SaleItemItemWise>> itemsItemWiseHashMap = new HashMap<String,ArrayList<SaleItemItemWise>>();
    HashMap<String,SaleDataItemWise> saleDataItemWiseHashMap = new HashMap<String, SaleDataItemWise>();
    ArrayList<String> saleDataItemWiseHashMapKeys = new ArrayList<String>();
    boolean itemwise = false;
    int saleFinalAmount;
    Date dFromDate;
    Date dToDate;
    LayoutInflater inflater;
    View alertLayout;
    EditText etFromDate;
    EditText etToDate;
    CheckBox cbItemwise;
    String fromDate = "";
    String toDate = "";
    private TextView tvFinalSaleAmount;

    //For edit sale item
    SaleData currentSaleData;
    ArrayList<SaleItem> currentItemsArrayList;
    RecyclerView recyclerViewItemEdit;
    TextView saleAmount;
    SaleItemEditAdapter itemEditadapter;
    boolean changedItems = false;
    boolean deletedItems = false;
    boolean changedSaleData = false;
    ArrayList<Long> deletedItemsList = new ArrayList();
    ArrayList<Long> changedItemsList = new ArrayList();

    public SalesReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sales_report, container, false);
        inflater = getLayoutInflater();
        alertLayout = inflater.inflate(R.layout.dialogue_sales_report_select, null);
        etFromDate = alertLayout.findViewById(R.id.etFromDate);
        etToDate = alertLayout.findViewById(R.id.etToDate);
        cbItemwise = alertLayout.findViewById(R.id.cbItemwise);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnExport = view.findViewById(R.id.btnExport);
        tvFinalSaleAmount = view.findViewById(R.id.tvFinalSaleAmount);
        recyclerView.setHasFixedSize(true);
        saleData = MainActivity.myAppDatabase.myDao().getSales();
        itemsList = MainActivity.myAppDatabase.myDao().getItems();
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cbItemwise.isChecked()) {
                        exportItemWiseEmailInCSV();
                    }
                    else{
                        exportEmailInCSV();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Can't export",Toast.LENGTH_LONG).show();
                }
            }
        });

        //For Sale wise
        ArrayList<String> saleIdsArrayList = new ArrayList<String>();
        for(Sales s: saleData){
            saleIdsArrayList.add(s.getSale_id());
        }
        List<String> distinctSaleIdsList = new ArrayList<String>(new HashSet<String>(saleIdsArrayList));

        for(String id: distinctSaleIdsList){
            ArrayList<SaleItem> saleitems = new ArrayList<SaleItem>();
            for(Sales s: saleData){
                if(id.equals(s.getSale_id())) {
                    String itemName = "";
                    for (Item item : itemsList) {
                        if (String.valueOf(item.getId()).equals(s.getItem())) {
                            itemName = item.getName();
                            break;
                        }
                    }
                    saleitems.add(new SaleItem(itemName, s.getQuantity(), s.getAmount(), s.getId()));
                }
            }

            itemsHashMap.put(id,saleitems);
            Log.e("itemsHashMap", "id:"+id+" saleitems:"+saleitems.toString());

        }

        for(String saleId: distinctSaleIdsList){
            for(Sales s: saleData){
                if(s.getSale_id().equals(saleId)){
                    saleDataHashMap.put(saleId,new SaleData(s.getSale_id(), s.getDate(), s.getSale_type(), s.getSale_amount(),itemsHashMap.get(saleId)));
                    break;
                }
            }
        }

        for(SaleData sd :saleDataHashMap.values()){
            Log.e("hashmap data :",""+sd.toString());
        }

        for(String key :saleDataHashMap.keySet()){
            saleDataHashMapKeys.add(key);
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

        cbItemwise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                itemwise = isChecked;
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
        alert.setTitle("Select Sale Report Options");
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
                itemwise = cbItemwise.isChecked();
                fromDate = etFromDate.getText().toString();
                toDate = etToDate.getText().toString();
                if(cbItemwise.isChecked()){
                    refreshDataByDateForItemWise();
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
            MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSalesFragment()).addToBackStack(null).commit();
        } else if (dFromDate.compareTo(dToDate) < 0) {
            System.out.println("dFromDate is before dToDate");
            saleFinalAmount = 0;
            for(Map.Entry entry:saleDataHashMap.entrySet()){
                SaleData sd = (SaleData)entry.getValue();
                Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(sd.getSaleDate());
                Log.e("dSaleDate:","//"+dSaleDate.toString());
                if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0) ){
                    Log.e("Added:","***"+sd.getSaleDate());
                    refreshedSaleDataHashMap.put((String)entry.getKey(),sd);
                    saleFinalAmount += Integer.parseInt(sd.getSaleAmount());
                }
            }

            for(SaleData sd :saleDataHashMap.values()){
                Log.e("saleDataHashMap:",""+sd.toString());
            }
            for(String key :refreshedSaleDataHashMap.keySet()){
                refreshedSaleDataHashMapKeys.add(key);
            }

            adapter = new SaleAdapter(getActivity().getBaseContext(),refreshedSaleDataHashMap,refreshedSaleDataHashMapKeys);
            ((SaleAdapter) adapter).setEditItemClickListener(this);
            recyclerView.setAdapter(adapter);
            tvFinalSaleAmount.setText("Total Sale Amount: "+saleFinalAmount);

        } else if (dFromDate.compareTo(dToDate) == 0) {
            System.out.println("Date1 is equal to Date2");
            saleFinalAmount = 0;
            for(Map.Entry entry:saleDataHashMap.entrySet()){
                SaleData sd = (SaleData)entry.getValue();
                if( sd.getSaleDate().equals(fromDate)){
                    Log.e("Added:","***"+sd.getSaleDate());
                    refreshedSaleDataHashMap.put((String)entry.getKey(),sd);
                    saleFinalAmount += Integer.parseInt(sd.getSaleAmount());
                }
            }

            for(SaleData sd :saleDataHashMap.values()){
                Log.e("saleDataHashMap:",""+sd.toString());
            }
            for(String key :refreshedSaleDataHashMap.keySet()){
                refreshedSaleDataHashMapKeys.add(key);
            }

            adapter = new SaleAdapter(getActivity().getBaseContext(),refreshedSaleDataHashMap,refreshedSaleDataHashMapKeys);
            ((SaleAdapter) adapter).setEditItemClickListener(this);
            recyclerView.setAdapter(adapter);
            tvFinalSaleAmount.setText("Total Sale Amount: "+saleFinalAmount);
        } else {
            System.out.println("How to get here?");
        }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),"Date parsing error",Toast.LENGTH_LONG).show();
            MainActivity.fragmentManager.popBackStack();
        }
    }

    public void refreshDataByDateForItemWise(){
        //dateWiseSaleData
        try {
            dFromDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
            dToDate = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);

            if (dFromDate.compareTo(dToDate) > 0) {
                Toast.makeText(getActivity(),"dFromDate is after dToDate",Toast.LENGTH_LONG).show();
                MainActivity.fragmentManager.popBackStack();
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container,new RecordSalesFragment()).addToBackStack(null).commit();
            } else if (dFromDate.compareTo(dToDate) < 0) {
                for(Sales s: saleData){
                    Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(s.getDate());
                    if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0)  ) {
                        dateWiseSaleData.add(s);
                    }
                }
            } else if (dFromDate.compareTo(dToDate) == 0) {
                for(Sales s: saleData){
                    Date dSaleDate = new SimpleDateFormat("dd-MM-yyyy").parse(s.getDate());
                    if( ((dFromDate.compareTo(dSaleDate) < 0) && (dToDate.compareTo(dSaleDate) > 0)) || (dFromDate.compareTo(dSaleDate) == 0) || (dToDate.compareTo(dSaleDate) == 0)  ) {
                        dateWiseSaleData.add(s);
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
            // For Itemwise
        ArrayList<String> itemIdsArrayList = new ArrayList<String>();
        for(Sales s: dateWiseSaleData){
            itemIdsArrayList.add(s.getItem());
        }
        List<String> distinctItemIdsList = new ArrayList<String>(new HashSet<String>(itemIdsArrayList));


        for(String id: distinctItemIdsList){
            ArrayList<SaleItemItemWise> saleItemItemWiseArrayList = new ArrayList<SaleItemItemWise>();
            for(Sales s: dateWiseSaleData){
                if(id.equals(s.getItem())) {
                    saleItemItemWiseArrayList.add(new SaleItemItemWise(s.getSale_id(),s.getDate(), s.getSale_type(), s.getQuantity(),s.getAmount()));
                }
            }
            itemsItemWiseHashMap.put(id,saleItemItemWiseArrayList);
            Log.e("itemsItemWiseHashMap", "id:"+id+" saleItemItemWiseArrayList:"+saleItemItemWiseArrayList.toString());

        }

        int FinalSaleAmount = 0;
        for(String itemId: distinctItemIdsList){
            for(Sales s: dateWiseSaleData){
                if(s.getItem().equals(itemId)){
                    String itemCode = "", itemName = "", itemPrice = "";
                    for (Item item : itemsList) {
                        if (String.valueOf(item.getId()).equals(s.getItem())) {
                            itemCode = item.getCode();
                            itemName = item.getName();
                            itemPrice = item.getPrice();
                            break;
                        }
                    }
                    int totalItemSaleAmount = 0;
                    for (SaleItemItemWise si : itemsItemWiseHashMap.get(itemId)){
                        totalItemSaleAmount += Integer.parseInt(si.getItemAmount());
                    }
                    saleDataItemWiseHashMap.put(itemId,new SaleDataItemWise(s.getItem(), itemCode, itemName, itemPrice, ""+totalItemSaleAmount, itemsItemWiseHashMap.get(itemId)));
                    FinalSaleAmount += totalItemSaleAmount;
                    break;
                }
            }
        }

        for(SaleDataItemWise sd :saleDataItemWiseHashMap.values()){
            Log.e("saleDataItemWiseHashMap",""+sd.toString());
        }
        for(String key :saleDataItemWiseHashMap.keySet()){
            saleDataItemWiseHashMapKeys.add(key);
        }

        adapter = new SaleItemWiseAdapter(getActivity().getBaseContext(),saleDataItemWiseHashMap,saleDataItemWiseHashMapKeys);
        recyclerView.setAdapter(adapter);
        tvFinalSaleAmount.setText("Total Sale Amount: "+FinalSaleAmount);
    }

    public void exportEmailInCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/Folder");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            final String filename = folder.toString() + "/" + "ExportSalesDataDateWise.csv";

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
                        fw.append("Sale Id");fw.append(',');
                        fw.append("Sale Date");fw.append(',');
                        fw.append("Sale Type");fw.append(',');
                        fw.append("Bill Amount");fw.append(',');
                        fw.append("Item Name");fw.append(',');
                        fw.append("Item Rate");fw.append(',');
                        fw.append("Item Quantity");fw.append(',');
                        fw.append("Item Amount");fw.append(',');
                        fw.append('\n');

                        int row = 1;
                        for(Map.Entry entry:refreshedSaleDataHashMap.entrySet()){
                            SaleData sd = (SaleData) entry.getValue();
                            for(SaleItem si: sd.getSaleItemsArray()){
                                Log.e("date:::",sd.getSaleDate());
                                fw.append(""+row);fw.append(',');
                                fw.append(sd.getSaleId());fw.append(',');
                                fw.append(" "+sd.getSaleDate());fw.append(',');
                                fw.append(sd.getSaleType());fw.append(',');
                                fw.append(sd.getSaleAmount());fw.append(',');
                                fw.append(si.getItemName());fw.append(',');
                                fw.append(""+(Integer.parseInt(si.getItemAmount())/Integer.parseInt(si.getItemQuantity())));fw.append(',');
                                fw.append(si.getItemQuantity());fw.append(',');
                                fw.append(si.getItemAmount());fw.append(',');
                                fw.append('\n');
                                row++;
                            }
                        }
                        fw.append('\n');
                        fw.append('\n');
                        fw.append("FromDate");fw.append(',');
                        fw.append("ToDate");fw.append(',');
                        fw.append("Total Sales");fw.append(',');fw.append('\n');
                        fw.append(fromDate);fw.append(',');
                        fw.append(toDate);fw.append(',');
                        fw.append(""+tvFinalSaleAmount.getText());fw.append(',');
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
                String message="File to be shared is " + "ExportSalesDataDateWise.csv" + ".";
                intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Sales Data");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.setData(Uri.parse("mailto:"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            } catch(Exception e)  {
                System.out.println("is exception raises during sending mail"+e);
            }

        }

    }

    public void exportItemWiseEmailInCSV() throws IOException {
        {

            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/Folder");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            final String filename = folder.toString() + "/" + "ExportSalesDataItemWise.csv";
            Log.e("exportItemWise#",filename);
            // show waiting screen
            final ProgressDialog progDailog = ProgressDialog.show(
                    getActivity(), "Exporting Sales Data ItemWise", "Exporting...",
                    true);//please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                }
            };

            Thread t = new Thread() {
                public void run() {

                    try {
                        FileWriter fw = new FileWriter(filename);
                        fw.append("No");fw.append(',');
                        fw.append("Item Code");fw.append(',');
                        fw.append("Item Name");fw.append(',');
                        fw.append("Item Price");fw.append(',');
                        fw.append("Sale Id");fw.append(',');
                        fw.append("Sale Date");fw.append(',');
                        fw.append("Sale Type");fw.append(',');
                        fw.append("Sale Item's Quantity");fw.append(',');
                        fw.append("Sale Item's Amount");fw.append(',');
                        fw.append('\n');

                        int row = 1;
                        for(Map.Entry entry:saleDataItemWiseHashMap.entrySet()){
                            SaleDataItemWise sd = (SaleDataItemWise) entry.getValue();
                            for(SaleItemItemWise si: sd.getSaleItemsArray()){
                                fw.append(""+row);fw.append(',');
                                fw.append(sd.getItemCode());fw.append(',');
                                fw.append(sd.getItemName());fw.append(',');
                                fw.append(sd.getItemPrice());fw.append(',');
                                fw.append(si.getSaleId());fw.append(',');
                                fw.append(" "+si.getSaleDate());fw.append(',');
                                fw.append(si.getSaleType());fw.append(',');
                                fw.append(si.getItemQuantity());fw.append(',');
                                fw.append(si.getItemAmount());fw.append(',');
                                fw.append('\n');
                                row++;
                            }
                        }
                        fw.append('\n');
                        fw.append('\n');
                        fw.append("FromDate");fw.append(',');
                        fw.append("ToDate");fw.append(',');
                        fw.append("Total Sales");fw.append(',');fw.append('\n');
                        fw.append(fromDate);fw.append(',');
                        fw.append(toDate);fw.append(',');
                        fw.append(""+tvFinalSaleAmount.getText());fw.append(',');

                    } catch (Exception e) {
                    }
                    finally {
                        handler.sendEmptyMessage(0);
                        progDailog.dismiss();

                    }
                }
            };
            t.start();
            try {
                //File Root= Environment.getExternalStorageDirectory();
                //String filelocation=Root.getAbsolutePath() + folder_name + "/" + file_name;
                String filelocation = filename;
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                String message="File to be shared is " + "ExportSalesDataItemWise.csv" + ".";
                intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Sales Data Item Wise");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.setData(Uri.parse("mailto:"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            } catch(Exception e)  {
                System.out.println("is exception raises during sending mail"+e);
            }
        }

    }

    @Override
    public void editItemClickOnClick(View v, final int position, SaleData saleData) {
        deletedItemsList = new ArrayList();
        changedItemsList = new ArrayList();
        changedItems = false;
        deletedItems = false;
        changedSaleData = false;
        currentSaleData =  saleData;
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialogue_sale_row_edit, null);
        final TextView saleId = alertLayout.findViewById(R.id.saleId);
        final EditText etDate = alertLayout.findViewById(R.id.etDate);
        final Spinner saleTypeSpinner = alertLayout.findViewById(R.id.saleTypeSpinner);
        saleAmount = alertLayout.findViewById(R.id.saleAmount);
        final Button deleteSale = alertLayout.findViewById(R.id.deleteSale);
        recyclerViewItemEdit = alertLayout.findViewById(R.id.recyclerViewItemEdit);


        final Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy", Locale.US);
        try {
            mCalendar.setTime(sdf.parse(currentSaleData.getSaleDate()));
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
                changedSaleData = true;
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
        saleId.setText("Sale Id: "+ currentSaleData.getSaleId());
        etDate.setText(currentSaleData.getSaleDate());
        saleAmount.setText("Sale Amount: "+ currentSaleData.getSaleAmount());
        String[] saleTypes  = {"Table","Parcel","Swiggy","Zomato","Uber Eats"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item,saleTypes);
        saleTypeSpinner.setAdapter(mAdapter);
        int spinnerSelectedId = 0;
        for(String type:saleTypes){
            if(currentSaleData.getSaleType().equals(type)){
                break;
            }else{
                spinnerSelectedId++;
            }
        }
        saleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changedSaleData = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        saleTypeSpinner.setSelection(spinnerSelectedId);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Edit Sale");
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
                if(currentSaleData.getSaleItemsArray().isEmpty()){
                    int result = MainActivity.myAppDatabase.myDao().deleteSaleBySaleId(currentSaleData.getSaleId());
                    if(result>0){
                        refreshedSaleDataHashMap.remove(""+currentSaleData.getSaleId());
                        refreshedSaleDataHashMapKeys.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, refreshedSaleDataHashMapKeys.size());
                        //adapter.notifyDataSetChanged();
                        //recyclerView.invalidate();
                        int sum = 0;
                        for(SaleData sd:refreshedSaleDataHashMap.values()){
                            sum += Integer.parseInt(sd.getSaleAmount());
                        }
                        tvFinalSaleAmount.setText("Total Sale Amount: "+sum);
                        dialog.dismiss();
                        Toast.makeText(getActivity().getBaseContext(), "Sale deleted", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity().getBaseContext(), "Sale NOT deleted", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                Boolean errorOccurred = false;
                if(deletedItems || changedItems){
                    if(deletedItems)
                    {
                        for(long id: deletedItemsList)
                        synchronized (this){
                            int result = MainActivity.myAppDatabase.myDao().deleteSaleById(id);
                            if(result == 0) errorOccurred = true;
                            Log.e("Deleted ITEMS",id+" changed: "+result);
                        }
                    }
                    for(SaleItem si: currentSaleData.getSaleItemsArray())
                    synchronized (this){
                        int result = MainActivity.myAppDatabase.myDao().updateSaleById(etDate.getText().toString(),saleTypeSpinner.getSelectedItem().toString()
                                ,currentSaleData.getSaleAmount(),si.getItemQuantity(),si.getItemAmount(),si.getId());
                        if(result == 0) errorOccurred = true;
                        Log.e("Changed ITEMS",si.id+" changed: "+result);
                    }
                    if(errorOccurred == true){
                        Toast.makeText(getActivity().getBaseContext(), "Sale Items NOT updated", Toast.LENGTH_SHORT).show();
                    }else{
                        refreshedSaleDataHashMap.put(currentSaleData.getSaleId(),new SaleData(currentSaleData.getSaleId(), etDate.getText().toString(), saleTypeSpinner.getSelectedItem().toString(), currentSaleData.getSaleAmount(),currentSaleData.getSaleItemsArray()));
                        adapter.notifyItemChanged(position);
                        adapter.notifyItemRangeChanged(position, refreshedSaleDataHashMapKeys.size());
                        //adapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                        int sum = 0;
                        for(SaleData sd:refreshedSaleDataHashMap.values()){
                            sum += Integer.parseInt(sd.getSaleAmount());
                        }
                        tvFinalSaleAmount.setText("Total Sale Amount: "+sum);
                        Toast.makeText(getActivity().getBaseContext(), "Sale Items updated", Toast.LENGTH_SHORT).show();
                    }

                }
                if(changedSaleData && !deletedItems && !changedItems){
                    int result = MainActivity.myAppDatabase.myDao().updateSaleBySaleId(currentSaleData.getSaleId(),etDate.getText().toString(),saleTypeSpinner.getSelectedItem().toString(),currentSaleData.getSaleAmount());
                    if(result == 0) errorOccurred = true;
                    Log.e("Changed SALEDATA",currentSaleData.getSaleId()+" changed: "+result);
                    if(errorOccurred == true){
                        Toast.makeText(getActivity().getBaseContext(), "Sale Data NOT updated", Toast.LENGTH_SHORT).show();
                    }else{
                        refreshedSaleDataHashMap.put(currentSaleData.getSaleId(),new SaleData(currentSaleData.getSaleId(), etDate.getText().toString(), saleTypeSpinner.getSelectedItem().toString(), currentSaleData.getSaleAmount(),currentSaleData.getSaleItemsArray()));
                        adapter.notifyItemChanged(position);
                        adapter.notifyItemRangeChanged(position, refreshedSaleDataHashMapKeys.size());
                        //adapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                        int sum = 0;
                        for(SaleData sd:refreshedSaleDataHashMap.values()){
                            sum += Integer.parseInt(sd.getSaleAmount());
                        }
                        tvFinalSaleAmount.setText("Total Sale Amount: "+sum);
                        Toast.makeText(getActivity().getBaseContext(), "Sale Data updated", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        final AlertDialog dialog = alert.create();
        deleteSale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int result = MainActivity.myAppDatabase.myDao().deleteSaleBySaleId(currentSaleData.getSaleId());
                if(result>0){
                    refreshedSaleDataHashMap.remove(""+currentSaleData.getSaleId());
                    refreshedSaleDataHashMapKeys.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, refreshedSaleDataHashMapKeys.size());
                    //adapter.notifyDataSetChanged();
                    //recyclerView.invalidate();
                    int sum = 0;
                    for(SaleData sd:refreshedSaleDataHashMap.values()){
                        sum += Integer.parseInt(sd.getSaleAmount());
                    }
                    tvFinalSaleAmount.setText("Total Sale Amount: "+sum);
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


        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewItemEdit.setLayoutManager(layoutManager);
        itemEditadapter = new SaleItemEditAdapter(getActivity().getBaseContext(), currentSaleData.getSaleItemsArray());
        itemEditadapter.setEditItemQuantityCallback(this);
        recyclerViewItemEdit.setAdapter(itemEditadapter);
        //itemEditadapter.notifyDataSetChanged();
        recyclerViewItemEdit.setHasFixedSize(true);
        recyclerViewItemEdit.setItemAnimator(new DefaultItemAnimator());
        dialog.show();

    }


    @Override
    public void editItemQuantityOnChanged(View v, int position, SaleItem saleItem) {
        Log.e("currentSD before",currentSaleData.toString());
        SaleItem currentSaleItem=currentSaleData.getSaleItemsArray().get(position);
        View child = recyclerViewItemEdit.getChildAt(position);
        SaleItemEditAdapter.SaleItemEditViewHolder holder = (SaleItemEditAdapter.SaleItemEditViewHolder) recyclerViewItemEdit.getChildViewHolder(child);
        currentSaleData.getSaleItemsArray().set(position,new SaleItem(holder.tvItemName.getText().toString(),holder.etQuntity.getText().toString(),holder.tvItemAmount.getText().toString(),saleItem.getId()));
        int totalSum = 0;
        for(SaleItem si: currentSaleData.getSaleItemsArray()){
            totalSum += Integer.parseInt(si.itemAmount);
        }
        currentSaleData.setSaleAmount(""+totalSum);
        saleAmount.setText("Sale Amount: "+totalSum);
        changedItems = true;
        changedItemsList.add(saleItem.getId());
        Log.e("currentSD after",currentSaleData.toString());
    }

    @Override
    public void deleteItemOnClick(View v, int position, SaleItem saleItem) {
        Log.e("currentSD before",currentSaleData.toString());
        currentSaleData.getSaleItemsArray().remove(position);
        itemEditadapter.notifyItemRemoved(position);
        itemEditadapter.notifyItemRangeChanged(position, currentSaleData.getSaleItemsArray().size());
        int totalSum = 0;
        for(SaleItem si: currentSaleData.getSaleItemsArray()){
            totalSum += Integer.parseInt(si.itemAmount);
        }
        currentSaleData.setSaleAmount(""+totalSum);
        saleAmount.setText("Sale Amount: "+totalSum);
        deletedItems = true;
        deletedItemsList.add(saleItem.id);
        Log.e("currentSD after",currentSaleData.toString());
    }

    public class SaleData{
        String saleId, saleDate , saleType ,saleAmount;
        ArrayList<SaleItem> saleItemsArray;

        public SaleData(String saleId, String saleDate, String saleType, String saleAmount, ArrayList<SaleItem> saleItemsArray) {
            this.saleId = saleId;
            this.saleDate = saleDate;
            this.saleType = saleType;
            this.saleAmount = saleAmount;
            this.saleItemsArray = saleItemsArray;
        }

        public String getSaleId() {
            return saleId;
        }

        public void setSaleId(String saleId) {
            this.saleId = saleId;
        }

        public String getSaleDate() {
            return saleDate;
        }

        public void setSaleDate(String saleDate) {
            this.saleDate = saleDate;
        }

        public String getSaleType() {
            return saleType;
        }

        public void setSaleType(String saleType) {
            this.saleType = saleType;
        }

        public String getSaleAmount() {
            return saleAmount;
        }

        public void setSaleAmount(String saleAmount) {
            this.saleAmount = saleAmount;
        }

        public ArrayList<SaleItem> getSaleItemsArray() {
            return saleItemsArray;
        }

        public void setSaleItemsArray(ArrayList<SaleItem> saleItemsArray) {
            this.saleItemsArray = saleItemsArray;
        }


        @Override
        public String toString() {
            String itemsData = "**";
            for (SaleItem si: this.saleItemsArray){
                itemsData += " ID: "+si.getId()+" ItemName: "+si.getItemName()+" ItemQuntity:"+si.getItemQuantity()+" ItemAmount:"+si.getItemAmount()+".";
            }
            return "SaleID: "+this.saleId + " saleDate: "+ this.saleDate + " saleType: " + this.saleType + " saleAmount: "+this.saleAmount+itemsData;
        }
    }

    public class SaleItem {
        String itemName, itemQuantity, itemAmount;
        long id = -1;
//        public SaleItem(String itemName, String itemQuantity, String itemAmount) {
//            this.itemName = itemName;
//            this.itemQuantity = itemQuantity;
//            this.itemAmount = itemAmount;
//        }

        public SaleItem(String itemName, String itemQuantity, String itemAmount, long id) {
            this.itemName = itemName;
            this.itemQuantity = itemQuantity;
            this.itemAmount = itemAmount;
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemQuantity() {
            return itemQuantity;
        }

        public void setItemQuantity(String itemQuantity) {
            this.itemQuantity = itemQuantity;
        }

        public String getItemAmount() {
            return itemAmount;
        }

        public void setItemAmount(String itemAmount) {
            this.itemAmount = itemAmount;
        }
        @Override
        public String toString() {

            return "id: "+this.id+" itemName: "+this.itemName + " itemQuantity: "+ this.itemQuantity + " itemAmount: " + this.itemAmount;
        }
    }

    public class SaleDataItemWise{
        String itemId, itemCode , itemName ,itemPrice, totalItemSaleAmount;
        ArrayList<SaleItemItemWise> saleItemsArray;

        public SaleDataItemWise(String itemId, String itemCode, String itemName, String itemPrice, String totalItemSaleAmount, ArrayList<SaleItemItemWise> saleItemsArray) {
            this.itemId = itemId;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.totalItemSaleAmount = totalItemSaleAmount;
            this.saleItemsArray = saleItemsArray;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(String itemPrice) {
            this.itemPrice = itemPrice;
        }

        public String getTotalItemSaleAmount() {
            return totalItemSaleAmount;
        }

        public void setTotalItemSaleAmount(String totalItemSaleAmount) {
            this.totalItemSaleAmount = totalItemSaleAmount;
        }

        public ArrayList<SaleItemItemWise> getSaleItemsArray() {
            return saleItemsArray;
        }

        public void setSaleItemsArray(ArrayList<SaleItemItemWise> saleItemsArray) {
            this.saleItemsArray = saleItemsArray;
        }

        @Override
        public String toString() {
            String itemsData = "**";
            for (SaleItemItemWise si: this.saleItemsArray){
                itemsData += si.toString();
            }
            return "itemId: "+this.itemId + " itemCode: "+ this.itemCode + " itemName: " +
                    this.itemName + " itemPrice: "+ this.itemPrice + " totalItemSaleAmount: "+this.totalItemSaleAmount + " itemsData: "+itemsData;
        }
    }

    public class SaleItemItemWise {
        String saleId, saleDate , saleType , itemQuantity, itemAmount;


        public SaleItemItemWise(String saleId, String saleDate, String saleType, String itemQuantity, String itemAmount) {
            this.saleId = saleId;
            this.saleDate = saleDate;
            this.saleType = saleType;
            this.itemQuantity = itemQuantity;
            this.itemAmount = itemAmount;
        }

        public String getSaleId() {
            return saleId;
        }

        public void setSaleId(String saleId) {
            this.saleId = saleId;
        }

        public String getSaleDate() {
            return saleDate;
        }

        public void setSaleDate(String saleDate) {
            this.saleDate = saleDate;
        }

        public String getSaleType() {
            return saleType;
        }

        public void setSaleType(String saleType) {
            this.saleType = saleType;
        }

        public String getItemQuantity() {
            return itemQuantity;
        }

        public void setItemQuantity(String itemQuantity) {
            this.itemQuantity = itemQuantity;
        }

        public String getItemAmount() {
            return itemAmount;
        }

        public void setItemAmount(String itemAmount) {
            this.itemAmount = itemAmount;
        }

        @Override
        public String toString() {
            return "saleId: "+this.saleId + " saleDate: "+ this.saleDate + " saleType: " +
                    this.saleType + " itemQuantity: "+ this.itemQuantity + " itemAmount: " + this.itemAmount;
        }
    }

}
