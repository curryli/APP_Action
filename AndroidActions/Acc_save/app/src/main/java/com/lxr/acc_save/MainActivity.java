package com.lxr.acc_save;
//http://blog.csdn.net/bin470398393/article/details/78918921
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lxr.acc_save.Utils.FileUtil;
import com.lxr.acc_save.Utils.SampleRate;
import com.lxr.acc_save.Utils.SystemUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SensorManager sManager;
    private Sensor mSensorAccelerometer;
    private TextView acc_info;
    private Button btn_start;
    private Button btn_clear;
    private static Context mContext;

    private String save_name  = "";

    private boolean processState = false;   //标记当前是否已经在计步


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
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //保证采集可控
        if(!processState){
            return;
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_start:
                acc_info.setText("x:None\ny:None\nz:None");
                if (processState == true) {
                    btn_start.setText("开始");
                    processState = false;
                }
                else{
                    btn_start.setText("停止");
                    processState = true;
                    FileUtil.set_save_file("Acc");  //按下开始就创建一个文件用于保存
                }
                break;
            case R.id.btn_clear:
                Log.e("Sensors","清除Acc_save目录下的所有文件");
                FileUtil.clear_Acc();
                break;
        }
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
        sManager.unregisterListener(this);
    }
}
