package com.ycy.cloudeditor.DataHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by kimi9 on 2018/2/20.
 */

public class NoteDBHelper extends SQLiteOpenHelper {

    public static final String DataBaseName = "CloudEditor";
    public static final String TableName = "noteInfo";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final int Version = 1;

    private Context mContext;

    public NoteDBHelper(Context context) {
        super(context, DataBaseName, null, Version);
        this.mContext = context;
    }

    public NoteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TableName + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ,"
                + TITLE + " TEXT NOT NULL,"
                + CONTENT + " TEXT,"
                + TIME + " TEXT NOT NULL,"
                + ")";
        try {
            db.execSQL(createTable);
            Toast.makeText(mContext, "Create Table", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
