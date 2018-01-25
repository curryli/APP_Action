package com.lxr.acc_save;
//http://blog.csdn.net/bin470398393/article/details/78918921

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
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lxr.acc_save.Utils.FileUtil;
import com.lxr.acc_save.Utils.JsonUtil;
import com.lxr.acc_save.Utils.OkHttpUtils;
import com.lxr.acc_save.Utils.SampleRate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener,
        SensorEventListener {
    private static final String TAG = "MainActivity";

    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;
    private TextView acc_info;
    private TextView acc_info1;
    private TextView acc_info2;

    private boolean processState = false;   //标记当前是否已经在计步
    private Timer mTimer;
    private TimerTask mTask;
    private int mCounter = 0;

    private Handler handler = new Handler();
    private List<String> mAccDataList = new ArrayList<String>();
    private static final int CACHE_LIMIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        FileUtil.init(mContext);
        setContentView(R.layout.activity_main);

        bindViews();

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);// CPU保存运行


        // 获取传感器管理器
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, mSensorAccelerometer, SampleRate.get_RATE_50Hz());


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

    }

    private void bindViews() {
        acc_info = (TextView) findViewById(R.id.acc_info);
        acc_info1 = (TextView) findViewById(R.id.acc_info1);
        acc_info2 = (TextView) findViewById(R.id.acc_info2);

        findViewById(R.id.btn_sitting).setOnLongClickListener(this);
        findViewById(R.id.btn_walking).setOnLongClickListener(this);
        findViewById(R.id.btn_upstairs).setOnLongClickListener(this);
        findViewById(R.id.btn_downstairs).setOnLongClickListener(this);
        findViewById(R.id.btn_jogging).setOnLongClickListener(this);
        findViewById(R.id.btn_standing).setOnLongClickListener(this);

        findViewById(R.id.btn_stop).setOnLongClickListener(this);
        findViewById(R.id.btn_clear).setOnLongClickListener(this);

    }

    @Override
    public boolean onLongClick(View view) {
        enableButtons();
        switch (view.getId()) {
            case R.id.btn_sitting:
                updateStatus(true);
                acc_info1.setText("当前采集行为:坐着");
                FileUtil.set_save_file("Acc_sitting");
                break;
            case R.id.btn_walking:
                updateStatus(true);
                acc_info1.setText("当前采集状态:行走");
                FileUtil.set_save_file("Acc_walking");
                break;
            case R.id.btn_upstairs:
                updateStatus(true);
                acc_info1.setText("当前采集状态:上楼");
                FileUtil.set_save_file("Acc_upstairs");
                break;
            case R.id.btn_downstairs:
                updateStatus(true);
                acc_info1.setText("当前采集状态:下楼");
                FileUtil.set_save_file("Acc_downstairs");
                break;
            case R.id.btn_jogging:
                updateStatus(true);
                acc_info1.setText("当前采集状态:慢跑");
                FileUtil.set_save_file("Acc_jogging");
                break;
            case R.id.btn_standing:
                updateStatus(true);
                acc_info1.setText("当前采集状态:站立");
                FileUtil.set_save_file("Acc_standing");
                break;
            case R.id.btn_stop:
                updateStatus(false);
                acc_info.setText("x:None\ny:None\nz:None");
                acc_info1.setText("当前采集行为:NONE");
                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
                break;
            case R.id.btn_clear:
                Log.e("Sensors", "清除Acc_save目录下的所有文件");
                FileUtil.clear_Acc();
                break;
        }
        return false;
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

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                refreshListener();
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //保证采集可控
        if (!processState) {
            return;
        }

        if (mAccDataList.size() == CACHE_LIMIT) {
            doRecognizeBehavior();
            return;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //加速度感应器
            float x_acc = sensorEvent.values[0];
            float y_acc = sensorEvent.values[1];
            float z_acc = sensorEvent.values[2];
            acc_info.setText("x:" + x_acc + "\ny:" + y_acc + "\nz:" + z_acc);    //读数更新
            Log.e("Sensors", "Accelerometer: x,y,z=" + x_acc + "," + y_acc + "," + z_acc);
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
            //FileUtil.writeToFile(x_acc + "," + y_acc + "," + z_acc + "," + df.format(new Date()));

            mAccDataList.add(x_acc + "," + y_acc + "," + z_acc + "|");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        unregisterReceiver(mReceiver);
    }

    private void refreshListener() {
        mSensorManager.unregisterListener(this);
        mWakeLock.acquire();// 屏幕熄后，CPU继续运行
        mSensorManager.registerListener(this, mSensorAccelerometer, SampleRate.get_RATE_50Hz());
    }

    private void doRecognizeBehavior() {
        Log.e(TAG, "##### start request.");
        try {
            String data = "";
            int size = mAccDataList.size();
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    String last = mAccDataList.get(i - 1);
                    data += last.substring(0, last.length() - 1);
                } else {
                    data += mAccDataList.get(i);
                }
            }
            OkHttpUtils.getInstance().doRecognizeBehavior(data, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "##### onFailure:" + e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "网络请求异常，请检查后重试!", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Log.e(TAG, "##### onResponse:" + str);
                    String msg = JsonUtil.getResultMsg(str);
                    String tempBehavior = "UNKNOWN";
                    if (msg.equals("0")){
                        tempBehavior = "跑步";
                    } else if (msg.equals("1")){
                        tempBehavior = "站立";
                    } else if (msg.equals("2")){
                        tempBehavior = "静止";
                    } else if (msg.equals("3")){
                        tempBehavior = "走路";
                    } else {
                        tempBehavior = "UNKNOWN";
                    }
                    final String behavior = tempBehavior;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, behavior , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "##### exception:" + e.toString());
            Toast.makeText(this, "网络请求异常.", Toast.LENGTH_SHORT).show();
        } finally {
            mAccDataList.clear();
            Log.e(TAG, "##### finish request.");
        }
    }

}
