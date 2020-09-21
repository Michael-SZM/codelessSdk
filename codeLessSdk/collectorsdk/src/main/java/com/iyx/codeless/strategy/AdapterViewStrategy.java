package com.iyx.codeless.strategy;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.iyx.codeless.ViewHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * user liuhuo
 * date 2017/4/7
 * <p>
 * Known Indirect Subclasses:
 * <p>
 * AdapterViewFlipper,
 * AppCompatSpinner,
 * ExpandableListView,
 * Gallery, GridView,
 * ListView,
 * Spinner,
 * StackView
 */

public class AdapterViewStrategy extends DataStrategy {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public Pair<Object, Map<String,Object>> fetchTargetData(@NonNull View container, IAsynCallBack iAsynCallBack) {
        Preconditions.checkNotNull(container);

        AdapterView adapterView = (AdapterView) container;

        View child = ViewHelper.findTouchTarget(adapterView);
        if (child == null) return null;
        if (child == adapterView) return null;

        //parse data
        int firstPos = adapterView.getFirstVisiblePosition();
        int index = adapterView.indexOfChild(child);
        if (index == -1) {
            return null;
        }

        int adapterPos = firstPos + index;

        Adapter adapter = adapterView.getAdapter();
        Object data = adapter.getItem(adapterPos);

        Map<String,Object> kvs = new HashMap<>();

        return Pair.create(data,kvs);
    }
}
