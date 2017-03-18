package com.example.henu.step.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 宋浩祥 on 2017/3/7.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "run.db";
    private static final int version = 2;
    private static final String CREATE_TABLE_RUN = "CREATE TABLE IF NOT EXISTS run(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "telephone TEXT,start_time INTEGER,end_time INTEGER,length REAL,duration INTEGER,consume REAL,points TEXT,is_update Boolean)";
    private static final String DROP_TABLE_RUN = "DROP TABLE IF EXISTS run";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RUN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_RUN);
        db.execSQL(CREATE_TABLE_RUN);
    }
}
