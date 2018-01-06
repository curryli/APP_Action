package com.pub.up.demos.gesturelock.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import static android.content.Context.SENSOR_SERVICE;


public class SystemUtil {

    public static void regPressureSensorListner(Context context, SensorEventListener sensorListener) {
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        /*通过SensorManager获取相应的（压力传感器）Sensor类型对象*/
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(sensorListener, sensor
                , SensorManager.SENSOR_DELAY_FASTEST);
    }

    public static void unregPressureSensorListner(Context context, SensorEventListener sensorListener) {
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        /*通过SensorManager获取相应的（压力传感器）Sensor类型对象*/
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.unregisterListener(sensorListener, sensor);
    }

    /**
     * dp to px
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dpToPx(Context context, float dp) {
        return (int) applyDimension(context, TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    /**
     * 单位转换
     *
     * @param context
     * @param unit    TypedValue.COMPLEX_UNIT_DIP
     * @param value   px
     * @return
     */
    public static float applyDimension(Context context, int unit, float value) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, displayMetrics);
    }

}
