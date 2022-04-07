package com.DIAPERS.diaper_project

import android.app.Activity
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.DIAPERS.diaper_project.Class.Cnt
import com.DIAPERS.diaper_project.Class.success
import kotlinx.android.synthetic.main.activity_cnt_add.*
import kotlinx.android.synthetic.main.fragment_graph.*
import kotlinx.android.synthetic.main.view_loader.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CntAddActivity  :  BasicActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cnt_add)
        init()
    }

    fun init(){
        //이용자 추가해주는 작업하기
        button_Add.setOnClickListener {
            thread_start()
        } //button_Add

        //다시 메인으로 돌아가기
        button_Cancel.setOnClickListener {
            Toast.makeText(this, "취소하였습니다.",Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish() //액티비티 끝내기
        }
    } //init

    //이용자 추가로직
    fun AddCnt(){
        val name = nameEditText.text.toString()
        val year = birthEditText.text.toString()
        val month = birthEditText2.text.toString()
        val day = birthEditText3.text.toString()
        val outer_product = outer_productEditText.text.toString()
        val inner_product = inner_productEditText.text.toString()
        val description = description_EditText.text.toString()

        //빈칸없이 모두 채웠을경우
        if(name.length>0 && year.length>0 && month.length>0 && day.length>0 && outer_product.length>0 && inner_product.length>0 && description.length>0 ){

            //서버를 통해 이용자를 파이어베이스에 추가함
            var cnt = Cnt(inner_product, false, description, name, outer_product, "$year-$month-$day")
            server.addCntResquest("Bearer " + currentuser?.access_token, cnt)
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                        Toast.makeText(this@CntAddActivity, "서버 통신 실패",Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_CANCELED)  //메인 액티비티로 실패했다고 보내줄거임(메인에선 onActivityResult함수가 받음)
                        finish() //액티비티 끝내기
                    }

                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            Log.e("태그   성공: ", response.body()?.succeed.toString())
                            Toast.makeText(this@CntAddActivity, name+"님을 추가하였습니다.",Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK) //성공했다는 표시 메인으로 보내줌. 메인에선 postUpdate()함수를 써서 다시 리사이클러뷰를 갱신해줄거임
                            finish() //액티비티 끝내기
                        } else {
                            Log.e("태그   실패: ", response.body()?.succeed.toString())
                            Toast.makeText(this@CntAddActivity, name+"님 추가 실패",Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_CANCELED)
                            finish() //액티비티 끝내기
                        }
                    }
                })
        }else{
            Toast.makeText(this, "빈칸없이 채워주세요.",Toast.LENGTH_SHORT).show()
        }
    }



    private fun thread_start() {
        var thread = Thread(null, getData()) //스레드 생성후 스레드에서 작업할 함수 지정(getDATA)
        thread.start()
        Log.e("태그", "thread_start시작됨.")
    }

    fun getData() = Runnable {
        kotlin.run {
            try {
                //원하는 자료처리(데이터 로딩 등)
                AddCnt()   //이용자 추가
            } catch (e: Exception) {
                // Log.e("태그", "getRelevant_Contents_Request 실패 ")
            }
        }
    }

    //데이터 등록시키는 작업 다 끝났을때(성공하거나 실패했을때) 이 함수 호출해서 로딩화면 제거하는 등의 작업해주는 핸들러 함수
    private fun handler() {
        var handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
            }
        }
        handler.obtainMessage().sendToTarget()
    }


}
