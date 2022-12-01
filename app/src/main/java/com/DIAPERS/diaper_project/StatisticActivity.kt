package com.DIAPERS.diaper_project

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.DIAPERS.diaper_project.Adapter.MyFragStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_statistic.*
import kotlin.collections.ArrayList


class StatisticActivity  :  BasicActivity(),
    FragmentListener {

    var textArray = arrayListOf<String>("그래프","통계")  //tabLayout에 붙을 텍스트 들임.
    var cnt_name_list: ArrayList<String>? =null//전역변수로 둠. onCreate에서 초기화
    var cnt_ids_list: ArrayList<String>? =null //전역변수로 둠. onCreate에서 초기화

    //이 액티비티에 붙힐 프래그먼트 2개를 만들어줌
     var gfragment: GraphFragment? = null
     var afragment: averageFragment? =null

    //FragmentListener 인터페이스 상속받아서 이 함수 꼭 오버라이드 해줘야함. 프래그먼트들 통신에 사용됨
    override fun onCommand(message: ArrayList<Double>) {
        afragment?.display(message)
        Log.e("태그","통계 액티비티통해서 통계프래그먼트의 display함수 실행완료")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    lateinit var cnt_id:String
    //스피너의 항목 선택했을때 이벤트 처리를 위해서 내부 클래스에 OnItemSelectedListener를 상속받고 이 클래스의 객체를 스피너에 달아줄거임
    inner class CustomOnItemSelectedListener : AdapterView.OnItemSelectedListener{

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        //특정 이용자 선택되었을때 발생할 이벤트 작업 - 해당 이용자에 맞는 log값들을 불러와서 그래프 그리기, 통계값 구하기
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            var name = parent?.getItemAtPosition(position).toString()  //이렇게 하면 선택된 항목의 문자열을 가져옴 (이용자들 이름)
            var i=0
            //여기서 그래프 프래그먼트를 한번 다시 만들어주지 않으면 그래프 ui가 에러났음..
            gfragment = null
            gfragment = GraphFragment()
            Log.e("태그"," 이용자 변경함. 새로운 GraphFragment()객체 생성.-액티비티코드임-  ")

            repeat(cnt_name_list!!.size){
                if(name== cnt_name_list!![i]){
                    cnt_id = cnt_ids_list!![i]  //cnt_id 변수에 사용자가 스피너에서 선택한 이용자 id값을 저장함

                    //프래그먼트로 이용자 이름에 맞는 cnt id값을 보내기
                    var bundle = Bundle()
                    bundle.putString("cnt_id", cnt_id )
                    gfragment!!.arguments = bundle
                }
                i++
            }
            //뷰페이저에 다시 프래그먼트들을 붙혀줌. 이때 어댑터에 인자를 하나 추가해서 내가 위에서 bundle넣어서 새로 만든 프래그먼트를 어댑터에 전달해줌
            viewpager2.adapter = MyFragStateAdapter(this@StatisticActivity, gfragment, afragment)

            //뷰페이저2객체를 슬라이딩 할때마다 tab의 위치도 바뀌어야함. 그 둘을 동기화 해주는 클래스인 TabLayoutMediator을 이용해줌.
            TabLayoutMediator(tabLayout, viewpager2){
                    tab, position -> tab.text = textArray[position]
            }.attach()
            Log.e("태그","뷰페이저만들어짐. 다른 이용자 선택되었다는 뜻")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        init()
    }

    @SuppressLint("SuspiciousIndentation")
    fun init(){
        //프래그먼트들 여기서 초기화
        gfragment = GraphFragment()
        afragment = averageFragment()

        var intent = intent         //이 액티비티로 넘어온 인텐트를 받음 (메인에서 이 액티비티로 올때 cnt_name, cnt_ids 리스트 넘겨줌)

            if(cnt_name_list !=null){
                cnt_name_list!!.clear()
                cnt_name_list = intent.getStringArrayListExtra("cnt_name")  //넘어온 이용자들 이름값들 담어줌
            }else{
                cnt_name_list = intent.getStringArrayListExtra("cnt_name")  //넘어온 이용자들 이름값들 담어줌
            }

        if(cnt_ids_list !=null){
            cnt_ids_list!!.clear()
            cnt_ids_list =   intent.getStringArrayListExtra("cnt_ids") //넘어온 이용자들 id값들 담아줌
        }else{
            cnt_ids_list =   intent.getStringArrayListExtra("cnt_ids") //넘어온 이용자들 id값들 담아줌
        }

        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ArrayList<String>())

        var i=0
        repeat(cnt_name_list!!.size){
            adapter.add(cnt_name_list!!.get(i))
            i++
        }

        spinner.adapter = adapter  // 스피너 객체에 바로 위에서 만든 어댑터를 달아줌 - 스피너위젯에 항목들 나열됨
        spinner.onItemSelectedListener= CustomOnItemSelectedListener() //특정 항목 클릭되었을때 이벤트가 처리. 내부클래스 만들어서 함.

        cnt_id =cnt_ids_list!![0]
        //프래그먼트로 첫번째 이용자 id값을 보내기 (아무도 선택 안했을때를 위해서)
        var bundle = Bundle()
        bundle.putString("cnt_id", cnt_id )
        gfragment!!.arguments = bundle


    } //init








}
