package com.gruppe1.pem.challengeme.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.Compare;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CompareDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns = { Constants.DB_COLUMN_COMPARE_ID, Constants.DB_COLUMN_COMPARE_NAME,
         Constants.DB_COLUMN_COMPARE_ITEM_IDS, Constants.DB_COLUMN_COMPARE_SAVE_DATE };

   public CompareDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public Compare getCompare(int id) {
      if (id == -1) {
         return createCompare("", -1, -1);
      }
      Cursor cursor = database.query(Constants.COMPARES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COMPARE_ID + "=" + id, null, null, null, null);
      cursor.moveToFirst();
      Compare compare = cursorToCompare(cursor);
      cursor.close();
      return compare;
   }

   public Compare editCompare(int id, String name, int itemId1, int itemId2) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_COMPARE_NAME, name);
      values.put(Constants.DB_COLUMN_COMPARE_ITEM_IDS, itemId1 + "|" + itemId2);
      database.update(Constants.COMPARES_DB_TABLE, values,
            Constants.DB_COLUMN_COMPARE_ID + "=" + id, null);
      Cursor cursor = database.query(Constants.COMPARES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COMPARE_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      Compare newCompare = cursorToCompare(cursor);
      cursor.close();
      return newCompare;
   }

   public Compare createCompare(String name, int itemId1, int itemId2) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_COMPARE_NAME, name);
      values.put(Constants.DB_COLUMN_COMPARE_ITEM_IDS, itemId1 + "|" + itemId2);
      long insertId = database.insert(Constants.COMPARES_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.COMPARES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COMPARE_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      Compare newCompare = cursorToCompare(cursor);
      cursor.close();
      return newCompare;
   }

   public void deleteCompare(int id) {
      System.out.println("Compare deleted with id: " + id);
      database.delete(Constants.COMPARES_DB_TABLE, Constants.DB_COLUMN_COMPARE_ID + " = " + id,
            null);
   }

   public void deleteComparesByItemId(int itemId) {
      database.delete(Constants.COMPARES_DB_TABLE,
            Constants.DB_COLUMN_COMPARE_ITEM_IDS + " LIKE '" + itemId + "|%' OR " +
                  Constants.DB_COLUMN_COMPARE_ITEM_IDS + " LIKE '%|" + itemId + "'", null);
   }

   public List<Compare> getAllCompares() {
      List<Compare> compares = new ArrayList<>();
      Cursor cursor =
            database.query(Constants.COMPARES_DB_TABLE, allColumns, null, null, null, null, null);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Compare compare = cursorToCompare(cursor);
         compares.add(compare);
         cursor.moveToNext();
      }
      cursor.close();
      return compares;
   }

   private Compare cursorToCompare(Cursor cursor) {
      Compare compare = new Compare();
      compare.setId(cursor.getInt(0));
      compare.setName(cursor.getString(1));
      compare.setTimestamp(cursor.getString(3));

      String[] idValues = cursor.getString(2)
            .split(Pattern.quote("|"));
      ArrayList<Integer> itemIds = new ArrayList<>();
      for (String idValue : idValues) {
         itemIds.add(Integer.parseInt(idValue));
      }
      compare.setItemIds(itemIds);
      return compare;
   }
}
