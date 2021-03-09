package com.example.diaper_project
//로그인해서 들어왔을때 창임. 여기서 로그아웃 가능


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Adapter.MainAdapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.currentUser
import com.example.diaper_project.Class.log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cnt_post.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

var cnt_name =  ArrayList<String>()  //StatisticActivity에서 spinner만들어줄때 쓰려고.

//전역으로 해둔 이유는 여러함수 안에서 불러와서 쓰고 싶기에. 등등
lateinit var mainAdapter: MainAdapter
lateinit var jsonarray: JSONArray //여기안엔 모든 이용자들(cnt)정보가 들어감
var currentuser: currentUser? = null //현재 로그인되어있는 회원정보
lateinit var sp:SharedPreferences

class MainActivity : BasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        save_log()
    }

     fun init() {
        //툴바 만들기
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)  //기본 제목을 없애줍니다.
        //actionBar.setDisplayHomeAsUpEnabled(true) // 자동으로 뒤로가기 버튼을 툴바에 만들어줌


        currentuser = intent.getSerializableExtra("current") as currentUser?  //로그인창에서 로그인해서 여기로 왔을때, 유저정보를 여기서 받음.
        //자동로그인으로 메인 왔을땐 밑에서 currentuser정보 받을거임.

        //SharedPreferences에 저장된 값들을 불러온다. (자동로그인 기능을 위해 주로 사용함)
        sp = getSharedPreferences("UserTokenKey", Context.MODE_PRIVATE)
        val token = sp.getString("TokenCode", "")!! // TokenCode키값에 해당하는 value값을 불러온다. 없다면 ""로 처리한다.
        val name = sp.getString("name", "")!!  //name키값에 해당하는 value값을 가져옴

        if (token == "")   //만약 SharedPreferences에 저장된 값이 없다면, 즉 로그인 안되어있을때.
        {
            var i = Intent(this, SignUpActivity::class.java)   //회원가입창 화면으로 이동
            startActivity(i)
            //이렇게 하는 이유는 이 앱의 첫 실행화면을 메인액티비티로 해두어서임. 그 이유는 나중에 자동로그인이 되어서 바로 메인부터 나오면, 메인액티비티에서 뒤로가기 했을때 로그인창 같은게 나오지 않기 때문에. 바로 앱이 꺼질수 있게 하기위함
        } else {          //회원가입or로그인 했을시or 자동로그인 되었을시

            if (currentuser == null) {           //자동로그인기능으로 들어온 경우
                currentuser = currentUser(name, token)
            }

            var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)  //화면에 보일 리사이클러뷰객체
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        //새 게시글 만들기위해 floatingActionButton눌렀을때
        floatingActionButton.setOnClickListener {
            var i = Intent(this, CntAddActivity::class.java)
            startActivity(i)
        }
    }  //init


    //툴바 버튼 눌렀을때 동작되는 함수 오버라이딩
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                //SharedPreferences에 있는 value값을 키값을 통해 접근해서 지워줌.
                val editor = sp.edit()
                editor.remove("TokenCode") // 키값을 통해 유저 access_token값을 삭제
                editor.remove("name")  //키값을 통해 uesrname값을 삭제
                editor.commit() // 여기서 커밋을 안해주면 저장이 안된다.

                var i = Intent(this, SignUpActivity::class.java)   //회원가입창 화면으로 이동
                startActivity(i)
                Toast.makeText(
                    this@MainActivity,
                    currentuser?.username + "님이 로그아웃 하셨습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.statistic -> {                    //통계 및 보고서를 선택했을시 (viewpager 이용해서 fragment여러개 해보는거 고려)
                var i = Intent(this, StatisticActivity::class.java)   //회원가입창 화면으로 이동
                i.putExtra("cnt_name", cnt_name)      //만약 내가 클래스 통해 만든 객체들을 putExtra로 보내려면 보내려는 객체 클래스(PostInfo)에 : Serializable 해줘야함. 여기선 객체아니라 ㄱㅊ
                startActivity(i)
            }
            R.id.account -> {                    //계정정보버튼을 선택했을시

            }

        }

        return super.onOptionsItemSelected(item)
    }


    //액티비티가 재실행되거나 홈버튼 눌러서 나갔다왔을때 등의 경우에 onCreate말고 이 함수가 실행됨. (이때마다 게시글들 새로고침 해주면될듯)
    //앱 처음 실행시엔 onCreate와 onResume함수가 둘다 실행되므로 중복되는 코드는 쓰지 않기
    override fun onResume() {
        super.onResume()
        postUpdate()
    } //onResume


    //이용자 정보 cardVIew를 삭제하거나 수정하거나 만들거나 등등 했을때 다 지웠다가 다시 바뀐 jsonarray를 서버로부터 받아와서 화면에 업데이트 시켜줄거임
    private fun postUpdate() {
        if (currentuser != null) {

            //cnt값(이용자) 모두 조회
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
                                jsonarray, server, cnt_name
                            )   //cnt_name리스트도 어댑터에 보내줘서 이용자 이름을 채워주도록 할거임. 그 후 Statistic액티비티에서 spinner만들때 쓸거.
                            recyclerView.adapter = mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용

                            Log.e("태그", "모두조회 response.body()내용:" + response.body().toString())
                        } else {
                            Log.e(
                                "태그",
                                "모두조회 실패:" + response.body().toString() + "errorbody: " + response.errorBody()
                            )
                            Toast.makeText(
                                this@MainActivity,
                                "서버에 접근했지만 올바르지 않은 데이터를 받았습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Log.e("태그", "현재유저값이null임")
        }
    }


    fun save_log(){  //저장하기 버튼 눌럿을때?

    }




}
