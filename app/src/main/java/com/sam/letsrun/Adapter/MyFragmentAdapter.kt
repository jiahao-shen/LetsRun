package com.sam.letsrun.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 自定义fragment适配器
 */
class MyFragmentAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentPagerAdapter(fm){

    private var fragmentList = fragments

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}