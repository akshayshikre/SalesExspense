package com.example.salesexspense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText  name , price, code;
    private Button addCategory ;
    private Spinner menuCatSpinner;
    List<MenuCategory> menuCategoriesList;
    ArrayList<String> menuCategoryName;


    public AddItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_item, container, false);

        name = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        code = view.findViewById(R.id.code);
        menuCatSpinner= view.findViewById(R.id.spinnerAddMenuCat);
        menuCatSpinner.setOnItemSelectedListener(this);
        addCategory = view.findViewById(R.id.addCategory);
        addCategory.setOnClickListener(this);

        menuCategoriesList = MainActivity.myAppDatabase.myDao().getMenuCategories();
        menuCategoryName = new ArrayList<String>();

        for(MenuCategory cat : menuCategoriesList){
            String name = cat.getName();
            String description = cat.getDescription();
            String color = cat.getColor();
            Log.e("MenuCategory List","name: "+name+" description:"+description+" color:"+color);
            menuCategoryName.add(name);
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, menuCategoryName);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        menuCatSpinner.setAdapter(dataAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        String cname = name.getText().toString();
        String cprice = price.getText().toString();
        String ccode = code.getText().toString();


        Item item = new Item();
        item.setName(cname);
        item.setPrice(cprice);
        item.setCode(ccode);
        String menuCatId="";
        if(menuCatSpinner.getSelectedItemPosition()>=0){
            menuCatId=String.valueOf(menuCategoriesList.get(menuCatSpinner.getSelectedItemPosition()).getId());
            item.setMenuCategory(menuCatId);
        }


        MainActivity.myAppDatabase.myDao().addItem(item);
        Toast.makeText(getActivity(),"Item added Successfully with, name:"+cname+" price:"+cprice+" menuCategoryId:"+menuCatId+" code:"+ccode,Toast.LENGTH_SHORT).show();
        name.setText("");
        price.setText("");
        code.setText("");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
