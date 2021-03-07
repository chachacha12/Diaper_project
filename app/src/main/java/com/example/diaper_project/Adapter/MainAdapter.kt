package com.example.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.util.Log
import android.view.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.Class.success
import com.example.diaper_project.HowlService
import com.example.diaper_project.R
import com.example.diaper_project.currentuser
import kotlinx.android.synthetic.main.cnt_post.view.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*




class MainAdapter(var activity: Activity, private var myDataset: JSONArray, var server:HowlService)  //myDataset은 cnt정보, server는 어댑터에는 없으므로 여기서 받아와서 접근해줌
 : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    //뷰홀더에 텍스트뷰말고 카드뷰를 넣음
    class MainViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(    //레이아웃 cnt_post에 있는 카드뷰를 가리키는 뷰홀더를 만듬. 이건 처음에 액티비티에서 recyclerView.adapter = mainAdapter 할때만 작동하고 그후엔 안함.
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val cardView: CardView = LayoutInflater.from(parent.context).inflate(R.layout.cnt_post, parent, false) as CardView   //item_post에 있는 뷰들에 접근가능하게 해줌.  inflate에 들어간 레이아웃은 row파일과 같은거임.

        val mainViewHolder = MainViewHolder(cardView)  //밑의 setOnClickListener에서 사용자가 선택한 특정뷰의 위치값 알아야해서 여기서 뷰홀더객체생성

        //특정 게시글을 눌렀을때 효과
        cardView.setOnClickListener {
        }

        return mainViewHolder
    }


    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
     //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        //기저귀 수량을 카운트하는 전역변수들
        var outer_open_number=0
        var outer_new_number=0
        var inner_open_number=0
        var inner_new_number=0

        var cardView = holder.cardView
        var name = cardView.nametextView
        val iObject = myDataset.getJSONObject(position)  //이용자 객체 하나씩 순서대로 가져옴
        name.text =iObject?.getString("name")      //이용자 이름을 가져옴


        cardView.button_plus_outer_open.setOnClickListener {
            cardView.textView_outer_open.text = "개봉: "+(++outer_open_number)
        }
        cardView.button_minus_outer_open.setOnClickListener {
            cardView.textView_outer_open.text = "개봉: "+(--outer_open_number)
        }
        cardView.button_plus_outer_new.setOnClickListener {
            cardView.textView_outer_new.text = "미개봉: "+(++outer_new_number)
        }
        cardView.button_minus_outer_new.setOnClickListener {
            cardView.textView_outer_new.text = "미개봉: "+(--outer_new_number)
        }
        cardView.button_plus_inner_open.setOnClickListener {
            cardView.textView_inner_open.text = "개봉: "+(++inner_open_number)
        }
        cardView.button_minu_inner_open.setOnClickListener {
            cardView.textView_inner_open.text = "개봉: "+(--inner_open_number)
        }
        cardView.button_plus_inner_new.setOnClickListener {
            cardView.textView_inner_new.text = "미개봉: "+(++inner_new_number)
        }
        cardView.button_minus_inner_new.setOnClickListener {
            cardView.textView_inner_new.text = "미개봉: "+(--inner_new_number)
        }

        cardView.button_save.setOnClickListener {   //저장 눌렀을때 일단 화면상에서 생성일을 변경해준다. db에 처리는 따로 해야함
            //즉각 생성일을 만들어서 화면에 띄우기
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date = Date()
            val createdAt: String = simpleDateFormat.format(date)
            cardView.timeTextView.text=  "마지막 저장일: "+createdAt


            /*
            //서버통해 파베에 log 저장하기
            var log = Log("","")
            server.addlogResquest("Bearer " + currentuser?.access_token, log)
                .enqueue(object : Callback<success> {
                    override fun onFailure(call: Call<success>, t: Throwable) {
                        Log.e("태그: ", "통신 아예 실패")
                    }

                    override fun onResponse(call: Call<success>, response: Response<success>) {
                        if (response.isSuccessful) {
                            Log.e("태그   성공: ", response.body()?.succeed.toString())
                        } else {
                            Log.e("태그   실패: ", response.body()?.succeed.toString())
                        }
                    }
                })

             */
        } //button_save


    } //onbindViewHolder
    override fun getItemCount() = myDataset.length()

}