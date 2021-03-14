package com.example.diaper_project

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_graph.*

/**
 * A simple [Fragment] subclass.
 */
class graphFragment : Fragment() {

    val entries = mutableListOf<com.github.mikephil.charting.data.BarEntry>()

    //아래에서 언급한 valueFormatter를 inner class로 등록해줌
    inner class MyXAxisFormatter : ValueFormatter(){
        private val days = arrayOf("1차","2차","3차","4차","5차","6차","7차")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //차트(그래프) 만들기 시작
        entries.add(com.github.mikephil.charting.data.BarEntry(1.2f, 20.0f))  //BarEntry(1.2f라는 좌표에, 20.0f만큼의 그래프 영역을 그린다)
        entries.add(com.github.mikephil.charting.data.BarEntry(2.2f,70.0f))
        entries.add(com.github.mikephil.charting.data.BarEntry(3.2f,30.0f))
        entries.add(com.github.mikephil.charting.data.BarEntry(4.2f,90.0f))
        entries.add(com.github.mikephil.charting.data.BarEntry(5.2f,70.0f))
        entries.add(com.github.mikephil.charting.data.BarEntry(6.2f,30.0f))
        entries.add(com.github.mikephil.charting.data.BarEntry(7.2f,90.0f))

        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onStart() {
        super.onStart()

        //차트세팅
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
            setPinchZoom(false)

            //Chart가 그려질때 애니메이션
            animateXY(0,800)

            //Chart 밑에 description 표시 유무
            description=null

            //Legend는 차트의 범례를 의미합니다
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
            //차트 내부에 Grid 표시 유무
            xAxis.setDrawGridLines(false)

            //x축 데이터 표시 위치
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //x축 데이터 갯수 설정
            xAxis.labelCount = entries.size
        }

        //데이터셋 추가 및 차트 띄우기
        var set = BarDataSet(entries,"DataSet")//데이터셋 초기화 하기
        set.color = ContextCompat.getColor(context!!,R.color.colorPrimaryDark)

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        val data = BarData(dataSet)
        data.barWidth = 0.3f//막대 너비 설정하기
        chart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
    }







}
