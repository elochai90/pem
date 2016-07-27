package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Attribute class
 */
public class Attribute {
    private int m_id;
    private int m_itemId;
    private AttributeType m_attributeType;
    private Object m_value;
    private Context m_context;
    private DataBaseHelper m_dbHelper;

    public Attribute(Context p_context, int p_id, DataBaseHelper p_dbHelper) {
        this.m_id = p_id;
        this.m_context = p_context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.ITEM_ATTR_DB_TABLE);

        if(p_id > 0) {
            // get data from existing item
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor itemData = this.m_dbHelper.select();

            if(itemData.moveToFirst()) {
                this.m_id = itemData.getInt(0);
                this.m_itemId = itemData.getInt(1);
                this.m_attributeType = AttributeType.getAttributeTypeById(m_context, itemData.getInt(2));
                this.m_value = itemData.getString(3);
            }
            itemData.close();
        }
    }


    public Attribute(Context p_context, int p_itemId, int p_attributeTypeId, DataBaseHelper p_dbHelper) {
        this.m_itemId = p_itemId;
        this.m_attributeType = AttributeType.getAttributeTypeById(m_context, p_attributeTypeId);

        this.m_context = p_context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.ITEM_ATTR_DB_TABLE);

        if(p_itemId > 0 && p_attributeTypeId > 0) {
            // get data from existing item
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"item_id=" + p_itemId + " AND attribute_type_id=" + p_attributeTypeId});
            Cursor itemData = this.m_dbHelper.select();

            if(itemData.moveToFirst()) {
                this.m_id = itemData.getInt(0);
                this.m_itemId = itemData.getInt(1);
                this.m_attributeType = AttributeType.getAttributeTypeById(m_context, itemData.getInt(2));
                this.m_value = itemData.getString(3);
            }
            itemData.close();

        } else {
            // prepare new item
            m_id = 0;
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

    public int getItemId() {
        return m_itemId;
    }

    public void setItemId(int m_itemId) {
        this.m_itemId = m_itemId;
    }

    public AttributeType getAttributeType() {
        return m_attributeType;
    }

    public void setAttributeType(AttributeType m_attributeType) {
        this.m_attributeType = m_attributeType;
    }

    public Object getValue() {
        return m_value;
    }

    public void setValue(Object m_value) {
        this.m_value = m_value;
    }


    /**
     * gets all attributes connected to an specific item
     * @param p_context application context
     * @param p_itemId item ID
     * @return ArrayList of Attributes
     */
    public static ArrayList<Attribute> getAttributesByItemId(Context p_context, int p_itemId) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEM_ATTR_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        dbHelper.setWhere("", new String[]{"item_id='" + p_itemId + "'"});
        Cursor cursor = dbHelper.select();

        ArrayList<Attribute> attributeList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            attributeList.add(new Attribute(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }
        cursor.close();
        dbHelper.close();
        return attributeList;
    }

    public static Attribute getAttributeExactColorByItemId(Context p_context, int p_itemId) {
        AttributeType attributeType = AttributeType.getAttributeTypeByName(p_context, p_context.getString(R.string.attr_type_ex_color_en));
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEM_ATTR_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        dbHelper.setWhere("", new String[]{"item_id='" + p_itemId + "' AND attribute_type_id='" + attributeType.getId() + "'"});
        Cursor cursor = dbHelper.select();
        cursor.moveToFirst();
        Attribute exactColorAttribute;
        if(cursor.getCount() == 0) {
            exactColorAttribute =  new Attribute(p_context, 0, dbHelper);
        } else {
            exactColorAttribute = new Attribute(p_context, cursor.getInt(0), dbHelper);
        }
        cursor.close();
        dbHelper.close();
        return exactColorAttribute;
    }

    /**
     * get all attributes from database
     * @return ArrayList<Attribute> all attributes
     */
    public static ArrayList<Attribute> getAllAttributes(Context p_context) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ITEM_ATTR_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setOrderBy("item_id ASC");
        ArrayList<Attribute> allAttributes = new ArrayList<>();

        Cursor allAttributesIterator = helper.select();
        allAttributesIterator.moveToFirst();

        while (!allAttributesIterator.isAfterLast()) {
            Attribute attribute = new Attribute(p_context, allAttributesIterator.getInt(0), helper);
            attribute.setItemId(allAttributesIterator.getInt(1));
            attribute.setAttributeType((AttributeType.getAttributeTypeById(p_context,allAttributesIterator.getInt(2))));
            attribute.setValue(allAttributesIterator.getString(3));

            allAttributes.add(attribute);
            allAttributesIterator.moveToNext();
        }
        allAttributesIterator.close();
        helper.close();
        return allAttributes;
    }


    /**
     * edits the attribute data
     * @param p_values Values to be edited
     */
    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();

        for (String key : keys) {
            String dbColumnValue = p_values.get(key);

            switch (key) {
                case "item_id":
                    this.setItemId(Integer.parseInt(dbColumnValue));
                    break;
                case "attribute_type_id":
                    this.setAttributeType(AttributeType.getAttributeTypeById(m_context, Integer.parseInt(dbColumnValue)));
                    break;
                case "attribute_value":
                    this.setValue(dbColumnValue);
                    break;
            }
        }
    }

    /**
     * saves the attribute data in the database
     */
    public void save() {
        if(this.m_id == 0) {
            // insert as new attribute
            this.m_dbHelper.setWhere("", new String[]{"item_id='" + this.m_itemId + "' AND attribute_type_id='" + this.m_attributeType.getId() + "'"});
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
            } else {
                //save changes to existing attribute
                this.m_dbHelper.setWhere("", new String[]{"_id=" + this.m_id});
                this.setAllValuesToDbHelper();
                this.m_dbHelper.update();
            }
        } else {
            //save changes to existing attribute
            this.m_dbHelper.setWhere("", new String[] {"_id=" + this.m_id});
            this.setAllValuesToDbHelper();
            this.m_dbHelper.update();
        }
        this.m_dbHelper.close();
    }

    /**
     * sets the attribute values to the database helper
     */
    private void setAllValuesToDbHelper(){
        this.m_dbHelper.deleteValues();
        this.m_dbHelper.setIntegerValue("item_id", this.m_itemId);
        this.m_dbHelper.setIntegerValue("attribute_type_id", this.m_attributeType.getId());
        this.m_dbHelper.setStringValue("attribute_value", this.m_value.toString());
    }

    /**
     * deletes the attribute
     */
    public void delete() {
        this.m_dbHelper.setWhere("", new String[]{"_id=" + this.m_id});
        this.m_dbHelper.delete();
        this.m_dbHelper.close();
    }
}
