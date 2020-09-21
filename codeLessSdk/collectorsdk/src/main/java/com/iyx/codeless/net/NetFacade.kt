package com.iyx.codeless.net

import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadDataBean
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import java.util.*

class NetFacade private constructor(private val netApi: CodeLessNetApi = DefaultNetApiIMpl()) {

    val tokenLog = "{\"id\":\"2\",\"key\":\"HwKsECx7c39eV93R865c8vXpkjHgfIPO\"}"

    companion object{
        var rBody :RequestBodyBaseInfoBean?=null
        var mLimit:Int = 2
        var mSessionId:String=""
        private var facade:NetFacade = NetFacade()
        fun Instance():NetFacade{
            return facade
        }
    }

    /**
     * 上报用户数据
     */
    fun uploadUserData(logToken: String = tokenLog, body: RequestBodyBaseInfoBean) {
        rBody = body
        actionFlowable(netApi.uploadUserData(logToken,body),onNext = {
            it.code
        },onError = {

        })

    }

    /**
     * 上报操作数据
     */
    fun uploadActionData(logToken: String,dataBean: UploadDataBean){
        actionFlowable(netApi.uploadActionData(logToken,dataBean),onNext = {
            it.data?.apply {
                mLimit = limit
                mSessionId = sessionId
            }
        },onError = {

        })
    }

    /**
     * 获取sessionId和配置
     */
    fun fetchSessionId(encrypt:(String,String)->String={_,_->tokenLog},logToken: String? = tokenLog, key: String="HwKsECx7c39eV93R865c8vXpkjHgfIPO", uuid:String, onComplete: () -> Unit = {}, onError: (Throwable?) -> Unit = {}) {
        logToken?.apply {
            val mm = encrypt(tokenLog,getencryptKey())
            actionFlowable(netApi.fetchSessionId(mm,key,uuid),onComplete = onComplete,onSubscribe = {

            },onNext = {
                it.data?.apply {
                    mLimit = limit
                    mSessionId = sessionId
                }
            },onError = onError)
        }

    }

    private fun <T> actionFlowable(flowable: Flowable<T>, onComplete: () -> Unit = {}, onSubscribe: (Subscription) -> Unit = {}, onNext: (T) -> Unit = {}, onError: (Throwable?) -> Unit = {}) {
        flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onNext(it)
                },{
                    onError(it)
                },{
                    onComplete
                })
    }


    private fun getencryptKey():String{
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis.toString()
    }

}