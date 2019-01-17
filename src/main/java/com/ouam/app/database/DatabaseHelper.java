package com.ouam.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OUAM.sqlite";

    public static final String TABLE_OFFLINE = "OUAM_OFFLINE_TABLE";

    public static final String ROW_ID = "ROW_ID";
    public static final String LOCATION_NAME = "LOCATION_NAME";
    public static final String LOCATION_LAT = "LOCATION_LAT";
    public static final String LOCATION_LONG = "LOCATION_LONG";
    public static final String PIN_TYPE = "PIN_TYPE";
    public static final String CREATED_AT = "CREATED_AT";

//    private static final String CREATE_TABLE = "CREATE TABLE " +
//            TABLE_OFFLINE + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_NAME + " TEXT ," + LOCATION_LAT + " TEXT, " +
//            LOCATION_LONG + " TEXT, "+PIN_TYPE+ " TEXT " + ")";

    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_OFFLINE + "(" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_NAME + " TEXT ," + LOCATION_LAT + " TEXT, " +
            LOCATION_LONG + " TEXT, " + PIN_TYPE + " TEXT, "+CREATED_AT+ " TEXT " + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE);

        onCreate(sqLiteDatabase);
    }
}
