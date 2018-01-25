package com.lxr.acc_save.Utils;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by XH on 2017/11/30.
 */

public class OkHttpUtils {

    ///http://172.18.186.66:8080/push?t=xxxxxxxxxxxxxxxxxx
    private static final String HOST_URL = "http://172.18.186.66:8080";
    private static final String TAG = "OkHttpUtils";

    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    //超时时间
    public static final int TIMEOUT = 1000 * 60;
    //json请求
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpUtils() {
        init();
    }

    public static synchronized OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                mInstance = new OkHttpUtils();
            }
        }
        return mInstance;
    }

    private void init() {
        mOkHttpClient = new OkHttpClient();

        //设置超时
        mOkHttpClient.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).
                writeTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public void doRecognizeBehavior(String data, Callback callback) throws IOException {
        String url = HOST_URL + "/push?t=" + data;
        Log.e(TAG, "##### request url :" + url);
        Request request = new Request.Builder().url(url).get().build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

}
