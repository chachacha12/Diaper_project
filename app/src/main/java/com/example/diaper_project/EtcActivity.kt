package com.example.diaper_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.utils.Utils.init
import kotlinx.android.synthetic.main.activity_etc.*

class EtcActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etc)
        init()
    }

    fun init(){
        button_news.setOnClickListener {
            var i = Intent(this, NewsActivity::class.java)
            startActivity(i)
        }
        button_service.setOnClickListener {
            var i = Intent(this, ServiceActivity::class.java)
            startActivity(i)
        }



    }
}
