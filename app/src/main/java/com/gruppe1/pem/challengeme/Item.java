package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Simon on 24.06.2015.
 */
public class Item {
    //0 = String, 1 = Integer
    public static final HashMap<String, Integer> dbColumns = new HashMap<String, Integer>() {{
        put("name", 0);
        put("image_file", 1);
        put("category_id", 1);
        put("is_wish", 1);
        put("primary_color", 0);
        put("rating", 1);
    }};

    private int m_id;
    private String m_name;
    private String m_imageFile;
    private int m_categoryId;
    private int m_isWish;
    private String m_primaryColor;
    private Float m_rating;

//    private HashMap<AttributeType, Object> attributes;

    private ArrayList<HashMap<AttributeType, String>> m_customAttributes;

    private Context m_context;
    private DataBaseHelper m_dbHelper;


    public Item(Context p_context, int p_id, DataBaseHelper p_dbHelper) {
        this.m_id = p_id;
        this.m_context = p_context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.ITEMS_DB_TABLE);

//        attributes = new HashMap<AttributeType, Object>();

        if(p_id > 0) {
            // get data from existing item
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor itemData = this.m_dbHelper.select();

            if(itemData.moveToFirst()) {
//                Log.e("###Item Id:###", "" + itemData.getInt(0));
                this.m_id = itemData.getInt(0);
                this.m_name = itemData.getString(1);
                this.m_imageFile = itemData.getString(2);
                this.m_categoryId = itemData.getInt(3);
                this.m_isWish = itemData.getInt(4);
                this.m_primaryColor = itemData.getString(5);
                this.m_rating= itemData.getFloat(6);

                //TODO handle item_attribute_types table for m_customAttributes

            } else {
                Log.e("###NO_SUCH_ITEM_ID", "" + m_id);
            }

        } else {
            // prepare new item
        }

    }

    public int getId() {
        return m_id;
    }

    public void setId(int p_id) {
        this.m_id = p_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String p_name) {
        this.m_name = p_name;
    }

    public String getImageFile() {
        return m_imageFile;
    }

    public void setImageFile(String p_imageFile) {
        this.m_imageFile = p_imageFile;
    }

    public int getCategoryId() {
        return m_categoryId;
    }

    public void setCategoryId(int p_categoryId) {
        this.m_categoryId = p_categoryId;
    }

    public int getIsWish() {
        return m_isWish;
    }

    public void setIsWish(int p_isWish) {
        System.out.println("setIsWish: " + p_isWish);
        this.m_isWish = p_isWish;
    }

    public String getPrimaryColor() {
        return m_primaryColor;
    }

    public void setPrimaryColor(String p_primaryColor) {
        this.m_primaryColor = p_primaryColor;
    }

    public Float getRating() {
        return m_rating;
    }

    public void setRating(Float p_rating) {
        this.m_rating = p_rating;
    }


    public static ArrayList<Item> getItemsByCategoryId(Context p_context, int p_categoryId) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "'"});
        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<Item>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }

        return itemList;
    }

    public static ArrayList<Item> getAllItems(Context p_context) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<Item>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }

        return itemList;
    }
    public static int getItemsCountByCategoryId(Context p_context, int p_categoryId) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"COUNT(*)"});
        dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "'"});
        Cursor cursor = dbHelper.select();

        int itemsCount = 0;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemsCount = cursor.getInt(0);
            cursor.moveToNext();
        }

        return itemsCount;
    }

    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext()) {
            String dbColumnName = iterator.next().toString();
            String dbColumnValue = p_values.get(dbColumnName);

            switch (dbColumnName) {
                case "name":
                    this.setName(dbColumnValue);
                    break;
                case "image_file":
                    this.setImageFile(dbColumnValue);
                    break;
                case "category_id":
                    this.setCategoryId(Integer.parseInt(dbColumnValue));
                    break;
                case "is_wish":
                    this.setIsWish(Integer.parseInt(dbColumnValue));
                    break;
                case "primary_color":
                    this.setPrimaryColor(dbColumnValue);
                    break;
                case "rating":
                    this.setRating(Float.parseFloat(dbColumnValue));
                    break;
            }
        }
    }

    public void save() {
        if(this.m_id == 0) {
            // insert as new item
            this.m_dbHelper.setWhere("", new String[]{"name='" + this.m_name + "'"});
            Cursor existingRowCursor = this.m_dbHelper.select();
            existingRowCursor.moveToFirst();
            int rowId;

            try {
                rowId = existingRowCursor.getInt(0);
            } catch (Exception e) {
                rowId = 0;
            }
            if(rowId == 0) {
                this.m_dbHelper.setStringValue("name", this.m_name);
                this.m_dbHelper.setStringValue("image_file", this.m_imageFile);
                this.m_dbHelper.setIntegerValue("category_id", this.m_categoryId);
                this.m_dbHelper.setIntegerValue("is_wish", this.m_isWish);
                this.m_dbHelper.setStringValue("primary_color", this.m_primaryColor);
                this.m_dbHelper.setFloatValue("rating", this.m_rating);

                System.out.println("in saving - Wish: " + this.m_isWish);

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
//                    Log.e("###ITEM INSERTED","id:" + id);
                } else {
                    Log.e("Item-Error", "save failed");
                }
            } else {
//                Log.e("###ITEM EXISTS", this.m_name + " - " + rowId);
            }
        } else {
            this.m_dbHelper.setWhere("", new String[]{"_id='" + this.m_id + "'"});
            this.m_dbHelper.setIntegerValue("_id", this.m_id);
            this.m_dbHelper.setStringValue("name", this.m_name);
            this.m_dbHelper.setStringValue("image_file", this.m_imageFile);
            this.m_dbHelper.setIntegerValue("category_id", this.m_categoryId);
            this.m_dbHelper.setIntegerValue("is_wish", this.m_isWish);
            this.m_dbHelper.setStringValue("primary_color", this.m_primaryColor);
            this.m_dbHelper.setFloatValue("rating", this.m_rating);

            System.out.println("in updating - Wish: " + this.m_isWish);
            System.out.println(this.m_dbHelper.getValues());

            if(this.m_dbHelper.update()) {
                System.out.println("update successful");

            } else {
                System.out.println("update NOT successful");
            }
            DataBaseHelper dbhelper = new DataBaseHelper(m_context);
            dbhelper.init();
            dbhelper.setTable(Constants.ITEMS_DB_TABLE);
            Item item = new Item(m_context, m_id, m_dbHelper);
            System.out.println("Nach speichern: " + item.getIsWish());
        }
    }
}
