package com.iyx.codeless.strategy;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.iyx.codeless.ResourceHelper;
import com.iyx.codeless.ViewHelper;

import java.util.HashMap;
import java.util.Map;

public class RecyclerViewStrategy extends DataStrategy {
    private static final String TAG = "AutoTracker";


    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public Pair<Object, Map<String,Object>> fetchTargetData(@NonNull View container, IAsynCallBack iAsynCallBack) {
        Preconditions.checkNotNull(container);

        Map<String,Object> kvs = new HashMap<>();
        RecyclerView recyclerView = (RecyclerView) container;
        String recyclerViewName= ResourceHelper.getResourceEntryName(container.getContext(),recyclerView.getId());



        View firstChild = ViewHelper.findTouchTarget(recyclerView);
        if (firstChild == null || firstChild == recyclerView) return Pair.create("",kvs);



        //parse data
        int adapterPos = recyclerView.getChildAdapterPosition(firstChild);
        kvs.put(PathRecorder.KEY_POSITION,adapterPos);
        kvs.put(PathRecorder.KEY_TYPE,"recyclerView");
        kvs.put(PathRecorder.KEY_CONTAINER_NAME,recyclerViewName);
        PathRecorder.getInstance().collect(kvs);

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
            Log.e("sss---","----recyclerView--item---:"+resName);
            kvs.put(PathRecorder.KEY_NAME,resName);
        }

        return Pair.create(touchTarget,kvs);
    }
}
