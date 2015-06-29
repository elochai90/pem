package com.gruppe1.pem.challengeme;

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
public class AttributeType {

    //0 = String, 1 = Integer
    public static final HashMap<String, Integer> dbColumns = new HashMap<String, Integer>() {{
        put("name", 0);
        put("value_type", 1);
        put("is_unique", 1);
    }};

    private int m_id;
    private String m_name;
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
//                Log.e("###AttrType Name:###", "" + attrTypeData.getInt(0));
                this.m_id = attrTypeData.getInt(0);
                this.m_name = attrTypeData.getString(1);
                this.m_valueType = attrTypeData.getInt(2);
                this.m_isUnique = attrTypeData.getInt(3);
            } else {
                Log.e("###NO_SUCH_CATEGORY_ID", "" + m_id);
            }

        } else {
            // prepare new attribute
        }
    }

    public int getId() {
        return m_id;
    }

    public void setId(int m_Id) {
        this.m_id = m_Id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String m_name) {
        this.m_name = m_name;
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

    public static ArrayList<AttributeType> getAttributeTypes(Context p_context) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        ArrayList<AttributeType> attributeTypes = new ArrayList<AttributeType>();

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();


        while (!allAttrTypesIterator.isAfterLast()) {
            AttributeType attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
            attributeTypes.add(attributeType);
            allAttrTypesIterator.moveToNext();
        }

        return attributeTypes;
    }

    public static AttributeType getAttributeTypeById(Context p_context, int attrTypeId) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setWhere("", new String[]{"_id='" + attrTypeId + "'"});
        AttributeType attributeType = null;

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();

        if(allAttrTypesIterator.getCount() == 0) {
            attributeType =  new AttributeType(p_context, 0, helper);
        } else {
            attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
        }
        return attributeType;
    }

    public static AttributeType getAttributeTypeByName(Context p_context, String attrName) {
        DataBaseHelper helper = new DataBaseHelper(p_context);
        helper.init();
        helper.setTable(Constants.ATTRIBUTE_TYPES_DB_TABLE);
        helper.setColumns(new String[]{"*"});
        helper.setWhere("", new String[]{"name='" + attrName + "'"});
        AttributeType attributeType = null;

        Cursor allAttrTypesIterator = helper.select();
        allAttrTypesIterator.moveToFirst();

        if(allAttrTypesIterator.getCount() == 0) {
            attributeType =  new AttributeType(p_context, 0, helper);
        } else {
            attributeType = new AttributeType(p_context, allAttrTypesIterator.getInt(0), helper);
        }
        return attributeType;
    }
    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();
        Iterator iterator = keys.iterator();

//        Log.e("###ATTR KEYS###", keys.toString());

        while (iterator.hasNext()) {
            String dbColumnName = iterator.next().toString();
            String dbColumnValue = p_values.get(dbColumnName);

            switch (dbColumnName) {
                case "name":
                    this.setName(dbColumnValue);
                    //Log.e("###ATTR EDIT###", "name is: " + dbColumnValue);
                    break;

                case "value_type":
                    this.setValueType(Integer.parseInt(dbColumnValue));
                    //Log.e("###ATTR EDIT###", "value_type is: " + dbColumnValue);
                    break;

                case "is_unique":
                    //Log.e("###ATTR EDIT###", "is unique is: " + dbColumnValue);
                    this.setIsUnique(Integer.parseInt(dbColumnValue));
                    break;

                default:
                    Log.e("###ATTR EDIT###", dbColumnName + " is not declared as columns in " + Constants.ATTRIBUTE_TYPES_DB_TABLE);
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
                this.m_dbHelper.setIntegerValue("value_type", this.m_valueType);
                this.m_dbHelper.setIntegerValue("is_unique", this.m_isUnique);

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
//                    Log.e("###ATTR INSERTED","name: " + this.m_name);
                } else {
                    Log.e("Attribute-Type-Error", "save failed");
                }
            } else {
//                Log.e("###ATTR TYPE EXISTS", this.m_name + " - " + rowId);
            }
        } else {
            //save changes to existing attribute type
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AttributeType && getId() == ((AttributeType) o).getId());

    }
}
