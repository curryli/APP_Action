package com.pub.gesturelibrary.db;

import android.content.ContentValues;
import android.content.Context;

public class GesturePathManager extends BaseDBManager {

    private static GesturePathManager mInstance;

    private GesturePathManager() {
    }

    public static GesturePathManager getInstance() {
        if (mInstance == null) {
            synchronized (GesturePathManager.class) {
                if (mInstance == null) {
                    mInstance = new GesturePathManager();
                }
            }
        }
        return mInstance;
    }

    public boolean saveGesturePath(Context context, int times, double ex, double ey, double
            pressure, double size, String time) {
        ContentValues values = new ContentValues();
        values.put(COL_TIMES, times);
        values.put(COL_EX, ex);
        values.put(COL_EY, ey);
        values.put(COL_PRESSURE, pressure);
        values.put(COL_SIZE, size);
        values.put(COL_TIME, time);

        boolean ret = false;
        long rowId = insert(context, TABLE_GESTURE_PATH, values);
        if (rowId > 0) {
            ret = true;
        }
        return ret;
    }

}
