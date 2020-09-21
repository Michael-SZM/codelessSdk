package com.iyx.codeless.net.ok_fit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class NetClient {

    val okClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .build()

    val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okClient)
            .baseUrl("https://testlogapi.yqj.cn").build()




}