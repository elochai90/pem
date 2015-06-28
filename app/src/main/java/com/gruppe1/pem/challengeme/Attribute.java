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
public class Attribute {


    //0 = String, 1 = Integer
    public static final HashMap<String, Integer> dbColumns = new HashMap<String, Integer>() {{
        put("item_id", 1);
        put("attribute_type_id", 1);
        put("attribute_value", 0);
    }};

    private int m_id;
    private int m_itemId;
    private AttributeType m_attributeType;
//    private int m_attrTypeId;
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
//                this.m_attrTypeId = itemData.getInt(2);
                this.m_value = itemData.getString(3);

            } else {
                Log.e("###NO_SUCH_ATTRIBUTE_ID", "" + m_id);
            }

        } else {
            // prepare new item
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


    public static ArrayList<Attribute> getAttributesByItemId(Context p_context, int p_itemId) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.ITEM_ATTR_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        dbHelper.setWhere("", new String[]{"item_id='" + p_itemId + "'"});
        Cursor cursor = dbHelper.select();

        ArrayList<Attribute> attributeList = new ArrayList<Attribute>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            attributeList.add(new Attribute(p_context, cursor.getInt(0), dbHelper));
            cursor.moveToNext();
        }

        return attributeList;
    }


    public void edit(HashMap<String, String> p_values) {
        Set<String> keys = p_values.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext()) {
            String dbColumnName = iterator.next().toString();
            String dbColumnValue = p_values.get(dbColumnName);

            switch (dbColumnName) {
                case "item_id":
                    this.setItemId(Integer.parseInt(dbColumnValue));
                    break;
                case "attribute_type_id":
                    this.setAttributeType(AttributeType.getAttributeTypeById(m_context,Integer.parseInt(dbColumnValue)));
                    break;
                case "attribute_value":
                    this.setValue(dbColumnValue);
                    break;
            }
        }
    }

    public void save() {
        if(this.m_id == 0) {
            // insert as new attribute
            m_dbHelper.deleteValues();
            this.m_dbHelper.setWhere("", new String[]{"item_id='" + this.m_itemId + "' AND attribute_type_id='" + this.m_attributeType.getId() + "'"});
            Cursor existingRowCursor = this.m_dbHelper.select();
            existingRowCursor.moveToFirst();
            int rowId;

            try {
                rowId = existingRowCursor.getInt(0);
            } catch (Exception e) {
                rowId = 0;
            }
            if(rowId == 0) {
                this.m_dbHelper.setIntegerValue("item_id", this.m_itemId);
                this.m_dbHelper.setIntegerValue("attribute_type_id", this.m_attributeType.getId());
                this.m_dbHelper.setStringValue("attribute_value", this.m_value.toString());

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
//                    Log.e("###ATTRIBUTE INSERTED","id:" + id);
                } else {
                    Log.e("Item-Error", "save failed");
                }
            } else {
//                Log.e("###ATTRIBUTE EXISTS", this.m_itemId + ":" + this.m_attributeType.getId() + " - " + rowId);
            }
        } else {
            //save changes to existing category
        }
    }

}
