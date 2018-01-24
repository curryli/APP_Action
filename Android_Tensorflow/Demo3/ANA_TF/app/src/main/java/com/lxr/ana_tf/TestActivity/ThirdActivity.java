package com.lxr.ana_tf.TestActivity;

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

import com.lxr.ana_tf.R;
import com.lxr.ana_tf.Utils.FileUtil;
import com.lxr.ana_tf.Utils.SampleRate;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener,
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


    private static final String MODEL_FILE = "file:///android_asset/optimized_tfdroid.pb";
    private static final String INPUT_NODE = "I:0";
    private static final String OUTPUT_NODE = "O:0";
    private static final int Height = 1;
    private static final int Width = 100;
    private static final int Channel = 3;
    private static final int goal_size = Height*Width*Channel;

    HashMap<Integer,String> category_Map = new HashMap<Integer,String> ();


    private final int N_SAMPLES = 100;
    private static ArrayList<Float> x;
    private static ArrayList<Float> y;
    private static ArrayList<Float> z;
    private static ArrayList<Float> input_signal;



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

        x = new ArrayList<Float>();
        y = new ArrayList<Float>();
        z = new ArrayList<Float>();
        input_signal = new ArrayList<Float>();

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
            x.add(event.values[0]);
            y.add(event.values[1]);
            z.add(event.values[2]);
        }

    }

    private float[] toFloatArray(List<Float> list)
    {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }


    private void normalize()
    {
        float x_m = 1.533021f; float y_m = -0.483626f; float z_m = 4.513545f;
        float x_s = 6.318019f; float y_s = 4.708760f; float z_s = 5.3044881f;

        for(int i = 0; i < N_SAMPLES; i++)
        {
            x.set(i,((x.get(i) - x_m)/x_s));
            y.set(i,((y.get(i) - y_m)/y_s));
            z.set(i,((z.get(i) - z_m)/z_s));
        }
    }

    private void activityPrediction()
    {
        if(x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {
            // Mean normalize the signal
            normalize();

            // Copy all x,y and z values to one array of shape N_SAMPLES*3
            for(int i=0;i<N_SAMPLES;i++) {
                input_signal.add(x.get(i));
                input_signal.add(y.get(i));
                input_signal.add(z.get(i));
            }

            Log.e("Sensors", input_signal.get(0).toString()+input_signal.get(1).toString()+input_signal.get(2).toString());

            inferenceInterface.fillNodeFloat(INPUT_NODE, new int[]{1, Height, Width, Channel}, toFloatArray(input_signal));

            inferenceInterface.runInference(new String[] {OUTPUT_NODE});

            float[] resu = {0,0,0,0};
            inferenceInterface.readNodeFloat(OUTPUT_NODE, resu);

            int idx_max = getMaxIndex(resu);
            curr_stat = category_Map.get(idx_max);
            model_tag.setText("当前行为:" + curr_stat);

            // Clear all the values
            x.clear(); y.clear(); z.clear(); input_signal.clear();
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
                FileUtil.clear_Acc();
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
