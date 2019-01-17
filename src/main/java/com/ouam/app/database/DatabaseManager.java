package com.ouam.app.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private static DatabaseManager mInstance;
    private static DatabaseHelper mDatabaseHelper;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase;


    public static synchronized void initialize(Context context,
                                               DatabaseHelper helper) {
        if (mInstance == null) {
            mInstance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }


    public static synchronized DatabaseManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(
                    DatabaseManager.class.getSimpleName()
                            + "is not initialized, call initialize(..) method first.");

        }

        return mInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }

}
