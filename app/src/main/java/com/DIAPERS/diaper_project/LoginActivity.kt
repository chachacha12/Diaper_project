package com.DIAPERS.diaper_project

//로그인하는 창

//신규회원 가입 화면임. 여기서 사용자가 친 이메일, 비밀번호를 받아서 가입 가능한지 판별?

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.DIAPERS.diaper_project.Class.Users
import com.DIAPERS.diaper_project.Class.currentUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.view_loader.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity :  BasicActivity() {

    lateinit var currentuser: currentUser

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    fun init() {

        checkbutton.setOnClickListener {
            login()
        }
    }

    fun login()   // 회원가입하려는 신규 사용자가 입력한 닉네임과 비밀번호를 가져와서 신규 가입되는지 확인하고 가입시키는 메소드?
    {
        var username = username_editText.text.toString()
        var password = password_editText.text.toString()

        if (username.length > 0 && password.length > 0) {
            loaderLayout.visibility = View.VISIBLE    //로딩화면 보여줌.  (view_loader 액티비티를 보여주어서)

            //사용자 auth 관련 post기능-로그인
            var loginusers = Users(username, password)
            server.loginRequest(loginusers).enqueue(object : Callback<currentUser> {
                override fun onFailure(call: Call<currentUser>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "서버와 통신 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    loaderLayout.visibility = View.GONE         //로딩화면끔
                }
                override fun onResponse(call: Call<currentUser>, response: Response<currentUser>) {
                    if(response.isSuccessful){

                        currentuser = currentUser(response.body()?.username.toString(),response.body()?.access_token!!)

                        //앱껏다가 켜도 로그아웃한거 아니면 자동 로그인 기능 줄것임
                        val sp = getSharedPreferences("UserTokenKey", Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putString("TokenCode", currentuser.access_token) // 이 부분이 실질적인 값을 저장하는 부분이다.
                        editor.putString("name", currentuser.username) // 이 부분이 실질적인 값을 저장하는 부분이다.
                        editor.commit() // 여기서 커밋을 안해주면 저장이 안된다.

                        var i = Intent(this@LoginActivity, MainActivity::class.java)    //회원가입 성공하면 바로 메인액티비티로 이동
                        i.putExtra("current", currentuser)  //현재로그인한 유저정보를 다른 액티비티에 넘김. name은 키값.
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                        Toast.makeText(this@LoginActivity,currentuser.username +"님이 로그인 하셨습니다.", Toast.LENGTH_SHORT).show()

                    }else{
                        loaderLayout.visibility = View.GONE         //로딩화면끔
                        Toast.makeText(this@LoginActivity, "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        loaderLayout.visibility = View.GONE         //로딩화면끔
                    }
                }
            })
        } else {   //아무것도 안친경우..
            Toast.makeText(this, "빈칸없이 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }  //login 함수

}