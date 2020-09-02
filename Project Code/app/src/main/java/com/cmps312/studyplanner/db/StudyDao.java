package com.cmps312.studyplanner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cmps312.studyplanner.model.Note;
import com.cmps312.studyplanner.model.Task;

import java.util.ArrayList;


public class StudyDao implements StudyContract {

    private SQLiteDatabase db;
    private StudyDbHelper helper;

    public StudyDao(Context context) {
        this.helper = new StudyDbHelper(context);
    }

    // Note functions

    public long addNote(Note note) {
        db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NotesTable.COL_TITLE, note.getTitle());
        cv.put(NotesTable.COL_DATE, note.getDate());
        cv.put(NotesTable.COL_CONTENT, note.getContent());

        return db.insert(NotesTable.TABLE_NAME, null, cv);
    }

    public long updateNote(Note note) {
        db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(NotesTable.COL_TITLE, note.getTitle());
        cv.put(NotesTable.COL_CONTENT, note.getContent());

        String whereClause = NotesTable.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(note.getId())};


        return db.update(NotesTable.TABLE_NAME, cv, whereClause, whereArgs);
    }

    public long deleteNote(int id) {

        db = helper.getWritableDatabase();

        String whereClause = NotesTable.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        return db.delete(NotesTable.TABLE_NAME, whereClause, whereArgs);
    }

    public Note getNote(int id) {
        db = helper.getReadableDatabase();

        String selection = NotesTable.COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(NotesTable.TABLE_NAME, NotesTable.COLUMNS, selection,
                selectionArgs, null, null, null);

        Note note = new Note();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            note = cursorToNote(cursor);
            cursor.close();
        }

        return note;
    }

    public ArrayList<Note> getNotes() {
        db = helper.getReadableDatabase();

        Cursor cursor = db.query(NotesTable.TABLE_NAME, NotesTable.COLUMNS, null,
                null, null, null, null);

        ArrayList<Note> notes = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursorToNote(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return notes;
    }

    //When a note is deleted, a user is redirected to the latest note
    public Note getLatestNote() {
        db = helper.getReadableDatabase();

        Cursor cursor = db.query(NotesTable.TABLE_NAME, NotesTable.COLUMNS, null,
                null, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToLast();
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        } else
            return null;
    }

    // Task functions

    public long addTask(Task task) {
        db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TasksTable.COL_TITLE, task.getTitle());
        cv.put(TasksTable.COL_DATE, task.getDate());
        cv.put(TasksTable.COL_TIME, task.getTime());
        cv.put(TasksTable.COL_STATUS, task.getStatus());

        return db.insert(TasksTable.TABLE_NAME, null, cv);
    }

    public long updateTask(Task task) {
        db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(TasksTable.COL_TITLE, task.getTitle());
        cv.put(TasksTable.COL_DATE, task.getDate());
        cv.put(TasksTable.COL_TIME, task.getTime());
        cv.put(TasksTable.COL_STATUS, task.getStatus());

        String whereClause = TasksTable.COL_ID + " = ?";

        return db.update(TasksTable.TABLE_NAME, cv, whereClause, null);
    }
    /*
    public long deleteTask(Task task) {
        db = helper.getWritableDatabase();



        return db.delete(TasksTable.TABLE_NAME, whereClause, whereArgs);
    }
    */


    public Task getTask(int id) {
        db = helper.getReadableDatabase();

        String selection = TasksTable.COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TasksTable.TABLE_NAME, TasksTable.COLUMNS, selection,
                selectionArgs, null, null, null);

        Task task = new Task();

        if (cursor != null) {
            cursor.moveToFirst();
            task = cursorToTask(cursor);
            cursor.close();
        }


        return task;
    }

    public ArrayList<Task> getTasks() {
        db = helper.getReadableDatabase();

        Cursor cursor = db.query(TasksTable.TABLE_NAME, TasksTable.COLUMNS, null,
                null, null, null, null);

        ArrayList<Task> tasks = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cursor.moveToNext();
                tasks.add(cursorToTask(cursor));
            }
            cursor.close();
        }


        return tasks;
    }


    private ArrayList<String> getSubtasks(Task task) {
        ArrayList<String> subtasks = new ArrayList<>();

        db = helper.getReadableDatabase();

        String selection = TasksTable.COL_ID + " = ?";
        Cursor cursor = db.query(SubTasksTable.TABLE_NAME, SubTasksTable.COLUMNS, selection,
               null, null, null, null);


        if (cursor != null && cursor.getCount() > 0) {
            while (!cursor.isAfterLast())
                subtasks.add(cursor.getString(cursor.getColumnIndex(SubTasksTable.COL_SUBTASK)));
            cursor.close();
        }

        return subtasks;
    }

    // CursorToModel functions
    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();

        note.setId(cursor.getInt(cursor.getColumnIndex(NotesTable.COL_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(NotesTable.COL_TITLE)));
        note.setDate(cursor.getString(cursor.getColumnIndex(NotesTable.COL_DATE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(NotesTable.COL_CONTENT)));

        return note;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();


        task.setTitle(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TITLE)));
        task.setDate(cursor.getString(cursor.getColumnIndex(TasksTable.COL_DATE)));
        task.setTime(cursor.getString(cursor.getColumnIndex(TasksTable.COL_TIME)));



        return task;
    }
}
