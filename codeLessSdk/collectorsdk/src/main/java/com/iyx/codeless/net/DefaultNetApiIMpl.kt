package com.iyx.codeless.net

import com.iyx.codeless.net.entitys.ConfigObj
import com.iyx.codeless.net.entitys.Result
import com.iyx.codeless.net.ok_fit.CodeLessService
import com.iyx.codeless.net.ok_fit.NetClient
import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadDataBean
import io.reactivex.Flowable

class DefaultNetApiIMpl :CodeLessNetApi{

    private val codeLessService:CodeLessService = createService(CodeLessService::class.java)

    /**
     * 创建Service的方法
     * @param sClass 声明网络请求方法的Service
     */
    private fun <T> createService(sClass: Class<T>): T {
        return NetClient().retrofit.create(sClass)
    }

    override fun uploadUserData(logToken: String, body: RequestBodyBaseInfoBean): Flowable<Result<Any>> {
        return codeLessService.uploadUserData(logToken,body)
    }

    override fun uploadActionData(logToken: String, uploadDataBean: UploadDataBean): Flowable<Result<ConfigObj>> {
        return codeLessService.uploadActionData(logToken,uploadDataBean)
    }

    override fun fetchSessionId(logToken: String, key: String, uuid: String): Flowable<Result<ConfigObj>> {
        return codeLessService.fetchSessionId(logToken,key,uuid)
    }


}