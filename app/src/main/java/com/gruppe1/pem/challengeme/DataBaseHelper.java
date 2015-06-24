package com.gruppe1.pem.challengeme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Simon on 12.06.2015.
 * handles the database connection and requests
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "organice";
    private static final String DB_PATH = "/data/data/com.gruppe1.pem.challengeme/databases/";
    private static final String DB_FULL_PATH = DB_PATH + DB_NAME;
    private static boolean db_existanceChecked = false;

    private final Context context;
    private SQLiteDatabase database;

    private String mTable;
    private String[] mColumns;
    private String mWhere;
    private String mOrderBy;
    private String mLimit;
    private ContentValues mValues;

    public DataBaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.mValues = new ContentValues();
    }

    public void init() {
        if(!db_existanceChecked) {
            try {
                this.createDataBase();
                db_existanceChecked = true;
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
        }

        try {
            this.openDataBase();
        } catch (SQLException sqle){
            try {
                throw sqle;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

    public void setTable(String p_table) {
        this.mTable = p_table;
    }

    public String getTable() {
        return this.mTable;
    }

    public void setColumns(String[] p_columns) {
        this.mColumns = p_columns;
    }

    public String[] getmColumns() {
        return this.mColumns;
    }

    public void setWhere(CharSequence p_concat, String[] p_restrictions) {
        this.mWhere = TextUtils.join(p_concat + " ", p_restrictions);
    }

    public String getWhere() {
        return this.mWhere;
    }

    public void setOrderBy(String p_orderBy) {
        this.mOrderBy = p_orderBy;
    }

    public String getmOrderBy() {
        return this.mOrderBy;
    }

    public void setIntegerValue(String p_key, int p_value) {
        this.mValues.put(p_key, p_value);
    }

    public void setStringValue(String p_key, String p_value) {
        this.mValues.put(p_key, p_value);
    }

    public ContentValues getValues(){
        return this.mValues;
    }

    @Override
    public synchronized void close() {
        if(database != null) {
            database.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(!dbExist){
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try{
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);

        } catch(SQLiteException e){
            //database does't exist yet.
        }

        if(checkDB != null){

        checkDB.close();
        }

    return checkDB != null;
}

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DB_FULL_PATH);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer) )> 0) {
        myOutput.write(buffer, 0, length);
        }

    //Close the streams
    myOutput.flush();
    myOutput.close();
    myInput.close();
}

    public void openDataBase() throws SQLException {
    //Open the database
    this.database = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    // test insert start

    //this.database.execSQL("INSERT INTO orga_nice_categories (name) VALUES (\"cat1\")");

    // test insert end

    }

    /*
     * --------------------------------------------------------------------
     * -------------------------- Query handling --------------------------
     * --------------------------------------------------------------------
     */

    public Cursor select() {
        return this.database.query(this.mTable, this.mColumns, this.mWhere, null, null,null, this.mOrderBy);
    }

    public int insert() {
        this.database.insert(this.mTable, null, this.mValues);
        Log.e("###", "inserted");
        this.mColumns = new String[]{"MAX(_id)"};
        Cursor cursor = this.select();

        Log.e("###DB###", this.mValues.toString());

        //return _id of (new) insert
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }

        return -1;
    }

    public void update(){
        this.database.update(this.mTable, this.mValues, this.mWhere, null);
    }

    public void delete() {

    }
}