package com.iyx.codeless.filedao

import android.text.TextUtils
import com.google.gson.Gson
import com.iyx.codeless.net.NetFacade.Companion.mSessionId
import com.iyx.codeless.utils.Utils.getContent
import com.iyx.filewr.RequestBodyBaseInfoBean
import com.iyx.filewr.UploadDataBean
import com.iyx.filewr.UploadUnit
import java.io.File

/**
 * 文件解析器
 */
interface FileDecoder {

    fun decode(file: File): UploadDataBean?

}

class DefaultFileDecoder : FileDecoder {

    private val gson = Gson()


    override fun decode(file: File): UploadDataBean? {
        val content: String? = getContent(file)
        if (TextUtils.isEmpty(content)) {
            file.delete()
            return null
        }

        val msgs = content!!.split("\n").toTypedArray()
        //base + event 每个文件中最少两行。小于两行则视为无效文件。
        //base + event 每个文件中最少两行。小于两行则视为无效文件。
        if (msgs.size < 2 || (msgs.size == 2 && TextUtils.isEmpty(msgs[1]))) {
            file.delete()
            return null
        }
        var reBody = gson.fromJson<RequestBodyBaseInfoBean>(msgs[0], RequestBodyBaseInfoBean::class.java)
        val collecActions = mutableListOf<UploadUnit>()
        for (i in 1 until msgs.size) {
            val cc = msgs[i]
            if (TextUtils.isEmpty(cc)) continue
            val action = gson.fromJson<UploadUnit>(msgs[i], UploadUnit::class.java)
            collecActions.add(action)
        }
        // 临时测试
        if (reBody == null){
            reBody = RequestBodyBaseInfoBean()
        }
        val uploadDataBean = UploadDataBean(reBody.key, reBody.uuid, reBody.user_id, mSessionId, collecActions,
                reBody.app_info, reBody.user_info, reBody.location, reBody.device)
        return uploadDataBean
    }

}