package com.lxr.ana_tf.Utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

    }

    public static void unregSensorListner(Context context, SensorEventListener sensorListener) {
        /*获取系统服务（SENSOR_SERVICE）返回一个SensorManager对象*/
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        sensorManager.unregisterListener(sensorListener, sensor);

        //加速度
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.unregisterListener(sensorListener, acc_sensor);
    }


}
