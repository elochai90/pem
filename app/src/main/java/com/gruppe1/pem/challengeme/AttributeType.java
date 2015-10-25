package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * AttributeType class
 */
public class AttributeType {
    private int m_id;
    private String m_name_en;
    private String m_name_de;
    private int m_valueType;
    private int m_isUnique;
    private Context m_context;
    private DataBaseHelper m_dbHelper;

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

    public AttributeType(Context context, int m_id, DataBaseHelper p_dbHelper) {
        this.m_context = context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);

        if(m_id > 0) {
            // get data from existing attribute
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + m_id});
            Cursor attrTypeData = this.m_dbHelper.select();

            if(attrTypeData.moveToFirst()) {
                this.m_id = attrTypeData.getInt(0);
                this.m_name_en = attrTypeData.getString(1);
                this.m_name_de = attrTypeData.getString(2);
                this.m_valueType = attrTypeData.getInt(3);
                this.m_isUnique = attrTypeData.getInt(4);
            }
            attrTypeData.close();

        }
    }

    public int getId() {
        return m_id;
    }

    public void setId(int m_Id) {
        this.m_id = m_Id;
    }

    public String getName() {
        SharedPreferences prefs = m_context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.KEY_LANGUAGE, "");
        switch (language) {
            case "en":
                return m_name_en;
            case "de":
                return m_name_de;
            default:
                return m_name_en;
        }
    }


    public void setNameEn(String m_name) {
        this.m_name_en = m_name;
    }

    public void setNameDe(String m_name) {
        this.m_name_de = m_name;
    }

    public int getValueType() {
        return m_valueType;
    }

    public void setValueType(int m_valueType) {
        this.m_valueType = m_valueType;
    }

    public int getIsUnique() {
        return m_isUnique;
    }

    public void setIsUnique(int is_unique) {
        this.m_isUnique = is_unique;
    }

    /**
     * get all attribute types
     * @param p_context application context
     * @return ArrayList of AttributeType Obejcts
     */
    public static ArrayList<AttributeType> getAttributeTypes(Context p_context) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        ArrayList<AttributeType> attributeTypes = new ArrayList<>();

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();


        while (!allAttrTypesIterator.isAfterLast()) {
            AttributeType attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
            attributeTypes.add(attributeType);
            allAttrTypesIterator.moveToNext();
        }

        allAttrTypesIterator.close();
        helper.close();
        return attributeTypes;
    }

    /**
     * gets an specific attribute type by id
     * @param p_context application context
     * @param attrTypeId AttributeType ID
     * @return AttributeType with requested ID
     */
    public static AttributeType getAttributeTypeById(Context p_context, int attrTypeId) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setWhere("", new String[]{"_id='" + attrTypeId + "'"});
        AttributeType attributeType;

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();

        if(allAttrTypesIterator.getCount() == 0) {
            attributeType =  new AttributeType(p_context, 0, helper);
        } else {
            attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
        }

        allAttrTypesIterator.close();
        helper.close();
        return attributeType;
    }

    /**
     * gets an AttributeType Obeject by name (not in use right now, due to missing unique statement on column `name`)
     * @param p_context application context
     * @param attrName name of the attribute type
     * @return AttributeType with requested name
     */
    public static AttributeType getAttributeTypeByName(Context p_context, String attrName) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setWhere("", new String[]{"name_en='" + attrName + "'"});
        AttributeType attributeType;

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();

        if(allAttrTypesIterator.getCount() == 0) {
            attributeType =  new AttributeType(p_context, 0, helper);
        } else {
            attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
        }

        allAttrTypesIterator.close();
        helper.close();
        return attributeType;
    }

    /**
     * Edits the AttributeType Object
     * @param p_values values to be edited
     */
    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();

        for (String key : keys) {
            String dbColumnValue = p_values.get(key);

            switch (key) {
                case "name_en":
                    this.setNameEn(dbColumnValue);
                    break;

                case "name_de":
                    this.setNameDe(dbColumnValue);
                    break;

                case "value_type":
                    this.setValueType(Integer.parseInt(dbColumnValue));
                    break;

                case "is_unique":
                    this.setIsUnique(Integer.parseInt(dbColumnValue));
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * save attribute type changes or insert new attribute type
     */
    public void save() {
        if(this.m_id == 0) {
            // insert as new attribute type
            m_dbHelper.deleteValues();
            this.m_dbHelper.setWhere("", new String[]{"name_en='" + this.m_name_en + "'"});
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
                this.m_dbHelper.setStringValue("name_en", this.m_name_en);
                this.m_dbHelper.setStringValue("name_de", this.m_name_de);
                this.m_dbHelper.setIntegerValue("value_type", this.m_valueType);
                this.m_dbHelper.setIntegerValue("is_unique", this.m_isUnique);

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AttributeType && getId() == ((AttributeType) o).getId());
    }
}
