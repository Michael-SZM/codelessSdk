package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.text.TextUtils;

import java.lang.reflect.Field;

/**
 * Copyright (c) 2014 Nono_Lilith All right reserved.
 */
public class CoreUtils {

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */
    @SuppressLint("RestrictedApi")
    @Nullable
    public static Field getDeclaredField(@NonNull Object object, @NonNull String fieldName) {
        Preconditions.checkNotNull(object);
        Preconditions.checkArgument(!TextUtils.isEmpty(fieldName));

        if (TextUtils.isEmpty(fieldName)) return null;

        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            } catch (NullPointerException e) {
                e.printStackTrace();

                return null;
            } catch (SecurityException e) {
                e.printStackTrace();

                return null;
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    public static Object getFieldValue(@NonNull Object object, @NonNull String fieldName) {
        Preconditions.checkNotNull(object);
        Preconditions.checkArgument(!TextUtils.isEmpty(fieldName));

        Field field = getDeclaredField(object, fieldName);
        if (field == null) return null;

        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
