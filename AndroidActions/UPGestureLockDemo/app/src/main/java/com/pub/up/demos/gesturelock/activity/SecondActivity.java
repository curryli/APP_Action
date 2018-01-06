package com.pub.up.demos.gesturelock.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import com.pub.gesturelibrary.enums.LockMode;
import com.pub.gesturelibrary.view.CustomLockView;
import com.pub.up.demos.gesturelock.R;
import com.pub.up.demos.gesturelock.constants.Constants;
import com.pub.up.demos.gesturelock.utils.PasswordUtil;
import com.pub.up.demos.gesturelock.utils.SystemUtil;

import butterknife.BindView;

import static com.pub.gesturelibrary.enums.LockMode.CLEAR_PASSWORD;
import static com.pub.gesturelibrary.enums.LockMode.SETTING_PASSWORD;
import static com.pub.gesturelibrary.enums.LockMode.VERIFY_PASSWORD;

public class SecondActivity extends BaseActivity {

    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.lv_lock)
    CustomLockView lvLock;
    @BindView(R.id.tv_hint)
    TextView tvHint;


    @Override
    public void beforeInitView() {
        setContentView(R.layout.activity_second);
    }

    /**
     * 初始化View
     */
    @Override
    public void initView() {
        //显示绘制方向
        lvLock.setShow(true);
        //允许最大输入次数
        lvLock.setErrorNumber(3);
        //密码最少位数
        lvLock.setPasswordMinLength(4);
        //编辑密码或设置密码时，是否将密码保存到本地，配合setSaveLockKey使用
        lvLock.setSavePin(true);
        //保存密码Key
        lvLock.setSaveLockKey(Constants.PASS_KEY);
    }

    /**
     * 设置监听回调
     */
    @Override
    public void initListener() {
        lvLock.setOnCompleteListener(onCompleteListener);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        //设置模式
        LockMode lockMode = (LockMode) getIntent().getSerializableExtra(Constants
                .INTENT_SECONDACTIVITY_KEY);
        setLockMode(lockMode);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO register
        Log.e("gesture-path", "onResume-register regPressureSensorListner");
        SystemUtil.regPressureSensorListner(this, mSensorEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO unregister
        Log.e("gesture-path", "onPause-unregister unregPressureSensorListner");
        SystemUtil.unregPressureSensorListner(this, mSensorEventListener);
    }

    /*声明一个SensorEventListener对象用于侦听Sensor事件，并重载onSensorChanged方法*/
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                /*压力传感器返回当前的压强，单位是百帕斯卡hectopascal（hPa）。*/
                float pressure = event.values[0];

                // TODO mark the pressure
                Log.e("gesture-path", "pressure(hPa)=" + String.valueOf(pressure));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * 密码输入模式
     */
    private void setLockMode(LockMode mode, String password, String msg) {
        lvLock.setMode(mode);
        if (mode == VERIFY_PASSWORD) {
            lvLock.setErrorNumber(1000);
        } else {
            lvLock.setErrorNumber(3);
        }
        lvLock.setClearPasssword(false);
        if (mode != SETTING_PASSWORD) {
            tvHint.setText("请输入已经设置过的密码");
            lvLock.setOldPassword(password);
        } else {
            tvHint.setText("请输入要设置的密码");
        }
        tvText.setText(msg);
    }

    /**
     * 密码输入监听
     */
    CustomLockView.OnCompleteListener onCompleteListener = new CustomLockView.OnCompleteListener() {
        @Override
        public void onComplete(String password, int[] indexs) {
            tvHint.setText(getPassWordHint());
            //finish();
        }

        @Override
        public void onError(String errorTimes) {
            tvHint.setText("密码错误，还可以输入" + errorTimes + "次");
        }

        @Override
        public void onPasswordIsShort(int passwordMinLength) {
            tvHint.setText("密码不能少于" + passwordMinLength + "个点");
        }

        @Override
        public void onAginInputPassword(LockMode mode, String password, int[] indexs) {
            tvHint.setText("请再次输入密码");
        }


        @Override
        public void onInputNewPassword() {
            tvHint.setText("请输入新密码");
        }

        @Override
        public void onEnteredPasswordsDiffer() {
            tvHint.setText("两次输入的密码不一致");
        }

        @Override
        public void onErrorNumberMany() {
            tvHint.setText("密码错误次数超过限制，不能再输入");
        }

    };


    /**
     * 密码相关操作完成回调提示
     */
    private String getPassWordHint() {
        String str = null;
        switch (lvLock.getMode()) {
            case SETTING_PASSWORD:
                str = "密码设置成功";
                break;
            case EDIT_PASSWORD:
                str = "密码修改成功";
                break;
            case VERIFY_PASSWORD:
                str = "密码正确";
                break;
            case CLEAR_PASSWORD:
                str = "密码已经清除";
                break;
        }
        return str;
    }

    /**
     * 设置解锁模式
     */
    private void setLockMode(LockMode mode) {
        String str = "";
        switch (mode) {
            case CLEAR_PASSWORD:
                str = "清除密码";
                setLockMode(CLEAR_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case EDIT_PASSWORD:
                str = "修改密码";
                setLockMode(LockMode.EDIT_PASSWORD, PasswordUtil.getPin(this), str);
                break;
            case SETTING_PASSWORD:
                str = "设置密码";
                setLockMode(SETTING_PASSWORD, null, str);
                break;
            case VERIFY_PASSWORD:
                str = "验证密码";
                setLockMode(VERIFY_PASSWORD, PasswordUtil.getPin(this), str);
                break;
        }
        tvText.setText(str);
    }


}
