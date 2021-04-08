package com.example.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.*
import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.log
import com.example.diaper_project.Class.success
import kotlinx.android.synthetic.main.cnt_post.view.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

     //cnt_name을 인자를 통해 어댑터로 보내준 이유는 어댑터에서 cnt정보를 서버에 접근할때 이 리스트안에 이용자들 이름 넣으면 나중에 통계 액티비티에서 서버에 접근 또 안해도 되어서임.
class MainAdapter(var activity: Activity, private var myDataset: JSONArray, var server:HowlService, var cnt_name:ArrayList<String>)  //myDataset은 cnt정보, server는 어댑터에는 없으므로 여기서 받아와서 접근해줌
 : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {


    //뷰홀더에 텍스트뷰말고 카드뷰를 넣음
    class MainViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    //처음에 리사이클러뷰 만들어질때 한번만 동작하고 싶은 작업들 여기에 해주기. onbindViewHolder에서는 앱켜져도 스크롤 내릴때 계속 여러번 작업 되는듯?
    override fun onCreateViewHolder(    //레이아웃 cnt_post에 있는 카드뷰를 가리키는 뷰홀더를 만듬. 이건 처음에 액티비티에서 recyclerView.adapter = mainAdapter 할때만 작동하고 그후엔 안함.
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val cardView: CardView = LayoutInflater.from(parent.context).inflate(
            R.layout.cnt_post,
            parent,
            false
        ) as CardView   //item_post에 있는 뷰들에 접근가능하게 해줌.  inflate에 들어간 레이아웃은 row파일과 같은거임.

        val mainViewHolder = MainViewHolder(cardView)  //밑의 setOnClickListener에서 사용자가 선택한 특정뷰의 위치값 알아야해서 여기서 뷰홀더객체생성

        //
        return mainViewHolder
    }


    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
    //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        //기저귀 수량을 카운트하는 변수들
         var outer_open_number=0
         var outer_new_number=0
         var inner_open_number=0
         var inner_new_number =0

        var cardView = holder.cardView
        var name = cardView.nametextView

        val iObject = myDataset.getJSONObject(position)  //이용자 객체(cnt) 하나씩 순서대로 가져옴
        name.text = iObject?.getString("name")      //이용자 이름을 가져옴

        /*
        if(cnt_name.size <= position){
            cnt_name.add(name.text.toString())                   //StatisticActivity에서 spinner만들어줄때 쓰려고.
        }
         */

        //UI상에 이용자들 각각 기저귀 수량 log값과 최신 생성일을 서버로부터 받아와서 띄워줄거임.
        var cnt_id = iObject.get("id").toString()  //cnt도큐먼트의 id값을 가져옴


        //이용자들의 가장 최신 log값들을 페이지네이션으로 하나씩만 가져와줌
        server.getLogListRequest("Bearer " + currentuser?.access_token, cnt_id, 0, 1)
            .enqueue(object : Callback<GetAll> {
                override fun onFailure(
                    call: Call<GetAll>,
                    t: Throwable
                ) {  //object로 받아옴. 서버에서 받은 object모델과 맞지 않으면 실패함수로 빠짐
                    Log.e("태그", "통신 아예 실패")
                }
                override fun onResponse(call: Call<GetAll>, response: Response<GetAll>) {
                    if (response.isSuccessful) {
                        var parser:SimpleDateFormat
                        var formatter:SimpleDateFormat
                        var output:String

                        //로그값이 하나도 파베에 없을때 (이용자 막 추가했을때 등등,,)
                        if(response.body()?.result.toString() == "[]"){
                            cardView.textView_outer_open.text = "개봉: " + outer_open_number
                            cardView.textView_outer_new.text ="미개봉: " + outer_new_number
                            cardView.textView_inner_open.text ="개봉: " + inner_open_number
                            cardView.textView_inner_new.text ="미개봉: " +  inner_new_number
                            cardView.timeTextView.text ="마지막 저장일: <저장된 값 없음>"
                        }else{
                            //로그값이 하나라도 파베에 있을때
                            var jsonArray = JSONArray(response.body()?.result)
                            val Object = jsonArray.getJSONObject(0)

                            outer_open_number = Object.getInt("outer_opened")
                            outer_new_number=Object.getInt("outer_new")
                            inner_open_number=Object.getInt("inner_opened")
                            inner_new_number= Object.getInt("inner_new")
                            //화면상의 뷰들에 log조회로 받아온 값들 넣어줌(기저귀 수량, 생성일)
                            cardView.textView_outer_open.text = "개봉: " + outer_open_number
                            cardView.textView_outer_new.text ="미개봉: " + outer_new_number
                            cardView.textView_inner_open.text ="개봉: " + inner_open_number
                            cardView.textView_inner_new.text ="미개봉: " +  inner_new_number

                            //가져온 날짜값을 다른 패턴으로 변환해서 그래프의 x축에 띄워줄거임
                            parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            formatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm")
                            output = formatter.format(parser.parse(Object.getString("time")))

                            cardView.timeTextView.text ="마지막 저장일: "+output


                        }
                    } else {
                        Log.e("태그", "전체 로그 조회실패" + response.body().toString())
                    }
                }
            })

        cardView.button_plus_outer_open.setOnClickListener {
            cardView.textView_outer_open.text = "개봉: " + (++outer_open_number)
        }
        cardView.button_minus_outer_open.setOnClickListener {
            cardView.textView_outer_open.text = "개봉: " + (--outer_open_number)
        }
        cardView.button_plus_outer_new.setOnClickListener {
            cardView.textView_outer_new.text = "미개봉: " + (++outer_new_number)
        }
        cardView.button_minus_outer_new.setOnClickListener {
            cardView.textView_outer_new.text = "미개봉: " + (--outer_new_number)
        }
        cardView.button_plus_inner_open.setOnClickListener {
            cardView.textView_inner_open.text = "개봉: " + (++inner_open_number)
        }
        cardView.button_minu_inner_open.setOnClickListener {
            cardView.textView_inner_open.text = "개봉: " + (--inner_open_number)
        }
        cardView.button_plus_inner_new.setOnClickListener {
            cardView.textView_inner_new.text = "미개봉: " + (++inner_new_number)
        }
        cardView.button_minus_inner_new.setOnClickListener {
            cardView.textView_inner_new.text = "미개봉: " + (--inner_new_number)
        }

        //저장버튼 클릭시 (생성일 즉각 바꿔주고, 서버에 로그값 추가해주기)
        cardView.button_save.setOnClickListener {
            //저장 눌렀을때 일단 화면상에서 생성일을 변경해준다. db에 처리는 따로 해야함
            //즉각 생성일을 만들어서 화면에 띄우기
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date = Date()
            val createdAt: String = simpleDateFormat.format(date)
            cardView.timeTextView.text = "마지막 저장일: " + createdAt

            //서버통해 파베에 log값 저장하기
            var log =log(cnt_id,createdAt, inner_open_number, inner_new_number, outer_open_number, outer_new_number,"코멘트없음", currentuser.toString(),"")
            server.addlogResquest("Bearer " + currentuser?.access_token, log)
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                        Log.e("태그: ", "통신 아예 실패")
                    }
                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            Log.e("태그   성공: ", response.body()?.succeed.toString())
                            Toast.makeText(activity, "저장 성공", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("태그   실패: ", response.body()?.succeed.toString()+",  createdAt:"+createdAt)
                            Toast.makeText(activity, "저장 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } //button_save

    } //onbindViewHolder

    override fun getItemCount() = myDataset.length()
}