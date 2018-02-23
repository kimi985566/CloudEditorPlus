package com.ycy.cloudeditor.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ycy.cloudeditor.Bean.NoteInfo;

/**
 * Created by kimi9 on 2018/2/20.
 */

public class NoteDataKeeper {

    private NoteDBHelper mNoteDBHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;

    public NoteDataKeeper() {

    }

    public NoteDataKeeper(Context context) {
        this.mContext = context;
        this.mNoteDBHelper = new NoteDBHelper(context);
    }

    public NoteDataKeeper(NoteDBHelper noteDBHelper, SQLiteDatabase SQLiteDatabase, Context context) {
        mNoteDBHelper = noteDBHelper;
        mSQLiteDatabase = SQLiteDatabase;
        mContext = context;
    }

    public void saveNote(NoteInfo noteInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDBHelper.ID, noteInfo.getGenius_id());
        contentValues.put(NoteDBHelper.TITLE, noteInfo.getTitle());
        contentValues.put(NoteDBHelper.CONTENT, noteInfo.getContent());
        contentValues.put(NoteDBHelper.TIME, noteInfo.getTime());

        mSQLiteDatabase = mNoteDBHelper.getWritableDatabase();

    }
}
