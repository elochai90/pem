package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private int m_primaryColorId;
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
                this.m_primaryColorId = itemData.getInt(5);
                this.m_rating= itemData.getFloat(6);

                //TODO handle item_attribute_types table for m_customAttributes

            } else {
                Log.e("###NO_SUCH_ITEM_ID", "" + m_id);
            }
            itemData.close();
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
        this.m_isWish = p_isWish;
    }

    public int getPrimaryColorId() {
        return m_primaryColorId;
    }

    public void setPrimaryColorId(int m_primaryColorId) {
        this.m_primaryColorId = m_primaryColorId;
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
        dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "' AND is_wish=0"});
        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<Item>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }
        cursor.close();
        dbHelper.close();
        return itemList;
    }

    public static ArrayList<Item> getAllItems(Context p_context, boolean p_wishListItems) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});

        if(p_wishListItems) {
            dbHelper.setWhere("", new String[]{"is_wish=1"});
        } else {
            dbHelper.setWhere("", new String[]{"is_wish=0"});
        }

        dbHelper.setOrderBy("name ASC");
        Cursor cursor = dbHelper.select();

        ArrayList<Item> itemList = new ArrayList<Item>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemList.add(new Item(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }
        cursor.close();
        dbHelper.close();
        return itemList;
    }
    public static int getItemsCountByCategoryId(Context p_context, int p_categoryId) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEMS_DB_TABLE);
        dbHelper.setColumns(new String[]{"COUNT(*)"});
        dbHelper.setWhere("", new String[]{"category_id='" + p_categoryId + "' AND is_wish=0"});
        Cursor cursor = dbHelper.select();

        int itemsCount = 0;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            itemsCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        dbHelper.close();
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
                    this.setPrimaryColorId(Integer.parseInt(dbColumnValue));
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
            m_dbHelper.deleteValues();
            this.m_dbHelper.setWhere("", new String[]{"name='" + this.m_name + "'"});
            Cursor existingRowCursor = this.m_dbHelper.select();
            existingRowCursor.moveToFirst();
            int rowId;

            try {
                rowId = existingRowCursor.getInt(0);
            } catch (Exception e) {
                rowId = 0;
            }
            existingRowCursor.close();
            if(rowId == 0) {
                this.setAllValuesToDbHelper();
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
            //save changes to existing item
            this.m_dbHelper.setWhere("", new String[] {"_id=" + this.m_id});
            this.setAllValuesToDbHelper();
            this.m_dbHelper.update();
        }
    }

    private void setAllValuesToDbHelper() {
        this.m_dbHelper.setStringValue("name", this.m_name);
        this.m_dbHelper.setStringValue("image_file", this.m_imageFile);
        this.m_dbHelper.setIntegerValue("category_id", this.m_categoryId);
        this.m_dbHelper.setIntegerValue("is_wish", this.m_isWish);
        this.m_dbHelper.setIntegerValue("primary_color", this.m_primaryColorId);
        this.m_dbHelper.setFloatValue("rating", this.m_rating);
    }

    public void delete() {
        this.m_dbHelper.setWhere("", new String[]{"_id=" + this.m_id});
        this.m_dbHelper.delete();
        this.m_dbHelper.close();
    }

}
