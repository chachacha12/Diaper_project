package com.DIAPERS.diaper_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.DIAPERS.diaper_project.Adapter.Statistic_LogAdapter
import com.DIAPERS.diaper_project.Class.GetAll
import com.DIAPERS.diaper_project.Class.log
import com.DIAPERS.diaper_project.Class.success
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.android.synthetic.main.noexisit_log.*
import kotlinx.android.synthetic.main.view_loader.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GraphFragment : Fragment() {

    //레트로핏을 만들어줌. 서버와 연결.  //프래그먼트는 BasicActivity를 상속 못 받아서,, (Statistic액티비티에서 server를 받아오려고 해봤는데 잘 모르겠음..)
    var retrofit = Retrofit.Builder()
        .baseUrl("https://diapers-dungji.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var server = retrofit.create(HowlService::class.java)  //서버와 만들어둔 인터페이스를 연결시켜줌.
    lateinit var id:String //액티비티에서 보낸 cnt id값임
    var entries = ArrayList<BarEntry>()  //겉기저귀 개수를 저장
    var entries2= ArrayList<BarEntry>()  //속기저귀 개수를 저장
    var days = ArrayList<String>()  //x축 데이터에 날짜를 표기해주기 위함
    lateinit var Statistic_LogAdapter: Statistic_LogAdapter  //리사이클러뷰에 쓸 어댑터
    var logArray = ArrayList<log>() //StatisticAdapter에 인자로 보내줄 값임. 그리고 어댑터에서 이걸로 로그 리사이클러뷰 만듬
    var log_size:Int = 0  //스피너를 통해 특정기간이 정해지면 그에 맞춰서 이 변수를 초기화 해줄거임. 일주일은 7로, 1개월을 30 등..
    var fragmentListener: FragmentListener? = null  //통계 프래그먼트와 통신을 위해 인터페이스 객체 선언

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentListener){  //액티비티가 FragmentListener 타입이라면, (즉, 상속받았다면)
            fragmentListener = context   //액티비티를 가져옴. (액티비티가 인터페이스를 상속받아서 가져와서 이 객체에 대입가능), 이제 액티비티에 있는 onCommand함수를 이 객체 통해 여기서도 쓸 수 있음
        }
    }

    override fun onDetach() {
        super.onDetach()
        if(fragmentListener !=null)
            fragmentListener = null
    }

    //아래에서 언급한 valueFormatter를 inner class로 등록해줌
    inner class MyXAxisFormatter : ValueFormatter(){
        //days = arrayOf("1차","2차","3차","4차","5차","6차","7차")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    //로그값 가져올 기간(일주, 한달, 세달)을 정하는 스피너에 이벤트처리로 달아줄 리스너를 내부클래스로 만듬.
    inner class CustomOnItemSelectedListener: AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(parent: AdapterView<*>?) {
            log_size = 7   //여기 안써주고 전역변수로 그냥 7로 초기화해두면 에러남..
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(parent?.getItemAtPosition(position).toString()){
                "일주일"->{
                    log_size = 7
                    Log.e("태그","스피너로 인해 일주일 선택됨, log_size: "+log_size)
                    Log.e("태그"," uiUpdate() 실행")
                    uiUpdate()  //ui를 다시 업데이트 시킴
                }
                "1개월"->{
                    log_size = 30
                    Log.e("태그","스피너로 인해 한달 선택됨, log_size: "+log_size)
                    Log.e("태그"," uiUpdate() 실행")
                    uiUpdate()
                }
                "3개월"->{
                    log_size = 90
                    Log.e("태그","스피너로 인해 세달 선택됨, log_size: "+log_size)
                    Log.e("태그"," uiUpdate() 실행")
                    uiUpdate()
                }
                "6개월"->{
                    log_size = 180
                    Log.e("태그","스피너로 인해 6개월 선택됨, log_size: "+log_size)
                    Log.e("태그"," uiUpdate() 실행")
                    uiUpdate()
                }
            }
        }
    }

    //스피너를 다른거 선택하거나 해서 데이터값이 바뀌었거나 했을때 다시 갱신시켜줌
    fun uiUpdate(){
        fragUpdate()  //다시 새로 리사이클러뷰와 그래프를 만들거임, 즉 갱신해줄거임
        //onStart()함수와 같은 작업(서버로부터 데이터 다 안가져왔으면 로딩화면 보여줌)
        if(entries.size<=0) {   //데이터 안들어왔으면 로딩화면만 보여줌
            LinearLayout_record.visibility = View.INVISIBLE //로그들 보여주는 리사이클러뷰를 가려줌
            LinearLayout_title.visibility = View.INVISIBLE  // "한달간 기저귀 수량변화" 등의 화면 가려줌
            chart.visibility = View.GONE
            loaderLayout.visibility = View.VISIBLE
            textView_clickorder.visibility = View.VISIBLE
        }
        //주로 앱 실행하고 처음 통계 액티비티 들어왔을때나 스피너로 가져올 날짜 일수 바꿀때 실행됨. 그 후엔 밑의 조건문들이 수행될 가능성 높음
        if(entries.size<=0){
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴-그래프 프래그먼트onResume에서")
                if (entries.size>0) {
                    //textView_clickorder2.visibility = View.INVISIBLE
                    makerecyclerView()  //로그 리사이클러뷰 생성
                    LinearLayout_title.visibility = View.VISIBLE
                    chart.visibility = View.VISIBLE
                    LinearLayout_record.visibility = View.VISIBLE
                    loaderLayout.visibility = View.GONE
                    makeChart()
                    textView_clickorder.visibility = View.INVISIBLE
                }
            }, 2000)  //3초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //액티비티에서 보낸 cnt id값을 여기서 받기
        if (arguments != null){
            Log.e("태그","arguments: "+arguments)
            id = arguments!!.getString("cnt_id").toString()
            Log.e("태그","id: "+id)
        }

        if (savedInstanceState != null) {  //이 프래그먼트가 한번이상 실행되었으면 데이터 상태 유지를 위해..
            entries =
                savedInstanceState?.getParcelableArrayList<BarEntry>("entries") as java.util.ArrayList<BarEntry>
            entries2 =
                savedInstanceState?.getParcelableArrayList<BarEntry>("entries2") as java.util.ArrayList<BarEntry>
            Log.e("태그", "savedInstanceState에 값 있는거확인: " + entries)
        } else {  //처음 앱 실행했을때
            Log.e("태그","@@onCreateView에서 fragUpdate()함수실행")
        }
        return  inflater.inflate(R.layout.fragment_graph, container, false)
    }

    //프래그먼트에서 리사이클러뷰를 만들땐 꼭 onViewCreated안에서 리사이클러뷰 만들어주는 작업해주기. onCreatView에서 만들면 리사이클러뷰가 초기화가 제대로 진행 안되서 null로 되는거 같음
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //스피너에 내가만든 리스너 클래스 객체를 달아줌 (이제 이벤트처리 가능해짐)
        spinner_period.onItemSelectedListener = CustomOnItemSelectedListener()
        Log.e("태그","onViewCreated통해 리사이클러뷰 만드는 로직수행")
       makerecyclerView()  //로그 리사이클러뷰 생성

    }

    //사용자가 실시간으로 게시글 삭제, 수정할때에 맞춰서 리스트 업데이트 해줄거임
    //인터페이스 객체를 어댑터말고 액티비티에 구현해둬야하는 이유는 onResume함수 등이 있어서 게시글 업데이트를 해줄수 있어서?
    //인터페이스를 구현한 익명객체를 생성해서 사용할거임. 그리고 이걸 어댑터에 인자로 넣어주면 어댑터에서도 사용가능.
    val onLogListener = object : OnLogListener {
        override fun onDelete(position: Int) {
            logArray.get(position).id  //특정위치의 로그 id값
            //로그삭제 로직
            server.deleteLogRequest("Bearer " + currentuser?.access_token,  logArray.get(position).id!!)
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                    }
                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            Toast.makeText(activity, "선택한 기록을 삭제하였습니다.",Toast.LENGTH_SHORT).show()
                            uiUpdate()
                        } else {
                            Toast.makeText(activity, "기록 삭제 실패.",Toast.LENGTH_SHORT).show()
                            Log.e("태그   로그 삭제실패: ", response.body()?.succeed.toString())
                        }
                    }
                })
        }
        //로그 수정작업
        override fun onModify(position: Int) {
            myStartActivity(LogModifyActivity::class.java, logArray.get(position))  //사용자가 선택한 로그를 인텐트에 실어서 보냄
        }
    }


    //데이터를 실어서 특정 액티비티에 보내주는 인텐트를 함수로 만들어둠  //로그 수정작업에 씀
    fun myStartActivity(c: Class<*>, log: log) {
        var i = Intent(activity, c)
        i.putExtra("log", log)  //내가 클래스 통해 만든 객체들을 putExtra로 보내려면 보내려는 객체 클래스(PostInfo)에 : Serializable 해줘야함
        startActivityForResult(i, 100)  //다른 액티비티 갔다가 그 결과값을 다시 이 액티비티로 가져올것이다.
    }

    //앱 처음 시작했을때나 로그를 수정, 삭제 해서 화면상에서 업데이트 해줄때 씀
    fun fragUpdate(){
        logArray.clear()
        entries.clear()
        entries2.clear()
        days.clear()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val cal = Calendar.getInstance()
        cal.time = Date()
        val createdAt: String = simpleDateFormat.format(cal.time)  //현재시각
        val fewdaysAgo:String  //현재시각으로부터 특정기간 전의 시각

        if(log_size==7){
            cal.add(Calendar.DATE, -7)
            fewdaysAgo = simpleDateFormat.format(cal.time)
        }else if(log_size==30){
            cal.add(Calendar.DATE, -30)
            fewdaysAgo = simpleDateFormat.format(cal.time)
        }else{
            cal.add(Calendar.DATE, -90)
            fewdaysAgo = simpleDateFormat.format(cal.time)
        }

        //Statistic_LogAdapter에 로그 객체로 만들어 보내줄 값들. 로그 리사이클러뷰를 만들기위해
        var time:String
        var inner_opened:Number
        var inner_new:Number
        var outer_opened:Number
        var outer_new:Number
        var created_by:String
        var modified_by:String
        var log_id:String
        var log: log
        var comments:String


        //서버로부터 특정기간 이용자별 로그를 가져옴.
        server.getLog_period_Request(
            "Bearer " + currentuser?.access_token,
            id,
            fewdaysAgo,
            createdAt,
            true
        ).enqueue(object : Callback<GetAll> {
            override fun onFailure(
                call: Call<GetAll>,
                t: Throwable
            ) {  //object로 받아옴. 서버에서 받은 object모델과 맞지 않으면 실패함수로 빠짐
                Log.e("태그", "fragUpdate()함수안에서 특정기간 이용자별 로그 페이지네이션해주는 통신 아예 실패")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                if (response.isSuccessful) {

                    var jsonArray = JSONArray(response.body()?.result)

                    if(jsonArray.length()==0){  //이용자를 처음 생성해서 LOG기록이 하나도 없을때
                        noexist_log_layout.visibility=View.VISIBLE
                    }else{  //log기록이 있을때
                        if( noexist_log_layout.visibility==View.VISIBLE) //기록이 존재하지 않습니다. 띄워주는 layout을 가려줌
                            noexist_log_layout.visibility=View.GONE

                        Log.e(
                            "태그",
                            "fragUpdate()함수안에서 이용자 기간 로그리스트 조회성공:" + jsonArray + "   jsonArray.length(): " + jsonArray.length()
                        )
                        //간단한 날짜로 변경해주려고
                        var parser:SimpleDateFormat
                        var formatter:SimpleDateFormat
                        var output:String
                        var i = 0
                        //여기안에 순서대로 1. 겉기저귀 평균 보유량, 2. 속기저귀 평균 보유량 3. 겉 기저귀 재고 무보유 일수 4. 속기저귀 재고 무보유 일수 를 저장함
                        var statistic_numbers = ArrayList<Double>()
                        var outer_average_sum =0 //겉기저귀 평균 보유량 합
                        var inner_average_sum=0  //속 기저귀 평균 보유량 합
                        var outer_nohold_daysum=0  //겉 기저귀 재고 무보유 일수
                        var inner_nohold_daysum=0 //속 기저귀 재고 무보유 일수

                        repeat(jsonArray.length()) {
                            val iObject = jsonArray.getJSONObject(i)
                            //받아온 각각의 로그값들을 로그객체로 만들어서 logArray안에 넣어줌. 어댑터 클래스 인자로 보내줄거임
                            time = iObject.getString("time")
                            inner_opened = iObject.getInt("inner_opened")
                            inner_new = iObject.getInt("inner_new")
                            outer_opened= iObject.getInt("outer_opened")
                            outer_new= iObject.getInt("outer_new")
                            created_by= iObject.getString("created_by")
                            modified_by = iObject.getString("modified_by")
                            comments = iObject.getString("comment")


                            log_id = iObject.get("id").toString()  //로그의 id값 가져옴. 이를 통해 로그삭제, 수정 해줄거임
                            log = log(id,time,inner_opened, inner_new, outer_opened, outer_new,comments, created_by, modified_by, log_id)
                            logArray.add(log)

                            //통계프래그먼트에 보내줘서 통계치 만들 값들 함께 생성
                            outer_average_sum += outer_new.toInt()
                            inner_average_sum += inner_new.toInt()
                            if(outer_new.toInt()==0 && outer_opened.toInt()==0 ){
                                outer_nohold_daysum++  //재고 무보유 일수를 1증가
                            }
                            if(inner_new.toInt()==0 && inner_opened.toInt()==0 ){
                                inner_nohold_daysum++
                            }

                            //그래프를 만들어주는 데이터셋의 리스트요소에다가 겉기저귀, 속기저귀 로그값을 추가함.
                            //그래프에서 데이터보여주는 순서 바꾸는법: 인덱스 0번째에 값을 넣어줌. 이러면 앞에 값이 있었으면 그대로 한칸씩 밀림. 즉 이런식으로 거꾸로 저장할수있음
                            entries?.add(0,
                                BarEntry(
                                    (i + 1).toFloat(),
                                    iObject.getInt("outer_new").toFloat()
                                )
                            )
                            Log.e("태그","entries순서: "+entries)

                            entries2?.add(0,
                                BarEntry(
                                    (i + 1).toFloat(),
                                    iObject.getInt("inner_new").toFloat()
                                )
                            )
                            //가져온 날짜값을 다른 패턴으로 변환해서 그래프의 x축에 띄워줄거임
                            parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            formatter = SimpleDateFormat("MM/dd")
                            output = formatter.format(parser.parse(iObject.getString("time")  ))
                            days.add(0,output)  //days 리스트안에 저장.
                            i++
                        }
                        outer_average_sum/i
                        //arrayList안에 위에서 구한 값들을 넣어줌
                        statistic_numbers.add((outer_average_sum/i).toDouble() )
                        statistic_numbers.add((inner_average_sum/i).toDouble() )
                        statistic_numbers.add(outer_nohold_daysum.toDouble())
                        statistic_numbers.add(inner_nohold_daysum.toDouble())
                        Log.e("태그", "fragUpdate()함수안에서 fragmentListener?.onCommand실행 ")
                        fragmentListener?.onCommand(statistic_numbers)  //어떻게 보면 액티비티 객체라고 할 수 있는 fragmentListener을 이용해서 액티비티에 있는 onCommand함수를 실행
                    }
                } else {
                    Log.e("태그", "기간 로그 조회실패" + response.body().toString())
                }
            }
        })
    }

    //리사이클러뷰를 여기서 제대로 만들어줌.
    fun makerecyclerView(){
        //recycler_log= view?.findViewById<RecyclerView>(R.id.recycler_log)!!  //화면에 보일 리사이클러뷰객체
        Statistic_LogAdapter = Statistic_LogAdapter(
            activity!!, logArray, onLogListener
        )   //cnt_name리스트도 어댑터에 보내줘서 이용자 이름을 채워주도록 할거임. 그 후 Statistic액티비티에서 spinner만들때 쓸거.
        recycler_log?.layoutManager = LinearLayoutManager(activity)         ///
        recycler_log?.adapter = Statistic_LogAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
        Log.e("태그", "makerecyclerView()함수 돌아감"+ "logArray: "+logArray)
    }

    override fun onStart() {
        super.onStart()

        //로딩화면 보여주기
        if(entries.size<=0) {
            LinearLayout_record.visibility = View.INVISIBLE //로그들 보여주는 리사이클러뷰를 가려줌
            LinearLayout_title.visibility = View.INVISIBLE
            chart.visibility = View.GONE
            loaderLayout.visibility = View.VISIBLE
            textView_clickorder.visibility = View.VISIBLE
        }
    }

    //다른 프래그먼트로 갔다가 다시 이 프래그먼트로 돌아오거나, 뭔가를 사용자가 클릭해서 상호작용할때마다 작동되는 함수인듯?
    override fun onResume() {
        super.onResume()

         Log.e("태그","onResume돌아감")

        //다른 프래그먼트 갔다가 여기 왔을때 동작완료되었다면 그래프띄워주기 위함
        if(entries.size>0){
            makerecyclerView()  //로그 리사이클러뷰 생성
            chart.visibility = View.VISIBLE
            LinearLayout_title.visibility = View.VISIBLE
            LinearLayout_record.visibility = View.VISIBLE
            loaderLayout.visibility = View.GONE
            makeChart()
            textView_clickorder.visibility = View.INVISIBLE
        }


        //주로 앱 실행하고 처음 통계 액티비티 들어왔을때나 스피너로 가져올 날짜 일수 바꿀때 실행됨. 그 후엔 밑의 조건문들이 수행될 가능성 높음
        if(entries.size<=0){
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴-그래프 프래그먼트onResume에서")
                if (entries.size>0) {
                    //textView_clickorder2.visibility = View.INVISIBLE
                    makerecyclerView()  //로그 리사이클러뷰 생성
                    LinearLayout_title.visibility = View.VISIBLE
                    chart.visibility = View.VISIBLE
                    LinearLayout_record.visibility = View.VISIBLE
                    loaderLayout.visibility = View.GONE
                    makeChart()
                    textView_clickorder.visibility = View.INVISIBLE
                }
            }, 2000)  //4초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(entries.size>0){
                makerecyclerView()  //로그 리사이클러뷰 생성
                LinearLayout_title.visibility = View.VISIBLE
                chart.visibility = View.VISIBLE
                LinearLayout_record.visibility = View.VISIBLE
                loaderLayout.visibility = View.GONE
                makeChart()
                textView_clickorder.visibility = View.INVISIBLE
            }
        }
    }


    //차트세팅, 만들기
    fun makeChart(){
        chart.apply {

            //터치, Pinch 상호작용
            setScaleEnabled(false)
            setTouchEnabled(true)
            setPinchZoom(true)

            //Chart가 그려질때 애니메이션
            animateXY(0, 800)

            //Chart 밑에 description 표시 유무
            description = null

            //Legend는 차트의 범례(참고사항)를 의미합니다
            //범례가 표시될 위치를 설정
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT

            //차트의 좌, 우측 최소/최대값을 설정합니다.
            //차트 제일 밑이 0부터 시작하고 싶은 경우 설정합니다.
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f

            //기본적으로 차트 우측 축에도 데이터가 표시됩니다
            //이를 활성화/비활성화 하기 위함
            axisRight.setDrawLabels(false)

            //xAxis, yAxis 둘다 존재하여 따로 설정이 가능합니다
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                granularity = 0.9f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, R.color.colorPrimary) //라벨 색상
                valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함         //GraphFragment.MyXAxisFormatter()원랜 이거엿음
                textSize = 10f // 텍스트 크기
                labelCount = entries!!.size
                spaceMin = 0.5f
                spaceMax =0.5f
            }
        }

        //데이터셋 추가 및 차트 띄우기
        if (entries!!.size > 0 && entries2!!.size > 0) {

            var graphArr = ArrayList<IBarDataSet>()

            var set = BarDataSet(entries, "겉기저귀 개수(미개봉팩)")//데이터셋 만들기, (겉기저귀 수량)
            set.color = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)

            var set2 = BarDataSet(entries2, "속기저귀 개수(미개봉팩)")//데이터셋 만들기, (속기저귀 수량)
            set2.color =
                ContextCompat.getColor(context!!, R.color.design_default_color_on_secondary)

            //막대그래프를 2개를 그룹으로해서 만들어줄거임 (그룹bar 형태로 만들거임)
            graphArr.add(set)
            graphArr.add(set2)
            val data = BarData(graphArr)

            data.barWidth = 0.2f//막대 너비 설정하기

            chart.run {
                this.data = data //차트의 데이터를 data로 설정해줌.
                setFitBars(true)
                invalidate()
                chart!!.groupBars(
                    0.7f,
                    0.5f,
                    0.02f
                )  //첫 인자는 그래프가 젤 왼쪽 y축으로 부터 얼마나 떨어질지, 두번째인자는 bar그룹들이 얼마나 떨어질지,
                //세번째는 같은 그룹내의 바들이 얼마나 떨어질지를 정해줌.
            }
        }
    } //makechart()

    //로그 수정하기 액티비티(cntAddactivity)에 갔다오면서 받은 데이터에 따른 동작처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            100 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {  //로그 수정에 성공했다면
                        fragUpdate()  //다시 새로 리사이클러뷰와 그래프를 만들거임, 즉 갱신해줄거임
                        //onStart()함수와 같은 작업(서버로부터 데이터 다 안가져왔으면 로딩화면 보여줌)
                        if(entries.size<=0) {
                            LinearLayout_record.visibility = View.INVISIBLE //로그들 보여주는 리사이클러뷰를 가려줌
                            chart.visibility = View.GONE
                            loaderLayout.visibility = View.VISIBLE
                            textView_clickorder.visibility = View.VISIBLE
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                }
            }
        }
    }



}
