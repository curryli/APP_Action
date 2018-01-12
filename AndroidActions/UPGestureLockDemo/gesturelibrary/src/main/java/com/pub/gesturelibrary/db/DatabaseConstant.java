package com.pub.gesturelibrary.db;

import android.os.Environment;

public interface DatabaseConstant {

    //public static final String DATABASE_NAME = "pub_db_cache";
    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + "pub_db_cache";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_GESTURE_PATH = "table_gesture_path";

    public static final String COL_ID = "id";
    public static final String COL_TIMES = "times";
    public static final String COL_EX = "ex";
    public static final String COL_EY = "ey";
    public static final String COL_PRESSURE = "pressure";
    public static final String COL_SIZE = "size";
    public static final String COL_TIME = "time";

    public static final String SQL_CREATE_TABLE_GESTURE_PATH = "CREATE TABLE IF NOT EXISTS " +
            TABLE_GESTURE_PATH + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_TIMES
            + " INTEGER NOT NULL," + COL_EX + " REAL DEFAULT 0.0," + COL_EY + " REAL DEFAULT 0,"
            + COL_PRESSURE + " REAL DEFAULT 0," + COL_SIZE + " REAL DEFAULT 0," + COL_TIME + " " +
            "TEXT" + ");";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS table1";
    public static final String SQL_INSERT_TABLE = "INSERT INTO table1(columns1) VALUES(values1)";
    public static final String SQL_DELETE_TABLE = "DELETE FROM table1";
    public static final String SQL_UPDATE_TABLE = "UPDATE table1 SET ";
    public static final String SQL_WHERE = " WHERE ";

    public static final String PLACE_HOLDER_TABLE1 = "table1";
    public static final String PLACE_HOLDER_COL1 = "columns1";
    public static final String PLACE_HOLDER_VALUES1 = "values1";
}
