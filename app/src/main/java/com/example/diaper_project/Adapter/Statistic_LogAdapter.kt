package com.example.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.graphics.Color
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.diaper_project.OnLogListener
import com.example.diaper_project.R
import com.example.diaper_project.StatisticActivity
import kotlinx.android.synthetic.main.cnt_post.view.*
import kotlinx.android.synthetic.main.item_log.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
                                    //괄호안은 어댑터클래스의 인자들
class Statistic_LogAdapter(var activity: Activity, private var myDataset: ArrayList<String>)    //인자로 onPostListener라는 인터페이스 객체를 준 이유는 어댑터안에서도 인터페이스의 onDelete, onModify 함수를 쓰기위해.
                                        : RecyclerView.Adapter<Statistic_LogAdapter.MainViewHolder>() {

    //뷰홀더에 텍스트뷰말고 카드뷰를 넣음
    class MainViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(    //레이아웃 item_post에 있는 카드뷰를 가리키는 뷰홀더를 만듬. 이건 처음에 액티비티에서 recyclerView.adapter = mainAdapter 할때만 작동하고 그후엔 안함.
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        val cardView: CardView = LayoutInflater.from(parent.context).inflate(R.layout.item_log , parent, false) as CardView   //item_Log에 있는 뷰들에 접근가능하게 해줌.  inflate에 들어간 레이아웃은 row파일과 같은거임.

        val mainViewHolder = MainViewHolder(cardView)  //밑의 setOnClickListener에서 사용자가 선택한 특정뷰의 위치값 알아야해서 여기서 뷰홀더객체생성

        //특정 게시글을 눌렀을때 효과
        cardView.setOnClickListener {
        }

        /*
        //게시글의 toolbar(점3개)버튼을 클릭했을때 효과
        cardView.menu.setOnClickListener {
            showPopup(it, mainViewHolder.adapterPosition)      //post.xml을 띄워줌. 밑에 있음. 구글에 android menu검색하고 developers사이트들어가서 코드 가져옴
        }
                                                         //mainViewHolder.adapterPosition을 넣어주는 이유는 사용자가 선택한 특정위치의 게시글을 삭제or수정해야 하기에.
         */
        Log.e("태그", "onCreateViewHolder 돌아감")
        return mainViewHolder
    }


    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
     //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        var cardView = holder.cardView
        var textView = cardView.time_textView
        textView!!.text = "aaaaaa"
        Log.e("태그", "onBindViewHolder 돌아감")

        /*
        var cardView = holder.cardView
        var titletextView = cardView.titletextView
        titletextView.text = myDataset?.get(position).title        //게시글의 제목을 가져옴

        var createdAt = cardView.createdAttextView  //게시글의 생성일을 가져옴
        createdAt.text = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(myDataset?.get(position).createdAt)

        var contentsList = myDataset?.get(position).contents   //게시글 내용인 데이터들
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var contentsLayout = cardView.contentsLayout  //여기안에 contentsList의 내용들(사진,영상,글) 등을 넣을거임

         */

    }


    override fun getItemCount() = myDataset!!.size

     /*
   //res안에 menu디렉토리 만든거에서, 그 안의 menu파일을 불러와서 toolbar보여주고, 클릭했을때 이벤트처리해줌  //developers사이트에서 가져온 함수.
    private fun showPopup(v: View, position: Int) {
        val popup = PopupMenu(StatisticActivity(), v)
        popup.setOnMenuItemClickListener {

            return@setOnMenuItemClickListener when (it.itemId) {
                R.id.modify -> {                    //수정하기 눌렀을때
                    onLogListener.onModify(position)      //게시글의 postList상에서의 위치를 인자를 통해 액티비티에 전달함. 그 후 액티비티에서 삭제로직을 통해 게시글 db, 스토리지에서 삭제.->어댑터에서 삭제로직 안하는 이유는 여기선 db접근해서 삭제는 할수있어도 실시간으로 업데이트는 못해줘서임. OnResume()함수 등이 액티비티에 존재.
                     true
                }
                R.id.delete -> {                  //삭제하기 눌렀을때
                    onLogListener.onDelete(position)
                    true
                }
                else -> false
            }
        }
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.log, popup.menu)
        popup.show()
    }

      */

}