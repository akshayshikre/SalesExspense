package com.example.salesexspense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.salesexspense.models.Category;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddCategoryFragment extends Fragment implements View.OnClickListener {

    private EditText  name , description;
    private Button addCategory ;


    public AddCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_category, container, false);

        name = view.findViewById(R.id.name);
        description = view.findViewById(R.id.description);

        addCategory = view.findViewById(R.id.addCategory);
        addCategory.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

       String cname = name.getText().toString();
       String cdescription = description.getText().toString();

       Category category = new Category();
        category.setName(cname);
        category.setDescription(cdescription);

       MainActivity.myAppDatabase.myDao().addCategory(category);
        Toast.makeText(getActivity(),"Category added Successfully ",Toast.LENGTH_SHORT).show();
        name.setText("");
        description.setText("");

    }
}
