package com.iyx.codeless.filedao

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.iyx.codeless.net.NetFacade
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.concurrent.LinkedBlockingDeque


class FileWriter(private val sp:SpExt,private var folderPath:String,val logToken:String) {

    var backupStrategy: BackupStrategy?=null

    var writer: Writer?=null
    var worker: Worker?=null

    init {
        checkExit()
    }

    fun write(msg:String){
        worker?.apply {
            if (!isStarted()){
                start()
            }
            enqueue(msg)
        }
    }

    fun forceUpload(){
        worker?.apply {
            forceUpload = true
            enqueue("")
        }
    }

    private fun checkExit() {
        if (folderPath == null) throw Exception("folderPath must be set")
        if (backupStrategy == null){
            backupStrategy = BackupStrategyDefault(logToken,sp)
        }
        if (writer == null){
            writer = Writer()
        }
        if (worker == null){
            worker = Worker(backupStrategy!!)
        }
    }


    inner class Worker(val backupStrategy: BackupStrategy):Runnable{

        val msgQueue = LinkedBlockingDeque<String>()
        var forceUpload:Boolean = false
        private val gson = Gson()

        var started :Boolean = false

        fun enqueue(msg: String){
            try {
                msgQueue.put(msg)
            }catch (e:Exception){
                Log.e("error:",e.message)
                Thread.currentThread().interrupt()
            }
        }

        fun isStarted():Boolean{
            synchronized(this){
                return started
            }
        }

        fun start(){
            synchronized(this){
                Thread(this).start()
                started = true
            }
        }

        override fun run() {
            var msg =""
            try {
                while (msgQueue.take().also { msg = it } != null){
                    writeMsg(msg)
                }
            }catch (e:Exception){
                synchronized(this) { started = false }
            }
        }


        fun writeMsg(msg: String){
            writer?.apply {
                var lastFile = getFile()
                if (forceUpload){
                    close()
                    backupStrategy?.onBackup(lastFile!!)
                    forceUpload = false
                    initFile()
                    return
                }
                //当前文件为空，或者不存在，或者需要备份，则关闭当前文件，创建新的文件
                var backup = false
                if (lastFile == null || !lastFile.exists() || (backupStrategy.shouldBackup(lastFile).also { backup=it })) {
                    if (backup) {
                        close()
                        sp.put(value = null)
                        backupStrategy?.onBackup(lastFile!!)
                    }
                    initFile()
                }
                if (TextUtils.isEmpty(msg).not()){
                    writer!!.appendLog(msg)
                }
            }
        }

        /**
         * 初始化流，并写入头
         */
        private fun initFile() {
            var lastName = sp.get()
            val fName = lastName?.apply {
                lastName
            }?: kotlin.run {
                val currTime = System.currentTimeMillis()
                val newFileName = "$currTime-tmp"
                newFileName
            }

            if (!writer!!.open(fName,onNewCreate = {name,_->
                        // 保存文件名，
                        sp.put(value = name)
                        // 每次新创建文件都要写一个头信息（进本信息）
                        val tmp  = gson.toJson(NetFacade.rBody?.apply {
                            // 每次请求完成更新SessionId
                            sessionId = NetFacade.mSessionId
                        })
                        writer!!.appendLog(tmp)// 写头文件--baseinfo
                    })) {
                return
            }

        }

    }



    inner class Writer{
        /**
         * The file name of last used log file.
         */
        private var lastFileName: String? = null

        /**
         * The current log file.
         */
        private var logFile: File? = null

        private var bufferedWriter: BufferedWriter? = null

        /**
         * Whether the log file is opened.
         *
         * @return true if opened, false otherwise
         */
        fun isOpened(): Boolean {
            return bufferedWriter != null
        }

        /**
         * Get the name of last used log file.
         * @return the name of last used log file, maybe null
         */
        fun getLastFileName(): String? {
            return lastFileName
        }

        /**
         * Get the current log file.
         *
         * @return the current log file, maybe null
         */
        fun getFile(): File? {
            return logFile
        }


        /**
         * Open the file of specific name to be written into.
         *
         * @param newFileName the specific file name
         * @return true if opened successfully, false otherwise
         */
        fun open(newFileName: String,onNewCreate:(String,File)->Unit={_,_->}): Boolean {
            lastFileName = newFileName
            logFile = File(folderPath, newFileName)

            var isNew = false
            // Create log file if not exists.
            logFile?.apply {
                if (this.exists().not()){
                    try {
                        val parent = this.parentFile
                        if (parent.exists().not()) {
                            parent.mkdirs()
                        }
                        this.createNewFile()
                        isNew = true
                    } catch (e: IOException) {
                        e.printStackTrace()
                        lastFileName = null
                        logFile = null
                        return false
                    }
                }

                // Create buffered writer.
                try {
                    bufferedWriter = BufferedWriter(FileWriter(logFile, true))
                    if (isNew){
                        onNewCreate(newFileName,logFile!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    lastFileName = null
                    logFile = null
                    return false
                }
            }?:return false

            return true
        }


        /**
         * Close the current log file if it is opened.
         *
         * @return true if closed successfully, false otherwise
         */
        fun close(): Boolean {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    return false
                } finally {
                    bufferedWriter = null
                    lastFileName = null
                    logFile = null
                }
            }
            return true
        }


        /**
         * Append the flattened log to the end of current opened log file.
         *
         * @param flattenedLog the flattened log
         */
        fun appendLog(flattenedLog: String?) {
            try {
                bufferedWriter!!.write(flattenedLog)
                bufferedWriter!!.newLine()
                bufferedWriter!!.flush()
            } catch (e: IOException) {
            }
        }

    }

}