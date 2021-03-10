package com.example.diaper_project.Adapter

//viewpager2.adapter에 붙힐 어댑터임.

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.diaper_project.StatisticActivity
import com.example.diaper_project.averageFragment
import com.example.diaper_project.graphFragment

class MyFragStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {  //몇개의 아이템(프래그먼트?)들을 제공해 줄건지 정하기
        return 2
    }

    override fun createFragment(position: Int): Fragment {   //페이지마다 어떤 프래그먼트를 줄지 정하기
        return when(position){
            0-> graphFragment()
            1-> averageFragment()
            else -> graphFragment()
        }
    }


}

