package com.example.salesexspense.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "item")
public class Item {


    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "item_name")
    private String name;

    @ColumnInfo(name = "item_price")
    private String price;

    @ColumnInfo(name = "item_menu_cat")
    private String menuCategory;

    @ColumnInfo(name = "item_code")
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
