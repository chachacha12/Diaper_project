package com.example.diaper_project

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.diaper_project.Class.GetAll
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.android.synthetic.main.view_loader.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    var entries2 = ArrayList<BarEntry>()  //속기저귀 개수를 저장

    var days = ArrayList<String>()  //x축 데이터에 날짜를 표기해주기 위함


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

                            //그래프를 만들어주는 데이터셋의 리스트요소에다가 겉기저귀, 속기저귀 로그값을 추가함.

                            //인덱스 0번째에 값을 넣어줌. 이러면 앞에 값이 있었으면 그대로 한칸씩 밀림. 즉 이런식으로 거꾸로 저장할수있음
                            entries.add(0,
                                BarEntry(
                                    (i + 1).toFloat(),
                                    iObject.getInt("outer_new").toFloat()
                                )
                            )
                            entries2.add(0,
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

                    } else {
                        Log.e("태그", "기간 로그 조회실패" + response.body().toString())
                    }

                }
            })
        }
        return  inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        //로딩화면 보여주기
        if(entries.size<=0) {
            chart.visibility = View.GONE
            loaderLayout.visibility = View.VISIBLE
        }
    }



    //다른 프래그먼트로 갔다가 다시 이 프래그먼트로 돌아오거나, 뭔가를 사용자가 클릭해서 상호작용할때마다 작동되는 함수인듯?
    override fun onResume() {
        super.onResume()


        /*
        //2초마다 총10번을 수시로 데이터가 서버로부터 왔는지 감시해줌. 데이터 들어왔으면 차트 만들어줌
        if(loaderLayout.visibility == View.VISIBLE){
            //for(i in 1..10) {
                Handler().postDelayed({
                    Log.e("태그", " Handler().postDelayed 구문 들어옴")
                    if (entries.size>0) {
                        chart.visibility = View.VISIBLE
                        loaderLayout.visibility = View.GONE
                        makeChart()
                        Log.e("태그", " ( if (entries.size>0))  구문 들어옴")
                    }
                }, 5000)  //2초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.

           // }
        }
         */

        
        //다른 프래그먼트 갔다가 여기 왔을때 동작완료되었다면 그래프띄워주기 위함
        if(entries.size>0){
            chart.visibility = View.VISIBLE
            loaderLayout.visibility = View.GONE
            makeChart()
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(entries.size>0){
                chart.visibility = View.VISIBLE
                loaderLayout.visibility = View.GONE
                makeChart()
            }
        }
    }


    //차트세팅, 만들기
    fun makeChart(){
        chart.apply {
            /*
             description.isEnabled = true //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.
             setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 정해주었다.
             setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
             setDrawBarShadow(false)//그래프의 그림자
             setDrawGridBackground(false)//격자구조 넣을건지
             axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                 axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈을 정해주었다
                 axisMinimum = 0f // 최소값 0
                 granularity = 50f // 50 단위마다 선을 그리려고 granularity 설정 해 주었다.
                 //위 설정이 20f였다면 총 5개의 선이 그려졌을 것
                 setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                 setDrawGridLines(true) //격자 라인 활용
                 setDrawAxisLine(false) // 축 그리기 설정
                 axisLineColor = ContextCompat.getColor(context,R.color.colorPrimary) // 축 색깔 설정
                 gridColor = ContextCompat.getColor(context,R.color.colorPrimaryDark) // 축 아닌 격자 색깔 설정
                 textColor = ContextCompat.getColor(context,R.color.colorAccent) // 라벨 텍스트 컬러 설정
                 textSize = 14f //라벨 텍스트 크기
             }
             xAxis.run {
                 position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                 granularity = 1f // 1 단위만큼 간격 두기
                 setDrawAxisLine(true) // 축 그림
                 setDrawGridLines(false) // 격자
                 textColor = ContextCompat.getColor(context,R.color.colorPrimary) //라벨 색상
                 valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함
                 textSize = 14f // 텍스트 크기
             }
             axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
             setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
             animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
             legend.isEnabled = false //차트 범례 설정

               */

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
                labelCount = entries.size
            }
        }

        //데이터셋 추가 및 차트 띄우기
        if (entries.size > 0 && entries2.size > 0) {

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
