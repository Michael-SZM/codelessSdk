package com.iyx.codeless.net.config

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.util.Preconditions
import android.text.TextUtils
import com.google.gson.Gson
import com.iyx.codeless.CodeLessSdk
import com.iyx.codeless.DDLogger
import com.iyx.codeless.net.NetFacade
import com.iyx.codeless.net.entitys.ConfigBean
import com.iyx.codeless.net.entitys.ConfigEntity
import com.iyx.codeless.utils.FileMd5Util
import com.iyx.codeless.utils.Utils
import java.io.File
import java.io.InputStream
import java.util.*

object ConfigFetcher {

    private const val FILE_NAME = "codeless.cfg"
    private const val TAG = "ConfigFetcher"

    val configEntityTmp by lazy { fetchConfig(CodeLessSdk.app) }

    fun fetchConfigFromAssets(context:Context): ConfigEntity {
        try {
            val inputStream: InputStream = context.resources.assets.open(FILE_NAME)
            val jsonStr = Utils.stream2String(inputStream)
            return Gson().fromJson(jsonStr,ConfigEntity::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ConfigEntity(configs =  ArrayList<ConfigBean>())
    }

    fun fetchConfig(context: Context):ConfigEntity{
        val file = File(context.externalCacheDir,FILE_NAME)
        val content = Utils.file2String(file)
        return if (TextUtils.isEmpty(content)){
            return fetchConfigFromAssets(context)
        }else{
            Gson().fromJson(content,ConfigEntity::class.java)
        }
    }


    @SuppressLint("RestrictedApi")
    fun downloadConfigFile(context: Context,down_url:String, md5:String){
        Preconditions.checkArgument(!TextUtils.isEmpty(down_url))
        Preconditions.checkArgument(!TextUtils.isEmpty(md5))

        //判断assets目录中的文件md5是否与服务器返回的md5相同

        //判断assets目录中的文件md5是否与服务器返回的md5相同
        val assetsStream: InputStream = context.resources.assets.open(FILE_NAME)
        if (assetsStream != null) {
            val oldMd5: String? = FileMd5Util.getMD5(assetsStream)
            val isNoNull = oldMd5 != null
            val equal = TextUtils.equals(oldMd5, md5)
            if (isNoNull && equal) {
                DDLogger.d(TAG, "配置文件内容未发生变化，不下载文件，使用asset中的文件...")
                return
            }
        }

        //判断本地目录中的文件md5是否与服务器返回的md5相同

        //判断本地目录中的文件md5是否与服务器返回的md5相同
        val file = File(context.externalCacheDir,FILE_NAME)
        if (file.exists()) {
            val oldMd5: String = FileMd5Util.getMD5(file)
            val isNoNull = oldMd5 != null
            val equal = TextUtils.equals(oldMd5, md5)
            if (isNoNull && equal) {
                DDLogger.d(TAG, "配置文件内容未发生变化，不下载文件,使用本地目录中的文件...")
                return
            }
        }

        // 下载文件
//        NetFacade().fetchConfig(url = down_url,onComplete = {
//
//        },onError = {
//
//        })

    }

}