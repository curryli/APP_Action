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

        //加速度
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener, acc_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        //压力
        Sensor pre_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(sensorListener, pre_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        // 重力
        Sensor gra_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(sensorListener, gra_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        // 线性加速度
        Sensor liacc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(sensorListener, liacc_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        // 磁场
        Sensor mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(sensorListener, mag_sensor
                , SensorManager.SENSOR_DELAY_FASTEST);

        // 矢量旋转
        Sensor rot_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(sensorListener, rot_sensor
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

        // 矢量旋转
        Sensor rot_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.unregisterListener(sensorListener, rot_sensor);


        // 磁场
        Sensor mag_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.unregisterListener(sensorListener, mag_sensor);


        // 线性加速度
        Sensor liacc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.unregisterListener(sensorListener, liacc_sensor);

        // 重力
        Sensor gra_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.unregisterListener(sensorListener, gra_sensor);

        //压力
        Sensor pre_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.unregisterListener(sensorListener, pre_sensor);

        //加速度
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.unregisterListener(sensorListener, acc_sensor);

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
