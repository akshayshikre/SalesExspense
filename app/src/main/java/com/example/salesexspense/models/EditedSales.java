package com.example.salesexspense.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "edited_sales")
public class EditedSales {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "created_date")
    private String created_date;

    @ColumnInfo(name = "item")
    private String item;

    @ColumnInfo(name = "quantity")
    private String quantity;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "sale_id")
    private String sale_id;

    @ColumnInfo(name = "sale_amount")
    private String sale_amount;

    @ColumnInfo(name = "sale_type")
    private String sale_type;

    @ColumnInfo(name = "edit_description")
    private String edit_description;


    public EditedSales(String date, String item, String quantity, String amount, String sale_id, String sale_amount, String sale_type, String created_date, String edit_description) {
        this.date = date;
        this.item = item;
        this.quantity = quantity;
        this.amount = amount;
        this.sale_id = sale_id;
        this.sale_amount = sale_amount;
        this.sale_type = sale_type;
        this.created_date = created_date;
        this.edit_description = edit_description;
    }

    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSale_amount() {
        return sale_amount;
    }

    public void setSale_amount(String sale_amount) {
        this.sale_amount = sale_amount;
    }

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String sale_type) {
        this.sale_type = sale_type;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getEdit_description() {
        return edit_description;
    }

    public void setEdit_description(String edit_description) {
        this.edit_description = edit_description;
    }
}
