package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Simon on 12.06.2015.
 *
 * handles the database connection and requests
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db";
    private static String DB_PATH = "/data/data/com.gruppe1.pem.challengeme/databases/";

    private SQLiteDatabase database;
    private final Context context;

    public DataBaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void init(){
        try {
            this.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
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
            String db_Path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(db_Path, null, SQLiteDatabase.OPEN_READONLY);

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

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

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
        String db_Path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(db_Path, null, SQLiteDatabase.OPEN_READWRITE);

        // test insert start

        this.database.execSQL("INSERT INTO orga_nice_categories (name) VALUES (\"cat1\")");

        String query = "SELECT * FROM orga_nice_categories";
        Cursor cursor = this.database.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Log.e("###NAME###", "" + cursor.getString(1));
        }

        // test insert end

    }
}