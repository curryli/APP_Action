package com.lxr.ana_tf.Utils;

/**
 * 加速度传感器采样率设定
 *
 * @author Liu_Longpo
 *
 */
public class SampleRate {
    private static int RATE_10Hz = 100000;

    private static int RATE_20Hz = 50000;

    private static int RATE_50Hz = 20000;

    private static int RATE_80Hz = 12500;

    private static int RATE_100Hz = 10000;

    public SampleRate() {
    };

    public static int get_RATE_10Hz() {
        return RATE_10Hz;
    }

    public static int get_RATE_20Hz() {
        return RATE_20Hz;
    }

    public static int get_RATE_50Hz() {
        return RATE_50Hz;
    }

    public static int get_RATE_80Hz() {
        return RATE_80Hz;
    }

    public static int get_SENSOR_RATE_100Hz() {
        return RATE_100Hz;
    }

}