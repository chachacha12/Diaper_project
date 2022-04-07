package com.DIAPERS.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.DIAPERS.diaper_project.Class.log
import com.DIAPERS.diaper_project.OnLogListener
import com.DIAPERS.diaper_project.R
import kotlinx.android.synthetic.main.fragment_graph.view.*
import kotlinx.android.synthetic.main.item_log.view.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

//activity는 getActivity()인거임. 즉 프래그먼트의 부모 액티비티를 가져와줌.(StatisticActivity)
class Statistic_LogAdapter(var activity: Activity, private var myDataset: ArrayList<log>, var onLogListener: OnLogListener)    //인자로 onPostListener라는 인터페이스 객체를 준 이유는 어댑터안에서도 인터페이스의 onDelete, onModify 함수를 쓰기위해.
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
        //게시글의 toolbar(점3개)버튼을 클릭했을때 효과
        cardView.menu.setOnClickListener {
            showPopup(it, mainViewHolder.adapterPosition)      //post.xml을 띄워줌. 밑에 있음. 구글에 android menu검색하고 developers사이트들어가서 코드 가져옴
        }
                                                         //mainViewHolder.adapterPosition을 넣어주는 이유는 사용자가 선택한 특정위치의 게시글을 삭제or수정해야 하기에.
        Log.e("태그", "onCreateViewHolder 돌아감")
        return mainViewHolder
    }

     //간단한 날짜로 변경해주려고
     lateinit var parser:SimpleDateFormat
     lateinit var formatter:SimpleDateFormat
     lateinit var output:String

    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
     //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        Log.e("태그","onBindViewHolder돌아감")

        var cardView = holder.cardView

        //가져온 날짜값을 다른 패턴으로 변환해줌
        parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        formatter = SimpleDateFormat("MM/dd HH:mm")
        output = formatter.format(parser.parse( myDataset.get(position).time))
        //로그 작성 시간
        var time_textView = cardView.time_textView
        time_textView!!.text =output
        //겉(개봉)
        var outer_textView = cardView.outer_textView3
        outer_textView.text = myDataset.get(position).outer_opened.toString()
        //겉(미개봉)
        var outer_new_textView = cardView.outer_new_textView3
        outer_new_textView.text = myDataset.get(position).outer_new.toString()
        //속(개봉)
        var inner_textView = cardView.inner_textView3
        inner_textView.text = myDataset.get(position).inner_opened.toString()
        //속(미개봉)
        var inner_new_textView = cardView.inner_new_textView3
        inner_new_textView.text = myDataset.get(position).inner_new.toString()
        //작성자
        var created_by_textView = cardView.created_by_textView3
        created_by_textView.text = myDataset.get(position).created_by
        //수정자
        var modified_by_textView = cardView.modified_by_textView3
        modified_by_textView.text = myDataset.get(position).modified_by

        var comment_textView = cardView.comment_textView
        comment_textView.text = myDataset.get(position).comment

    }


    override fun getItemCount() = myDataset!!.size


   //res안에 menu디렉토리 만든거에서, 그 안의 menu파일을 불러와서 toolbar보여주고, 클릭했을때 이벤트처리해줌  //developers사이트에서 가져온 함수.
    private fun showPopup(v: View, position: Int) {
        val popup = PopupMenu(activity, v)
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


}