package com.example.gamecentertoni;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameCenter.db";
    private static final int DATABASE_VERSION = 1;

    // Tables' name:
    private static final String TABLE_USERS = "users_game";
    private static final String TABLE_GAME2048 = "game_2048";
    private static final String TABLE_BREAKOUT = "game_breakout";

    // Columnas en comun:
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_SCORE = "puntuation_game";
    private static final String COLUMN_TIME = "time_game";

    // User table columns:
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAMES = "surnames";
    private static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Create table users:
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_SURNAMES + " TEXT NOT NULL,"
            + COLUMN_DATE_OF_BIRTH + " TEXT NOT NULL,"
            + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE,"
            + COLUMN_PASSWORD + " TEXT NOT NULL);";

    // Create table game 2048:
    private static final String CREATE_TABLE_GAME2048 = "CREATE TABLE " + TABLE_GAME2048 + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_SCORE + " INTEGER NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";

    private static final String CREATE_TABLE_BREAKOUT = "CREATE TABLE " + TABLE_BREAKOUT + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_SCORE + " INTEGER NOT NULL, "
            + COLUMN_TIME + " TEXT NOT NULL, "
            + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "));";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables:
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GAME2048);
        db.execSQL(CREATE_TABLE_BREAKOUT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME2048);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BREAKOUT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create new tables
        onCreate(db);
    }

    // Enable foreign key support

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // User CRUD operations
    public long addUser(String name, String surnames, String dateOfBirth, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAMES, surnames);
        values.put(COLUMN_DATE_OF_BIRTH, dateOfBirth);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        // Insert row
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count > 0;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    // 2048 Game stats operations
    public long add2048Stats(int userId, int score, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);

        // Insert row
        long id = db.insert(TABLE_GAME2048, null, values);
        db.close();
        return id;
    }

    public List<GameStats> get2048StatsByUserId(int userId) {
        List<GameStats> statsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_SCORE, COLUMN_TIME};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COLUMN_SCORE + " DESC";

        Cursor cursor = db.query(TABLE_GAME2048, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                GameStats stats = new GameStats();
                stats.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                stats.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)));
                stats.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));

                statsList.add(stats);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return statsList;
    }

    public int getHighest2048Score(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int highScore = 0;

        String query = "SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_GAME2048 +
                " WHERE " + COLUMN_USER_ID + " = " + userId;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            highScore = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return highScore;
    }

    // Breakout Game stats operations
    public long addBreakoutStats(int userId, int score, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIME, time);

        // Insert row
        long id = db.insert(TABLE_BREAKOUT, null, values);
        db.close();
        return id;
    }

    public List<GameStats> getBreakoutStatsByUserId(int userId) {
        List<GameStats> statsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_SCORE, COLUMN_TIME};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COLUMN_SCORE + " DESC";

        Cursor cursor = db.query(TABLE_BREAKOUT, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                GameStats stats = new GameStats();
                stats.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                stats.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)));
                stats.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));

                statsList.add(stats);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return statsList;
    }

    public int getHighestBreakoutScore(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int highScore = 0;

        String query = "SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_BREAKOUT +
                " WHERE " + COLUMN_USER_ID + " = " + userId;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            highScore = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return highScore;
    }
}