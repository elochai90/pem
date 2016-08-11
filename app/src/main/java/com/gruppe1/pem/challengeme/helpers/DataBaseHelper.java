package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * handles the database connection and requests
 */
public class DataBaseHelper extends SQLiteOpenHelper {

   /* For update DB do:
      - update DATABASE_VERSION
      - in onUpgrade:
            if (oldVersion < 2) {
               db.execSQL(TODO);
               mDataBaseInstance = db;
            }
      - in DataSource: save/create new column, save new column in allColumns, in cursorTo add new
       column
    */

   public static DataBaseHelper mInstance = null;
   public static SQLiteDatabase mDataBaseInstance = null;

   private static final String DATABASE_NAME = "organice";
   private static final int DATABASE_VERSION = 1;

   String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + Constants.CATEGORIES_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_CATEGORY_NAME_EN + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_CATEGORY_NAME_DE + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_CATEGORY_PARENT_CATEGORY_ID + " INT(5) NOT NULL DEFAULT -1," +
         Constants.DB_COLUMN_CATEGORY_DEFAULT_ATTR_TYPE + " INT(3) NOT NULL DEFAULT -1," +
         Constants.DB_COLUMN_CATEGORY_ICON + " VARCHAR(50) DEFAULT 'kleiderbuegel'," +
         Constants.DB_COLUMN_CATEGORY_COLOR + " VARCHAR(50) DEFAULT '5d5d5d'" +
         ")";

   String CREATE_TABLE_ITEMS = "CREATE TABLE " + Constants.ITEMS_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_ITEM_NAME + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_ITEM_IMAGE_FILE + " VARCHAR(255)," +
         Constants.DB_COLUMN_ITEM_CATEGORY_ID + " INT(5) NOT NULL DEFAULT -1," +
         Constants.DB_COLUMN_ITEM_IS_WISH + " INT(1) DEFAULT 0," +
         Constants.DB_COLUMN_ITEM_PRIMARY_COLOR + " INT(3)," +
         Constants.DB_COLUMN_ITEM_RATING + " FLOAT(2), " +
         "FOREIGN KEY (" + Constants.DB_COLUMN_ITEM_CATEGORY_ID + ") REFERENCES " +
         Constants.CATEGORIES_DB_TABLE + "(" + Constants.DB_COLUMN_CATEGORY_ID +
         ") ON DELETE CASCADE" +
         ")";

   String CREATE_TABLE_ATTRIBUTE_TYPES = "CREATE TABLE " + Constants.ATTRIBUTE_TYPES_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_DE + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_ATTRIBUTE_TYPE_VALUE_TYPE + " INT(3) NOT NULL DEFAULT 0," +
         Constants.DB_COLUMN_ATTRIBUTE_TYPE_IS_UNIQUE + " INT(1) DEFAULT 0" +
         ")";

   String CREATE_TABLE_COMPARES = "CREATE TABLE " + Constants.COMPARES_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_COMPARE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_COMPARE_NAME + " VARCHAR(50) NOT NULL DEFAULT 'My Compare'," +
         Constants.DB_COLUMN_COMPARE_ITEM_IDS + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_COMPARE_SAVE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
         ")";

   String CREATE_TABLE_COLORS = "CREATE TABLE " + Constants.COLORS_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_COLOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_COLOR_NAME_EN + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_COLOR_NAME_DE + " VARCHAR(50) NOT NULL DEFAULT ''," +
         Constants.DB_COLUMN_COLOR_HEX + " VARCHAR(50) NOT NULL DEFAULT ''" +
         ")";

   String CREATE_TABLE_ITEM_ATTRIBUTE_TYPES = "CREATE TABLE " + Constants.ITEM_ATTR_DB_TABLE +
         "(" +
         Constants.DB_COLUMN_ATTRIBUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
         Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + " INT NOT NULL," +
         Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID + " INT NOT NULL," +
         Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_VALUE + " VARCHAR(255)," +
         "FOREIGN KEY (" + Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + ") REFERENCES " +
         Constants.ITEMS_DB_TABLE + "(" + Constants.DB_COLUMN_ITEM_ID +
         ") ON DELETE CASCADE, " +
         "FOREIGN KEY (" + Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID + ") REFERENCES " +
         Constants.ATTRIBUTE_TYPES_DB_TABLE + "(" + Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID +
         ")" +
         ")";

   private DataBaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   public static SQLiteDatabase getDataBaseInstance(Context context) {
      if (mInstance == null) {
         mInstance = new DataBaseHelper(context);
      }
      if (mDataBaseInstance == null || !mDataBaseInstance.isOpen()) {
         mDataBaseInstance = mInstance.getWritableDatabase();
      }
      return mDataBaseInstance;
   }

   public static void closeDataBaseInstance() {
      if (mDataBaseInstance != null && mDataBaseInstance.isOpen()) {
         mDataBaseInstance.close();
         mInstance.close();
         mInstance = null;
         mDataBaseInstance = null;
      }
   }

   @Override
   public void onCreate(SQLiteDatabase database) {
      database.execSQL(CREATE_TABLE_CATEGORIES);
      database.execSQL(CREATE_TABLE_ITEMS);
      database.execSQL(CREATE_TABLE_ATTRIBUTE_TYPES);
      database.execSQL(CREATE_TABLE_ITEM_ATTRIBUTE_TYPES);
      database.execSQL(CREATE_TABLE_COMPARES);
      database.execSQL(CREATE_TABLE_COLORS);

      database.execSQL("PRAGMA foreign_keys=ON;");
      mDataBaseInstance = database;
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
   }
}