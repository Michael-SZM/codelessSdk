package com.iyx.codeless.filedao

import android.content.Context

class SpExt(private val ctx:Context) {

    val FILE_NAME:String = "tmp_file_name_recorder"
    val KEY_OF_TMP_FILE_NAME:String = "key_of_tmp_file_name"

    fun put(key:String=KEY_OF_TMP_FILE_NAME, value:String?){
        val sp = ctx.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        sp.edit().apply {
            putString(key,value)
            commit()
        }
    }

    fun get(key:String=KEY_OF_TMP_FILE_NAME):String?{
        val sp = ctx.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE)
        return sp.getString(key,null)
    }

}