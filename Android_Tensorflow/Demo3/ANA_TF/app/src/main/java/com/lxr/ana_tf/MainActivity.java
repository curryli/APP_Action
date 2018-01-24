package com.lxr.ana_tf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lxr.ana_tf.Utils.FileUtil;
import com.lxr.ana_tf.Utils.SampleRate;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.lxr.ana_tf.Utils.MathUtil.getMax;
import static com.lxr.ana_tf.Utils.MathUtil.getMean;
import static com.lxr.ana_tf.Utils.MathUtil.getMin;
import static com.lxr.ana_tf.Utils.MathUtil.getStd;
import static com.lxr.ana_tf.Utils.MathUtil.toFloatArray;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private Context mContext;
    private TextView acc_info;
    private TextView model_tag;
    private TextView acc_info2;

    private boolean processState = false;   //标记当前是否已经在计步
    private Timer mTimer;
    private TimerTask mTask;
    private int mCounter = 0;


    //private static final String MODEL_FILE = "file:///android_asset/optimized_tfdroid.pb";
    private static final String MODEL_FILE = "file:///android_asset/optimized_softmax.pb";
    private static final String INPUT_NODE = "I:0";
    private static final String OUTPUT_NODE = "O:0";

    HashMap<Integer,String> category_Map = new HashMap<Integer,String> ();

    private final int N_SAMPLES = 100;
    private static ArrayList<Float> grav_max;
    private static ArrayList<Float> grav_min;
    private static ArrayList<Float> grav_std;
    private static ArrayList<Float> grav_mean;
    private static ArrayList<Float> grav_buff;


    private static String curr_stat = "None";

    private TensorFlowInferenceInterface inferenceInterface;


    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        inferenceInterface = new TensorFlowInferenceInterface();
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_FILE);


        FileUtil.init(mContext);
        setContentView(R.layout.activity_main);

        bindViews();

        // 获取传感器管理器
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, mSensorAccelerometer, SampleRate.get_RATE_50Hz());

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);  //屏幕关闭以后，重新注册采集器
        registerReceiver(mReceiver, filter);

        category_Map.put(0,"跑步");
        category_Map.put(1,"站立");
        category_Map.put(2,"静止");
        category_Map.put(3,"走路");

        grav_max = new ArrayList<Float>();
        grav_min = new ArrayList<Float>();
        grav_std = new ArrayList<Float>();
        grav_mean = new ArrayList<Float>();
        grav_buff = new ArrayList<Float>();
    }

    private void bindViews() {
        acc_info = (TextView) findViewById(R.id.acc_info);
        model_tag = (TextView) findViewById(R.id.model_tag);
        acc_info2 = (TextView) findViewById(R.id.acc_info2);

        findViewById(R.id.btn_start).setOnClickListener(this);


        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //保证采集可控
        if (!processState) {
            return;
        }
        activityPrediction();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //加速度感应器
            acc_info.setText("x:" + event.values[0] + "\ny:" + event.values[1] + "\nz:" + event.values[2]);    //读数更新
            float gravity = (float) Math.sqrt(Math.pow(event.values[0], 2)+Math.pow(event.values[1], 2)+Math.pow(event.values[2], 2));
            grav_buff.add(gravity);
        }

    }

    private void activityPrediction()
    {
        if(grav_buff.size()==N_SAMPLES) {
            float g_mean = getMean(toFloatArray(grav_buff));
            float g_max = getMax(toFloatArray(grav_buff));
            float g_min = getMin(toFloatArray(grav_buff));
            float g_std = getStd(toFloatArray(grav_buff));

            ArrayList<Float> input_signal = new  ArrayList<Float>();
            input_signal.add(g_mean);
            input_signal.add(g_max);
            input_signal.add(g_min);
            input_signal.add(g_std);

            Log.e("Sensors", input_signal.get(0).toString() + " " + input_signal.get(1).toString() + " " + input_signal.get(2).toString() + " " + input_signal.get(3).toString());

            inferenceInterface.fillNodeFloat(INPUT_NODE, new int[]{1, input_signal.size()}, toFloatArray(input_signal));

            inferenceInterface.runInference(new String[]{OUTPUT_NODE});

            float[] resu = {0, 0, 0, 0};
            inferenceInterface.readNodeFloat(OUTPUT_NODE, resu);

            int idx_max = getMaxIndex(resu);
            curr_stat = category_Map.get(idx_max);
            model_tag.setText("当前行为:" + curr_stat);

            grav_mean.clear();
            grav_max.clear();
            grav_min.clear();
            grav_std.clear();

            grav_buff.clear();
            input_signal.clear();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public static int getMaxIndex(float[] arr){
        int maxIndex = 0;   //获取到的最大值的角标
        for(int i=0; i<arr.length; i++){
            if(arr[i] > arr[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    public void onClick(View v) {
        enableButtons();
        switch (v.getId()) {
            case R.id.btn_start:
                updateStatus(true);
                break;

            case R.id.btn_stop:
                updateStatus(false);
                acc_info.setText("x:None\ny:None\nz:None");
                model_tag.setText("当前行为:NONE");
                break;
            case R.id.btn_clear:
                Log.e("Sensors", "清除Acc_save目录下的所有文件");
                break;
        }
    }

    private void enableButtons() {
        findViewById(R.id.btn_start).setEnabled(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.btn_start).setEnabled(true);
            }
        }, 1000);
    }

    private void updateStatus(boolean process) {
        acc_info2.setText("计时: 0 秒");
        if (mCounter > 0) {
            mCounter = 0;
            if (mTimer != null) {
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                mTimer.purge();
                mTimer = null;
            }
        }
        if (process) {
            mTimer = new Timer();
            mTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCounter++;
                            acc_info2.setText("计时: " + mCounter + " 秒");
                        }
                    });
                }
            };
            mTimer.schedule(mTask, 1000, 1000);
        }
        processState = process;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sManager.unregisterListener(this, mSensorAccelerometer);
        unregisterReceiver(mReceiver);
    }

    private void refreshListener(){
        sManager.unregisterListener(this, mSensorAccelerometer);
        sManager.registerListener(this, mSensorAccelerometer, SampleRate.get_RATE_50Hz());
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                refreshListener();
            }
        }
    };

}
