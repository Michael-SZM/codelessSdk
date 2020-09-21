package com.iyx.codeless.strategy;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;

import com.iyx.codeless.CodeLessSdk;

public class DataStrategyResolver {
    private static DataStrategy sRecyclerViewDataStrategy = new RecyclerViewStrategy();
    private static DataStrategy sExpandableListViewDataStrategy = new ExpandableListViewStrategy();
    private static DataStrategy sListViewDataStrategy = new AdapterViewStrategy();
    private static DataStrategy sGridViewDataStrategy = new AdapterViewStrategy();
    private static DataStrategy sViewPagerDataStrategy = new ViewPagerStrategy();
    private static DataStrategy sEditTextDataStrategy = new EditTextStrategy();
//    private static DataStrategy sTabLayoutDataStrategy = new TabLayoutStrategy();
    
    
    @SuppressLint("RestrictedApi")
    public static DataStrategy resolveDataStrategy(@NonNull View targetView) {
        Preconditions.checkNotNull(targetView);
        
        if (targetView instanceof RecyclerView) {
            return sRecyclerViewDataStrategy;
        } else if (targetView instanceof ExpandableListView) {
            return sExpandableListViewDataStrategy;
        } else if (targetView instanceof ListView) {// 必须放在ExpandableListView判断之后，因为ExpandableListView继承ListView
            return sListViewDataStrategy;
        } else if (targetView instanceof GridView) {
            return sGridViewDataStrategy;
        } else if (targetView instanceof ViewPager) {
            return sViewPagerDataStrategy;
        }else if (targetView instanceof EditText){
            return sEditTextDataStrategy;
        }/*else if (targetView instanceof TabLayout) {
            return sTabLayoutDataStrategy;
        }*/

        // 注册自定义策略
        if (CodeLessSdk.codeLessSdk.getCusStrategys().size()>=1){
            DataStrategy dataStrategy = CodeLessSdk.codeLessSdk.getCusStrategys().get(targetView.getClass());
            return dataStrategy;
        }
        
        return null;
    }
}