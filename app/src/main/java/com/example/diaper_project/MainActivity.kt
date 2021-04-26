package com.example.diaper_project
//로그인해서 들어왔을때 창임. 여기서 로그아웃 가능


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Adapter.MainAdapter
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.currentUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_loader.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

//mainActivity에 있는 전역변수들은 다른 액티비티에서도 접근가능!!
var cnt_name =  ArrayList<String>()  //모든 이용자 이름을 저장해둔 리스트. StatisticActivity에서 spinner만들어줄때 쓰려고.
var cnt_ids =  ArrayList<String>()  //모든 이용자 id를 저장해둔 리스트. StatisticActivity에서 spinner만들어줄때 쓰려고.

//전역으로 해둔 이유는 여러함수 안에서 불러와서 쓰고 싶기에. 등등
lateinit var mainAdapter: MainAdapter
var jsonarray: JSONArray? = null //여기안엔 모든 이용자들(cnt)정보가 들어감
var currentuser: currentUser? = null //현재 로그인되어있는 회원정보.
lateinit var sp:SharedPreferences
var server_access_success:Boolean = true  //처음에 앱 킬때 서버에서 값가져오기 실패했을때 다시 postUpdate()를 실행해주기 위한 변수

class MainActivity : BasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        postUpdate()
    }

    fun init() {
        //툴바 만들기
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)  //기본 제목을 없애줍니다.
        //actionBar.setDisplayHomeAsUpEnabled(true) // 자동으로 뒤로가기 버튼을 툴바에 만들어줌

        currentuser =
            intent.getSerializableExtra("current") as currentUser?  //로그인창에서 로그인해서 여기로 왔을때, 유저정보를 여기서 받음.
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

            //var recyclerView = findViewById<RecyclerView>(R.id.recyclerView)  //화면에 보일 리사이클러뷰객체
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        //새 게시글 만들기위해 floatingActionButton눌렀을때
        floatingActionButton.setOnClickListener {
            var i = Intent(this, CntAddActivity::class.java)
            startActivityForResult(i, 100)
        }
    }  //init

    //이용자 추가하기 액티비티(cntAddactivity)에 갔다오면서 받은 데이터에 따른 동작처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            100 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        jsonarray =
                            null  //다시 초기화 시켜줌. 이렇게 해야 onStart와 onResume함수 통해 값 갱신(이용자 추가)되어서 보여짐
                        postUpdate()
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                }
            }
        }
    }

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

                //통계 액티비티로 cnt값들의 name과 id값들을 각각 리스트로 담아서 보내줄거임. 그래서 spinner에서 사용자가 특정 이용자 선택하면 name과 id리스트의 같은 인덱스에 매칭시켜서 찾아주면됨
                if (jsonarray != null) {  //서버로부터 cnt값 모두 조회성공해서 가져왔다면
                    var i = Intent(this, StatisticActivity::class.java)   //회원가입창 화면으로 이동
                    i.putExtra(
                        "cnt_name",
                        cnt_name
                    )      //만약 내가 클래스 통해 만든 객체들을 putExtra로 보내려면 보내려는 객체 클래스(PostInfo)에 : Serializable 해줘야함. 여기선 객체아니라 ㄱㅊ
                    i.putExtra("cnt_ids", cnt_ids)
                    startActivity(i)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "데이터 가져오는 중이므로 잠시 기다려 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.account -> {                    //계정정보버튼을 선택했을시
                var i = Intent(this, UserinfoActivity::class.java)   //회원가입창 화면으로 이동
                startActivity(i)
            }
            R.id.cnt_info -> {                    //이용자정보버튼을 선택했을시
                if (jsonarray != null) {  //서버로부터 cnt값 모두 조회성공해서 가져왔다면
                    var i = Intent(this, CntinfoActivity::class.java)   //이용자 정보화면으로 이동

                    i.putExtra(
                        "jsonarray",
                        jsonarray.toString()
                    )      //jsonarray을 인텐트에 실어서 넘겨주고 싶을땐, 우선 String으로 형변환 시켜줘서 보내고 받는 쪽에서 다시 새로 jsonArray객체를 생성해주면 된다!
                    startActivity(i)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "데이터 가져오는 중이므로 잠시 기다려 주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        //아직 서버로부터 데이터를 못받아왔을때는 로딩화면을 보여줌
        if (jsonarray == null) {
            textView_clickorder2.visibility = View.VISIBLE
            loaderLayout.visibility = View.VISIBLE
        }
    }

    //액티비티가 재실행되거나 홈버튼 눌러서 나갔다왔을때 등의 경우에 onCreate말고 이 함수가 실행됨. (이때마다 게시글들 새로고침 해주면될듯)
    //앱 처음 실행시엔 onCreate와 onResume함수가 둘다 실행되므로 중복되는 코드는 쓰지 않기
    override fun onResume() {
        super.onResume()

        // 데이터가 서버로부터 왔는지 감시해줌. 데이터 들어왔으면  만들어줌
        if (jsonarray == null) {
            // for(i in 1..10) {
            Handler().postDelayed({
                Log.e("태그", " Handler().postDelayed 구문 들어옴")
                if (jsonarray != null) {
                    textView_clickorder2.visibility = View.INVISIBLE
                    loaderLayout.visibility = View.GONE
                    Log.e("태그", " (jsonarray != null)  구문 들어옴")
                }
            }, 4000)  //5초가 지났을때 {}괄호안의 내용을 수행하게되는 명령임.
            // }
        }


        //다른 화면 갔다가 여기 왔을때 데이터작업 완료되었으면 로딩화면 없애줌
        if (jsonarray != null) {
            //recyclerView.adapter = mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
            loaderLayout.visibility = View.GONE
            textView_clickorder2.visibility = View.INVISIBLE
        }

        //화면 클릭했을때 동작완료되었다면 그래프띄워주기 위함
        loaderLayout.setOnClickListener {
            if (jsonarray != null) {
                //recyclerView.adapter = mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용
                loaderLayout.visibility = View.GONE
                textView_clickorder2.visibility = View.INVISIBLE
            }
            if (server_access_success == false) {  //처음 앱켰을때 서버접근 실패했을때를 대비해서 다시한번 서버에 요청해줄 작업
                postUpdate()
            }
        }
    } //onResume

    // 삭제하거나 수정하거나 만들거나 등등 했을때 다 지웠다가 다시 바뀐 jsonarray를 서버로부터 받아와서 화면에 업데이트 시켜줄거임
    private fun postUpdate() {
        if (currentuser != null) {

            //통계액티비티로 이동할때 보내주는 리스트들을 다시 초기화 해주고 밑에서 새로 값 채워줌. 그래야 통계 액티비티가서도(스피너 등) 수정된 내용들이 들어가니까.
            cnt_name.clear()
            cnt_ids.clear()
            //cnt값(이용자) 모두 조회
            server.getAllRequest("Bearer " + currentuser?.access_token)
                .enqueue(object : Callback<GetAll> {
                    override fun onFailure(
                        call: Call<GetAll>,
                        t: Throwable
                    ) {
                        server_access_success = false  //이 전역변수를 변경해줌으로 다시한번 요청해줄거임
                        Log.e("태그", "통신 아예 실패")
                        Toast.makeText(
                            this@MainActivity,
                            "서버와 통신 실패하였습니다. 화면을 터치해서 데이터를 다시 가져와주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                        if (response.isSuccessful) {
                            server_access_success = true
                            jsonarray = JSONArray(response.body()?.result)  //어댑터에 넘겨줄 값임

                            //리사이클러뷰를 여기서 제대로 만들어줌.
                            mainAdapter = MainAdapter(
                                this@MainActivity,
                                jsonarray!!, server
                            )
                            recyclerView.adapter =
                                mainAdapter    //리사이클러뷰의 어댑터에 내가 만든 어댑터 붙힘. 사용자가 게시글 지우거나 수정 등 해서 데이터 바뀌면 어댑터를 다른걸로 또 바꿔줘야함 ->notifyDataSetChanged()이용

                            //if(cnt_name.size==0){ //이 조건문 안해주면, 앱 나갔다 들어왔을때 통계 액티비티에서 스피너에 이용자 이름 두배로 계속 늘어나는 오류 생김. (앱 나갔다오면 다시 main이 onCreate되어서 이미 값 들어간 전역변수에 또 값들어와서 그러는듯)
                            //통계 액티비티에 보내줄 리스트를 여기서 만들어줌
                            var i = 0
                            repeat(jsonarray!!.length()) {
                                val iObject = jsonarray!!.getJSONObject(i)
                                cnt_name.add(iObject.getString("name"))
                                cnt_ids.add(iObject.get("id").toString())
                                i++
                            }
                            Log.e("태그", " cnt name:" + cnt_name + ",  id:" + cnt_ids)
                            //}
                        } else {
                            server_access_success = false  //이 전역변수를 변경해줌으로 다시한번 요청해줄거임
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

}
