package com.iyx.codeless;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;

public class CodeLessFacade {


    private DataBinderDelegate dataBinderDelegate = new DataBinderDelegate();

    public DataBinderDelegate getDataBinderDelegate() {
        return dataBinderDelegate;
    }

    public View wrapView(Context context, @LayoutRes int resource, ViewGroup root){
        return LayoutInflaterWrapper.inflate(context, resource, root);
    }

    public LayoutInflater wrapInflater(LayoutInflater layoutInflater){
        return LayoutInflaterWrapper.wrapInflater(layoutInflater);
    }


    public void handleWindow(Window window){
        Window.Callback callback = window.getCallback();
        View decorView = window.getDecorView();

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, callback);
        window.setCallback(wrapper);
        dataBinderDelegate.mDataConfigure = wrapper;
    }

    public static void handleDialog(Window window){
        Window.Callback callback = window.getCallback();
        View decorView = window.getDecorView();

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, callback);
        window.setCallback(wrapper);
    }


    public static LayoutInflater wrapInflaterStatic(LayoutInflater layoutInflater){
        return LayoutInflaterWrapper.wrapInflater(layoutInflater);
    }

    public static View wrapViewStatic(LayoutInflater layoutInflater, @LayoutRes int resource, ViewGroup root,boolean f){
        return wrapInflaterStatic(layoutInflater).inflate(resource, root,f);
    }

    public static void wrapPopWindow(PopupWindow popup){
        DDDecorView decorView = new DDDecorView(popup.getContentView().getContext());
        decorView.addView(popup.getContentView());

        popup.setContentView(decorView);

        WindowCallbackWrapper wrapper = new WindowCallbackWrapper(decorView, null);
        decorView.setCallbackWrapper(wrapper);
    }

}
