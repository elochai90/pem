package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.gruppe1.pem.challengeme.helpers.Constants;
import com.gruppe1.pem.challengeme.helpers.DataBaseHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by Simon on 29.06.2015.
 */
public class Compare implements Serializable{

    private int id;
    private ArrayList<Integer> itemIds;
    private String name;
    private String timestamp;

    private transient Context context;
    private transient DataBaseHelper dbHelper;

    public Compare(Context p_context, int p_id, DataBaseHelper p_dbHelper) {
        this.id = p_id;
        this.context = p_context;
        this.dbHelper = p_dbHelper;
        this.dbHelper.setTable(Constants.COMPARES_DB_TABLE);
        itemIds = new ArrayList<Integer>();

        if(p_id > -1) {
            this.dbHelper.setColumns(new String[]{"*"});
            this.dbHelper.setWhere("", new String[]{"_id=" + p_id});
            Cursor compareCursor = this.dbHelper.select();

            if(compareCursor.moveToFirst()) {
                this.name = compareCursor.getString(1);
                this.timestamp = compareCursor.getString(3);
                String[] idValues = compareCursor.getString(2).split(Pattern.quote("|"));

                for (String idValue : idValues) {
                    itemIds.add(Integer.parseInt(idValue));
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
        getDbHelper().setStringValue("name", this.name);
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
        // TODO: close dbHelper?
    }

    public static ArrayList<Compare> geAllCompares(Context p_context) {
        DataBaseHelper dbHelper = new DataBaseHelper(p_context);
        dbHelper.init();
        dbHelper.setTable(Constants.COMPARES_DB_TABLE);
        dbHelper.setColumns(new String[]{"*"});
        Cursor compareCursor = dbHelper.select();
        compareCursor.moveToFirst();

        ArrayList<Compare> allCompares = new ArrayList<>();

        while (!compareCursor.isAfterLast()) {
            Compare tmpCompare = new Compare(p_context, compareCursor.getInt(0), dbHelper);
            allCompares.add(tmpCompare);
            compareCursor.moveToNext();
        }
        compareCursor.close();
        dbHelper.close();
        return allCompares;
    }


    public void delete() {
        getDbHelper().setTable(Constants.COMPARES_DB_TABLE);
        this.dbHelper.setWhere("", new String[]{"_id=" + this.id});
        this.dbHelper.delete();
        this.dbHelper.close();
    }

    public void closeDBConnection() {
        this.dbHelper.close();
    }

    private DataBaseHelper getDbHelper() {
        if(!dbHelper.isOpen()) {
            System.out.println("db helper was closed");
            dbHelper = new DataBaseHelper(context);
            dbHelper.init();
        }
        return dbHelper;
    }
}



