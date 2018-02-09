package com.tusadmin.trackurspot_admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tusadmin.trackurspot_admin.Databases.OverSpeedDatabase;
import com.tusadmin.trackurspot_admin.Databases.SOSDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KishoreKumar on 29-Jul-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DataManager";

    private static final String TABLE_SOS = "sos_table";
    private static final String S_NAME = "name";
    private static final String S_TIME = "time";

    private static final String TABLE_OVERSPEED = "overspeed_table";
    private static final String O_NAME = "name";
    private static final String O_TIME = "time";

    private String CREATE_TABLE_SOS = "CREATE TABLE " + TABLE_SOS + "("
            + S_NAME + " TEXT,"
            + S_TIME + " VARCHAR(100))";

    private String CREATE_TABLE_OVERSPEED = "CREATE TABLE " + TABLE_OVERSPEED + "("
            + O_NAME + " TEXT,"
            + O_TIME + " VARCHAR(100))";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SOS);
        db.execSQL(CREATE_TABLE_OVERSPEED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SOS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_OVERSPEED);
        // Create tables again
        onCreate(db);
    }

    public void addS0S(SOSDatabase sos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(S_NAME, sos.getName());
        values.put(S_TIME, sos.getDate());
        db.insert(TABLE_SOS, null, values);
        db.close();
    }

    public void addOverSpeed(OverSpeedDatabase overSpeed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(O_NAME, overSpeed.getName());
        values.put(O_TIME, overSpeed.getDate());
        db.insert(TABLE_OVERSPEED, null, values);
        db.close();
    }

    public List<SOSDatabase> getAll_SOSdata() {

        List<SOSDatabase> soslist = new ArrayList<SOSDatabase>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SOS, new String[]{
                        S_NAME, S_TIME},
                null, null, null, null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SOSDatabase sos = new SOSDatabase();
                sos.setName(cursor.getString(0));
                sos.setDate(cursor.getString(1));
                // Adding data to list
                soslist.add(sos);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return list
        return soslist;
    }

    public List<OverSpeedDatabase> getAll_OverSpeeddata() {

        List<OverSpeedDatabase> overSpeedlist = new ArrayList<OverSpeedDatabase>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OVERSPEED, new String[]{
                        O_NAME, O_TIME},
                null, null, null, null,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OverSpeedDatabase overSpeed = new OverSpeedDatabase();
                overSpeed.setName(cursor.getString(0));
                overSpeed.setDate(cursor.getString(1));
                // Adding data to list
                overSpeedlist.add(overSpeed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return list
        return overSpeedlist;
    }

    public boolean delete_SOS(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        // non-negative value if successfully deleted
        return db.delete(TABLE_SOS,S_TIME + "=" + name,null) > 0 ;
    }

    public boolean delete_overspeed(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        // non-negative value if successfully deleted
        return db.delete(TABLE_OVERSPEED,O_TIME + "=" + name,null) > 0 ;
    }

}