package com.iyx.codeless;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.iyx.codeless.filedao.FileFacade;
import com.iyx.codeless.filedao.SpExt;
import com.iyx.codeless.net.config.ConfigFetcher;
import com.iyx.codeless.net.config.DownLoadMsg;
import com.iyx.codeless.strategy.DataStrategy;
import com.iyx.codeless.utils.RxBus;
import com.iyx.filewr.RequestBodyBaseInfoBean;
import com.iyx.filewr.UploadUnit;

import java.util.HashMap;
import java.util.Map;


public class CodeLessSdk {

    public static CodeLessSdk codeLessSdk = new CodeLessSdk();
    public static Context app;
    private Map<String,Object> commonParams = new HashMap<>();
    private static FileFacade fileFacade;

    public void init(Context context,Map<String,Object> commonParams){
        this.app = context.getApplicationContext();
        if(commonParams != null){
            this.commonParams.putAll(commonParams);
        }

        // 初始化文件模块
        fileFacade = FileFacade.Companion.Instance(new SpExt(app),new FileFacade.Config(context.getExternalCacheDir().getAbsolutePath(),new RequestBodyBaseInfoBean()
                ,""));

        RxBus.INSTANCE.toObservable(DownLoadMsg.class).subscribe(downLoadMsg -> {
            // 配置文件不存在，下载,,,,download_url,md5通过接口返回
            // todo 补全参数
//            ConfigFetcher.INSTANCE.downloadConfigFile(app,"","");
        });
    }

    /**
     * 是否开启log打印
     * @param b
     */
    public void needPrintLog(boolean b){
        DDLogger.enableLogger(b);
    }

    private CodeLessSdk(){
//        new NetFacade(new DefaultNetApiImpl()).fetchConfig("", () -> null, throwable -> null);
    }

    private  Map<Class<? extends  ViewGroup>,DataStrategy> cusStrategys = new HashMap<>();


    public void registerStrategy(Class<? extends ViewGroup> clazz, DataStrategy dataStrategy){
        cusStrategys.put(clazz,dataStrategy);
    }

    public Map<Class<? extends  ViewGroup>, DataStrategy> getCusStrategys() {
        return cusStrategys;
    }

    public static Application.ActivityLifecycleCallbacks callback = new InnerActivityLifecycleCallBacks();

    public void appendOpRecord(UploadUnit unitUpload) {
        fileFacade.write(unitUpload);
    }

    private static class InnerActivityLifecycleCallBacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            fileFacade.fresh();
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}
