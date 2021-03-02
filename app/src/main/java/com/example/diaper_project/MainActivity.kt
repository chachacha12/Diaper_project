package com.example.diaper_project
//로그인해서 들어왔을때 창임. 여기서 로그아웃 가능


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Adapter.MainAdapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.currentUser
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

lateinit var mainAdapter: MainAdapter
lateinit var jsonarray: JSONArray //여기안엔 모든 이용자들(cnt)정보가 들어감
var currentuser: currentUser? = null //현재 로그인한 회원정보

class MainActivity : BasicActivity() {

    //전역으로 해둔 이유는 여러함수 안에서 불러와서 쓰고 싶기에. 등등



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {

        currentuser =
            intent.getSerializableExtra("current") as currentUser?  //현재 로그인한 유저정보를 로그인액티비티에서 받음.


        if (currentuser == null)   //만약 현재 유저가 null이면... (즉, 로그인이 아직 안되어있다는 뜻)
        {
            var i = Intent(this, SignUpActivity::class.java)   //회원가입창 화면으로 이동
            startActivity(i)
            //이렇게 하는 이유는 이 앱의 첫 실행화면을 메인액티비티로 해두어서임. 그 이유는 메인액티비티에서 뒤로가기 했을때 로그인창 같은게 나오면 보기 안좋으니까, 바로 앱이 꺼질수 있게 하기위함
        } else {
            //회원가입or로그인 했을시  (원래 여기에 파이어베이스의 인증 프로필 업데이트를 썻다가 그거 안쓰고 데이터베이스(클라우드firestore) 쓰기로 해서 지우고 이거씀

            var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)  //화면에 보일 리사이클러뷰객체
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)

            //postUpdate()
        }




        /*
        logoutbutton.setOnClickListener {  //로그아웃버튼 눌렀을때
            Firebase.auth.signOut()  //현재 로그인된 계정이 로그아웃됨
            var i = Intent(this, SignUpActivity::class.java)   //회원가입창 화면으로 이동
            startActivity(i)
        }


        //새 게시글 만들기위해 floatingActionButton눌렀을때
        floatingActionButton.setOnClickListener {
            //var i = Intent(this, WritePostActivity::class.java)
           // startActivity(i)
        }
         */


    }  //init


    //액티비티가 재실행되거나 홈버튼 눌러서 나갔다왔을때 등의 경우에 onCreate말고 이 함수가 실행됨. (이때마다 게시글들 새로고침 해주면될듯)
    //앱 처음 실행시엔 onCreate와 onResume함수가 둘다 실행되므로 중복되는 코드는 쓰지 않기
    override fun onResume() {
        super.onResume()
        postUpdate()
    } //onResume


    //이용자 정보 cardVIew를 삭제하거나 수정하거나 만들거나 등등 했을때 다 지웠다가 다시 바뀐 jsonarray를 서버로부터 받아와서 화면에 업데이트 시켜줄거임
    private fun postUpdate() {
        if (currentuser != null) {

            server.getAllRequest("Bearer " + currentuser?.access_token)
                .enqueue(object : Callback<GetAll> {
                    override fun onFailure(
                        call: Call<GetAll>,
                        t: Throwable
                    ) {
                        Log.e("태그", "통신 아예 실패")
                      //  Toast.makeText(this@MainActivity, "서버와 통신 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                        if (response.isSuccessful) {
                             jsonarray = JSONArray(response.body()?.result.toString())  //어댑터에 넘겨줄 값임

                            //리사이클러뷰를 여기서 제대로 만들어줌.
                            mainAdapter = MainAdapter(
                                this@MainActivity,
                                jsonarray
                            )
                            recyclerView.adapter =
                                mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용

                            Log.e("태그", "모두조회 response.body()내용:" + response.body().toString())
                        } else {
                            Log.e(
                                "태그",
                                "모두조회 실패:" + response.body().toString() + "errorbody: " + response.errorBody()
                            )
                           Toast.makeText(this@MainActivity, "서버에 접근했지만 올바르지 않은 데이터를 받았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }else{
            Log.e("태그", "현재유저값이null임")
        }
    }


}
