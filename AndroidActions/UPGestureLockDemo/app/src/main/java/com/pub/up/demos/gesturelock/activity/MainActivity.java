package com.pub.up.demos.gesturelock.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.pub.gesturelibrary.enums.LockMode;
import com.pub.gesturelibrary.util.StringUtils;
import com.pub.up.demos.gesturelock.R;
import com.pub.up.demos.gesturelock.constants.Constants;
import com.pub.up.demos.gesturelock.utils.PasswordUtil;
import com.pub.up.demos.gesturelock.utils.ToastUtil;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_setting)
    Button mBtnSetting;
    @BindView(R.id.btn_edit)
    Button mBtnEdit;
    @BindView(R.id.btn_verify)
    Button mBtnVerify;
    @BindView(R.id.btn_clear)
    Button mBtnClear;

    @Override
    public void beforeInitView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initListener() {
        mBtnSetting.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mBtnVerify.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                actionSecondActivity(LockMode.CLEAR_PASSWORD);
                break;
            case R.id.btn_edit:
                actionSecondActivity(LockMode.EDIT_PASSWORD);
                break;
            case R.id.btn_setting:
                actionSecondActivity(LockMode.SETTING_PASSWORD);
                break;
            case R.id.btn_verify:
                actionSecondActivity(LockMode.VERIFY_PASSWORD);
                break;
        }
    }

    private void actionSecondActivity(LockMode mode) {
        if (mode != LockMode.SETTING_PASSWORD) {
            if (StringUtils.isEmpty(PasswordUtil.getPin(this))) {
                ToastUtil.showMessage(getBaseContext(), "请先设置密码");
                return;
            }
        }

        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(Constants.INTENT_SECONDACTIVITY_KEY, mode);
        startActivity(intent);
    }

}
