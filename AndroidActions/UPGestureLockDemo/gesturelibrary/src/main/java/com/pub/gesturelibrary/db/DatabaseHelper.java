package com.pub.gesturelibrary.db;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;

public class DatabaseHelper implements DatabaseConstant {

    protected synchronized long insert(Context context, String table, ContentValues values) {
        SQLiteDatabase db = PubSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        long rowId = 0;
        try {
            db.beginTransaction();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                rowId = insert(db, table, values);
            } else {
                rowId = db.insert(table, null, values);
            }
            db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rowId;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private long insert(SQLiteDatabase db, String table, ContentValues values) throws
            SQLException {
        String sql = SQL_INSERT_TABLE;
        sql = sql.replace(PLACE_HOLDER_TABLE1, table);

        int size = values.size();
        if (size == 0) {
            return 0;
        }

        Object[] obj = new Object[size];
        StringBuffer placeHolder = new StringBuffer();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                placeHolder.append(",");
            }
            placeHolder.append("?");
        }
        String place = placeHolder.toString();
        sql = sql.replace(PLACE_HOLDER_VALUES1, place);

        int index = 0;
        for (String key : values.keySet()) {
            if (!TextUtils.isEmpty(key)) {
                place = place.replaceFirst("\\?", key);
                obj[index++] = values.get(key);
            }
        }

        sql = sql.replace(PLACE_HOLDER_COL1, place);
        SQLiteStatement statement = generateStatement(db, sql, obj);
        return statement.executeInsert();
    }

    protected synchronized int deleteAll(Context context, String table) {
        return delete(context, table, null, null);
    }

    protected synchronized int delete(Context context, String table, String whereClause,
                                          String[] whereArgs) {
        SQLiteDatabase db = PubSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        int rows = 0;
        try {
            db.beginTransaction();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                rows = delete(db, table, whereClause, whereArgs);
            } else {
                rows = db.delete(table, whereClause, whereArgs);
            }
            db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rows;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private int delete(SQLiteDatabase db, String table, String whereClause, String[]
            whereArgs) throws SQLException {
        SQLiteStatement statement = null;
        String sql = SQL_DELETE_TABLE;
        sql = sql.replace(PLACE_HOLDER_TABLE1, table);
        if (!TextUtils.isEmpty(whereClause)) {
            sql += SQL_WHERE + whereClause;
            statement = generateStatement(db, sql, whereArgs);
        } else {
            statement = db.compileStatement(sql);
        }
        return statement.executeUpdateDelete();
    }

    protected synchronized int update(Context context, String table, String whereClause,
                                          String[] whereArgs, ContentValues values) {
        SQLiteDatabase db = PubSQLiteOpenHelper.getInstance(context).getWritableDatabase();
        int rows = 0;
        try {
            db.beginTransaction();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                rows = update(db, table, values, whereClause, whereArgs);
            } else {
                rows = db.update(table, values, whereClause, whereArgs);
            }
            db.setTransactionSuccessful();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rows;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private int update(SQLiteDatabase db, String table, ContentValues values, String
            whereClause, String[] whereArgs) throws SQLException {
        int size = values.size();
        int length = whereArgs.length;
        if (size == 0) {
            return 0;
        }
        String sql = SQL_UPDATE_TABLE;
        sql = sql.replace(PLACE_HOLDER_TABLE1, table);

        Object[] obj = new Object[size + length];
        int index = 0;
        StringBuffer setValues = new StringBuffer();
        for (String key : values.keySet()) {
            if (!TextUtils.isEmpty(key)) {
                if (index != 0) {
                    setValues.append(", ");
                }
                setValues.append(key + "=?");
                obj[index++] = values.get(key);
            }
        }
        for (int i = 0; i < length; i++) {
            obj[index++] = whereArgs[i];
        }

        sql += setValues.toString();

        if (!TextUtils.isEmpty(whereClause)) {
            sql += SQL_WHERE + whereClause;
        }

        SQLiteStatement statement = generateStatement(db, sql, obj);
        return statement.executeUpdateDelete();
    }

    protected synchronized ContentValues[] query(Context context, String table, String[]
            columns, String selection, String[] selectionArgs, String groupBy, String having,
                                                     String orderBy) {
        SQLiteDatabase db = PubSQLiteOpenHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having,
                orderBy);
        ContentValues[] result = new ContentValues[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, values);
            result[index++] = values;
        }
        cursor.close();
        db.close();
        return result;
    }

    private SQLiteStatement generateStatement(SQLiteDatabase db, String sql, Object[]
            value) {
        SQLiteStatement statement = db.compileStatement(sql);
        int size = value.length;
        statement.clearBindings();
        for (int i = 0; i < size; i++) {
            Object obj = value[i];
            if (obj == null) {
                statement.bindNull(i + 1);
            } else if (obj instanceof Double) {
                statement.bindDouble(i + 1, (Double) obj);
            } else if (obj instanceof String) {
                statement.bindString(i + 1, (String) obj);
            } else if (obj instanceof Long) {
                statement.bindLong(i + 1, (Long) obj);
            } else if (obj instanceof byte[]) {
                statement.bindBlob(i + 1, (byte[]) obj);
            } else if (obj instanceof Integer) {
                statement.bindLong(i + 1, ((Integer) obj).longValue());
            }
        }
        return statement;
    }

    static class PubSQLiteOpenHelper extends SQLiteOpenHelper {

        private static PubSQLiteOpenHelper mInstance;

        private PubSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public static PubSQLiteOpenHelper getInstance(Context context) {
            if (mInstance == null) {
                synchronized (PubSQLiteOpenHelper.class) {
                    if (mInstance == null) {
                        mInstance = new PubSQLiteOpenHelper(context);
                    }
                }
            }
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.beginTransaction();
                upgrade(db);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }

        private void create(SQLiteDatabase db) {
            SQLiteStatement statement = db.compileStatement(SQL_CREATE_TABLE_GESTURE_PATH);
            statement.execute();
        }

        private void upgrade(SQLiteDatabase db) {
            dropTable(db, TABLE_GESTURE_PATH);
            create(db);
        }

        private void dropTable(SQLiteDatabase db, String table) throws SQLException {
            String sql = SQL_DROP_TABLE;
            sql = sql.replace(PLACE_HOLDER_TABLE1, table);
            SQLiteStatement statement = db.compileStatement(sql);
            statement.execute();
        }

    }

}
