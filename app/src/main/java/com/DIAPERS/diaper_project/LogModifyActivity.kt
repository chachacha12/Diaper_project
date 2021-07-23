package com.DIAPERS.diaper_project

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.DIAPERS.diaper_project.Class.log
import com.DIAPERS.diaper_project.Class.success
import kotlinx.android.synthetic.main.activity_log_modify.*
import kotlinx.android.synthetic.main.view_loader.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class LogModifyActivity :  BasicActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_modify)
        init()
    }



    fun init(){
        //GraphFragment에서 보내준 인테트를 여기서 받음. 그 인텐트 안에 있던 객체 데이터를 꺼냄
        val data = intent.getSerializableExtra("log") as  log //직렬화된 객체를 받고자하는 log객체로 형변환 시켜줘야함.

        //GraphFragment에서 가져온 로그객체는 현재 시간값이 yyyy-MM-dd'T'HH:mm:ss형태로 저장되어있는데, 로그를 추가하거나 수정(패치)하거나 할때는 yyyy-MM-dd HH:mm이 형식으로 넣어줘야함. 그래야 에러안남.
        var parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        var create_time = formatter.format(parser.parse(data.time)!!)

        timeTextView.text =create_time
        outer_open_editText.hint = data.outer_opened.toString()
        outer_new_editText.hint = data.outer_new.toString()
        inner_open_editTextView.hint = data.inner_opened.toString()
        inner_new_editTextView.hint = data.inner_new.toString()

        //취소버튼 클릭
        button_cancel.setOnClickListener {
            Toast.makeText(this,"취소하였습니다.",Toast.LENGTH_SHORT).show()
            finish()
        }
        //저장버튼 클릭
        button_save.setOnClickListener {

            var outer_open = outer_open_editText.text.toString()
            var outer_new = outer_new_editText.text.toString()
            var inner_open = inner_open_editTextView.text.toString()
            var inner_new = inner_new_editTextView.text.toString()

            if(outer_open.length >0 && outer_new.length >0 && inner_open.length >0 && inner_new.length >0){
                loaderLayout.visibility = View.VISIBLE    //로딩화면 보여줌.  (view_loader 액티비티를 보여주어서)
                //이용자log 정보수정 PATCH기능-log수정
                var log = log(data.cnt, create_time, inner_open.toInt(), inner_new.toInt(), outer_open.toInt(),outer_new.toInt(), "수정완료")
                server.modifiy_log("Bearer " + currentuser?.access_token, data.id!!, log)
                    .enqueue(object : Callback<success> {
                        override fun onFailure(call: Call<success>, t: Throwable) {
                            Toast.makeText(this@LogModifyActivity,"서버 접근 실패",Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<success>, response: Response<success>) {
                            Log.e("태그","data.id!!: "+data.id!!)
                            if (response.isSuccessful) {
                                Toast.makeText(this@LogModifyActivity,"저장되었습니다.",Toast.LENGTH_SHORT).show()
                                loaderLayout.visibility = View.GONE         //로딩화면끔
                                setResult(Activity.RESULT_OK)  //성공했다는 결과값을 GraphFragment에 보냄
                                finish()
                            } else {
                                Toast.makeText(this@LogModifyActivity,"저장에 실패하였습니다.",Toast.LENGTH_SHORT).show()
                                loaderLayout.visibility = View.GONE         //로딩화면끔
                            }
                        }
                    })
            }else{
                Toast.makeText(this,"빈칸없이 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
    } //init




}
