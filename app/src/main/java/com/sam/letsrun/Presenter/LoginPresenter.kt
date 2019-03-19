package com.sam.letsrun.Presenter

import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.RegexUtils
import com.google.gson.Gson
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.Model.LoginResponse
import com.sam.letsrun.View.LoginView
import com.sam.runapp.Net.RetrofitUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 登录界面presenter
 */
class LoginPresenter {

    lateinit var mView: LoginView

    /**
     * 登录
     * @param telephone
     * @param password
     */
    fun login(telephone: String, password: String) = when {
        !NetworkUtils.isConnected() -> {
            mView.netError()
        }
        telephone == "" -> {
            mView.telephoneNotNull()
        }
        !RegexUtils.isMobileExact(telephone) -> {
            mView.telephoneNotValid()
        }
        password == "" -> {
            mView.passwordNotNull()
        }
        else -> {
            val service = RetrofitUtils.getService()
            val request = hashMapOf("telephone" to telephone, "password" to EncryptUtils.encryptMD5ToString(password))
            service.login(Gson().toJson(request))
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                            mView.netError()
                        }
                        override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                            if (response == null) {
                                mView.loginFailed()
                                return
                            }
                            val loginResponse = response.body() as LoginResponse
                            when (loginResponse.msg) {
                                Const.LOGIN_SUCCESS -> {
                                    mView.loginSuccess(loginResponse)
                                }
                                Const.TELEPHONE_NOT_EXIST -> mView.telephoneNotExist()

                                Const.PASSWORD_ERROR -> mView.passwordError()

                                Const.UNKNOWN_ERROR -> mView.loginFailed()

                                Const.ALREADY_LOGIN -> mView.alreadyLogin()
                            }
                        }
                    })
        }
    }

}

