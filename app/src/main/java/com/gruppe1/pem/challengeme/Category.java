package com.gruppe1.pem.challengeme;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Simon on 13.06.2015.
 */
public class Category {
    private static final String DB_TABLE = Constants.DB_TABLE_PREFIX + "categories";

    private String m_id;
    private String m_name;
    private String m_parent_cat_id;
    private ArrayList<Attribute> m_defaultAttributes;
    private DataBaseHelper m_dbHelper;

    public Category(int m_id, DataBaseHelper p_dbHelper) {
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(DB_TABLE);

        if(m_id > 0) {
            // get data from existing category
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + m_id});
            this.m_dbHelper.select();
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

    public String getId() {
        return m_id;
    }

    public void setId(String m_id) {
        this.m_id = m_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String m_name) {
        this.m_name = m_name;
    }

    public String getParentCatId() {
        return m_parent_cat_id;
    }

    public void setParentCatId(String m_parent_cat_id) {
        this.m_parent_cat_id = m_parent_cat_id;
    }

    public ArrayList<Attribute> getDefaultAttributes() {
        return m_defaultAttributes;
    }

    public void setDefaultAttributes(ArrayList<Attribute> m_defaultAttributes) {
        this.m_defaultAttributes = m_defaultAttributes;
    }

    /**
     * get all categories from database
     * @return ArrayList<Category> all categories
     */
    public static ArrayList<Category> getAllCategories() {
        return null;
    }

    public void save() {
        //save changes
    }
}
