package com.lxr.acc_save.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {


    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "msg";

    public static String getResultCode(String jsonResp) {
        String result = "";
        try {
            JSONObject json = new JSONObject(jsonResp);
            result = json.getString(KEY_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getResultMsg(String jsonResp) {
        String result = "";
        try {
            JSONObject json = new JSONObject(jsonResp);
            result = json.getString(KEY_MESSAGE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
