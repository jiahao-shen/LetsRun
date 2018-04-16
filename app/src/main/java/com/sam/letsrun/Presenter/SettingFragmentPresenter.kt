package com.sam.letsrun.Presenter

import com.google.gson.Gson
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.LogoutResponse
import com.sam.letsrun.View.SettingFragmentView
import com.sam.runapp.Net.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 设置presenter
 */
class SettingFragmentPresenter {

    lateinit var mView: SettingFragmentView

    /**
     * 退出登录
     * @param telephone
     * @param token
     */
    fun logout(telephone: String, token: String) {
        val service = RetrofitUtils.getService()
        val request = hashMapOf("telephone" to telephone, "token" to token)
        service.logout(Gson().toJson(request))
                .enqueue(object : Callback<LogoutResponse> {
                    override fun onFailure(call: Call<LogoutResponse>?, t: Throwable?) {
                        mView.logoutFailed()
                    }

                    override fun onResponse(call: Call<LogoutResponse>?, response: Response<LogoutResponse>?) {
                        if (response != null) {
                            val logoutResponse = response.body() as LogoutResponse
                            when (logoutResponse.msg) {
                                Const.LOGOUT_SUCCESS -> mView.logoutSuccess()

                                Const.LOGOUT_FAILED -> mView.logoutFailed()

                                Const.UNKNOWN_ERROR -> mView.unKnownError()
                            }
                        } else {
                            mView.unKnownError()
                        }

                    }
                })
    }
}