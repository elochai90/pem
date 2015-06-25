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
        put("buy_date", 0);
        put("store", 0);
        put("is_wish", 1);
        put("primary_color", 0);
        put("secondary_color", 0);
        put("pattern", 0);
        put("rating", 1);
    }};

    private int m_id;
    private String m_name;
    private String m_imageFile;
    private int m_categoryId;
    private String m_buyDate;
    private String m_store;
    private int m_isWish;
    private String m_primaryColor;
    private String m_secondaryColor;
    private float m_rating;
    private int m_pattern;

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
                Log.e("###Item Id:###", "" + itemData.getInt(0));
                this.m_id = itemData.getInt(0);
                this.m_name = itemData.getString(1);
                this.m_imageFile = ""; //itemData.getString(2);

//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Category"), itemData.getInt(3));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Buy Date"), itemData.getString(4));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Store"), itemData.getString(5));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"In Wishlist"), itemData.getInt(6));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Color"), itemData.getString(7));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Exact Color"), itemData.getString(8));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Rating"), itemData.getFloat(9));
//                attributes.put(AttributeType.getAttributeTypeByName(p_context,"Has Pattern"), itemData.getInt(10));

                this.m_categoryId = itemData.getInt(3);
                this.m_buyDate = itemData.getString(4);
                this.m_store = itemData.getString(5);
                this.m_isWish = itemData.getInt(6);
                this.m_primaryColor = itemData.getString(7);
                this.m_secondaryColor= itemData.getString(8);
                this.m_rating= itemData.getFloat(8);
                this.m_pattern= itemData.getInt(8);

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

    public String getBuyDate() {
        return m_buyDate;
    }

    public void setBuyDate(String p_buyDate) {
        this.m_buyDate = p_buyDate;
    }

    public String getStore() {
        return m_store;
    }

    public void setStore(String p_store) {
        this.m_store = p_store;
    }

    public int getIsWish() {
        return m_isWish;
    }

    public void setIsWish(int p_isWish) {
        this.m_isWish = p_isWish;
    }

    public String getPrimaryColor() {
        return m_primaryColor;
    }

    public void setPrimaryColor(String p_primaryColor) {
        this.m_primaryColor = p_primaryColor;
    }

    public String getSecondaryColor() {
        return m_secondaryColor;
    }

    public void setSecondaryColor(String p_secondaryColor) {
        this.m_secondaryColor = p_secondaryColor;
    }

    public int getPattern() {
        return m_pattern;
    }

    public void setPattern(int p_pattern) {
        this.m_pattern = p_pattern;
    }

    public float getRating() {
        return m_rating;
    }

    public void setRating(float p_rating) {
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

//    public void putAttributeValue(AttributeType attributeType, Object attributeValue) {
//        attributes.put(attributeType, attributeValue);
//    }
//
//    public Object getAttributeValue(Context context, AttributeType attributeType) {
//
//        return attributes.get(attributeType.getName());
////        ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(context);
//
//    }

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
                case "buy_date":
                    this.setBuyDate(dbColumnValue);
                    break;
                case "store":
                    this.setStore(dbColumnValue);
                    break;
                case "is_wish":
                    this.setIsWish(Integer.parseInt(dbColumnValue));
                    break;
                case "primary_color":
                    this.setPrimaryColor(dbColumnValue);
                    break;
                case "secondary_color":
                    this.setSecondaryColor(dbColumnValue);
                    break;
                case "patter":
                    this.setPattern(Integer.parseInt(dbColumnValue));
                    break;
                case "rating":
                    this.setRating(Float.parseFloat(dbColumnValue));
                    break;
            }
        }
    }

    public void save() {
        if(this.m_id == 0) {
            // insert as new categoy
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
                this.m_dbHelper.setStringValue("buy_date", this.m_buyDate);
                this.m_dbHelper.setStringValue("store", this.m_store);
                this.m_dbHelper.setIntegerValue("is_wish", this.m_isWish);
                this.m_dbHelper.setStringValue("primary_color", this.m_primaryColor);
                this.m_dbHelper.setStringValue("secondary_color", this.m_secondaryColor);
                this.m_dbHelper.setIntegerValue("pattern", this.m_pattern);
                this.m_dbHelper.setFloatValue("rating", this.m_rating);

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
                    Log.e("###ITEM INSERTED","id:" + id);
                } else {
                    Log.e("Item-Error", "save failed");
                }
            } else {
                Log.e("###ITEM EXISTS", this.m_name + " - " + rowId);
            }
        } else {
            //save changes to existing category
        }
    }
}
