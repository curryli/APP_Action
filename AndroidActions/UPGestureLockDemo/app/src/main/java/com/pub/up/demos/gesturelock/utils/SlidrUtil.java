package com.pub.up.demos.gesturelock.utils;

import android.content.Context;
import android.graphics.Color;

import com.pub.up.demos.gesturelock.R;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.r0adkll.slidr.model.SlidrPosition;


public class SlidrUtil {

    /**
     * 获取手势返回上一页库配置
     */
    public static SlidrConfig getConfig(Context context, SlidrListener slidrListener) {
        SlidrConfig config = new SlidrConfig.Builder()
                .primaryColor(context.getResources().getColor(R.color.colorPrimary))
                .secondaryColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .position(SlidrPosition.LEFT)
                .sensitivity(1f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.25f)
                .listener(slidrListener)
                .edge(true)
                .edgeSize(0.18f)
                .build();
        return config;
    }
}
