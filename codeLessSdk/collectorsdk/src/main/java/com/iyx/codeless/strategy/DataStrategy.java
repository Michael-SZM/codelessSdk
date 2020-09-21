package com.iyx.codeless.strategy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import java.util.Map;

public abstract class DataStrategy {
    /**
     * 返回点击事件对应的视图上下文和数据上下文
     *
     * @param container
     * @return
     */
    @Nullable
    public abstract Pair<Object, Map<String,Object>> fetchTargetData(@NonNull View container, IAsynCallBack iAsynCallBack);


    public void clear(){}

    public interface IAsynCallBack{
        void onASyncBack(Object o);
    }

}