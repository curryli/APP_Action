package com.lxr.acc_save;
//http://blog.csdn.net/bin470398393/article/details/78918921

import android.content.Context;
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

import com.lxr.acc_save.Utils.FileUtil;
import com.lxr.acc_save.Utils.SampleRate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private Context mContext;
    private TextView acc_info;
    private TextView acc_info1;
    private TextView acc_info2;

    private boolean processState = false;   //标记当前是否已经在计步
    private Timer mTimer;
    private TimerTask mTask;
    private int mCounter = 0;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        FileUtil.init(mContext);
        setContentView(R.layout.activity_main);

        bindViews();

        // 获取传感器管理器
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sManager.registerListener(this, mSensorAccelerometer, SampleRate.get_RATE_20Hz());


    }

    private void bindViews() {
        acc_info = (TextView) findViewById(R.id.acc_info);
        acc_info1 = (TextView) findViewById(R.id.acc_info1);
        acc_info2 = (TextView) findViewById(R.id.acc_info2);

        findViewById(R.id.btn_sitting).setOnClickListener(this);
        findViewById(R.id.btn_walking).setOnClickListener(this);
        findViewById(R.id.btn_upstairs).setOnClickListener(this);
        findViewById(R.id.btn_downstairs).setOnClickListener(this);
        findViewById(R.id.btn_jogging).setOnClickListener(this);
        findViewById(R.id.btn_standing).setOnClickListener(this);

        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //保证采集可控
        if (!processState) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //加速度感应器
            float x_acc = event.values[0];
            float y_acc = event.values[1];
            float z_acc = event.values[2];
            acc_info.setText("x:" + x_acc + "\ny:" + y_acc + "\nz:" + z_acc);    //读数更新
            Log.e("Sensors", "Accelerometer: x,y,z=" + x_acc + "," + y_acc + "," + z_acc);
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
            FileUtil.writeToFile(x_acc + "," + y_acc + "," + z_acc + "," + df.format(new Date()));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onClick(View v) {
        enableButtons();
        switch (v.getId()) {
            case R.id.btn_sitting:
                updateStatus(true);
                acc_info1.setText("当前采集行为:坐着");
                FileUtil.set_save_file("Sitting");
                break;
            case R.id.btn_walking:
                updateStatus(true);
                acc_info1.setText("当前采集状态:行走");
                FileUtil.set_save_file("Walking");
                break;
            case R.id.btn_upstairs:
                updateStatus(true);
                acc_info1.setText("当前采集状态:上楼");
                FileUtil.set_save_file("Upstairs");
                break;
            case R.id.btn_downstairs:
                updateStatus(true);
                acc_info1.setText("当前采集状态:下楼");
                FileUtil.set_save_file("Downstairs");
                break;
            case R.id.btn_jogging:
                updateStatus(true);
                acc_info1.setText("当前采集状态:慢跑");
                FileUtil.set_save_file("Jogging");
                break;
            case R.id.btn_standing:
                updateStatus(true);
                acc_info1.setText("当前采集状态:站立");
                FileUtil.set_save_file("Standing");
                break;
            case R.id.btn_stop:
                updateStatus(false);
                acc_info.setText("x:None\ny:None\nz:None");
                acc_info1.setText("当前采集行为:NONE");
                break;
            case R.id.btn_clear:
                Log.e("Sensors", "清除Acc_save目录下的所有文件");
                FileUtil.clear_Acc();
                break;
        }
    }

    private void enableButtons() {
        findViewById(R.id.btn_sitting).setEnabled(false);
        findViewById(R.id.btn_walking).setEnabled(false);
        findViewById(R.id.btn_upstairs).setEnabled(false);
        findViewById(R.id.btn_downstairs).setEnabled(false);
        findViewById(R.id.btn_jogging).setEnabled(false);
        findViewById(R.id.btn_standing).setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.btn_sitting).setEnabled(true);
                findViewById(R.id.btn_walking).setEnabled(true);
                findViewById(R.id.btn_upstairs).setEnabled(true);
                findViewById(R.id.btn_downstairs).setEnabled(true);
                findViewById(R.id.btn_jogging).setEnabled(true);
                findViewById(R.id.btn_standing).setEnabled(true);
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


    //向量求模
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sManager.unregisterListener(this, mSensorAccelerometer);
    }
}
