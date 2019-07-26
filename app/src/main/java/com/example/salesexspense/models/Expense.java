package com.example.salesexspense.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "expense")
public class Expense {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "created_date")
    private String created_date;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "amount")
    private String amount;

    public Expense() {
    }

    public Expense(int id, String date, String category, String description, String amount, String created_date) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", created_date='" + created_date + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                '}';
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
