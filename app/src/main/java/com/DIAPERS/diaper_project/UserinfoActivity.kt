package com.DIAPERS.diaper_project

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.DIAPERS.diaper_project.Adapter.Userinfo_Adapter
import com.DIAPERS.diaper_project.Class.GetAll
import com.DIAPERS.diaper_project.Class.success

import kotlinx.android.synthetic.main.activity_userinfo.*
import kotlinx.android.synthetic.main.view_loader.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserinfoActivity : BasicActivity() {
    //전역으로 해둔 이유는 여러함수 안에서 불러와서 쓰고 싶기에. 등등
    lateinit var userinfoAdapter: Userinfo_Adapter
    var jsonarray: JSONArray? = null //여기안엔 모든 사용자들(user)정보가 들어감
    var Userid_Array = ArrayList<String>()  //사용자 도큐먼트의 id값들을 저장하는 리스트 (사용자 삭제로직때 필요해서)
    var level:Double =0.0 //사용자계정들을 삭제할 수 있는지 권한레벨을 확인해줄때를 위해

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        init()
        thread_start()
    }

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
                UserUpdate()
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
        if(jsonarray ==null ) {
            //textView_clickorder2.visibility = View.VISIBLE
            loaderLayout.visibility = View.VISIBLE
        }

         */
    }

    override fun onResume() {
        super.onResume()

        /*
        // 데이터가 서버로부터 왔는지 감시해줌. 데이터 들어왔으면  만들어줌
        if(jsonarray ==null){
            // for(i in 1..10) {
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴")
                if (jsonarray != null) {
                    //textView_clickorder2.visibility = View.INVISIBLE
                    loaderLayout.visibility = View.GONE
                    Log.e("태그", " (jsonarray != null)  구문 들어옴")
                }
            }, 2000)  //3초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
            // }
        }

        //다른 화면 갔다가 여기 왔을때 데이터작업 완료되었으면 로딩화면 없애줌
        if(jsonarray !=null ){
            //recyclerView.adapter = mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
            loaderLayout.visibility = View.GONE
            //textView_clickorder2.visibility = View.INVISIBLE
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if(jsonarray !=null ){
                //recyclerView.adapter = mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
                loaderLayout.visibility = View.GONE
               // textView_clickorder2.visibility = View.INVISIBLE
            }
        }

         */

    }

    fun init(){
        //recyclerView = recyclerView_user  //화면에 보일 리사이클러뷰객체
        recyclerView_user.setHasFixedSize(true)
        recyclerView_user.layoutManager = LinearLayoutManager(this)
        name.text = currentuser?.username.toString()  //현재 접속한 계정의 아이디 값을 적어줌
    }

    // 삭제하거나 수정하거나 만들거나 등등 했을때 다 지웠다가 다시 바뀐 jsonarray를 서버로부터 받아와서 화면에 업데이트 시켜줄거임
    private fun  UserUpdate() {
        var user_id:String

        if (currentuser != null) {
            loaderLayout.visibility = View.VISIBLE  //로딩화면 보여줌


            //사용자user 관련 get기능-모두 조회
            server.getAllusers_Request("Bearer " + currentuser!!.access_token)
                .enqueue(object : Callback<GetAll> {
                    override fun onFailure(
                        call: Call<GetAll>,
                        t: Throwable
                    ) {
                        Log.e("태그", "통신 아예 실패")
                    }
                    override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                        if (response.isSuccessful) {
                            jsonarray = JSONArray(response.body()?.result)

                            var i=0
                            repeat(jsonarray!!.length()) {
                                val iObject = jsonarray!!.getJSONObject(i)
                                user_id = iObject.get("id").toString()  //사용자의 id값 가져옴. 이를 통해 로그삭제, 수정 해줄거임
                                Userid_Array.add(user_id)  //사용자 도큐먼트의 id값을 저장(삭제로직때 필요함)
                                if(currentuser!!.username == iObject.get("username").toString()){
                                    level =  iObject.get("level") as Double     //현재 계정의 레벨값 알아냄(계정들 삭제때 필요함함)
                               }
                                i++
                            }

                            //리사이클러뷰를 여기서 제대로 만들어줌.
                            userinfoAdapter = Userinfo_Adapter(
                                this@UserinfoActivity, jsonarray!!, onUserListener, level
                            )   //cnt_name리스트도 어댑터에 보내줘서 이용자 이름을 채워주도록 할거임. 그 후 Statistic액티비티에서 spinner만들때 쓸거.
                            recyclerView_user.adapter = userinfoAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
                            loaderLayout.visibility = View.GONE  //로딩화면 끔끔
                        } else {
                            Log.e(
                                "태그",
                                "모두조회 실패:" + response.body().toString() + "   errorbody: " + response.errorBody()
                            )
                        }
                    }
                })

        }
    }

    //사용자가 실시간으로 게시글 삭제, 수정할때에 맞춰서 리스트 업데이트 해줄거임
    //인터페이스 객체를 어댑터말고 액티비티에 구현해둬야하는 이유는 onResume함수 등이 있어서 게시글 업데이트를 해줄수 있어서?
    //인터페이스를 구현한 익명객체를 생성해서 사용할거임. 그리고 이걸 어댑터에 인자로 넣어주면 어댑터에서도 사용가능.
    val onUserListener = object : OnUserListener {
        override fun onDelete(position: Int) {
            var id = Userid_Array.get(position)  //특정위치의 사용자 id값

            //사용자삭제 로직
            server.deleteUserRequest("Bearer " + currentuser?.access_token, id)
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                        Log.e("태그: ", "서버 통신 아예 실패")
                        Toast.makeText(this@UserinfoActivity, "서버 통신실패",Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            //thread_start()

                            UserUpdate()  //다시 리사이클러뷰 어댑터 붙이는 작업 등을 통해 화면 갱신해줌

                            Handler().postDelayed({
                                if (jsonarray != null) {
                                    loaderLayout.visibility = View.GONE
                                }
                            }, 2000)  //2초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.

                            Toast.makeText(this@UserinfoActivity, "사용자를 삭제하였습니다.",Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("태그   사용자 삭제실패: ", response.body()?.succeed.toString())
                            Toast.makeText(this@UserinfoActivity, "사용자를 삭제하지 못하였습니다.",Toast.LENGTH_SHORT).show()

                        }
                    }
                })
        }
    }






}
