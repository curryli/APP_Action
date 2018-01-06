package com.pub.up.demos.gesturelock.utils;

import android.content.Context;

import com.pub.gesturelibrary.util.ConfigUtil;
import com.pub.up.demos.gesturelock.constants.Constants;


public class PasswordUtil {

    /**
     * 获取设置过的密码
     */
    public static String getPin(Context context) {
        String password = ConfigUtil.getInstance(context).getString(Constants.PASS_KEY);
        return password;
    }
}
