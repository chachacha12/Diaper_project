package com.example.diaper_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Adapter.MainAdapter
import com.example.diaper_project.Adapter.Userinfo_Adapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.log
import com.example.diaper_project.Class.success
import com.example.diaper_project.databinding.ActivityMainBinding
import com.example.diaper_project.databinding.ActivityUserinfoBinding
import kotlinx.android.synthetic.main.activity_main.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        init()
        UserUpdate()
    }

    override fun onStart() {
        super.onStart()

        //아직 서버로부터 데이터를 못받아왔을때는 로딩화면을 보여줌
        if(jsonarray ==null ) {
            //textView_clickorder2.visibility = View.VISIBLE
            loaderLayout.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

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

                            //리사이클러뷰를 여기서 제대로 만들어줌.
                            userinfoAdapter = Userinfo_Adapter(
                                this@UserinfoActivity, jsonarray!!, onUserListener
                            )   //cnt_name리스트도 어댑터에 보내줘서 이용자 이름을 채워주도록 할거임. 그 후 Statistic액티비티에서 spinner만들때 쓸거.
                            recyclerView_user.adapter = userinfoAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용

                            var i=0
                            repeat(jsonarray!!.length()) {
                                val iObject = jsonarray!!.getJSONObject(i)
                                user_id = iObject.get("id").toString()  //로그의 id값 가져옴. 이를 통해 로그삭제, 수정 해줄거임
                                Userid_Array.add(user_id)  //사용자 도큐먼트의 id값을 저장(삭제로직때 필요함)
                                i++
                            }
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
