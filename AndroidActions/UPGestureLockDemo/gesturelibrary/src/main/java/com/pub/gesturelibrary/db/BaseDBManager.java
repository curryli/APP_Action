package com.pub.gesturelibrary.db;

import android.content.ContentValues;
import android.content.Context;

public class BaseDBManager extends DatabaseHelper {

    protected synchronized long insert(Context context, String table, ContentValues values) {
        return insert(context, table, values);
    }

    protected synchronized int delete(Context context, String table, String whereClause, String[]
            whereArgs) {
        return delete(context, table, whereClause, whereArgs);
    }

    protected synchronized int update(Context context, String table, String whereClause, String[]
            whereArgs, ContentValues values) {
        return update(context, table, whereClause, whereArgs, values);
    }

    protected synchronized ContentValues[] query(Context context, String table, String[] columns,
                                                 String selection, String[] selectionArgs, String
                                                         groupBy, String having, String orderBy) {
        return query(context, table, columns, selection, selectionArgs, groupBy, having,
                orderBy);
    }

}
