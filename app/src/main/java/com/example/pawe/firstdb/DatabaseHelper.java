package com.example.pawe.firstdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DB.db";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String DATE_TYPE = " TEXT ";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.DB.TABLE_PERSONS + " ( " +
                    DatabaseContract.DB._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseContract.DB.COLUMN_FIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.DB.COLUMN_DATE + DATE_TYPE + COMMA_SEP +
                    DatabaseContract.DB.COLUMN_IMAGE_PATH + TEXT_TYPE + COMMA_SEP+
                    DatabaseContract.DB.COLUMN_LASTNAME + TEXT_TYPE + " );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseContract.DB.TABLE_PERSONS;

    public DatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        /*db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);*/
    }
}
