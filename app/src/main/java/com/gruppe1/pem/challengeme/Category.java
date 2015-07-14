package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Category class
 */
public class Category {
    private int m_id;
    private String m_name;
    private int m_parent_category_id = Constants.DEFAULT_CATEGORY_ID;
    private String m_icon;
    // TODO add default size to sql init
    private int m_defaultSizeType;
    private DataBaseHelper m_dbHelper;
    private Context context;

    public Category(Context context, int m_id, DataBaseHelper p_dbHelper) {
        this.context = context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.CATEGORIES_DB_TABLE);

        if(m_id > 0) {
            // get data from existing category
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + m_id});
            Cursor categoryData = this.m_dbHelper.select();

            if(categoryData.moveToFirst()) {
                this.m_id = categoryData.getInt(0);
                this.m_name = categoryData.getString(1);
                this.m_parent_category_id = categoryData.getInt(2);
                this.m_defaultSizeType = categoryData.getInt(3);
                this.m_icon = categoryData.getString(4);
            }
            categoryData.close();
        }
    }

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

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

    public int getDefaultSizeType() {
        return m_defaultSizeType;
    }

    public void setDefaultSizeType(int m_defaultSizeType) {
        this.m_defaultSizeType = m_defaultSizeType;
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
        helper.setTable(Constants.CATEGORIES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setOrderBy("name ASC");
        ArrayList<Category> allCategories = new ArrayList<>();

        Cursor allCategoriesIterator = helper.select();
        allCategoriesIterator.moveToFirst();

        while (!allCategoriesIterator.isAfterLast()) {
            Category category = new Category(p_context, allCategoriesIterator.getInt(0), helper);
            category.setName(allCategoriesIterator.getString(1));
            category.setParentCategoryId(allCategoriesIterator.getInt(2));
            category.setDefaultSizeType(allCategoriesIterator.getInt(3));

            category.setIcon(allCategoriesIterator.getString(4));
            allCategories.add(category);
            allCategoriesIterator.moveToNext();
        }

        allCategoriesIterator.close();
        helper.close();
        return allCategories;
    }

    /**
     * Edit a Category
     * @param p_values values to be edited
     */
    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();

        for (String key : keys) {
            String dbColumnValue = p_values.get(key);

            switch (key) {
                case "name":
                    this.setName(dbColumnValue);
                    break;

                case "parent_categorie_id":
                    this.setParentCategoryId(Integer.parseInt(dbColumnValue));
                    break;

                case "default_attribute_type":
                    this.setDefaultSizeType(Integer.parseInt(dbColumnValue));
                    break;

                case "icon":
                    this.setIcon(dbColumnValue);
                    break;

                default:
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
            existingRowCursor.close();
            if(rowId == 0) {
                this.setAllValuesToDbHelper();
                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
                }
            }
        } else {
            //save changes to existing category
            this.m_dbHelper.setWhere("", new String[] {"_id=" + this.m_id});
            this.setAllValuesToDbHelper();
            this.m_dbHelper.update();
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Category && getId() == ((Category) o).getId());
    }

    /**
     * sets all values to database helper object
     */
    private void setAllValuesToDbHelper(){
        this.m_dbHelper.deleteValues();
        this.m_dbHelper.setStringValue("name", this.m_name);
        this.m_dbHelper.setIntegerValue("parent_category_id", this.m_parent_category_id);
        this.m_dbHelper.setIntegerValue("default_attribute_type", this.m_defaultSizeType);
        String iconValue = (this.m_icon != null) ? this.m_icon : Constants.DEFAULT_CAT_ICON;
        this.m_dbHelper.setStringValue("icon", iconValue);
    }

    /**
     * delete category
     */
    public void delete() {
        this.m_dbHelper.setWhere("", new String[]{"_id=" + this.m_id});
        this.m_dbHelper.delete();
        m_dbHelper.close();
    }
}
