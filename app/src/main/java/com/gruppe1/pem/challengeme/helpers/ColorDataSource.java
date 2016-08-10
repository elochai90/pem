package com.gruppe1.pem.challengeme.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.Color;

import java.util.ArrayList;
import java.util.List;

public class ColorDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns = { Constants.DB_COLUMN_COLOR_ID, Constants.DB_COLUMN_COLOR_NAME_EN,
         Constants.DB_COLUMN_COLOR_NAME_DE, Constants.DB_COLUMN_COLOR_HEX };

   public ColorDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public Color getColor(int id) {
      Cursor cursor = database.query(Constants.COLORS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COLOR_ID + "=" + id, null, null, null, null);
      cursor.moveToFirst();
      if (cursor.getCount() == 0) {
         return null;
      }
      Color color = cursorToColor(cursor);
      cursor.close();
      return color;
   }

   public Color editColor(int id, String nameEn, String nameDe, String hex) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_COLOR_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_COLOR_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_COLOR_HEX, hex);
      database.update(Constants.COLORS_DB_TABLE, values, Constants.DB_COLUMN_COLOR_ID + "=" + id,
            null);
      Cursor cursor = database.query(Constants.COLORS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COLOR_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      Color color = cursorToColor(cursor);
      cursor.close();
      return color;
   }

   public Color createColor(String nameEn, String nameDe, String hex) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_COLOR_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_COLOR_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_COLOR_HEX, hex);
      long insertId = database.insert(Constants.COLORS_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.COLORS_DB_TABLE, allColumns,
            Constants.DB_COLUMN_COLOR_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      Color color = cursorToColor(cursor);
      cursor.close();
      return color;
   }

   public void deleteColor(Color color) {
      long id = color.getId();
      System.out.println("Color deleted with id: " + id);
      database.delete(Constants.COLORS_DB_TABLE, Constants.DB_COLUMN_COLOR_ID + " = " + id, null);
   }

   public List<Color> getAllColors() {
      SharedPreferences prefs =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
      String language = prefs.getString(Constants.KEY_LANGUAGE, "");
      String order;
      switch (language) {
         case "en":
            order = Constants.DB_COLUMN_COLOR_NAME_EN + " ASC";
            break;
         case "de":
            order = Constants.DB_COLUMN_COLOR_NAME_DE + " ASC";
            break;
         default:
            order = Constants.DB_COLUMN_COLOR_NAME_EN + " ASC";
      }
      List<Color> colors = new ArrayList<>();
      Cursor cursor =
            database.query(Constants.COLORS_DB_TABLE, allColumns, null, null, null, null, order);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Color color = cursorToColor(cursor);
         colors.add(color);
         cursor.moveToNext();
      }
      cursor.close();
      return colors;
   }

   private Color cursorToColor(Cursor cursor) {
      Color color = new Color();
      color.setId(cursor.getInt(0));
      color.setNameEn(cursor.getString(1));
      color.setNameDe(cursor.getString(2));
      color.setHexColor(cursor.getString(3));
      return color;
   }
}
