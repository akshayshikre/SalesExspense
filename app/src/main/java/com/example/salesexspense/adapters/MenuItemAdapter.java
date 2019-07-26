package com.example.salesexspense.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.salesexspense.MainActivity;
import com.example.salesexspense.R;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;

import java.util.List;

public class MenuItemAdapter extends BaseAdapter {
    private Context mContext;
    public List<Item> menuItemArrayList;
    public List<MenuCategory> menuCategoriesList;

    public interface MenuItemClick {
        void menuItemClickOnClick(View v, int position, Item item, MenuCategory menuCat, TextView tv);
    }

    private MenuItemClick menuItemClickCallback;

    public void setMenuItemClickListener(MenuItemClick listener) {
        this.menuItemClickCallback = listener;
    }

    // Constructor
    public MenuItemAdapter(Context c, List<Item> menuItemArrayList){
        mContext = c;
        this.menuItemArrayList = menuItemArrayList;
        menuCategoriesList = MainActivity.myAppDatabase.myDao().getMenuCategories();
    }

    @Override
    public int getCount() {
        return menuItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell, parent, false);
        }
        String color = "#ffffff";
        MenuCategory menuCat = null;
        for(MenuCategory cat : menuCategoriesList){
            if(String.valueOf(cat.getId()).equals(menuItemArrayList.get(position).getMenuCategory())){
                color = cat.getColor();
                menuCat = cat;
                Log.e("MenuCategory","name: "+cat.getName()+" description:"+cat.getDescription()+" color:"+cat.getColor());
            }
        }

        final Button codeButton = (Button) convertView.findViewById(R.id.btnCell);
        final TextView quantTextView = (TextView) convertView.findViewById(R.id.tvCell);
        codeButton.setBackgroundColor(Color.parseColor(color));
        codeButton.setText(menuItemArrayList.get(position).getCode());
        final MenuCategory finalMenuCat = menuCat;
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuItemClickCallback != null) {
                    menuItemClickCallback.menuItemClickOnClick(v, position, menuItemArrayList.get(position), finalMenuCat, quantTextView);
                }
            }
        });

        return convertView;
    }

}