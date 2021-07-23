package com.DIAPERS.diaper_project

//부모클래스(액티비티)임
// 모든 액티비티에 공통으로 필요한 코드를 이 클래스에 넣고 다른 액티비티에서 이 클래스를 상속받을거임
//다른 액티비티들 만들고 클래스이름옆 : 에다가 BasicActivity쓰면 상속됨

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


open class BasicActivity : AppCompatActivity() {

    //레트로핏을 만들어줌. 서버와 연결
    var retrofit = Retrofit.Builder()
        .baseUrl("https://diapers-dungji.herokuapp.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var server = retrofit.create(HowlService::class.java)  //서버와 만들어둔 인터페이스를 연결시켜줌.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED //화면의 가로세로 관련 문제 해결을 위해..?
    }

}