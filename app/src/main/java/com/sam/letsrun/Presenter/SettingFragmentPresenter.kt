package com.sam.letsrun.Presenter

import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.LogoutResponse
import com.sam.letsrun.Model.UpdateUserInfoResponse
import com.sam.letsrun.Model.User
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
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()
        val request = hashMapOf("telephone" to telephone, "token" to token)
        service.logout(Gson().toJson(request))
                .enqueue(object : Callback<LogoutResponse> {
                    override fun onFailure(call: Call<LogoutResponse>?, t: Throwable?) {
                        mView.logoutFailed()
                    }

                    override fun onResponse(call: Call<LogoutResponse>?, response: Response<LogoutResponse>?) {
                        if (response == null) {
                            mView.logoutFailed()
                            return
                        }
                        val logoutResponse = response.body() as LogoutResponse
                        when (logoutResponse.msg) {
                            Const.LOGOUT_SUCCESS -> mView.logoutSuccess()

                            Const.LOGOUT_FAILED -> mView.logoutFailed()

                            Const.UNKNOWN_ERROR -> mView.unKnownError()
                        }
                    }
                })
    }

    fun updateUserInfo(user: User, token: String) {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()
        val request = hashMapOf("token" to token, "user" to user)
        service.updateUserInfo(Gson().toJson(request))
                .enqueue(object : Callback<UpdateUserInfoResponse> {
                    override fun onFailure(call: Call<UpdateUserInfoResponse>, t: Throwable) {
                        mView.updateUserInfoFailed()
                    }

                    override fun onResponse(call: Call<UpdateUserInfoResponse>, response: Response<UpdateUserInfoResponse>?) {
                        if (response == null) {
                            mView.updateUserInfoFailed()
                            return
                        }
                        val updateUserInfoResponse = response.body() as UpdateUserInfoResponse
                        when (updateUserInfoResponse.msg) {
                            Const.UNKNOWN_ERROR -> mView.unKnownError()

                            Const.UPDATE_INFO_FAILED -> mView.updateUserInfoFailed()

                            Const.UPDATE_INFO_SUCCESS -> mView.updateUserInfoSuccess(user)
                        }
                    }
                })

    }
}