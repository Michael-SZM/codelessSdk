package com.iyx.codeless.net.entitys

import java.io.Serializable
import java.util.*

//data class ConfigEntity(var configs: ArrayList<String>, val events: ArrayList<String>)
data class ConfigEntity(var configs: ArrayList<ConfigBean>)

data class ConfigBean(val key:String,val event:String)


data class ConfigObj(val sessionId:String="",val limit: Int=2 ):Serializable