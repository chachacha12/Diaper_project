package com.example.diaper_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.diaper_project.Adapter.MyFragStateAdapter
import com.example.diaper_project.Class.GetAll
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_statistic.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatisticActivity  :  BasicActivity() {

    var textArray = arrayListOf<String>("그래프","통계")  //tabLayout에 붙을 텍스트 들임.


    //스피너의 항목 눌렀을때 이벤트 처리를 위해서 내부 클래스에 OnItemClickListener를 상속받고 이 클래스의 객체를 스피너에 달아줄거임
    inner class CustomOnItemSelectedListener : AdapterView.OnItemClickListener{

        //특정 이용자 선택되었을때 발생할 이벤트 작업 - 해당 이용자에 맞는 log값들을 불러와서 그래프 그리기, 통계값 구하기
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var name = parent?.getItemAtPosition(position).toString()  //이렇게 하면 선택된 항목의 문자열을 가져옴 (이용자들 이름)

        }
    }

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

        spinner.adapter = adapter  // 스피너 객체에 바로 위에서 만든 어댑터를 달아줌 - 스피너위젯에 항목들 나열됨
        //spinner.onItemClickListener = CustomOnItemSelectedListener()  //특정 항목 클릭되었을때 이벤트가 처리


        //뷰페이저에 내가 만든 어댑터(몇개의 프래그먼트를 붙일지와 어떤 프래그먼트를 어느 페이지에 붙일지를 정해둠)를 붙혀줌.
        viewpager2.adapter =
            MyFragStateAdapter(this@StatisticActivity)

        //뷰페이저2객체를 슬라이딩 할때마다 tab의 위치도 바뀌어야함. 그 둘을 동기화 해주는 클래스인 TabLayoutMediator을 이용해줌.
        TabLayoutMediator(tabLayout, viewpager2){
                tab, position -> tab.text = textArray[position]
        }.attach()

    } //init


    /*
    //이 액티비티에서 프래그먼트로 데이터 줄때 사용할 함수
    fun setDataAtFragment(fragment: Fragment, logs:JSONArray){
        val bundle=Bundle()
        bundle.putString("logs", logs.toString())
        fragment.arguments = bundle
    }

    //특정 프래그먼트 띄워주는 함수 만듬
    fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.viewpager2, fragment)
    }

     */





}
