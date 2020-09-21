package com.iyx.codeless.filedao

import android.util.Log
import com.iyx.codeless.net.NetFacade
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

interface BackupStrategy {

    fun shouldBackup(file: File): Boolean

    fun onBackup(file: File)
}


object FileTaskExecutor{

    private const val CORE_THREAD = 1
    private const val MAX_THREAD = 4
    private val mExecutorService: ExecutorService = ThreadPoolExecutor(
            CORE_THREAD, MAX_THREAD,
            0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue<Runnable>(), ThreadPoolExecutor.DiscardPolicy()
    )

    fun execute(uploadTask: Runnable?) {
        mExecutorService.execute(uploadTask)
    }

}


class BackupStrategyDefault(val logToken:String,val sp:SpExt,val fileDecoder: FileDecoder = DefaultFileDecoder(),val limit:Int=2,val debug:Boolean = false):BackupStrategy{

    private var LIMIT_TIME = 1000 * 60 * limit
    get() {
        return 1000 * 60 * NetFacade.mLimit
    }
    private val MAX_FILE_LEN = 1024 * 5
    private var mFileCreateTime: Long = -1

    override fun shouldBackup(file: File): Boolean {
        if (mFileCreateTime < 0) {
            val backupFilename: String = getTimestamp(file.name)
            mFileCreateTime = backupFilename.toLong()
        } else {
            //如果文件创建时间超过了创建时间，就备份
            if (System.currentTimeMillis() - mFileCreateTime > LIMIT_TIME) {
                mFileCreateTime = -1
                if (debug){
                    Log.d("BackupStrategy3", "文件创建了太久，需要备份准备上传")
                }
                return true
            }
        }

        return file.length() > MAX_FILE_LEN
    }

    override fun onBackup(file: File) {
        if (file != null && file.exists()) {
            val name: String = getNoPostfixFileName(file.name)
            //备份文件 修改后缀
            val fullFile =
                    File(file.parent, "$name.full")
            if (!file.renameTo(fullFile)) {
                Log.e("BackupStrategy3", "备份失败")
            }
            //启动上传线程
            FileTaskExecutor.execute(UploadTask(fullFile,logToken,fileDecoder))
        }

    }

    /**
     * 根据文件名获取创建文件的时间
     */
    fun getTimestamp(name: String): String {
        return name.substring(0,name.lastIndexOf("-"))
    }

    /**
     * 根据文件名获取创建无后缀文件名
     */
    fun getNoPostfixFileName(name: String): String {
        return name.substring(0, name.lastIndexOf("-"))
    }


    inner class UploadTask(val file:File,val logToken:String,val fileDecoder:FileDecoder):Runnable{

        override fun run() {
            try {
                doUploadDataFromFile()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 解析文件，并上报
         */
        private fun doUploadDataFromFile() {
            fileDecoder.decode(file)?.apply {
                file.delete()
                NetFacade.Instance().uploadActionData(logToken,this)
            }

        }

    }

}