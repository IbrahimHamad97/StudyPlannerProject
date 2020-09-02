package com.cmps312.studyplanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Locale;

public class StudyDbHelper extends SQLiteOpenHelper implements StudyContract {

    //Create tables
    private static final String SQL_CREATE_TASK_TABLE = String.format(Locale.US,
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s TEXT,%s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TasksTable.TABLE_NAME, TasksTable.COL_ID, TasksTable.COL_TITLE, TasksTable.COL_DESC,
            TasksTable.COL_DATE, TasksTable.COL_TIME, TasksTable.COL_STATUS);
    private static final String SQL_CREATE_SUBTASK_TABLE = String.format(Locale.US,
            "CREATE TABLE %s (%s INTEGER, %s TEXT, PRIMARY KEY (%s,%s))",
            SubTasksTable.TABLE_NAME, SubTasksTable.COL_ID, SubTasksTable.COL_SUBTASK,
            SubTasksTable.COL_ID, SubTasksTable.COL_SUBTASK);
    private static final String SQL_CREATE_NOTE_TABLE = String.format(Locale.US,
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s TEXT, %s TEXT, %s TEXT)",
            NotesTable.TABLE_NAME, NotesTable.COL_ID, NotesTable.COL_TITLE, NotesTable.COL_DATE,
            NotesTable.COL_CONTENT);

    //Drop tables
    private static final String SQL_DROP_NOTE_TABLE = String.format(Locale.US,
            "DROP TABLE IF EXISTS %s", NotesTable.TABLE_NAME);
    private static final String SQL_DROP_SUBTASK_TABLE = String.format(Locale.US,
            "DROP TABLE IF EXISTS %s", SubTasksTable.TABLE_NAME);
    private static final String SQL_DROP_TASK_TABLE = String.format(Locale.US,
            "DROP TABLE IF EXISTS %s", TasksTable.TABLE_NAME);


    public StudyDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_SUBTASK_TABLE);
        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_NOTE_TABLE);
        db.execSQL(SQL_DROP_SUBTASK_TABLE);
        db.execSQL(SQL_DROP_TASK_TABLE);
        onCreate(db);
    }
}
