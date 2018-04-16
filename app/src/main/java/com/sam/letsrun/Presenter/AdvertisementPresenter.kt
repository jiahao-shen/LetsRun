package com.sam.letsrun.Presenter

import com.google.gson.Gson
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.AdvertisementResponse
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
     * @param telephone
     * @param token
     */
    fun loadInformation(telephone: String, token: String) {
        //若本地token或者telephone一个为空,则返回失败
        if (telephone == "" || token == "")
            mView.loadFailed()
        else {
            val service = RetrofitUtils.getService()
//            val request = AdvertisementRequest(telephone, token)
            val request = hashMapOf("telephone" to telephone, "token" to token)
            //否则初始化用户信息
            service.loadInfo(Gson().toJson(request))
                    .enqueue(object : Callback<AdvertisementResponse> {
                        override fun onFailure(call: Call<AdvertisementResponse>?, t: Throwable?) {
                            //失败
                            mView.loadFailed()
                        }

                        override fun onResponse(call: Call<AdvertisementResponse>?, response: Response<AdvertisementResponse>) {
                            val myResponse = response.body() as AdvertisementResponse
                            when (myResponse.msg) {
                                //失败
                                Const.REQUEST_FAILED -> mView.loadFailed()

                                //成功
                                Const.REQUEST_SUCCESS -> {
                                    mView.loadSuccess(myResponse.user!!)
                                }
                            }
                        }
                    })
        }
    }
}