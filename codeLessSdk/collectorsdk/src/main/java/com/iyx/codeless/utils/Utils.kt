package com.iyx.codeless.utils

import android.annotation.SuppressLint
import android.support.v4.util.Preconditions
import android.util.Log
import com.iyx.codeless.net.config.DownLoadMsg
import java.io.*

object Utils {

    /**
     * @param is
     * @return 返回null，流读取失败
     */
    @SuppressLint("RestrictedApi")
    fun stream2String(inputStream: InputStream): String? {
        Preconditions.checkNotNull(inputStream)
        var fr: InputStreamReader? = null
        try {
            fr = InputStreamReader(inputStream)
            val br = BufferedReader(fr)
            val sb = StringBuilder()
            var line :String? =""
            while (true){
                line = br.readLine()
                if (line == null) break
                sb.append(line)
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                fr?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun file2String(file:File):String?{
        return if (!file.exists()){
            RxBus.post(DownLoadMsg())
            ""
        }else{
            stream2String(FileInputStream(file))
        }
    }


    fun getContent(file: File?): String? {
        val fileInputStream: FileInputStream
        var content: String? = null
        try {
            fileInputStream = FileInputStream(file)
            var bs: ByteArray? = getBytes(fileInputStream)
            fileInputStream.close()
            bs?.apply {
                content = String(this)
            }
        } catch (e: IOException) {
            Log.e("getContent", e.message)
        }
        return content
    }


    /**
     * 将一个输入流转换成字节数组
     *
     * @param inputStream 输入流
     * @return 输出字节数组
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val arrayOutputStream = ByteArrayOutputStream()
        var buffer: ByteArray? = ByteArray(1024)
        var len = -1
        while (inputStream.read(buffer).also { len = it } != -1) {
            arrayOutputStream.write(buffer, 0, len)
        }
        buffer = arrayOutputStream.toByteArray()
        arrayOutputStream.flush()
        arrayOutputStream.close()
        return buffer
    }

}