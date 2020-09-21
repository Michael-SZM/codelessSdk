package com.iyx.codeless.net.entitys

import java.io.Serializable

data class Result<T> (val code:Int=0,val msg:String="",val data:T?):Serializable