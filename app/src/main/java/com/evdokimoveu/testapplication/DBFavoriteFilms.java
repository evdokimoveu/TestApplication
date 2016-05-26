package com.evdokimoveu.testapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBFavoriteFilms extends SQLiteOpenHelper {

    public final static String DB_TITLE_FIELD = "title";
    public final static String DB_POSTER_FIELD = "poster";
    public final static String DB_VOTE_AVERAGE_FIELD = "vote_average";
    public final static String DB_OVERVIEW_FIELD = "overview";
    public final static String DB_DATE_FIELD = "date";
    public final static String DB_ID_FIELD = "id";

    public final static String DATA_BASE_NAME = "favorite_films.db";
    public final static String TABLE_FILM = "films";

    public DBFavoriteFilms(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_FILM +
                " (" +
                DB_ID_FIELD +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_TITLE_FIELD + " VARCHAR NOT NULL, " +
                DB_POSTER_FIELD + " VARCHAR NOT NULL, " +
                DB_VOTE_AVERAGE_FIELD + " VARCHAR NOT NULL, " +
                DB_OVERVIEW_FIELD + " VARCHAR NOT NULL, " +
                DB_DATE_FIELD + " VARCHAR NOT NULL); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILM);
        onCreate(db);
    }
}