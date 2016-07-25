package com.speedyfirecyclone.cardstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CodeStore.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "codes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_RAW = "raw";
    public static final String COLUMN_FAVOURITE = "favourite";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_TITLE + " TEXT, " + COLUMN_FORMAT + " TEXT, " + COLUMN_DATA + " TEXT, " + COLUMN_RAW + " TEXT, " + COLUMN_FAVOURITE + " INT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insert(String title, String format, String data, String raw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_FORMAT, format);
        contentValues.put(COLUMN_DATA, data);
        contentValues.put(COLUMN_RAW, raw);
        contentValues.put(COLUMN_FAVOURITE, 0);

        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean update(Integer id, String title, String format, String data, String raw) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_FORMAT, format);
        contentValues.put(COLUMN_DATA, data);
        contentValues.put(COLUMN_RAW, raw);
        contentValues.put(COLUMN_FAVOURITE, 0);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean favourite(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FAVOURITE, 1);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{Integer.toString(id)});

        return true;
    }

    public boolean unfavourite(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FAVOURITE, 0);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{Integer.toString(id)});

        return true;
    }

    public Integer delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Cursor get(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_FAVOURITE + " DESC " + ", " + COLUMN_TITLE + " ASC", null);
        return res;
    }
}