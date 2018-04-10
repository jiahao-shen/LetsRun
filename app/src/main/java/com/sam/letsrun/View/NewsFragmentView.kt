package com.sam.letsrun.View

import com.sam.letsrun.Model.News

/**
 * 新闻类View
 */
interface NewsFragmentView {

    fun loadSuccess(newsList: ArrayList<News>)

    fun loadError()
}