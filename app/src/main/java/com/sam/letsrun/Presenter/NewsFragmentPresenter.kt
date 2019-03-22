package com.sam.letsrun.Presenter

import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.News
import com.sam.letsrun.Model.NewsResponse
import com.sam.letsrun.View.NewsFragmentView
import com.sam.runapp.Net.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by sam on 2018/4/10.
 */
class NewsFragmentPresenter {

    lateinit var mView: NewsFragmentView

    fun loadNews() {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()
        service.loadNews()
                .enqueue(object : Callback<NewsResponse> {
                    override fun onFailure(call: Call<NewsResponse>?, t: Throwable?) {
                        mView.loadError()
                    }

                    override fun onResponse(call: Call<NewsResponse>?, response: Response<NewsResponse>?) {
                        if (response == null) {
                            mView.loadError()
                            return
                        }
                        val newsResponse = response.body() as NewsResponse
                        when (newsResponse.error_code) {
                            0 -> {
                                val newsList = newsResponse.result.data
                                for (item in newsList) {
                                    item.thumbnail_pic_s?.let { item.type = News.SINGLE }
                                    item.thumbnail_pic_s03?.let { item.type = News.MULTI }
                                }
                                mView.loadSuccess(newsList)
                            }
                            else -> {
                                mView.loadError()
                            }
                        }
                    }
                })
    }
}