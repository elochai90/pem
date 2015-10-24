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
 * Color class
 */
public class Color {
    private int m_id;
    private String m_name_en;
    private String m_name_de;
    private String m_hex;

    private Context m_context;
    private DataBaseHelper m_dbHelper;

    public Color(Context p_context, int p_id, DataBaseHelper p_dbHelper) {
        this.m_id = p_id;
        this.m_context = p_context;
        this.m_dbHelper = p_dbHelper;
        this.m_dbHelper.setTable(Constants.COLORS_DB_TABLE);

        if(p_id > 0) {
            // get data from existing item
            this.m_dbHelper.setColumns(new String[]{"*"});
            this.m_dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor itemData = this.m_dbHelper.select();

            if(itemData.moveToFirst()) {
                this.m_id = itemData.getInt(0);
                this.m_name_en = itemData.getString(1);
                this.m_name_de = itemData.getString(2);
                this.m_hex = itemData.getString(3);

            }
            itemData.close();
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

    public String getHexColor() {
        return m_hex;
    }

    public void setHexColor(String color) {
        this.m_hex = color;
    }


    /**
     * gets all stored colors
     * @param p_context application context
     * @return ArrayList with all colors
     */
    public static ArrayList<Color> getAllColors(Context p_context) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.COLORS_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        SharedPreferences prefs = p_context.getSharedPreferences(Constants.MY_PREFERENCES, Activity.MODE_PRIVATE);
        String language = prefs.getString(Constants.KEY_LANGUAGE, "");
        switch (language) {
            case "en":
                dbHelper.setOrderBy("name_en ASC");
            case "de":
                dbHelper.setOrderBy("name_de ASC");
            default:
                dbHelper.setOrderBy("name_en ASC");
        }
        ArrayList<Color> allColors = new ArrayList<>();

        Cursor allColorsIterator = dbHelper.select();
        allColorsIterator.moveToFirst();

        while (!allColorsIterator.isAfterLast()) {
            Color color = new Color(p_context, allColorsIterator.getInt(0), dbHelper);
            color.setNameEn(allColorsIterator.getString(1));
            color.setNameDe(allColorsIterator.getString(2));
            color.setHexColor(allColorsIterator.getString(3));
            allColors.add(color);
            allColorsIterator.moveToNext();
        }

        allColorsIterator.close();
        dbHelper.close();
        return allColors;
    }


    /**
     * Edit Colot
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
                case "hex":
                    this.setHexColor(dbColumnValue);
                    break;
            }
        }
    }

    /**
     * save color
     */
    public void save() {
        if(this.m_id == 0) {
            // insert as new color
            m_dbHelper.setTable(Constants.COLORS_DB_TABLE);
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

            if (rowId == 0) {
                this.m_dbHelper.setStringValue("name_en", this.m_name_en);
                this.m_dbHelper.setStringValue("name_de", this.m_name_de);
                this.m_dbHelper.setStringValue("hex", this.m_hex);

                int id = this.m_dbHelper.insert();

                if (id > -1) {
                    this.m_id = id;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Color && getId() == ((Color) o).getId());
    }
}
