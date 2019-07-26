package com.example.salesexspense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salesexspense.models.MenuCategory;


public class AddMenuCategoryFragment extends Fragment implements View.OnClickListener {

    private EditText name , description, color;
    private Button  addCategory ;


    public AddMenuCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_menu_category, container, false);

        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);

        addCategory = view.findViewById(R.id.addMenuCategory);
        addCategory.setOnClickListener(this);

        color = view.findViewById(R.id.color);
        color.setText("#ffffff");

        return view;
    }

    @Override
    public void onClick(View v) {

        String cname = name.getText().toString();
        String cdescription = description.getText().toString();
        String ccolor = color.getText().toString();

        MenuCategory category = new MenuCategory();
        category.setName(cname);
        category.setDescription(cdescription);
        category.setColor(ccolor);

        MainActivity.myAppDatabase.myDao().addMenuCategory(category);
        Log.e("AddMenuCat","Category added Successfully with name:"+cname+" cdescription:"+cdescription+" ccolor:");
        Toast.makeText(getActivity(),"Category added Successfully with name:"+cname+" cdescription:"+cdescription+" ccolor:"+ccolor,Toast.LENGTH_SHORT).show();
        name.setText("");
        description.setText("");
        color.setText("#ffffff");
    }
}
