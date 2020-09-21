package com.iyx.codeless.net.ok_fit

import com.iyx.codeless.net.entitys.ConfigObj
import com.iyx.codeless.net.entitys.Result
import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadDataBean
import io.reactivex.Flowable
import retrofit2.http.*

/**
 * 参考 ref http://wiki.iyunxiao.com/pages/viewpage.action?pageId=389082026
 */
interface CodeLessService {

    @POST(" /api/collect/push/user")
    fun  uploadUserData(@Header("log-token") logToken: String, @Body body: RequestBodyBaseInfoBean): Flowable<Result<Any>>

    @POST(" /api/collect/push/log")
    fun  uploadActionData(@Header("log-token") logToken: String, @Body body: UploadDataBean): Flowable<Result<ConfigObj>>

    // 获取sessionId和配置
    @GET("/api/collect/session")
    fun fetchSessionId(@Header("log-token") logToken: String, @Query("key") key:String, @Query("uuid") uuid:String):Flowable<Result<ConfigObj>>

}