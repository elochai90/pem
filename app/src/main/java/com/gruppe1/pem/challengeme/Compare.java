package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Simon on 29.06.2015.
 */
public class Compare {
    final static String DB_TABLE = Constants.DB_TABLE_PREFIX + "compares";

    private ArrayList<Integer> itemIds;
    private String name;
    private String timestamp;

    private Context context;
    private DataBaseHelper dbHelper;

    public Compare(Context p_context, int p_id) {
        this.context = p_context;
        this.dbHelper = new DataBaseHelper(this.context);
        dbHelper.init();
        this.dbHelper.setTable(DB_TABLE);
        itemIds = new ArrayList<Integer>();

        this.timestamp = (System.currentTimeMillis()/1000) + "";

        if(p_id > -1) {
            this.dbHelper.setColumns(new String[]{"*"});
            this.dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor compareCursor = this.dbHelper.select();

            if(compareCursor.moveToFirst()) {
                String[] idValues = compareCursor.getString(2).split("|");
            } else {
                Log.e("###NO_SUCH_COMPARE_ID", "" + p_id);
            }

            compareCursor.close();
        }

    }

    public void addItemId(int p_id) {
        this.itemIds.add(p_id);
    }

    public ArrayList<Integer> getItemIds() {
        return this.itemIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp(){
        return this.timestamp;
    }

    public void insert() {
        this.dbHelper.setStringValue("name", this.name);
        String idConcat = "";

        Iterator idIterator = itemIds.iterator();

        while (idIterator.hasNext()) {
            idConcat += idIterator.next().toString();

            if(idIterator.hasNext()) {
                idConcat += "|";
            }
        }

        this.dbHelper.setStringValue("item_ids", idConcat);
        this.dbHelper.setStringValue("save_date", timestamp);

        this.dbHelper.insert();
    }
}



