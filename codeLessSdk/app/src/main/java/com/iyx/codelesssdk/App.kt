package com.iyx.codelesssdk

import android.app.Application
import com.iyx.codeless.CodeLessSdk

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        CodeLessSdk.codeLessSdk.init(this,null)
        CodeLessSdk.codeLessSdk.needPrintLog(true)
        registerActivityLifecycleCallbacks(CodeLessSdk.callback)
    }

}