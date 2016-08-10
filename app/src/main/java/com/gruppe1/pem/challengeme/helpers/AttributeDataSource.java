package com.gruppe1.pem.challengeme.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gruppe1.pem.challengeme.Attribute;
import com.gruppe1.pem.challengeme.AttributeType;
import com.gruppe1.pem.challengeme.R;

import java.util.ArrayList;
import java.util.List;

public class AttributeDataSource {

   private Context context;
   // Database fields
   private SQLiteDatabase database;
   private String[] allColumns =
         { Constants.DB_COLUMN_ATTRIBUTE_ID, Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID,
               Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID,
               Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_VALUE };

   public AttributeDataSource(Context context) {
      this.context = context;
      database = DataBaseHelper.getDataBaseInstance(context);
   }

   public Attribute editAttribute(int itemId, int attrTypeId, String attrValue) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID, itemId);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID, attrTypeId);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_VALUE, attrValue);
      database.update(Constants.ITEM_ATTR_DB_TABLE, values,
            Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + "=" + itemId + " AND " +
                  Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID + "=" + attrTypeId, null);
      Cursor cursor = database.query(Constants.ITEM_ATTR_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + "=" + itemId + " AND " +
                  Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID + "=" + attrTypeId, null, null,
            null, null);
      if (cursor.getCount() == 0) {
         return null;
      }
      cursor.moveToFirst();
      Attribute newAttribute = cursorToAttribute(cursor);
      cursor.close();
      return newAttribute;
   }

   public Attribute createAttribute(int itemId, int attrTypeId, String attrValue) {
      ContentValues values = new ContentValues();
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID, itemId);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID, attrTypeId);
      values.put(Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_VALUE, attrValue);
      long insertId = database.insert(Constants.ITEM_ATTR_DB_TABLE, null, values);
      Cursor cursor = database.query(Constants.ITEM_ATTR_DB_TABLE, allColumns,
            Constants.DB_COLUMN_ATTRIBUTE_ID + " = " + insertId, null, null, null, null);
      cursor.moveToFirst();
      Attribute newAttribute = cursorToAttribute(cursor);
      cursor.close();
      return newAttribute;
   }

   public void deleteAttribute(Attribute attribute) {
      long id = attribute.getId();
      System.out.println("Attribute deleted with id: " + id);
      database.delete(Constants.ITEM_ATTR_DB_TABLE, Constants.DB_COLUMN_ATTRIBUTE_ID + " = " + id,
            null);
   }

   public List<Attribute> getAllAttributes() {
      List<Attribute> attributes = new ArrayList<>();
      Cursor cursor =
            database.query(Constants.ITEM_ATTR_DB_TABLE, allColumns, null, null, null, null,
                  Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + " ASC");
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Attribute attribute = cursorToAttribute(cursor);
         attributes.add(attribute);
         cursor.moveToNext();
      }
      cursor.close();
      return attributes;
   }

   public List<Attribute> getAttributesByItemId(int itemId) {
      List<Attribute> attributes = new ArrayList<>();
      String whereExpression = Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + "='" + itemId + "'";
      Cursor cursor =
            database.query(Constants.ITEM_ATTR_DB_TABLE, allColumns, whereExpression, null, null,
                  null, null);
      cursor.moveToFirst();
      while (!cursor.isAfterLast()) {
         Attribute attribute = cursorToAttribute(cursor);
         attributes.add(attribute);
         cursor.moveToNext();
      }
      cursor.close();
      return attributes;
   }

   public Attribute getAttributeExactColorByItemId(int itemId) {
      AttributeTypeDataSource dataSource = new AttributeTypeDataSource(context);
      AttributeType attributeType =
            dataSource.getAttributeTypeByName(context.getString(R.string.attr_type_ex_color_en));

      String whereExpression = Constants.DB_COLUMN_ATTRIBUTE_ITEM_ID + "='" + itemId + "' AND " +
            Constants.DB_COLUMN_ATTRIBUTE_ATTRIBUTE_TYPE_ID + " = '" +
            attributeType.getId() + "'";
      Cursor cursor =
            database.query(Constants.ITEM_ATTR_DB_TABLE, allColumns, whereExpression, null, null,
                  null, null);
      cursor.moveToFirst();
      Attribute attribute = cursorToAttribute(cursor);
      cursor.close();
      return attribute;
   }

   private Attribute cursorToAttribute(Cursor cursor) {
      AttributeTypeDataSource dataSource = new AttributeTypeDataSource(context);
      AttributeType attributeType = dataSource.getAttributeTypeById(cursor.getInt(2));

      Attribute attribute = new Attribute();
      attribute.setId(cursor.getInt(0));
      attribute.setItemId(cursor.getInt(1));
      attribute.setAttributeType(attributeType);
      attribute.setValue(cursor.getString(3));
      return attribute;
   }
}
