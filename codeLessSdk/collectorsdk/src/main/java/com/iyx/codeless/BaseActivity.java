package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;


public abstract class BaseActivity extends AppCompatActivity implements DataConfigureImp {

    /*用于配置自定义布局绑定的数据(自动打点使用)*/
    protected DataConfigureImp mDataConfigure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //配置自动打点
        wrapOriginalCallbackForAutoTrack();
    }

    private void test001(){
        Window window = getWindow();
        window.setBackgroundDrawable(null);
        window.setCallback(null);
        CodeLessFacade.handleDialog(window);
    }

    protected void wrapOriginalCallbackForAutoTrack() {
        Window.Callback callback = getWindow().getCallback();
        View decorView = getWindow().getDecorView();

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, callback);
        getWindow().setCallback(wrapper);
        mDataConfigure = wrapper;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        wrapOriginalCallbackForAutoTrack();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        if (name.equals("ListView")) {
//            return new DDListView(context, attrs);
//        } else if (name.equals("GridView")) {
//            return new DDGridView(context, attrs);
//        } else if (name.equals("ExpandableListView")) {
//            return new DDExpandableListView(context, attrs);
//        }

        return super.onCreateView(parent, name, context, attrs);
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public DataConfigureImp configLayoutData(@IdRes int id, @NonNull Object object) {
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

    /**
     * 自动为布局根节点添加key为id_namespace_tag的tag值，该值为
     * 文件名称
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflaterWrapper.inflate(this, layoutResID, null);
        setContentView(view);
    }

    /**
     * LayoutInflaterWrapper 自动为布局根节点添加key为id_namespace_tag的tag值，该值为
     * 文件名称
     *
     * @return
     */
    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return LayoutInflaterWrapper.wrapInflater(super.getLayoutInflater());
    }

}
