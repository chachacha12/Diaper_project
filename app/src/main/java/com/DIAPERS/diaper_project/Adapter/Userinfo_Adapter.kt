package com.DIAPERS.diaper_project.Adapter

//GalleryAdapter클래스를 복사해서 좀 바꿔서 써준 어댑터임

import android.app.Activity
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.DIAPERS.diaper_project.*
import com.DIAPERS.diaper_project.databinding.ActivityUserinfoBinding
import com.DIAPERS.diaper_project.databinding.ItemUserBinding
import kotlinx.android.synthetic.main.item_log.view.*
import org.json.JSONArray

//activity는 getActivity()인거임. 즉 프래그먼트의 부모 액티비티를 가져와줌.(StatisticActivity)
class Userinfo_Adapter(var activity: Activity, private var myDataset: JSONArray, var onUserListener: OnUserListener, var level:Double)    //인자로 OnUserListener 인터페이스 객체를 준 이유는 어댑터안에서도 인터페이스의 onDelete 함수를 쓰기위해.
                                        : RecyclerView.Adapter<Userinfo_Adapter.MainViewHolder>() {                    //인자로 준 level은, 사용자가 특정계정 삭제버튼 눌렀을때 로직이 이 어댑터에서 일어나기에 이 level값으로 권한있는지 없는지 판별해줌

    //뷰홀더에 텍스트뷰말고 카드뷰를 넣음
    class MainViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(    //레이아웃 item_post에 있는 카드뷰를 가리키는 뷰홀더를 만듬. 이건 처음에 액티비티에서 recyclerView.adapter = mainAdapter 할때만 작동하고 그후엔 안함.
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {

        //뷰바인딩
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val mainViewHolder = MainViewHolder(binding)  //밑의 setOnClickListener에서 사용자가 선택한 특정뷰의 위치값 알아야해서 여기서 뷰홀더객체생성


        //게시글의 toolbar(점3개)버튼을 클릭했을때 효과
        binding.cardView.menu.setOnClickListener {
            showPopup(it, mainViewHolder.adapterPosition)      //post.xml을 띄워줌. 밑에 있음. 구글에 android menu검색하고 developers사이트들어가서 코드 가져옴
        }


                                                         //mainViewHolder.adapterPosition을 넣어주는 이유는 사용자가 선택한 특정위치의 게시글을 삭제or수정해야 하기에.
        Log.e("태그", "onCreateViewHolder 돌아감")
        return mainViewHolder
    }


    // 여기서 리사이클러뷰의 리스트 하나하나 가리키는 뷰홀더와 내가 주는 데이터(게시글)가 연결되어짐. 즉 리사이클러뷰 화면에 띄워짐
     //액티비티에서 게시글 업데이트 해주려고 mainAdapter.notifyDataSetChanged() 하면 이 함수만 작동함.
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        Log.e("태그","onBindViewHolder돌아감")

        with(holder){
            val iObject = myDataset.getJSONObject(position)  //사용자 객체(user) 하나씩 순서대로 가져옴
            binding.username.text = iObject?.getString("realname")      //사용자 이름을 가져옴
            binding.userid.text = iObject?.getString("username")
            binding.userdescription.text = iObject?.getString("description")
            binding.userlevel.text = iObject?.getString("level")
        }

    }


    override fun getItemCount() = myDataset!!.length()

   //res안에 menu디렉토리 만든거에서, 그 안의 menu파일을 불러와서 toolbar보여주고, 클릭했을때 이벤트처리해줌  //developers사이트에서 가져온 함수.
    private fun showPopup(v: View, position: Int) {
       val popup = PopupMenu(activity, v)
       popup.setOnMenuItemClickListener {

           return@setOnMenuItemClickListener when (it.itemId) {
               R.id.delete -> {                  //삭제하기 눌렀을때
                   if(level>=2) {  //2이상일때
                       onUserListener.onDelete(position)
                   }else{        //권한레벨이 2이상인 사용자만 다른 계정 삭제가 가능함
                       Toast.makeText(activity, "접근 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                   }
                   true
               }
               else -> false
           }
       }
       val inflater: MenuInflater = popup.menuInflater
       inflater.inflate(R.menu.user, popup.menu)
       popup.show()
   }




}