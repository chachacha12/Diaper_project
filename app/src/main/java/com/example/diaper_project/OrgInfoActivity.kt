package com.example.diaper_project

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.diaper_project.Adapter.Userinfo_Adapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.GetOne
import com.example.diaper_project.Class.Org
import kotlinx.android.synthetic.main.activity_org_info.*
import kotlinx.android.synthetic.main.activity_userinfo.*
import kotlinx.android.synthetic.main.view_loader.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrgInfoActivity :  BasicActivity() {

    var jsonObject: JSONObject? = null //기관정보를 받아올거임 여기에

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_info)
        init()
    }

    fun init(){
        //기관정보 get기능
        server.get_Organization_Request("Bearer " + currentuser!!.access_token)
            .enqueue(object : Callback<GetOne> {
                override fun onFailure(
                    call: Call<GetOne>,
                    t: Throwable
                ) {
                    Log.e("태그", "기관정보get 통신 아예 실패")
                }
                @RequiresApi(Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<GetOne>, response: Response<GetOne>) {
                    if (response.isSuccessful) {

                        Log.e("태그", "response.body()?.result: "+response.body()!!.result)

                       // var a = response!!.body()!!.result
                        //jsonObject = JSONObject(response.body()!!.result.toString())

                       // chief.text = jsonObject!!.getString("chief")



                       // Log.e("태그", "jsonObject: "+jsonObject)

                        //Log.e("태그", "response : "+ response.body()!!.result.getString("fax"))
                        //jsonObject = JSONObject(response.body()!!.result)
                        //chief.text = response.body()!!.result.getString("chief")  //로그의 id값 가져옴. 이를 통해 로그삭제, 수정 해줄거임


                    } else {
                        Log.e(
                            "태그",
                            "기관정보get실패:" + response.body().toString() + "   errorbody: " + response.errorBody()
                        )
                    }
                }
            })
    } //init

    override fun onStart() {
        super.onStart()
        //아직 서버로부터 데이터를 못받아왔을때는 로딩화면을 보여줌
        if(jsonObject ==null ) {
            loaderLayout.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()

        // 데이터가 서버로부터 왔는지 감시해줌. 데이터 들어왔으면  만들어줌
        if(jsonObject ==null){
            // for(i in 1..10) {
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴")
                if (jsonObject != null) {
                    loaderLayout.visibility = View.GONE
                    Log.e("태그", " (jsonObject != null)  구문 들어옴")
                }
            }, 2000)  //2초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
            // }
        }

        //다른 화면 갔다가 여기 왔을때 데이터작업 완료되었으면 로딩화면 없애줌
        if(jsonObject !=null ){
           loaderLayout.visibility = View.GONE
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(jsonObject !=null ){
                loaderLayout.visibility = View.GONE
            }
        }

    }




}
