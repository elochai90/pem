package com.gruppe1.pem.challengeme;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Simon on 13.06.2015.
 */
public class Category {
    private static final String DB_TABLE = Constants.DB_TABLE_PREFIX + "categories";

    private int m_id;
    private String m_name;
    private int m_parent_category_id = Constants.DEFAULT_CATEGORY_ID;
    // TODO add default size to sql init
    private int m_defaultAttributeType;
    private DataBaseHelper m_dbHelper;

    // used for output resource strings
    private static final String[] databaseColumnsIdentifiers = {"id", "category_name", "parent_category_id"};

    public Category(int m_id, DataBaseHelper p_dbHelper) {
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

    /**
     * get all categories from database
     * @return ArrayList<Category> all categories
     */
    public static ArrayList<Category> getAllCategories() {
        return null;
    }

    /**
     * save category changes or insert new category
     */
    public void save() {
        if(this.m_id == 0) {
            // insert as new categoy
            this.m_dbHelper.setStringValue("name", this.m_name);
            this.m_dbHelper.setIntegerValue("parent_category_id", this.m_parent_category_id);

            int id = this.m_dbHelper.insert();

            if (id > -1) {
                this.m_id = id;
            } else {
                Log.e("Category-Error", "save failed");
            }
        } else {
            //save changes to existing category
        }
    }
}
