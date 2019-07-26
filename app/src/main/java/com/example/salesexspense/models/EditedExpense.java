package com.example.salesexspense.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "edited_expense")
public class EditedExpense {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "expense_id")
    private String expenseId;

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

    public EditedExpense() {
    }

    public EditedExpense(int id, String date, String category, String description, String amount, String expenseId, String created_date) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.expenseId = expenseId;
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
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

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(String created_date) {
        this.created_date = created_date;
    }
}
