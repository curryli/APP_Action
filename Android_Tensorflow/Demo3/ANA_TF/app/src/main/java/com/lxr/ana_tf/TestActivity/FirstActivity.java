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

import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener,
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
//        //保证采集可控
//        if (!processState) {
//            return;
//        }
//
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            //加速度感应器
//            float x_acc = event.values[0];
//            float y_acc = event.values[1];
//            float z_acc = event.values[2];
//            acc_info.setText("x:" + x_acc + "\ny:" + y_acc + "\nz:" + z_acc);    //读数更新
//            Log.e("Sensors", "Accelerometer: x,y,z=" + x_acc + "," + y_acc + "," + z_acc);
//            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
//            FileUtil.writeToFile(x_acc + "," + y_acc + "," + z_acc + "," + df.format(new Date()));
//        }

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


    public String run_model(){
        double[][][] inputs =  {{{-0.16078986,0.05999735,0.99063231}
                ,{-0.16230565,0.06609883,0.99243776}
                ,{-0.16230565,0.06203118,0.98521614}
                ,{-0.16533723,0.06203118,0.98882686}
                ,{-0.16533723,0.06813266,0.99243776}
                ,{-0.16533723,0.064065,0.99243776}
                ,{-0.16533723,0.06813266,0.99063231}
                ,{-0.16230565,0.064065,0.99063231}
                ,{-0.16382144,0.06203118,0.99243776}
                ,{-0.16533723,0.064065,0.99063231}
                ,{-0.16533723,0.07016648,0.9870214 }
                ,{-0.16685303,0.064065,0.99063231}
                ,{-0.16533723,0.064065,0.99063231}
                ,{-0.16230565,0.06813266,0.99243776}
                ,{-0.16382144,0.06609883,0.99785393}
                ,{-0.16382144,0.06609883,0.99063231}
                ,{-0.16230565,0.06813266,0.99243776}
                ,{-0.16533723,0.064065,0.99063231}
                ,{-0.16382144,0.06203118,0.99243776}
                ,{-0.16685303,0.06203118,0.99424321}
                ,{-0.16533723,0.06813266,0.99063231}
                ,{-0.16533723,0.064065,0.9870214 }
                ,{-0.16382144,0.06813266,0.9870214 }
                ,{-0.16382144,0.064065,0.99063231}
                ,{-0.16382144,0.06813266,0.99243776}
                ,{-0.16533723,0.06609883,0.9870214 }
                ,{-0.16078986,0.064065,0.99063231}
                ,{-0.16533723,0.06609883,0.98882686}
                ,{-0.16382144,0.05592969,0.98521614}
                ,{-0.16382144,0.064065,0.99424321}
                ,{-0.16230565,0.064065,0.99243776}
                ,{-0.16382144,0.06203118,0.99243776}
                ,{-0.16533723,0.06609883,0.99063231}
                ,{-0.16230565,0.05999735,0.98521614}
                ,{-0.16836882,0.064065,0.98882686}
                ,{-0.16533723,0.064065,0.99604848}
                ,{-0.16382144,0.06203118,0.98882686}
                ,{-0.16230565,0.064065,0.98882686}
                ,{-0.16533723,0.064065,0.98521614}
                ,{-0.16685303,0.06609883,0.98882686}
                ,{-0.16685303,0.064065,0.98882686}
                ,{-0.16685303,0.06813266,0.99424321}
                ,{-0.16533723,0.064065,0.99785393}
                ,{-0.16230565,0.06813266,0.99063231}
                ,{-0.16382144,0.07220031,0.99243776}
                ,{-0.16230565,0.064065,0.99424321}
                ,{-0.16685303,0.064065,0.9870214 }
                ,{-0.16533723,0.05999735,0.9870214 }
                ,{-0.16533723,0.06609883,0.99243776}
                ,{-0.16382144,0.06609883,0.99063231}
                ,{-0.16533723,0.05999735,0.99063231}
                ,{-0.16685303,0.06203118,0.99424321}
                ,{-0.16382144,0.06609883,0.9870214 }
                ,{-0.16685303,0.06609883,0.98882686}
                ,{-0.16382144,0.064065,0.9870214 }
                ,{-0.16533723,0.064065,0.99243776}
                ,{-0.16533723,0.06609883,0.98521614}
                ,{-0.16382144,0.06813266,0.98882686}
                ,{-0.16382144,0.06813266,0.99604848}
                ,{-0.16533723,0.06203118,0.98882686}
                ,{-0.16382144,0.06203118,0.9870214 }
                ,{-0.16533723,0.064065,0.99604848}
                ,{-0.16382144,0.06609883,0.99604848}
                ,{-0.16230565,0.05999735,0.99243776}
                ,{-0.16685303,0.064065,0.99063231}
                ,{-0.16230565,0.064065,0.99063231}
                ,{-0.16382144,0.06609883,0.9870214 }
                ,{-0.16836882,0.06609883,0.99424321}
                ,{-0.16533723,0.06813266,0.98882686}
                ,{-0.16230565,0.064065,0.98882686}
                ,{-0.16685303,0.064065,0.9870214 }
                ,{-0.16230565,0.05999735,0.98882686}
                ,{-0.16382144,0.064065,0.99063231}
                ,{-0.16533723,0.06813266,0.98882686}
                ,{-0.16533723,0.064065,0.99243776}
                ,{-0.16533723,0.06203118,0.98882686}
                ,{-0.16382144,0.06813266,0.98882686}
                ,{-0.16230565,0.06813266,0.99243776}
                ,{-0.16685303,0.06813266,0.97979978}
                ,{-0.16230565,0.07016648,0.98882686}
                ,{-0.16533723,0.06813266,0.98882686}
                ,{-0.16382144,0.064065,0.99063231}
                ,{-0.16382144,0.06609883,0.9870214 }
                ,{-0.16230565,0.05999735,0.9870214 }
                ,{-0.16078986,0.06609883,0.99243776}
                ,{-0.16533723,0.06203118,0.9870214 }
                ,{-0.16382144,0.06813266,0.99063231}
                ,{-0.16382144,0.07016648,0.98521614}
                ,{-0.16836882,0.064065,0.9870214 }
                ,{-0.16685303,0.06203118,0.99243776}
                ,{-0.16685303,0.06609883,0.99063231}
                ,{-0.16685303,0.06203118,0.99424321}
                ,{-0.16230565,0.06203118,0.99424321}
                ,{-0.16533723,0.06609883,0.9870214 }
                ,{-0.16382144,0.06609883,0.98882686}
                ,{-0.16078986,0.06609883,0.99243776}
                ,{-0.16685303,0.06203118,0.9870214 }
                ,{-0.16382144,0.06609883,0.9870214 }
                ,{-0.16836882,0.06203118,0.98521614}
                ,{-0.16533723,0.064065,0.98521614}}};

        float[] inputFloats = new float[Height*Width*Channel];

        for (int i = 0; i < inputs.length; i++) {         // 遍历数组
            for (int j = 0; j < inputs[0].length; j++) {
                for (int k = 0; k < inputs[0][0].length; k++) {
                    inputFloats[i*Width*Channel + j*Channel +k] = (float)inputs[i][j][k];
                }
            }
        }

        Log.e("Sensors", "inputFloats:" + String.valueOf(inputFloats[0]));

        inferenceInterface.fillNodeFloat(INPUT_NODE, new int[]{1, Height, Width, Channel}, inputFloats);
        //inferenceInterface.fillNodeFloat(KEEP_PROB_NODE,new int[]{1},new float[]{1.0f});


        inferenceInterface.runInference(new String[] {OUTPUT_NODE});

        float[] resu = {0,0,0,0};
        inferenceInterface.readNodeFloat(OUTPUT_NODE, resu);

        String ret_str = Integer.toString(getMaxIndex(resu));
        return  ret_str;
    }

    @Override
    public void onClick(View v) {
        enableButtons();
        switch (v.getId()) {
            case R.id.btn_start:
                updateStatus(true);
                String category = run_model();

                model_tag.setText("当前行为:" + category);

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
