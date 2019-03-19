package com.sam.letsrun.Presenter

import android.content.Context
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.RegexUtils
import com.google.gson.Gson
import com.mob.MobSDK
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Model.CheckPhoneResponse
import com.sam.letsrun.View.RegisterFragmentView
import com.sam.runapp.Net.RetrofitUtils
import org.jetbrains.anko.runOnUiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by sam on 2018/3/17.
 */
class RegisterFragmentPresenter {

    lateinit var mView: RegisterFragmentView

    /**
     * 发送短信
     * @param telephone
     */
    fun sendMessage(telephone: String) {
        when {
            telephone == "" -> {
                mView.telephoneNotNull()
            }
            !RegexUtils.isMobileExact(telephone) -> {
                mView.telephoneNotValid()
            }
            else -> {
                checkPhone(telephone)
            }
        }
    }

    /**
     * 检查验证码
     * @param telephone
     * @param code
     */
    fun checkCode(telephone: String, code: String) = when {
        !NetworkUtils.isConnected() -> {
            mView.netError()
        }
        telephone == "" -> {
            mView.telephoneNotNull()
        }
        !RegexUtils.isMobileExact(telephone) -> {
            mView.telephoneNotValid()
        }
        code == "" -> {
            mView.codeNotNull()
        }
        else -> {
            mView.codeCheckSuccess()
//            SMSSDK.submitVerificationCode("86", telephone, code)
        }
    }

    /**
     * 初始化短信服务
     */
    fun initMessage(context: Context) {
        MobSDK.init(context)
        SMSSDK.registerEventHandler(object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, Object: Any?) {
                when (event) {
                    SMSSDK.EVENT_GET_VERIFICATION_CODE -> {
                        when (result) {
                            SMSSDK.RESULT_COMPLETE -> {
                                context.runOnUiThread {
                                    mView.codeSendSuccess()
                                }
                            }
                            SMSSDK.RESULT_ERROR -> {
                                context.runOnUiThread {
                                    mView.codeSendError()
                                }
                            }
                        }
                    }
                    SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE -> {
                        when (result) {
                            SMSSDK.RESULT_COMPLETE -> {
                                context.runOnUiThread {
                                    mView.codeCheckSuccess()
                                }
                            }
                            SMSSDK.RESULT_ERROR -> {
                                context.runOnUiThread {
                                    mView.codeCheckError()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    /**
     * 检查短信是否已经被注册
     * @param telephone
     */
    private fun checkPhone(telephone: String) {
        if (!NetworkUtils.isConnected()) {
            mView.netError()
            return
        }
        val service = RetrofitUtils.getService()
        val request = hashMapOf("msg" to Const.CHECK_PHONE, "telephone" to telephone)
        service.checkPhone(Gson().toJson(request))
                .enqueue(object : Callback<CheckPhoneResponse> {
                    override fun onFailure(call: Call<CheckPhoneResponse>?, t: Throwable?) {
                        mView.unKnownError()
                    }

                    override fun onResponse(call: Call<CheckPhoneResponse>?, response: Response<CheckPhoneResponse>?) {
                        if (response == null) {
                            mView.unKnownError()
                            return
                        }
                        val result = response.body() as CheckPhoneResponse
                        Logger.json(Gson().toJson(result))
                        when (result.msg) {
                            Const.TELEPHONE_NOT_EXIST -> {
//                                SMSSDK.getVerificationCode("86", telephone)
                            }
                            Const.TELEPHONE_ALREADY_EXIST -> {
                                mView.telephoneAlreadyExist()
                            }
                            Const.UNKNOWN_ERROR -> {
                                mView.unKnownError()
                            }
                        }
                    }
                })
    }

}