package com.iyx.filewr

import java.io.Serializable

class UploadUnit(val element:Any?=null,val operand:Info):Serializable

data class Info(val event_id:String="",
                val event_type:String="",
                val page:Page=Page(),
                val stay_time:Long = 0,
                val event_args:Map<String,String>
):Serializable


data class Page(val page_title:String="",
                val page:String=""
):Serializable


// 上报用户数据使用
data class RequestBodyBaseInfoBean(var key:String?="",
                                   var uuid:String?="",
                                   var user_id:String="",
                                   var sessionId:String="",
                                   var app_info:AppInfo= AppInfo(),
                                   var user_info:UserInfo= UserInfo(),
                                   var location:LocationInfo= LocationInfo(),
                                   var device:DeviceInfo=DeviceInfo()
):Serializable


data class AppInfo(var app_id:String?=null,
                    var app_version:String?=null,
                    var app_package_id:String?=null,
                    var sdk_version:String?=null
):Serializable

data class UserInfo(var open_id:String="",
                    var union_id:String="",
                    var ip:String="",
                    var student_id:String="",
                    var school_id:String="",
                    var grade:String="",
                    var nick_name:String="",
                    var user_name:String="",
                    var age:Int=0,
                    var gender:Int=-1,
                    var avatar_url:String=""

):Serializable

data class LocationInfo(val lg:Double=0.0,
                        val lat:Double=0.0,
                        val city:String="",
                        val province:String="",
                        val district:String="",
                        val country:String=""
):Serializable

data class DeviceInfo(val base_info:DeviceBaseInfo=DeviceBaseInfo(),
                      val os:OSInfo= OSInfo(),
                      val network:NetWorkInfo=NetWorkInfo(),
                      val browser:BrowserInfo?=null
):Serializable

data class DeviceBaseInfo(val device_id:String="",
                          val device_type:String="",
                          val device_brand:String="",
                          val device_model:String="",
                          val screen_width:Int=720,
                          val screen_height:Int=360
):Serializable

data class OSInfo(val os_name:String="",val os_version:String=""):Serializable

data class NetWorkInfo(val network_type:String="",
                       val network_operator:String="",
                       val network_ip:String=""
):Serializable

data class BrowserInfo(val browser:String="",
                       val browser_version:String="",
                       val browser_useragent:String="",
                       val weixin_version:String=""
):Serializable


// 上报数据使用
class UploadDataBean(
    key: String? = "",
    uuid: String? = "",
    user_id: String? = "",
    sessionId: String? = null,
    val collect_info:List<UploadUnit>,
    val app_info:AppInfo= AppInfo(),
    val user_info:UserInfo= UserInfo(),
    val location:LocationInfo= LocationInfo(),
    val device:DeviceInfo=DeviceInfo()
) :Serializable