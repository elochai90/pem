package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by Simon on 29.06.2015.
 */
public class Compare implements Serializable{
    final static String DB_TABLE = Constants.DB_TABLE_PREFIX + "compares";

    private int id;
    private ArrayList<Integer> itemIds;
    private String name;
    private String timestamp;

    private transient Context context;
    private transient DataBaseHelper dbHelper;

    public Compare(Context p_context, int p_id) {
        this.id = p_id;
        this.context = p_context;
        this.dbHelper = new DataBaseHelper(this.context);
        dbHelper.init();
        this.dbHelper.setTable(DB_TABLE);
        itemIds = new ArrayList<Integer>();

        if(p_id > -1) {
            this.dbHelper.setColumns(new String[]{"*"});
            this.dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor compareCursor = this.dbHelper.select();

            if(compareCursor.moveToFirst()) {
                this.name = compareCursor.getString(1);
                this.timestamp = compareCursor.getString(3);
                String[] idValues = compareCursor.getString(2).split(Pattern.quote("|"));

                for(int i = 0; i < idValues.length; i++) {
                    itemIds.add(Integer.parseInt(idValues[i]));
                }


            } else {
                Log.e("###NO_SUCH_COMPARE_ID", "" + p_id);
            }

            compareCursor.close();
        } else {
            this.timestamp = (System.currentTimeMillis()) + "";
        }

    }

    public void setId(int p_id) {
        this.id = p_id;
    }

    public int getId(){
        return this.id;
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

    public static ArrayList<Compare> geAllCompares(Context p_context) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.DB_TABLE_PREFIX + "compares");
        dbHelper.setColumns(new String[]{"*"});
        Cursor compareCursor = dbHelper.select();
        compareCursor.moveToFirst();

        ArrayList<Compare> allCompares = new ArrayList<Compare>();

        while (!compareCursor.isAfterLast()) {
            Compare tmpCompare = new Compare(p_context, compareCursor.getInt(0));
            allCompares.add(tmpCompare);
            compareCursor.moveToNext();
        }

        return allCompares;
    }

}


