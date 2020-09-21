package com.iyx.codelesssdk

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.iyx.codeless.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_01).apply {
            setOnClickListener {
                Toast.makeText(this@MainActivity,"---^^---",Toast.LENGTH_SHORT).show()
            }
        }

    }
}
