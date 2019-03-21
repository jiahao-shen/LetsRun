package com.sam.letsrun.Presenter

import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.AdvertisementResponse
import com.sam.letsrun.Model.User
import com.sam.letsrun.View.AdvertisementView
import com.sam.runapp.Net.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 广告类界面presenter
 */
class AdvertisementPresenter {

    lateinit var mView: AdvertisementView

    /**
     * 请求用户信息
     * @param user
     * @param token
     */
    fun loadInformation(user: User, token: String) = when {
        !NetworkUtils.isConnected() -> {
            mView.netError()
        }
        //若本地token或者telephone一个为空,则返回失败
        user.telephone == "" || token == "" -> {
            mView.loadFailed()
        }
        else -> {
            val service = RetrofitUtils.getService()
            val request = hashMapOf("telephone" to user.telephone, "token" to token)
            //否则初始化用户信息
            service.loadInfo(Gson().toJson(request))
                    .enqueue(object : Callback<AdvertisementResponse> {
                        override fun onFailure(call: Call<AdvertisementResponse>?, t: Throwable?) {
                            mView.loadFailed()
                        }

                        override fun onResponse(call: Call<AdvertisementResponse>?, response: Response<AdvertisementResponse>?) {
                            if (response == null) {
                                mView.loadFailed()
                                return
                            }
                            val myResponse = response.body() as AdvertisementResponse
                            when (myResponse.msg) {
                                //失败
                                Const.REQUEST_FAILED -> mView.loadFailed()
                                //成功
                                Const.REQUEST_SUCCESS -> {
                                    mView.loadSuccess(myResponse.user)
                                }
                            }
                        }
                    })
        }
    }
}