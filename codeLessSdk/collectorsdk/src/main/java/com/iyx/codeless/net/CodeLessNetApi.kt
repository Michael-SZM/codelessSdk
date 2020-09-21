package com.iyx.codeless.net

import com.iyx.codeless.net.entitys.ConfigObj
import com.iyx.codeless.net.entitys.Result
import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadDataBean
import io.reactivex.Flowable

interface CodeLessNetApi {

    val timeOut: Long
        get() = 5000L

    fun  uploadUserData(logToken: String,body: RequestBodyBaseInfoBean):Flowable<Result<Any>>

    fun  uploadActionData(logToken: String,uploadDataBean: UploadDataBean):Flowable<Result<ConfigObj>>

    fun  fetchSessionId(logToken: String,key:String,uuid:String):Flowable<Result<ConfigObj>>

}