package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.util.Preconditions;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import com.iyx.codeless.strategy.DataStrategy;
import com.iyx.codeless.strategy.DataStrategyResolver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * user liushuo
 * date 2017/4/6
 */

public class WindowCallbackWrapper extends SimpleWindowCallback implements DataConfigureImp {
    public static final String TAG = "WindowCallbackWrapper";

    private WeakReference<View> mViewRef;

    private Map<Integer, Object> mDataLayout = new HashMap<>();

    Object objectTmp;
    Map<String,Object> mapTmp;
    Pair<View, Object> targets = null;

    //通过view实例的hash code决定忽略指定view的自动打点功能
    private Set<Integer> mIgnoreViews = new HashSet<>(0);

    boolean hasPreDeal = false; // 是否提前处理过了

    EventRecord lastEventRecord = new EventRecord();


    private static class EventRecord{
        long downTime =0;
        float downX =0f;
        float downY = 0f;

        public EventRecord() {
        }

        public EventRecord(long downTime, float downX, float downY) {
            this.downTime = downTime;
            this.downX = downX;
            this.downY = downY;
        }
    }



    @StringDef({
            EventType.longClick,
            EventType.doubleClick,
            EventType.sweep,
            EventType.shortClick,
            EventType.checkFail,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {
        String longClick = "LONG_CLICK";
        String doubleClick = "DOUBLE_CLICK";
        String sweep = "SWIP";
        String shortClick = "SHORT_CLICK";
        String checkFail = "";
    }

    /**
     * @param view     用于查找TouchTarget
     * @param callback 用于传递触摸事件,传null，不传递事件
     */
    public WindowCallbackWrapper(@NonNull View view, @Nullable Window.Callback callback) {
        super(callback);
        mViewRef = new WeakReference<>(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void ignoreAutoTrack(@NonNull View view) {
        Preconditions.checkNotNull(view);

        int viewHashCode = view.hashCode();
        mIgnoreViews.add(viewHashCode);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        String eventType = EventType.checkFail;
        int actionMasked = ev.getActionMasked();
        long downTiem = 0;
        if (ev.getAction() ==MotionEvent.ACTION_DOWN){
            downTiem = System.currentTimeMillis();
//            Log.e("event--","cusdown:"+downTiem);
            EventRecord tmpLastEventRecord = new EventRecord(downTiem,ev.getX(),ev.getY());
//            long deltaTime = downTiem-lastEventRecord.downTime;
//            if ( !(deltaTime> ViewConfiguration.getDoubleTapTimeout() || deltaTime < 40)) {
//                int deltaX = (int) lastEventRecord.downX - (int) ev.getX();
//                int deltaY = (int) lastEventRecord.downY - (int) ev.getY();
//                int slopSquare = 100*100;
//                if (deltaX * deltaX + deltaY * deltaY < slopSquare){
//                    eventType = EventType.doubleClick;
//                    Log.e("event--","--doubleClick");
//
//                }
//            }
            lastEventRecord = tmpLastEventRecord;
//            Log.e("xx--","--receive--down"+"--x:"+ev.getX()+"---y:"+ev.getY());
        }
        final ViewConfiguration configuration = ViewConfiguration.get(mViewRef.get().getContext().getApplicationContext());
        final int deltaX = (int) (ev.getX() - lastEventRecord.downX);
        final int deltaY = (int) (ev.getY() - lastEventRecord.downY);
        int distance = (deltaX * deltaX) + (deltaY * deltaY);
        if (distance > configuration.getScaledTouchSlop() * configuration.getScaledTouchSlop()) {
//            Log.e("event--","--sweep---scroll");
            eventType = EventType.sweep;
        }

        if (actionMasked != MotionEvent.ACTION_UP) {
            return super.dispatchTouchEvent(ev);
        }

        if (TextUtils.isEmpty(eventType) || eventType.equals(EventType.shortClick)){
            long duration = System.currentTimeMillis() - lastEventRecord.downTime;
            if (duration >= ViewConfiguration.getLongPressTimeout()){
                // 长按
                eventType = EventType.longClick;
//                Log.e("event--","cuurenNow:"+System.currentTimeMillis()+"---downTime:"+ev.getDownTime()+"---cusdown:"+lastEventRecord.downTime);
//                Log.e("event--","--longClick--offset:-"+ViewConfiguration.getLongPressTimeout()+"---duration:"+duration);
            }
        }

//        Log.e("event--","current--event-bef-:"+eventType);
        if (TextUtils.isEmpty(eventType)){
//            Log.e("event--","current--event--:"+eventType);
//            Log.e("event--","--short---click");
            eventType = EventType.shortClick;
        }


//        Log.e("res--",eventType);

//        Log.e("xx--","--receive--up"+"--x:"+ev.getX()+"---y:"+ev.getY());
        analyzeMotionEvent(eventType);

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 配制自定义布局的数据绑定关系，自定义布局内的任何
     * 控件发生点击行为时，发送的埋点都会携带改数据
     */
    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public DataConfigureImp configLayoutData(@IdRes int id, @NonNull Object object) {
        Preconditions.checkNotNull(object);

        mDataLayout.put(id, object);
        return this;
    }

    /**
     * 分析用户的点击行为
     */
    private void analyzeMotionEvent(@EventType String eventType) {
        DDLogger.d("szmres--","res is "+eventType);
        if (mViewRef == null || mViewRef.get() == null) {
            DDLogger.e(TAG, "window is null");
            return;
        }
        hasPreDeal = false;
        ViewGroup decorView = (ViewGroup) mViewRef.get();
        int content_id = android.R.id.content;
        ViewGroup content = (ViewGroup) decorView.findViewById(content_id);
        if (content == null) {
            content = decorView; //对于非Activity DecorView 的情况处理
        }
        targets = findActionTargets(content, new ExtCallBack() {
            @Override
            public void ext(Object object,Map<String,Object> map,Object asyncBack) {
                // 跟新详细信息，
                objectTmp = object;
                mapTmp = map;
                if ((objectTmp == null || objectTmp == "") && (mapTmp == null || mapTmp.isEmpty()) && (asyncBack == null || asyncBack == "")){
                    // 无效的操作，需要过滤掉
                    return;
                }
                postToNext(targets,asyncBack);
                hasPreDeal = true;
            }
        });
        if (!hasPreDeal){
            postToNext(targets,null);
        }
    }

    private void postToNext(Pair<View, Object> targets,Object asyncBack) {
        if (targets == null) {
            DDLogger.e(TAG, "has no action targets!!!");
            return;
        }

        //发送任务在单线程池中
        int hashcode = targets.first.hashCode();
        if (mIgnoreViews.contains(hashcode)) return;

        TrackerExecutor.getHandler().post(TrackPostAction.create(targets.first, targets.second,objectTmp,mapTmp,asyncBack));
    }

    /**
     * @param root 必然非null
     * @return null，找不到touchtarget相关数据
     */
    @Nullable
    private Pair<View, Object> findActionTargets(@NonNull ViewGroup root,ExtCallBack extCallBack) {

        View touchTarget;

        View strategyView = null;

        View configDataView = null;
        int configId = -1;


        ViewGroup vg = root;
        while (true) {
            touchTarget = ViewHelper.findTouchTarget(vg);

            //无法找到touchTarget 相关信息
            if (touchTarget == null) return null;

            int vId = touchTarget.getId();
            if (mDataLayout.containsKey(vId)) {

                configDataView = touchTarget;
                configId = vId;

                //互斥操作
                if (strategyView != null) {
                    strategyView = null;
                }

            }

            // 手机ext信息，eg:position，支持Object和map2中形式
            DataStrategy tmpStrategy = DataStrategyResolver.resolveDataStrategy(touchTarget);
            if (tmpStrategy != null) {

                Pair<Object,Map<String,Object>> valuePair = tmpStrategy.fetchTargetData(touchTarget, new DataStrategy.IAsynCallBack() {
                    @Override
                    public void onASyncBack(Object o) {
                        if (o == null){
                            return;
                        }
                        extCallBack.ext(null,null,o);
                    }
                });
                extCallBack.ext(valuePair.first,valuePair.second,null);

                return Pair.create(touchTarget, mDataLayout.get(configId));
            }

            //已经找到touchTarget
            if (touchTarget == vg) break;

            boolean isVG = touchTarget instanceof ViewGroup;
            //已经找到touchTarget
            if (!isVG) break;

            //未找到touchTarget
            vg = (ViewGroup) touchTarget;
        }


        if (configDataView != null) {
            return Pair.create(touchTarget, mDataLayout.get(configId));
        }

        return Pair.create(touchTarget, null);
    }

    private interface ExtCallBack{
        void ext(Object object, Map<String, Object> map, Object asyncBack);
    }

}