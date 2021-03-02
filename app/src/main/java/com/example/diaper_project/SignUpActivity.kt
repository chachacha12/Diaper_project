package com.example.diaper_project

//신규회원 가입 화면임.

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diaper_project.Class.Users
import com.example.diaper_project.Class.currentUser
import com.example.diaper_project.Class.success
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.view_loader.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignUpActivity :  BasicActivity() {

    lateinit var currentuser: currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        checkbutton.setOnClickListener {
            signup()
        }

        gotoLoginButton.setOnClickListener {         //로그인버튼 누르면 로그인액티비티로 이동
            var i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }



   override fun onBackPressed() {     // 이 액티비티에서 뒤로가기버튼을 눌럿을때 처리 - 이거안해주면 메인에서 로그아웃해서 나왓는데 뒤로가기 눌르면 다시 메인창으로 가는 오류 생겨서. 앱 그냥 꺼지게 하기
        super.onBackPressed()
       moveTaskToBack(true) // 태스크를 백그라운드로 이동
       android.os.Process.killProcess(android.os.Process.myPid()) // 앱 프로세스 종료
       System.exit(1)
    }

    fun signup()   // 회원가입하려는 신규 사용자가 입력한 이메일과 비밀번호를 가져와서 신규 가입되는지 확인하고 가입시키는 메소드?
    {
        var username = username_editText.text.toString()
        var password = password_editText.text.toString()
        var realname = realname_editText.text.toString()
        var description = description_editText.text.toString()


        if(username.length > 0 && password.length >0 && realname.length > 0 && description.length >0) {
            loaderLayout.visibility = View.VISIBLE    //로딩화면 보여줌.
            var users = Users(username, password , realname, description)

            //사용자 auth 관련 post기능-등록
            server.postResquest(users).enqueue(object : Callback<success> {
                override fun onFailure(call: Call<success>, t: Throwable) {
                    loaderLayout.visibility = View.GONE         //로딩화면끔
                    Toast.makeText(this@SignUpActivity, "서버와 통신 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<success>, response: Response<success>) {
                    Log.e("성공",response.body().toString())
                    Toast.makeText(this@SignUpActivity, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()

                    var i = Intent(this@SignUpActivity, MainActivity::class.java)    //회원가입 성공하면 바로 메인액티비티로 이동
                    i.putExtra("current", currentuser)  //현재로그인한 유저정보를 메인액티비티에 넘김. name은 키값.
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
            })
        }else{   //아무것도 안친경우..
            Toast.makeText(this, "빈칸없이 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }  //signup 함수






}