package com.gruppe1.pem.challengeme.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ItemDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns = { Constants.DB_COLUMN_ITEM_ID, Constants.DB_COLUMN_ITEM_NAME,
         Constants.DB_COLUMN_ITEM_IMAGE_FILE, Constants.DB_COLUMN_ITEM_CATEGORY_ID,
         Constants.DB_COLUMN_ITEM_IS_WISH, Constants.DB_COLUMN_ITEM_PRIMARY_COLOR,
         Constants.DB_COLUMN_ITEM_RATING };

   public ItemDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public Item getItem(int id) {
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_ID + "=" + id, null, null, null, null);
      cursor.moveToFirst();
      Item item = cursorToItem(cursor);
      cursor.close();
      return item;
   }

   public Item editItemIsWishlist(int id, int isWish) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ITEM_IS_WISH, isWish);
      database.update(Constants.ITEMS_DB_TABLE, values, Constants.DB_COLUMN_ITEM_ID + "=" + id,
            null);
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      Item newItem = cursorToItem(cursor);
      cursor.close();
      return newItem;
   }

   public Item editItem(int id, String name, String imageFile, int categoryId, int isWish,
         int primaryColorId, float rating) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ITEM_NAME, name);
      values.put(Constants.DB_COLUMN_ITEM_IMAGE_FILE, imageFile);
      values.put(Constants.DB_COLUMN_ITEM_CATEGORY_ID, categoryId);
      values.put(Constants.DB_COLUMN_ITEM_IS_WISH, isWish);
      values.put(Constants.DB_COLUMN_ITEM_PRIMARY_COLOR, primaryColorId);
      values.put(Constants.DB_COLUMN_ITEM_RATING, rating);
      database.update(Constants.ITEMS_DB_TABLE, values, Constants.DB_COLUMN_ITEM_ID + "=" + id,
            null);
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      Item newItem = cursorToItem(cursor);
      cursor.close();
      return newItem;
   }

   public Item createItem(String name, String imageFile, int categoryId, int isWish,
         int primaryColorId, float rating) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ITEM_NAME, name);
      values.put(Constants.DB_COLUMN_ITEM_IMAGE_FILE, imageFile);
      values.put(Constants.DB_COLUMN_ITEM_CATEGORY_ID, categoryId);
      values.put(Constants.DB_COLUMN_ITEM_IS_WISH, isWish);
      values.put(Constants.DB_COLUMN_ITEM_PRIMARY_COLOR, primaryColorId);
      values.put(Constants.DB_COLUMN_ITEM_RATING, rating);
      long insertId = database.insert(Constants.ITEMS_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      Item newItem = cursorToItem(cursor);
      cursor.close();
      return newItem;
   }

   public void deleteItem(int id) {
      System.out.println("Item deleted with id: " + id);
      database.delete(Constants.ITEMS_DB_TABLE, Constants.DB_COLUMN_ITEM_ID + " = " + id, null);
      CompareDataSource dataSource = new CompareDataSource(context);
      dataSource.deleteComparesByItemId(id);
   }

   public List<Item> getAllItems() {
      List<Item> items = new ArrayList<>();
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_IS_WISH + "=0", null, null, null,
            Constants.DB_COLUMN_ITEM_NAME + " ASC");

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Item item = cursorToItem(cursor);
         items.add(item);
         cursor.moveToNext();
      }
      cursor.close();
      return items;
   }

   public List<Item> getAllWishlistItems() {
      List<Item> items = new ArrayList<>();
      Cursor cursor = database.query(Constants.ITEMS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ITEM_IS_WISH + "=1", null, null, null,
            Constants.DB_COLUMN_ITEM_NAME + " ASC");

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Item item = cursorToItem(cursor);
         items.add(item);
         cursor.moveToNext();
      }
      cursor.close();
      return items;
   }

   private Item cursorToItem(Cursor cursor) {
      Item item = new Item();
      item.setId(cursor.getInt(0));
      item.setName(cursor.getString(1));
      item.setImageFile(cursor.getString(2));
      item.setCategoryId(cursor.getInt(3));
      item.setIsWish(cursor.getInt(4));
      item.setPrimaryColorId(cursor.getInt(5));
      item.setRating(cursor.getFloat(6));
      return item;
   }

   public int getItemsCountByCategoryId(int p_categoryId, boolean showAlsoWishlistItems) {
      CategoryDataSource datasource = new CategoryDataSource(context);

      List<Integer> allUnderCategoriesIds = datasource.getAllUnderCategoryIds(p_categoryId);

      allUnderCategoriesIds.add(p_categoryId);
      String categoriesIdsExpression = "";
      if (allUnderCategoriesIds.size() > 0) {
         String categoriesIdsString = allUnderCategoriesIds.toString();
         categoriesIdsString = categoriesIdsString.replace("[", "(");
         categoriesIdsString = categoriesIdsString.replace("]", ")");
         categoriesIdsExpression =
               (Constants.DB_COLUMN_ITEM_CATEGORY_ID + " in " + categoriesIdsString);
      }
      String wishlistExpression = "";
      if (!showAlsoWishlistItems) {
         wishlistExpression = (" AND " + Constants.DB_COLUMN_ITEM_IS_WISH + "=0");
      }
      String selection = categoriesIdsExpression + wishlistExpression;
      int itemsCount = 0;
      Cursor cursor =
            database.query(Constants.ITEMS_DB_TABLE, new String[] { "COUNT(*)" }, selection, null,
                  null, null, null);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         itemsCount = cursor.getInt(0);
         cursor.moveToNext();
      }
      cursor.close();
      return itemsCount;
   }

   public ArrayList<Item> getSearchResults(String query, HashSet<Integer> filterCategoryIds,
         boolean filterWishlist, int filterRating, ArrayList<Integer> filterColorIds) {
      String filterWishlistExpression =
            filterWishlist ? " AND " + Constants.DB_COLUMN_ITEM_IS_WISH + "=1" : "";
      String filterRatingExpression = (filterRating != -1) ?
            (" AND " + Constants.DB_COLUMN_ITEM_RATING + "=" + filterRating) : "";
      String filterColorExpression = "";
      if (filterColorIds.size() > 0) {
         String filterColorIdsString = filterColorIds.toString();
         filterColorIdsString = filterColorIdsString.replace("[", "(");
         filterColorIdsString = filterColorIdsString.replace("]", ")");
         filterColorExpression =
               (" AND " + Constants.DB_COLUMN_ITEM_PRIMARY_COLOR + " in " + filterColorIdsString);
      }
      String filterCategoryExpression = "";
      if (filterCategoryIds.size() > 0) {
         CategoryDataSource categoryDataSource = new CategoryDataSource(context);
         HashSet<Integer> underCategoriesList = new HashSet<>();
         for (Integer id : filterCategoryIds) {
            underCategoriesList.addAll(categoryDataSource.getAllUnderCategoryIds(id));
         }
         if (underCategoriesList.size() > 0) {
            filterCategoryIds.addAll(underCategoriesList);
         }
         String filterCategoryIdsString = filterCategoryIds.toString();
         filterCategoryIdsString = filterCategoryIdsString.replace("[", "(");
         filterCategoryIdsString = filterCategoryIdsString.replace("]", ")");
         filterCategoryExpression =
               (" AND " + Constants.DB_COLUMN_ITEM_CATEGORY_ID + " in " + filterCategoryIdsString);
      }
      String whereExpression =
            Constants.DB_COLUMN_ITEM_NAME + " LIKE '%" + query + "%'" + filterCategoryExpression +
                  filterWishlistExpression +
                  filterRatingExpression + filterColorExpression;

      Cursor cursor =
            database.query(Constants.ITEMS_DB_TABLE, allColumns, whereExpression, null, null, null,
                  null);

      ArrayList<Item> searchItems = new ArrayList<>();

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Item item = cursorToItem(cursor);
         searchItems.add(item);
         cursor.moveToNext();
      }
      cursor.close();
      return searchItems;
   }

   public ArrayList<Item> getItemsByCategoryId(int p_categoryId, boolean showAlsoWishlistItems) {
      String whereExpression;
      if (showAlsoWishlistItems) {
         whereExpression = Constants.DB_COLUMN_ITEM_CATEGORY_ID + "='" + p_categoryId + "'";
      } else {
         whereExpression = Constants.DB_COLUMN_ITEM_CATEGORY_ID + "='" + p_categoryId + "' AND " +
               Constants.DB_COLUMN_ITEM_IS_WISH + "=0";
      }

      Cursor cursor =
            database.query(Constants.ITEMS_DB_TABLE, allColumns, whereExpression, null, null, null,
                  Constants.DB_COLUMN_ITEM_NAME + " ASC");

      ArrayList<Item> itemList = new ArrayList<>();

      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
         Item item = cursorToItem(cursor);
         itemList.add(item);
         cursor.moveToNext();
      }
      cursor.close();
      return itemList;
   }

   public ArrayList<Item> getItemsByCategoryIdWithImage(int p_categoryId,
         boolean showAlsoWishlistItems) {
      String whereExpression;
      if (showAlsoWishlistItems) {
         whereExpression = Constants.DB_COLUMN_ITEM_CATEGORY_ID + "='" + p_categoryId + "'";
      } else {
         whereExpression = Constants.DB_COLUMN_ITEM_CATEGORY_ID + "='" + p_categoryId + "' AND " +
               Constants.DB_COLUMN_ITEM_IS_WISH + "=0 AND NOT " +
               Constants.DB_COLUMN_ITEM_IMAGE_FILE + "=''";
      }

      Cursor cursor =
            database.query(Constants.ITEMS_DB_TABLE, allColumns, whereExpression, null, null, null,
                  Constants.DB_COLUMN_ITEM_NAME + " ASC");

      ArrayList<Item> itemList = new ArrayList<>();

      cursor.moveToFirst();

      while (!cursor.isAfterLast()) {
         Item item = cursorToItem(cursor);
         itemList.add(item);
         cursor.moveToNext();
      }
      cursor.close();
      return itemList;
   }
}
