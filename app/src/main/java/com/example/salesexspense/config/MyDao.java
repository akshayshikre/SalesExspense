package com.example.salesexspense.config;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.salesexspense.models.Category;
import com.example.salesexspense.models.Expense;
import com.example.salesexspense.models.Item;
import com.example.salesexspense.models.MenuCategory;
import com.example.salesexspense.models.Sales;

import java.util.List;


@Dao
public interface MyDao {

    @Insert
    public void addCategory(Category category);

    @Insert
    public void addMenuCategory(MenuCategory category);

    @Query("select * from menu_category")
    public List<MenuCategory> getMenuCategories();

    @Query("select category_name from category")
    public List<String> getCategories();

    @Query("select * from category")
    public List<Category> getAllCategories();

    @Insert
    public void addExpense(Expense expense);

    @Query("select * from expense")
    public List<Expense> getExpenses();

    @Query("UPDATE expense SET date = :date, category = :category, description = :description, amount = :amount WHERE id = :id")
    int updateExpense(int id, String date, String category, String description, String amount);

    @Query("DELETE FROM expense WHERE id = :id")
    int deleteExpenseById(int id);

    @Insert
    public void addItem(Item item);

    @Query("select * from item")
    public List<Item> getItems();

    @Insert
    public void addSales(Sales sales);

    @Query("SELECT * FROM sales ORDER BY sale_id DESC LIMIT 1;")
    public List<Sales> getLastSale();

    @Query("SELECT * FROM sales;")
    public List<Sales> getSales();

    @Query("UPDATE sales SET date = :date, sale_type = :sale_type, sale_amount = :sale_amount WHERE sale_id = :sale_id")
    int updateSaleBySaleId(String sale_id, String date, String sale_type, String sale_amount);

    @Query("UPDATE sales SET date = :date, sale_type = :sale_type, sale_amount = :sale_amount, quantity = :quantity, amount = :amount WHERE id = :id")
    int updateSaleById(String date, String sale_type, String sale_amount, String quantity, String amount, long id);

    @Query("DELETE FROM sales WHERE id = :id")
    int deleteSaleById(long id);

    @Query("DELETE FROM sales WHERE sale_id = :sale_id")
    int deleteSaleBySaleId(String sale_id);

}
