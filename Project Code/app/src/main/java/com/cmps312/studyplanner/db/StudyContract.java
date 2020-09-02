package com.cmps312.studyplanner.db;

import android.provider.BaseColumns;

public interface StudyContract {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "StudyPlanDb";

    final class TasksTable implements BaseColumns {
        public static final String TABLE_NAME = "Tasks";
        public static final String COL_ID = "_id";
        public static final String COL_TITLE = "title";
        public static final String COL_DESC = "shortdescription";
        public static final String COL_DATE = "date";
        public static final String COL_TIME = "time" ;
        public static final String COL_STATUS = "status";

        public static final String[] COLUMNS = {COL_TITLE, COL_DESC, COL_DATE, COL_TIME, COL_STATUS};
    }

    final class SubTasksTable implements BaseColumns {
        public static final String TABLE_NAME = "SubTasks";
        public static final String COL_ID = "_id";
        public static final String COL_TITLE = "title";
        public static final String COL_SUBTASK = "subtask";

        public static final String[] COLUMNS = {COL_TITLE, COL_SUBTASK};

    }

    final class NotesTable implements BaseColumns {
        public static final String TABLE_NAME = "Notes";
        public static final String COL_ID = "_id";
        public static final String COL_TITLE = "title";
        public static final String COL_DATE = "date";
        public static final String COL_CONTENT = "content";

        public static final String[] COLUMNS = {COL_ID, COL_TITLE, COL_DATE, COL_CONTENT};
    }
}
