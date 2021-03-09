package com.example.diaper_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_statistic.*

class StatisticActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        init()
    }
    fun init(){

        var intent = intent         //이 액티비티로 넘어온 인텐트를 받음 (메인에서 이 액티비티로 올때 cnt_name 리스트 넘겨줌)
        var cnt_name_list = intent.getStringArrayListExtra("cnt_name")  //cnt_name_list라는 배열에 받아온 리스트값들 담어줌
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ArrayList<String>())
        var i=0
        repeat(cnt_name_list.size){
            adapter.add(cnt_name_list.get(i))
            i++
        }

        spinner.adapter = adapter  // 스피너 객체에 위에서 만든 어댑터 달아줌


    }


}
