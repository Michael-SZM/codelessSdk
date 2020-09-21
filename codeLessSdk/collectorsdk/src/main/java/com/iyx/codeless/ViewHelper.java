package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.util.Preconditions;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

public class ViewHelper {

    @SuppressLint("RestrictedApi")
    public static View findTouchTarget(@NonNull ViewGroup ancestor) {
        Preconditions.checkNotNull(ancestor);

        try {
            Field firstTouchTargetField = CoreUtils.getDeclaredField(ancestor, "mFirstTouchTarget");
            if (firstTouchTargetField == null) {
                Log.e("exception--","logReflectException----mFirstTouchTarget");
                return ancestor;
            }

            firstTouchTargetField.setAccessible(true);
            Object firstTouchTarget = firstTouchTargetField.get(ancestor);
            if (firstTouchTarget == null) return ancestor;

            Field firstTouchViewField = firstTouchTarget.getClass().getDeclaredField("child");
            if (firstTouchViewField == null) {
                Log.e("exception--","logReflectException----child");
                return ancestor;
            }

            firstTouchViewField.setAccessible(true);
            View firstTouchView = (View) firstTouchViewField.get(firstTouchTarget);
            if (firstTouchView == null) return ancestor;

            return firstTouchView;

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
