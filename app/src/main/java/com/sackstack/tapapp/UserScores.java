package com.sackstack.tapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;

/**
 * Help from: https://github.com/mitchtabian/SaveReadWriteDeleteSQLite/blob/master/SaveAndDisplaySQL/app/src/main/java/com/tabian/saveanddisplaysql/DatabaseHelper.java
 */

public class UserScores extends SQLiteOpenHelper {

    private static final String TAG = "UserScores";

    private static final String TABLE_NAME = "scores";
    private static final String COL_BPM = "bpm";
    private static final String COL_TAPS = "taps";
    private static final String COL_TIME = "time";
    private static final String COL_DIFFICULTY = "difficulty";


    public UserScores(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserScoresTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void createUserScoresTable(SQLiteDatabase newdb) {
        if (newdb == null) {
            newdb = this.getWritableDatabase();
        }

        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_BPM + " INTEGER NOT NULL, " +
                COL_TAPS + " INTEGER NOT NULL, " +
                COL_TIME + " INTEGER NOT NULL, " +
                COL_DIFFICULTY + " TEXT NOT NULL, " +
                "PRIMARY KEY ("+COL_BPM+", "+COL_DIFFICULTY+"))";

        Log.d(TAG, "Creating scores table");
        newdb.execSQL(createTable);
    }

    public int addScore(int bpm, int taps, String difficulty) {
        int prevTaps = getBPMScore(bpm, difficulty);
        if (prevTaps >= taps) {
            Log.d(TAG, "Prev tap score higher: (prevTaps."+prevTaps +", taps."+taps +")");
            return 0;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        int time = (60 / bpm) * 1000 * taps;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_BPM, bpm);
        contentValues.put(COL_TAPS, taps);
        contentValues.put(COL_TIME, time);
        contentValues.put(COL_DIFFICULTY, difficulty);
        Log.d(TAG, "AddingScore: (bpm." + bpm + ", taps."+taps+", time."+time+", difficulty."+difficulty+")");

        long result = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return -1;
        } else {
            return 1;
        }
    }

    public int[] getMaxBPM(String difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        int taps = -1;
        int[] result = new int[2];
        Arrays.fill(result, -1);
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE bpm = (SELECT max(bpm) FROM "+TABLE_NAME+") AND difficulty = \""+difficulty+"\"";
            data = db.rawQuery(query, null);
            if(data.getCount() > 0) {
                data.moveToFirst();
                result[0] = data.getInt(data.getColumnIndex("bpm"));
                result[1] = data.getInt(data.getColumnIndex("taps"));
                return result;
            }
            return result;

    }

    public int[] getMaxTaps(String difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        int taps = -1;
        int[] result = new int[2];
        Arrays.fill(result, -1);
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE taps = (SELECT max(taps) FROM "+TABLE_NAME+") AND difficulty = \""+difficulty+"\"";

            data = db.rawQuery(query, null);
            if(data.getCount() > 0) {
                data.moveToFirst();
                result[0] = data.getInt(data.getColumnIndex("bpm"));
                result[1] = data.getInt(data.getColumnIndex("taps"));
                return result;
            }
            return result;

    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns all the data from database
     * @return
     */
    public int getBPMScore(int bpm, String difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        int taps = -1;
        String query = "SELECT taps FROM " + TABLE_NAME +
                        " WHERE bpm = "+bpm+
                        " AND difficulty = \""+difficulty+"\"";
        try {
            data = db.rawQuery(query, null);
            if(data.getCount() > 0) {
                data.moveToFirst();
                return data.getInt(data.getColumnIndex("taps"));
            }
            return taps;
        } finally {
            data.close();
        }
    }

    /**
     * Delete from database
     */
    public void clearScores(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + TABLE_NAME;
        Log.d(TAG, "Dropping scores table");
        db.execSQL(query);
        createUserScoresTable(null);
    }

}
























