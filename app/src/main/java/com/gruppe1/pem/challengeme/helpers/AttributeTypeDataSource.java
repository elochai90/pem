package com.gruppe1.pem.challengeme.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.AttributeType;

import java.util.ArrayList;
import java.util.List;

public class AttributeTypeDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns =
         { Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID, Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN,
               Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_DE,
               Constants.DB_COLUMN_ATTRIBUTE_TYPE_VALUE_TYPE,
               Constants.DB_COLUMN_ATTRIBUTE_TYPE_IS_UNIQUE };

   public AttributeTypeDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public AttributeType editAttributeType(int id, String nameEn, String nameDe, int valueType,
         int isUnique) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_VALUE_TYPE, valueType);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_IS_UNIQUE, isUnique);
      database.update(Constants.ATTRIBUTE_TYPES_DB_TABLE, values,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + "=" + id, null);
      Cursor cursor = database.query(Constants.ATTRIBUTE_TYPES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + " = " + id, null, null, null, null);
      cursor.moveToFirst();
      AttributeType newAttributeType = cursorToAttributeType(cursor);
      cursor.close();
      return newAttributeType;
   }

   public AttributeType createAttributeType(String nameEn, String nameDe, int valueType,
         int isUnique) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN, nameEn);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_DE, nameDe);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_VALUE_TYPE, valueType);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_TYPE_IS_UNIQUE, isUnique);
      long insertId = database.insert(Constants.ATTRIBUTE_TYPES_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.ATTRIBUTE_TYPES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      AttributeType newAttributeType = cursorToAttributeType(cursor);
      cursor.close();
      return newAttributeType;
   }

   public AttributeType getAttributeTypeByName(String attributeTypeName) {
      Cursor cursor = database.query(Constants.ATTRIBUTE_TYPES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_NAME_EN + "='" + attributeTypeName + "'", null, null,
            null, null);
      cursor.moveToFirst();
      AttributeType attributeType = cursorToAttributeType(cursor);
      cursor.close();
      return attributeType;
   }

   public AttributeType getAttributeTypeById(int attributeTypeId) {
      Cursor cursor = database.query(Constants.ATTRIBUTE_TYPES_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + "='" + attributeTypeId + "'", null, null, null,
            null);
      cursor.moveToFirst();
      AttributeType attributeType = cursorToAttributeType(cursor);
      cursor.close();
      return attributeType;
   }

   public void deleteAttributeType(AttributeType attributeType) {
      long id = attributeType.getId();
      System.out.println("Attribute Type deleted with id: " + id);
      database.delete(Constants.ATTRIBUTE_TYPES_DB_TABLE,
            Constants.DB_COLUMN_ATTRIBUTE_TYPE_ID + " = " + id, null);
   }

   public List<AttributeType> getAllAttributeTypes() {
      List<AttributeType> attributeTypes = new ArrayList<>();
      Cursor cursor =
            database.query(Constants.ATTRIBUTE_TYPES_DB_TABLE, allColumns, null, null, null, null,
                  null);

      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         AttributeType attributeType = cursorToAttributeType(cursor);
         attributeTypes.add(attributeType);
         cursor.moveToNext();
      }
      cursor.close();
      return attributeTypes;
   }

   private AttributeType cursorToAttributeType(Cursor cursor) {
      AttributeType attributeType = new AttributeType();
      attributeType.setId(cursor.getInt(0));
      attributeType.setNameEn(cursor.getString(1));
      attributeType.setNameDe(cursor.getString(2));
      attributeType.setValueType(cursor.getInt(3));
      attributeType.setIsUnique(cursor.getInt(4));
      return attributeType;
   }
}
