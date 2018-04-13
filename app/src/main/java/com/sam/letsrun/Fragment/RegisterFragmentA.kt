package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand
import com.orhanobut.logger.Logger
import com.sam.letsrun.Presenter.RegisterFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterFragmentView
import com.sam.letsrun.View.RegisterView
import kotlinx.android.synthetic.main.fragment_register_a.*
import org.jetbrains.anko.support.v4.toast
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 注册界面A
 * 填写并验证手机号
 */
class RegisterFragmentA : Fragment(), RegisterFragmentView {

    lateinit var mView: RegisterView
    var presenter = RegisterFragmentPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMessage()
        presenter.mView = this
        messageButton.setOnClickListener {
            val telephone = telephoneText.text.toString()
            presenter.sendMessage(telephone)
        }
        nextButton.setOnClickListener {
            val telephone = telephoneText.text.toString()
            val code = codeText.text.toString()
            presenter.checkCode(telephone, code)
        }
    }


    override fun codeSendSuccess() {
        toast("验证码已发送")
        //倒计时期间禁止发送
        messageButton.isEnabled = false
        messageButton.bootstrapBrand = DefaultBootstrapBrand.INFO
        val count = 30
        Observable.interval(1, TimeUnit.SECONDS)        //短信发送按钮倒计时
                .take(count + 1)
                .map{ aLong ->
                    count - aLong
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Long>() {
                    @SuppressLint("SetTextI18n")
                    override fun onNext(t: Long?) {
                        //更新button的文本
                        messageButton.text = "${t}秒"
                    }

                    override fun onCompleted() {
                        //倒计时结束后更新文本并按钮重置
                        messageButton.text = "再次发送"
                        messageButton.isEnabled = true
                        messageButton.bootstrapBrand = DefaultBootstrapBrand.PRIMARY
                    }

                    override fun onError(e: Throwable?) {
                        Logger.i(e.toString())
                    }
                })

    }

    override fun codeSendError() {
        toast("未知错误,请稍后再试")
    }

    override fun codeCheckSuccess() {
        toast("验证成功")
        mView.initTelephone(telephoneText.text.toString())
        mView.nextFragment()
    }

    override fun codeCheckError() {
        toast("验证失败")
    }

    override fun netError() {
        toast("网络异常,请检查")
    }

    override fun telephoneAlreadyExist() {
        toast("该手机已被注册,请更换")
    }

    override fun unKnownError() {
        toast("未知错误,请稍候再试")
    }


    /**
     * 初始化短信服务
     */
    private fun initMessage() {
        activity?.let { presenter.initMessage(it) }
    }

    override fun telephoneNotNull() {
        toast("手机号不能为空")
    }

    override fun telephoneNotValid() {
        toast("请输入正确的手机号")
    }

    override fun codeNotNull() {
        toast("验证码不能为空")
    }
}
