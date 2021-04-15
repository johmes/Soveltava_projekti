package com.example.malko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    private static final String TAG = "Database";

    private static final String TABLE_NAME = "login_data";
    private static final String COL1 = "ID";
    private static final String COL2 = "LOGGED";

    public Database(Context context) {
        super(context, TABLE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                COL2 + " BOOLEAN)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
/*    public boolean updateData(boolean logged) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, logged);

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, contentValues, ")
        }
    }*/
    // ADD DATA TO DB
    public boolean addData(boolean item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }
    // GET DATA FROM DB
    public Cursor getData(boolean item) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + item + " FROM " + TABLE_NAME, null);
        Log.d(TAG, "getData: Getting " + item + " from " + TABLE_NAME);
        return cursor;
    }

}
