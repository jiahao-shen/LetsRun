package com.sam.letsrun.Custom

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 自定义viewPager
 * 新增加了禁止滑动属性
 */
class MyViewPager : ViewPager {

    private var isScroll = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {}

    fun setScroll(i: Boolean) {
        this.isScroll = i
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isScroll && super.onInterceptTouchEvent(ev)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isScroll && super.onTouchEvent(ev)
    }

}