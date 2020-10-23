package com.iyx.codeless.strategy;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class EditTextStrategy extends DataStrategy{

    private IAsynCallBack mIAsynCallBack;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mIAsynCallBack.onASyncBack(s.toString());
        }
    };

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public Pair<Object, Map<String, Object>> fetchTargetData(@NonNull View container, IAsynCallBack iAsynCallBack) {
        Preconditions.checkNotNull(container);
        mIAsynCallBack = iAsynCallBack;
        EditText editText = (EditText) container;

        editText.removeTextChangedListener(watcher);
        editText.addTextChangedListener(watcher);

        Map<String,Object> kvs = new HashMap<>();
        kvs.clear();
        return Pair.create("",kvs);
    }


    @Override
    public void clear() {
        super.clear();
    }
}
