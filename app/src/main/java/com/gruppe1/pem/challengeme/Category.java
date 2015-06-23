package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Simon on 13.06.2015.
 */
public class Category {
    private static final String DB_TABLE = Constants.DB_TABLE_PREFIX + "categories";
    //0 = String, 1 = Integer
    public static final HashMap<String, Integer> dbColumns = new HashMap<String, Integer>() {{
        put("name", 0);
        put("parent_categorie_id", 1);
        put("default_attribute_type", 1);
        put("icon", 1);
    }};
    private int m_id;
    private String m_name;
    private int m_parent_category_id = Constants.DEFAULT_CATEGORY_ID;
    private String m_icon;
    // TODO add default size to sql init
    private int m_defaultAttributeType;
    private DataBaseHelper m_dbHelper;
    private Context context;


    public Category(Context context, int m_id, DataBaseHelper p_dbHelper) {
        this.context = context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(DB_TABLE);

        if(m_id > 0) {
            // get data from existing category
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + m_id});
            Cursor categoryData = this.m_dbHelper.select();

            if(categoryData.moveToFirst()) {
                Log.e("###Category Id:###", "" + categoryData.getInt(0));
                this.m_id = categoryData.getInt(0);
                this.m_name = categoryData.getString(1);
                this.m_parent_category_id = categoryData.getInt(2);
                this.m_icon = categoryData.getString(4);
            } else {
                Log.e("###NO_SUCH_CATEGORY_ID", "" + m_id);
            }

        } else {
            // prepare new category
        }
    }

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

    public DataBaseHelper getDbHelper() {
        return m_dbHelper;
    }

    public void setDbHelper(DataBaseHelper m_dbHelper) {
        this.m_dbHelper = m_dbHelper;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int m_id) {
        this.m_id = m_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String m_name) {
        this.m_name = m_name;
    }

    public int getParentCategoryId() {
        return m_parent_category_id;
    }

    public void setParentCategoryId(int m_parent_cat_id) {
        this.m_parent_category_id = m_parent_cat_id;
    }

    public int getDefaultAttributeType() {
        return m_defaultAttributeType;
    }

    public void setDefaultAttributeType(int m_defaultAttributeType) {
        this.m_defaultAttributeType = m_defaultAttributeType;
    }

    public String getIcon() {
        return m_icon;
    }

    public void setIcon(String m_icon) {
        this.m_icon = m_icon;
    }

    /**
     * get all categories from database
     * @return ArrayList<Category> all categories
     */
    public static ArrayList<Category> getAllCategories(Context p_context) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(DB_TABLE);
        helper.setColumns(new String[]{"*"});
        ArrayList<Category> allCategories = new ArrayList<Category>();

        Cursor allCategoriesIterator = helper.select();
        allCategoriesIterator.moveToFirst();
        Log.e("###All Cat count###", "" + allCategoriesIterator.getCount());

        while (!allCategoriesIterator.isAfterLast()) {
            Category category = new Category(p_context, allCategoriesIterator.getInt(0), helper);
            category.setName(allCategoriesIterator.getString(1));
            category.setParentCategoryId(allCategoriesIterator.getInt(2));
            category.setDefaultAttributeType(allCategoriesIterator.getInt(3));

            category.setIcon(allCategoriesIterator.getString(4));
            //Log.e("###All Cat call###", "name: " + allCategories.getString(1));
            allCategories.add(category);
            allCategoriesIterator.moveToNext();
        }

        return allCategories;
    }

    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();
        Iterator iterator = keys.iterator();

        Log.e("KEYS", keys.toString());

        while (iterator.hasNext()) {
            String dbColumnName = iterator.next().toString();
            String dbColumnValue = p_values.get(dbColumnName);

            Log.e("DBCOLUMNS", dbColumnName.toString());

            switch (dbColumnName) {
                case "name":
                    this.setName(dbColumnValue);
                    //Log.e("###CAT EDIT###", "name is: " + dbColumnValue);
                    break;

                case "parent_categorie_id":
                    this.setParentCategoryId(Integer.parseInt(dbColumnValue));
                    //Log.e("###CAT EDIT###", "parent_cat_id is: " + dbColumnValue);
                    break;

                case "default_attribute_type":
                    //Log.e("###CAT EDIT###", "default_attribute_type is: " + dbColumnValue);
                    this.setDefaultAttributeType(Integer.parseInt(dbColumnValue));
                    break;

                case "icon":

                    Log.e("###CAT EDIT ICON###", "icon is: " + dbColumnValue);
                    this.setIcon(dbColumnValue);
                    break;

                default:
                    Log.e("###CAT EDIT###", dbColumnName + " is not declared as columns in " + DB_TABLE);
                    break;
            }
        }
    }

    /**
     * save category changes or insert new category
     */
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
                this.m_dbHelper.setIntegerValue("parent_category_id", this.m_parent_category_id);
                this.m_dbHelper.setIntegerValue("default_attribute_type", this.m_defaultAttributeType);
                String iconValue = (this.m_icon != null) ? this.m_icon : Constants.DEFAULT_CAT_ICON;

                this.m_dbHelper.setStringValue("icon", iconValue);

                Log.e("###DB###", this.toString());

                int id = this.m_dbHelper.insert();

                    if (id > -1) {
                    this.m_id = id;
                    Log.e("###CAT INSERTED","id:" + id);
                } else {
                    Log.e("Category-Error", "save failed");
                }
            } else {
                Log.e("###CAT EXISTS", this.m_name + " - " + rowId);
            }
        } else {
            //save changes to existing category
        }
    }
}
