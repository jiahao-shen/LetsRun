package com.sam.letsrun.Presenter

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.Model.RegisterRequest
import com.sam.letsrun.Model.RegisterResponse
import com.sam.letsrun.Model.User
import com.sam.letsrun.View.RegisterView
import com.sam.runapp.Net.RetrofitUtils
import com.yalantis.ucrop.UCrop
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * 注册presenter
 */
class RegisterPresenter {

    lateinit var mView: RegisterView

    fun register(telephone: String, password: String, userName: String, gender: String, birthday: String?, blood: String?, height: Int?, weight: Int?) {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()

        val user = User(telephone, EncryptUtils.encryptMD5ToString(password), userName, null, null, birthday, gender, blood, height, weight)

        val request = RegisterRequest(Const.REGISTER, user)
        val file = File(Const.LOCAL_PATH, "$telephone.jpg")
        val fileBody = MultipartBody.Part.createFormData("img", file.name, RequestBody.create(Const.FILETYPE, file))
        service.register(GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(request), fileBody)
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onFailure(call: Call<RegisterResponse>?, t: Throwable?) {
                        mView.unKnownError()
                    }

                    override fun onResponse(call: Call<RegisterResponse>?, response: Response<RegisterResponse>?) {
                        if (response == null) {
                            mView.unKnownError()
                            return
                        }
                        val result = response.body() as RegisterResponse
                        when (result.msg) {
                            Const.REGISTER_SUCCESS -> {
                                result.token?.let { mView.registerSuccess(it) }
                            }
                            Const.UNKNOWN_ERROR -> {
                                mView.unKnownError()
                            }
                        }
                    }
                })
    }

}