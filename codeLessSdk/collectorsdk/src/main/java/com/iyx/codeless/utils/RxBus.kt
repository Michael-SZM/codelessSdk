package com.iyx.codeless.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

object RxBus {


    private var mBus: Subject<Any>? = null

    init {
        mBus = PublishSubject.create<Any>().toSerialized();
    }

    /**
     * 发送事件
     */
    fun post(event: Any?) {
        mBus!!.onNext(event!!)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservable(eventType: Class<T>?): Observable<T>? {
        return mBus!!.ofType(eventType)
    }


    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return mBus!!.hasObservers()
    }

}