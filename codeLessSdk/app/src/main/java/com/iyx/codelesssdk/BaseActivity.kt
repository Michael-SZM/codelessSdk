package com.iyx.codelesssdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import com.iyx.codeless.CodeLessFacade
import com.iyx.codeless.DataConfigureImp

class BaseActivity : AppCompatActivity() {

    private val codeLessFacade = CodeLessFacade()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeLessFacade.handleWindow(window)
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        codeLessFacade.handleWindow(window)
    }

    override fun setContentView(layoutResID: Int) {
        setContentView(codeLessFacade.wrapView(this,layoutResID,null))
    }

    override fun getLayoutInflater(): LayoutInflater {
        return codeLessFacade.wrapInflater(super.getLayoutInflater())
    }

}