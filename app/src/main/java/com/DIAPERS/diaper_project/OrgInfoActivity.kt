package com.DIAPERS.diaper_project

import android.os.*
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.DIAPERS.diaper_project.Class.GetOne
import kotlinx.android.synthetic.main.activity_org_info.*
import kotlinx.android.synthetic.main.view_loader.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrgInfoActivity :  BasicActivity() {

    // var jsonObject: JSONObject? = null //기관정보를 받아올거임 여기에
    //var check: Boolean = false  //서버로부터 데이터 가져왔는지 판별하는 변수

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_org_info)
        thread_start()  //스레드 발생시켜서 서버로부터 데이터 가져올동안 로딩화면 보여줌. 데이터 다 가져오면 로딩화면 지워줌
    }

    //서버로부터 데이터 가져오는 작업
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

                        chief.text = "chief:  "+response.body()!!.result.chief
                        fax.text = "fax:  "+response.body()!!.result.fax
                        location.text = "location:  "+response.body()!!.result.location
                        naming.text = "name:  "+response.body()!!.result.name
                        phone.text = "phone:  "+response.body()!!.result.phone
                        //check = true
                        loaderLayout.visibility = View.GONE
                    } else {
                        Log.e(
                            "태그",
                            "기관정보get실패:" + response.body().toString() + "   errorbody: " + response.errorBody()
                        )
                    }
                }
            })
    } //init


    private fun thread_start(){
        loaderLayout.visibility = View.VISIBLE  //로딩화면보여줌
        Log.e("로딩태그","로딩화면보여줌")
        var thread = Thread(null, getData()) //스레드 생성후 스레드에서 작업할 함수 지정(getDATA)
        thread.start()
        Log.e("로딩태그","thread_start시작됨.")
    }

    fun getData() = Runnable {
        kotlin.run {
            try {
                //원하는 자료처리(데이터 로딩 등)
                init()
                Log.e("로딩태그","getData성공. 데이터 가져옴")
                //자료처리 완료후 핸들러의 post 사용해서 이벤트 던짐

                handler()
                Log.e("로딩태그","핸들러 통해서 메인ui의 로딩화면 Gone함")
            }catch (e:Exception){
                Log.e("로딩태그","getData실패")
            }
        }
    }

    //데이터 가져오는 postUpdate작업 다 끝나면 로딩화면 제거하는 작업해주는 핸들러 함수
    private fun handler(){
        var handler = object:Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                //loaderLayout.visibility = View.GONE  //로딩화면 끔끔
            }
        }
        handler.obtainMessage().sendToTarget()
    }




    override fun onStart() {
        super.onStart()
        /*

        //아직 서버로부터 데이터를 못받아왔을때는 로딩화면을 보여줌
        if(!check) {
            loaderLayout.visibility = View.VISIBLE
        }

         */
    }


    override fun onResume() {
        super.onResume()

        /*
        // 데이터가 서버로부터 왔는지 감시해줌. 데이터 들어왔으면  만들어줌
        if(!check){
            // for(i in 1..10) {
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴")
                if (check) {
                    loaderLayout.visibility = View.GONE
                    Log.e("태그", " (jsonObject != null)  구문 들어옴")
                }
            }, 1000)  //2초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
            // }
        }

        //다른 화면 갔다가 여기 왔을때 데이터작업 완료되었으면 로딩화면 없애줌
        if(check){
           loaderLayout.visibility = View.GONE
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(check){
                loaderLayout.visibility = View.GONE
            }
        }

         */

    }




}
