package com.iyx.codeless.strategy;//package com.example.myapplication.dedao.strategy;
//
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.util.Preconditions;
//
//import com.example.myapplication.analy.ViewHelper;
//import com.google.android.material.tabs.TabLayout;
//
//
///**
// * Created by liushuo on 2017/5/31.
// */
//
//public class TabLayoutStrategy extends DataStrategy {
//    @Nullable
//    @Override
//    public Object fetchTargetData(@NonNull View container) {
//        Preconditions.checkNotNull(container);
//
//        TabLayout tabLayout = (TabLayout) container;
//
//        View child = ViewHelper.findTouchTarget(tabLayout);
//        if (child == null) return null;
//        if (!(child instanceof ViewGroup)) return null;
//        if (child == tabLayout) return null;
//
//        ViewGroup tabStrip = (ViewGroup) child;
//        child = ViewHelper.findTouchTarget(tabStrip);
//        if (child == null) return null;
//        if (child == tabStrip) return null;
//
//        int index = tabStrip.indexOfChild(child);
//        if (index < 0) return null;
//
//        TabLayout.Tab tab = tabLayout.getTabAt(index);
//        if (tab == null) return null;
//
//        String name = (String) tab.getText();
//        View customView = tab.getCustomView();
//        if (name == null && customView != null) {
//            String log_name = (String) customView.getContentDescription();
//            return TrackData.create(null, null, Strings.nullToEmpty(log_name));
//        }
//
//        String log_name = Strings.nullToEmpty(name);
//        return TrackData.create(null, null, log_name);
//    }
//
//
//}
