package com.ouam.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.ouam.app.entity.OfflinePinEntity;
import com.ouam.app.utils.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DatabaseUtil {

    private SQLiteDatabase mSqliteDataBase;
    DatabaseHelper mDbHelper = null;

    public void open() throws SQLiteException {

        mSqliteDataBase = DatabaseManager.getInstance().openDatabase();
    }

    public DatabaseUtil(Context context) {
        mDbHelper = new DatabaseHelper(context);
    }

    public void close() {
        DatabaseManager.getInstance().closeDatabase();
    }


    /*insert the pin details to db*/
    public boolean insertPinLocation(OfflinePinEntity offlinePinEntity) {
        open();

        // Inserting record
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.LOCATION_NAME, offlinePinEntity.getLocationName());
            contentValues.put(DatabaseHelper.LOCATION_LAT, offlinePinEntity.getLatitude());
            contentValues.put(DatabaseHelper.LOCATION_LONG, offlinePinEntity.getLongitude());
            contentValues.put(DatabaseHelper.PIN_TYPE, offlinePinEntity.getPinType());
            contentValues.put(DatabaseHelper.CREATED_AT, DateUtil.getDateTime());

            try {


                mSqliteDataBase.insert(DatabaseHelper.TABLE_OFFLINE, null, contentValues);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //  }
        return false;

    }


    public boolean isAlreadyExist(String lat) {
        String Query = "Select * from " + DatabaseHelper.TABLE_OFFLINE + " where " +
                DatabaseHelper.LOCATION_LAT + " = " + lat;


        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    public boolean checkExist(OfflinePinEntity offlinePinEntity) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] args = new String[]{offlinePinEntity.getLatitude(), offlinePinEntity.getLongitude()
                , offlinePinEntity.getPinType()};
        String filter = String.format("%s=? AND %s=? AND %s=?", DatabaseHelper.LOCATION_LAT, DatabaseHelper.LOCATION_LONG
                , DatabaseHelper.PIN_TYPE);

        return DatabaseUtils.queryNumEntries(db, DatabaseHelper.TABLE_OFFLINE, filter, args) > 0;
    }


    /*get all offine pins*/
    public ArrayList<OfflinePinEntity> getAllPins() {
        ArrayList<OfflinePinEntity> allPinsList = new ArrayList<>();
        /*select query*/
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_OFFLINE;

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                OfflinePinEntity offlinePinEntity = new OfflinePinEntity();
                offlinePinEntity.setId(Integer.parseInt(cursor.getString(0)));
                offlinePinEntity.setLocationName(cursor.getString(1));
                offlinePinEntity.setLatitude(cursor.getString(2));
                offlinePinEntity.setLongitude(cursor.getString(3));
                offlinePinEntity.setPinType(cursor.getString(4));
                offlinePinEntity.setDateTime(cursor.getString(5));
                allPinsList.add(offlinePinEntity);
            } while (cursor.moveToNext());
        }

        return allPinsList;

    }


    /*Delete fav doctor in database*/
    public void deletePin(int id) {
        try {

            open();

            mSqliteDataBase.delete(DatabaseHelper.TABLE_OFFLINE, DatabaseHelper.ROW_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("OUAM", e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            close();
        }
    }

}
