package com.example.diaper_project

import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Adapter.MainAdapter
import com.example.diaper_project.Adapter.Statistic_LogAdapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.log
import com.example.diaper_project.Class.success
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_graph.*
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

    var entries = ArrayList<BarEntry>()  //겉기저귀 개수를 저장
    var entries2= ArrayList<BarEntry>()  //속기저귀 개수를 저장
    var days = ArrayList<String>()  //x축 데이터에 날짜를 표기해주기 위함
    lateinit var Statistic_LogAdapter: Statistic_LogAdapter  //리사이클러뷰에 쓸 어댑터
    var logArray = ArrayList<log>() //StatisticAdapter에 인자로 보내줄 값임. 그리고 어댑터에서 이걸로 로그 리사이클러뷰 만듬


    //아래에서 언급한 valueFormatter를 inner class로 등록해줌
    inner class MyXAxisFormatter : ValueFormatter(){

        //days = arrayOf("1차","2차","3차","4차","5차","6차","7차")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelableArrayList("entries", entries)
        outState.putParcelableArrayList("entries2", entries2)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        //액티비티에서 보낸 cnt id값을 여기서 받기
        var id:String
        if (arguments != null){
            Log.e("태그","arguments: "+arguments)
            id = arguments!!.getString("cnt_id").toString()
            Log.e("태그","id: "+id)
        }else{
            Log.e("태그","arguments: "+arguments)
            id = "2yIBG0kMlHBGngM6I02L"  //데이터를 못받아오면 김명규id로 초기화
        }


        if (savedInstanceState != null) {  //이 프래그먼트가 한번이상 실행되었으면 데이터 상태 유지를 위해..
            entries =
                savedInstanceState?.getParcelableArrayList<BarEntry>("entries") as java.util.ArrayList<BarEntry>
            entries2 =
                savedInstanceState?.getParcelableArrayList<BarEntry>("entries2") as java.util.ArrayList<BarEntry>
            Log.e("태그", "savedInstanceState에 값 있는거확인: " + entries)
        } else {  //처음 앱 실행했을때

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val cal = Calendar.getInstance()
            cal.time = Date()
            val createdAt: String = simpleDateFormat.format(cal.time)
            cal.add(Calendar.DATE, -7)
            val sevendaysAgo = simpleDateFormat.format(cal.time)

            //Statistic_LogAdapter에 로그 객체로 만들어 보내줄 값들. 로그 리사이클러뷰를 만들기위해
            var time:String
            var inner_opened:Number
            var inner_new:Number
            var outer_opened:Number
            var outer_new:Number
            var created_by:String
            var modified_by:String
            var log: log

            //서버로부터 특정기간 이용자별 로그를 페이지네이션해서 특정개수만 가져옴.size값으로 조절
            server.getLog_period_Request(
                "Bearer " + currentuser?.access_token,
                id,
                0,
                7,
                sevendaysAgo,
                createdAt
            ).enqueue(object : Callback<GetAll> {
                override fun onFailure(
                    call: Call<GetAll>,
                    t: Throwable
                ) {  //object로 받아옴. 서버에서 받은 object모델과 맞지 않으면 실패함수로 빠짐
                    Log.e("태그", "통신 아예 실패")
                }


                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                    if (response.isSuccessful) {

                        val jsonArray = JSONArray(response.body()?.result)
                        Log.e(
                            "태그",
                            "이용자 기간 로그리스트 조회성공:" + jsonArray + "   jsonArray.length(): " + jsonArray.length()
                        )

                        //간단한 날짜로 변경해주려고
                        var parser:SimpleDateFormat
                        var formatter:SimpleDateFormat
                        var output:String
                        var i = 0
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
                            log = log(id,time,inner_opened, inner_new, outer_opened, outer_new,"코멘트없음", created_by, modified_by)
                            logArray.add(log)

                            //그래프를 만들어주는 데이터셋의 리스트요소에다가 겉기저귀, 속기저귀 로그값을 추가함.
                            //인덱스 0번째에 값을 넣어줌. 이러면 앞에 값이 있었으면 그대로 한칸씩 밀림. 즉 이런식으로 거꾸로 저장할수있음
                            entries?.add(0,
                                BarEntry(
                                    (i + 1).toFloat(),
                                    iObject.getInt("outer_new").toFloat()
                                )
                            )
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
                            days.add(0, output)  //days 리스트안에 저장.
                            i++
                        }
                        Log.e("태그","logArray생성완료:  "+logArray)
                    } else {
                        Log.e("태그", "기간 로그 조회실패" + response.body().toString())
                    }
                }
            })
        }
        return  inflater.inflate(R.layout.fragment_graph, container, false)
    }

    //프래그먼트에서 리사이클러뷰를 만들땐 꼭 onViewCreated안에서 리사이클러뷰 만들어주는 작업해주기. onCreatView에서 만들면 리사이클러뷰가 초기화가 제대로 진행 안되서 null로 되는거 같음
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makerecyclerView()  //로그 리사이클러뷰 생성
    }

    //사용자가 실시간으로 게시글 삭제, 수정할때에 맞춰서 리스트 업데이트 해줄거임
    //인터페이스 객체를 어댑터말고 액티비티에 구현해둬야하는 이유는 onResume함수 등이 있어서 게시글 업데이트를 해줄수 있어서?
    //인터페이스를 구현한 익명객체를 생성해서 사용할거임. 그리고 이걸 어댑터에 인자로 넣어주면 어댑터에서도 사용가능.
    val onLogListener = object : OnLogListener {
        override fun onDelete(position: Int) {        //postList상에서의 게시글 위치값을 받아서 지워줄거임


            //로그삭제 로직
            server.deleteLogRequest("Bearer " + currentuser?.access_token, "y1HBUq9LmUbrhhWDvLYz")
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            Log.e("태그   로그 삭제성공: ", response.body()?.succeed.toString())
                        } else {
                            Log.e("태그   로그 삭제실패: ", response.body()?.succeed.toString())
                        }
                    }
                })




        }

        //게시글 수정작업
        override fun onModify(position: Int) {

        }
    }

    //리사이클러뷰를 여기서 제대로 만들어줌.
    fun makerecyclerView(){
        var recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_log)!!  //화면에 보일 리사이클러뷰객체
        Statistic_LogAdapter = Statistic_LogAdapter(
            activity!!, logArray, onLogListener
        )   //cnt_name리스트도 어댑터에 보내줘서 이용자 이름을 채워주도록 할거임. 그 후 Statistic액티비티에서 spinner만들때 쓸거.
        recyclerView?.layoutManager = LinearLayoutManager(activity)         ///
        recyclerView?.adapter = Statistic_LogAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
        Log.e("태그", "makerecyclerView()함수 돌아감"+ "logArray: "+logArray)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        //로딩화면 보여주기
        if(entries.size<=0) {
            LinearLayout_record.visibility = View.INVISIBLE //로그들 보여주는 리사이클러뷰를 가려줌
            chart.visibility = View.GONE
            loaderLayout.visibility = View.VISIBLE
            textView_clickorder.visibility = View.VISIBLE
        }

    }

    //다른 프래그먼트로 갔다가 다시 이 프래그먼트로 돌아오거나, 뭔가를 사용자가 클릭해서 상호작용할때마다 작동되는 함수인듯?
    override fun onResume() {
        super.onResume()


        //다른 프래그먼트 갔다가 여기 왔을때 동작완료되었다면 그래프띄워주기 위함
        if(entries.size>0){
            makerecyclerView()  //로그 리사이클러뷰 생성
            chart.visibility = View.VISIBLE
            LinearLayout_record.visibility = View.VISIBLE
            loaderLayout.visibility = View.GONE
            makeChart()
            textView_clickorder.visibility = View.INVISIBLE
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(entries.size>0){
                makerecyclerView()  //로그 리사이클러뷰 생성
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
                valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함
                textSize = 10f // 텍스트 크기
                labelCount = entries!!.size
            }
        }

        //데이터셋 추가 및 차트 띄우기
        if (entries!!.size > 0 && entries2!!.size > 0) {

            var graphArr = ArrayList<IBarDataSet>()

            var set = BarDataSet(entries, "겉기저귀 개수")//데이터셋 만들기, (겉기저귀 수량)
            set.color = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)

            var set2 = BarDataSet(entries2, "속기저귀 개수")//데이터셋 만들기, (속기저귀 수량)
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


}
