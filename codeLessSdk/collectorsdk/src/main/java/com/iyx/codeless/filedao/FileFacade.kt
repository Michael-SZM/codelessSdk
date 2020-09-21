package com.iyx.codeless.filedao

import com.google.gson.Gson
import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadUnit

class FileFacade(val sp:SpExt,val config:Config) {

    var fileWriter:FileWriter?=null

    companion object {
        private var facade: FileFacade? = null
        fun Instance(sp:SpExt,config:Config): FileFacade {
            if (facade == null){
                synchronized(FileFacade::class){
                    facade = FileFacade(sp,config)
                }
            }
            return facade!!
        }
    }

    init {
        fileWriter = FileWriter(sp,config.dirPath,config.logToken)
    }


    /**
     * 写单个操作记录
     */
    fun write(uploadUnit: UploadUnit){
        fileWriter?.apply {
            write(Gson().toJson(uploadUnit))
        }
    }

    /**
     * 刷新一下，检察是否需要上传
     */
    fun fresh(){
        fileWriter?.apply {
            write("")
        }
    }

    /**
     * 强制上传
     */
    fun forceUpload(){
        fileWriter?.forceUpload()
    }

    class Config(var dirPath:String,var body:RequestBodyBaseInfoBean,val logToken:String){}


}