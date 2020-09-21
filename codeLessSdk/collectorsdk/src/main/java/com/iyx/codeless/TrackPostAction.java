package com.iyx.codeless;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iyx.codeless.net.EventType;
import com.iyx.codeless.net.config.ConfigFetcher;
import com.iyx.codeless.net.entitys.ConfigBean;
import com.iyx.codeless.strategy.PathRecorder;
import com.iyx.filewr.Info;
import com.iyx.filewr.Page;
import com.iyx.filewr.UploadUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iyx.codeless.CodeLessSdk.codeLessSdk;

/**
 */

public class TrackPostAction implements Runnable {
    private static final String TAG = WindowCallbackWrapper.TAG;

    private View mActionTarget;
    private Object mContextData;// 通过configLayoutData方法手动绑定的数据，
    private Object dataParse;// 使用策略解析出来的数据，，，针对复杂的数据统计
    private Map<String,Object> kvs;//针对简单的数据统计，
    private Object asyncBack;// 异步回调信息


    @SuppressLint("RestrictedApi")
    public static TrackPostAction create(@NonNull View actionTarget, @Nullable Object contextData, @Nullable Object dataParse, @Nullable Map<String,Object> map, @Nullable Object asyncBack) {
        Preconditions.checkNotNull(actionTarget);

        return new TrackPostAction(actionTarget, contextData,dataParse,map,asyncBack);
    }

    private TrackPostAction(@NonNull View actionTarget, @Nullable Object contextData,@Nullable Object dataParse,@Nullable Map<String,Object> map,@NonNull Object asyncBack) {
        mActionTarget = actionTarget;
        mContextData = contextData;
        this.dataParse = dataParse;
        kvs = map;
        this.asyncBack = asyncBack;
    }

    @Override
    public void run() {
        String idName = ResourceHelper.getGlobalIdName(mActionTarget);//success get the idName
        DDLogger.d(TAG, String.format("global id name=%s", idName));




        DDLogger.d("xxx--","asyncBack:"+asyncBack);
        // 遍历map,把详细信息拼接上去
//        if (kvs != null && !kvs.isEmpty()){
//            StringBuilder sb = new StringBuilder();
//            Set<Map.Entry<String, Object>> entries = kvs.entrySet();
//            for (Map.Entry<String, Object> entry:entries){
//                sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
//            }
//            Log.e("xxx--","kvs:"+sb.toString());
//        }

        DDLogger.d("kk--","----bef--"+idName);
        if (TextUtils.isEmpty(idName)){
            StringBuilder sb = new StringBuilder();
            String activityName = mActionTarget.getContext().getClass().getSimpleName();
            String layoutId = ResourceHelper.getLayoutFileName(mActionTarget);
            sb.append(activityName).append("_").append(layoutId).append("_");
        }
        if (PathRecorder.getInstance().getPathRecord().size() > 0){// 有嵌套容器，截掉最后一段（_xxxxx）
            if (idName != null){
                int lastIndex = idName.lastIndexOf("_");
                idName = idName.substring(0,lastIndex);
            }
        }

        String tvMsg = "";
        if (mActionTarget instanceof TextView){
            tvMsg = ((TextView)mActionTarget).getText().toString();
        }
        idName = idName + "_"+tvMsg;
        DDLogger.d("kk--","----aft--"+idName);
        String finalP = idName + "_"+PathRecorder.getInstance().getPath();
        DDLogger.e("kk--",finalP);
//        Log.e("zz--",PathRecorder.getInstance().getPath());

        PathRecorder.getInstance().clear();

        DDLogger.d("xxx--",String.format("global id name=%s", idName));


        // 解析事件，并拼接位置
        String text = calculateEvent(finalP);
        // make a Page
        // make a Info
        // make a UploadUnit
        Page page = new Page("",text);
        Info info = new Info(finalP, EventType.CLICK,page,0,new HashMap<String, String>());
        UploadUnit unitUpload = new UploadUnit(null,info);
        // todo write to file
        codeLessSdk.appendOpRecord(unitUpload);
    }

    private String calculateEvent(String uniqueKey){
        ArrayList<ConfigBean> configs = ConfigFetcher.INSTANCE.getConfigEntityTmp().getConfigs();
        for (ConfigBean configBean:configs){
            if (match(uniqueKey,configBean.getKey())){
                List<Integer> posis = pickPositions(uniqueKey);
                if (posis.size()>=1){
                    return configBean.getEvent()+posis.toString();
                }
                return configBean.getEvent();
            }
        }
        return uniqueKey;
    }

    private boolean match(String targetStr,String regx) {
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(targetStr);
        return matcher.find();
    }

    private List<Integer> pickPositions(String targetStr){
        List<Integer> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("_\\d+_");
        Matcher matcher = pattern.matcher(targetStr);
        while (matcher.find()){
            list.add(Integer.parseInt(matcher.group(0).replace("_","")));
        }
        return list;
    }

}

