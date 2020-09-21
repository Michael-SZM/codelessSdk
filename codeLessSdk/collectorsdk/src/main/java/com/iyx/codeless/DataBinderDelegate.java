package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.view.View;

public class DataBinderDelegate implements DataConfigureImp{

    /*用于配置自定义布局绑定的数据(自动打点使用)*/
    protected DataConfigureImp mDataConfigure;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public DataConfigureImp configLayoutData(int id, @NonNull Object object) {
        Preconditions.checkNotNull(object);

        mDataConfigure.configLayoutData(id, object);
        return mDataConfigure;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void ignoreAutoTrack(@NonNull View view) {
        Preconditions.checkNotNull(view);

        mDataConfigure.ignoreAutoTrack(view);
    }
}
