package com.gruppe1.pem.challengeme.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.Category;
import com.gruppe1.pem.challengeme.Item;

import java.util.ArrayList;
import java.util.List;

public class CategoryDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns =
         { Constants.DB_COLUMN_CATEGORY_ID, Constants.DB_COLUMN_CATEGORY_NAME_EN,
               Constants.DB_COLUMN_CATEGORY_NAME_DE,
               Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID,
               Constants.DB_COLUMN_CATEGORY_DEFAULT_ATTR_TYPE, Constants.DB_COLUMN_CATEGORY_COLOR,
               Constants.DB_COLUMN_CATEGORY_ICON };

   public CategoryDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public List<Category> getAllBaseCategories() {
      SharedPreferences prefs =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      String orderBy;
      switch (language) {
         case "en":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
         case "de":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_DE + " ASC";
            break;
         default:
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
      }
      List<Category> categories = new ArrayList<>();
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID + "= -1 ", null, null, null, orderBy);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Category category = cursorToCategory(cursor);
         categories.add(category);
         cursor.moveToNext();
      }
      cursor.close();
      return categories;
   }

   public Category getCategory(int id) {
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_ID + "=" + id, null, null, null, null);

      if (cursor.getCount() == 0) {
         return null;
      }
      cursor.moveToFirst();
      Category category = cursorToCategory(cursor);
      cursor.close();
      return category;
   }

   public Category editCategory(int id, String nameEn, String nameDe, int parentCategoryId,
         int defaultSizeType, String icon, String color) {
      if (id == -1) {
         return createCategory(nameEn, nameDe, parentCategoryId, defaultSizeType, icon, color);
      }
      String iconValue = (icon != null) ? icon : Constants.DEFAULT_CAT_ICON;
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_CATEGORY_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_CATEGORY_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID, parentCategoryId);
      values.put(Constants.DB_COLUMN_CATEGORY_DEFAULT_ATTR_TYPE, defaultSizeType);
      values.put(Constants.DB_COLUMN_CATEGORY_ICON, iconValue);
      values.put(Constants.DB_COLUMN_CATEGORY_COLOR, color);
      database.update(Constants.CATEGORIES_DB_TABLE, values,
            Constants.DB_COLUMN_CATEGORY_ID + "=" + id, null);
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      Category newCategory = cursorToCategory(cursor);
      cursor.close();
      return newCategory;
   }

   public Category createCategory(String nameEn, String nameDe, int parentCategoryId,
         int defaultSizeType, String icon, String color) {
      String iconValue = (icon != null) ? icon : Constants.DEFAULT_CAT_ICON;
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_CATEGORY_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_CATEGORY_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID, parentCategoryId);
      values.put(Constants.DB_COLUMN_CATEGORY_DEFAULT_ATTR_TYPE, defaultSizeType);
      values.put(Constants.DB_COLUMN_CATEGORY_ICON, iconValue);
      values.put(Constants.DB_COLUMN_CATEGORY_COLOR, color);
      long insertId = database.insert(Constants.CATEGORIES_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      Category newCategory = cursorToCategory(cursor);
      cursor.close();
      return newCategory;
   }

   public void deleteCategory(int id) {
      System.out.println("Category deleted with id: " + id);
      ItemDataSource itemDataSource = new ItemDataSource(context);
      ArrayList<Item> items = itemDataSource.getItemsByCategoryId(id, true);
      for (Item c : items) {
         itemDataSource.deleteItem(c.getId());
      }
      database.delete(Constants.CATEGORIES_DB_TABLE, Constants.DB_COLUMN_CATEGORY_ID + " = " + id,
            null);
   }

   public List<Category> getAllCategories() {
      String orderBy;
      SharedPreferences prefs =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      switch (language) {
         case "en":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
         case "de":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_DE + " ASC";
            break;
         default:
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
      }
      List<Category> categories = new ArrayList<>();
      Cursor cursor =
            database.query(Constants.CATEGORIES_DB_TABLE, allColumns, null, null, null, null,
                  orderBy);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Category category = cursorToCategory(cursor);
         categories.add(category);
         cursor.moveToNext();
      }
      cursor.close();
      return categories;
   }

   public List<Category> getCategoriesWithParentCategory(int parentCategoryId) {
      SharedPreferences prefs =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      String orderBy;
      switch (language) {
         case "en":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
         case "de":
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_DE + " ASC";
            break;
         default:
            orderBy = Constants.DB_COLUMN_CATEGORY_NAME_EN + " ASC";
            break;
      }
      List<Category> categories = new ArrayList<>();
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID + "=" + parentCategoryId, null, null,
            null, orderBy);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Category category = cursorToCategory(cursor);
         categories.add(category);
         cursor.moveToNext();
      }
      cursor.close();
      return categories;
   }

   public List<Integer> getAllUnderCategoryIds(int parentCategoryId) {
      List<Integer> categoryIds = new ArrayList<>();
      Cursor cursor = database.query(Constants.CATEGORIES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID + "=" + parentCategoryId, null, null,
            null, null);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         categoryIds.add(cursor.getInt(0));
         categoryIds.addAll(getAllUnderCategoryIds(cursor.getInt(0)));
         cursor.moveToNext();
      }
      cursor.close();
      return categoryIds;
   }

   private Category cursorToCategory(Cursor cursor) {
      Category category = new Category();
      category.setId(cursor.getInt(0));
      category.setNameEn(cursor.getString(1));
      category.setNameDe(cursor.getString(2));
      category.setParentCategoryId(cursor.getInt(3));
      category.setDefaultSizeType(cursor.getInt(4));
      category.setColor(cursor.getString(5));
      category.setIcon(cursor.getString(6));
      return category;
   }
}
