package com.example.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.graphics.Color
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.R
import kotlinx.android.synthetic.main.cnt_post.view.*
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

//괄호안은 어댑터클래스의 인자들
class MainAdapter(var activity: Activity, private var myDataset: JSONArray)
 : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    //뷰홀더에 텍스트뷰말고 카드뷰를 넣음
    class MainViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(    //레이아웃 item_post에 있는 카드뷰를 가리키는 뷰홀더를 만듬. 이건 처음에 액티비티에서 recyclerView.adapter = mainAdapter 할때만 작동하고 그후엔 안함.
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val cardView: CardView = LayoutInflater.from(parent.context).inflate(R.layout.cnt_post, parent, false) as CardView   //item_post에 있는 뷰들에 접근가능하게 해줌.  inflate에 들어간 레이아웃은 row파일과 같은거임.

        val mainViewHolder = MainViewHolder(cardView)  //밑의 setOnClickListener에서 사용자가 선택한 특정뷰의 위치값 알아야해서 여기서 뷰홀더객체생성

        //특정 게시글을 눌렀을때 효과
        cardView.setOnClickListener {
        }

        /*
        //게시글의 toolbar(점3개)버튼을 클릭했을때 효과
        cardView.menu.setOnClickListener {
            showPopup(it, mainViewHolder.adapterPosition)      //post.xml을 띄워줌. 밑에 있음. 구글에 android menu검색하고 developers사이트들어가서 코드 가져옴
        }                                                     //mainViewHolder.adapterPosition을 넣어주는 이유는 사용자가 선택한 특정위치의 게시글을 삭제or수정해야 하기에.
         */
        return mainViewHolder
    }


    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
     //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {



        var cardView = holder.cardView
        var name = cardView.nametextView
        val iObject = myDataset.getJSONObject(position)  //이용자 객체 하나씩 순서대로 가져옴

        name.text =iObject?.getString("name")      //이용자 이름을 가져옴

    }

    override fun getItemCount() = myDataset.length()

}