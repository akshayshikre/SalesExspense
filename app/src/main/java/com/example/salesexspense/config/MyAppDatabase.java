package com.example.salesexspense.config;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.salesexspense.models.Category;
import com.example.salesexspense.models.Expense;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;
import com.example.salesexspense.models.Sales;

@Database(entities = {Category.class, Item.class, Expense.class, Sales.class, MenuCategory.class},version = 1,exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {

    public abstract MyDao myDao();
}
