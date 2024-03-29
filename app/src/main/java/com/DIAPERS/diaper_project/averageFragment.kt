package com.DIAPERS.diaper_project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.DIAPERS.diaper_project.Class.GetAll
import com.DIAPERS.diaper_project.databinding.FragmentAverageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class averageFragment : Fragment() {

    //그래프프래그먼트에서 받은 통계치들을 잠깐 저장해둘 전역변수들
    var a:String=""  //겉 평균 보유량
    var b:String=""  //속 평균 보유량
    var c:String=""  //겉 무보유 일수
    var d:String=""  //속 무보유 일수

    //뷰바인딩을 함 - xml의 뷰들에 접근하기 위해서
    private var _binding: FragmentAverageBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //xml뷰에 접근하기 위해 뷰 바인딩 해줌 뷰 바인딩 가져오기. 뷰바인딩 클래스는 xml과 연동이 되어있기 때문에 layout를 명시해줄필요가없다.
        _binding = FragmentAverageBinding.inflate(inflater, container,false)
        val view = binding?.root
        return view
    }


    override fun onStart() {
        super.onStart()
    }

    //프래그먼트 갱신시
    override fun onResume() {
        super.onResume()
        binding?.OuterHoldAverage?.text = a
        binding?.InnerHoldAverage?.text = b
        binding?.OuterNoholdDaycount?.text = c
        binding?.InnerNoholdDaycount?.text = d
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    //이 함수는 액티비티에서 사용될 함수임. 액티비티에서 이 프래그먼트 객체 불러와서 이 함수 사용될거임. (프래그먼트 통신을 위해)
    fun display(message:ArrayList<Double>){
        //이 함수가 액티비티로 불려가서 밑의 동작함(이 프래그먼트의 전역변수들에 그래프프래그먼트에서 받은 값들 넣어줌)
        //그리고 이 프래그먼트가 생성될때 그 전역변수들 값으로  텍스트뷰들에 띄워줌
        //그래프프래그에서 받은 값들을 여기서 바로 텍스트뷰에 대입시키지 않는 이유는..값을 받을땐 아직, 이 프래그먼트가 생성
        //안되어 있을때 인가봄. 그래서 텍스트뷰가 null로 나옴
        a = message[0].toString()
        b = message[1].toString()
        c = message[2].toString()
        d = message[3].toString()
    }



}
