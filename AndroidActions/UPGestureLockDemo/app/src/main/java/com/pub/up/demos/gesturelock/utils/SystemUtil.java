package com.pub.up.demos.gesturelock.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import static android.content.Context.SENSOR_SERVICE;


public class SystemUtil {

    public static void regSensorListner(Context context, SensorEventListener sensorListener) {
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        sensorManager.registerListener(sensorListener, sensor
                , SensorManager.SENSOR_DELAY_FASTEST);
        //方向
        Sensor ori_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(sensorListener, ori_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        //陀螺仪
        Sensor gry_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(sensorListener, gry_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);
    }

    public static void unregSensorListner(Context context, SensorEventListener sensorListener) {
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        sensorManager.unregisterListener(sensorListener, sensor);

        //方向
        Sensor ori_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.unregisterListener(sensorListener, ori_sensor);

        //陀螺仪
        Sensor gry_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.unregisterListener(sensorListener, gry_sensor);
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
