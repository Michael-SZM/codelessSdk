package com.iyx.codeless.strategy;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;


import com.iyx.codeless.ResourceHelper;
import com.iyx.codeless.ViewHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * user liuhuo
 * date 2017/4/7
 */

public class ViewPagerStrategy extends DataStrategy {
    private static final String TAG = "AutoTracker";


    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public Pair<Object, Map<String,Object>> fetchTargetData(@NonNull View container, IAsynCallBack iAsynCallBack) {
        Preconditions.checkNotNull(container);
        Map<String,Object> kvs = new HashMap<>();

        ViewPager adapterView = (ViewPager) container;
        String viewPagerName= ResourceHelper.getResourceEntryName(container.getContext(),adapterView.getId());


        int position = adapterView.getCurrentItem();
        kvs.put(PathRecorder.KEY_POSITION,position);
        kvs.put(PathRecorder.KEY_TYPE,"ViewPager");
        kvs.put(PathRecorder.KEY_CONTAINER_NAME,viewPagerName);
        PathRecorder.getInstance().collect(kvs);

        View firstChild = ViewHelper.findTouchTarget(adapterView);
        if (firstChild == null || firstChild == adapterView) return Pair.create(null,kvs);

        View touchTarget = null;
        if (firstChild instanceof ViewGroup){
            ViewGroup tmpVG = (ViewGroup) firstChild;
            while (true){
                touchTarget = ViewHelper.findTouchTarget(tmpVG);
                //无法找到touchTarget 相关信息
                if (touchTarget == null) return Pair.create("",kvs);

                //已经找到touchTarget
                if (touchTarget == tmpVG) break;

                boolean isVG = touchTarget instanceof ViewGroup;
                //已经找到touchTarget
                if (!isVG) break;

                DataStrategy tmpStrategy = DataStrategyResolver.resolveDataStrategy(touchTarget);
                if (tmpStrategy != null) {
                    return tmpStrategy.fetchTargetData(touchTarget,null);
                }

                //未找到touchTarget
                tmpVG = (ViewGroup) touchTarget;
            }
        }

        if (touchTarget != null){
            String resName= ResourceHelper.getResourceEntryName(container.getContext(),touchTarget.getId());
            Log.e("sss---","----ViewPager--item---:"+resName);
            kvs.put(PathRecorder.KEY_NAME,resName);
        }

        return Pair.create(touchTarget,kvs);
    }
}
