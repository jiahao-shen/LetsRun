package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.LoginResponse
import com.sam.letsrun.Presenter.LoginPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.LoginView
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 登录
 * TODO("添加背景")
 */
class LoginActivity : AppCompatActivity(), LoginView {

    private var presenter = LoginPresenter()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Utils.init(application)     //工具类初始化

        presenter.mView = this      //view一定不要忘记初始化

        initPermission()
        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        loginButton.setOnClickListener {
            val telephone = telephoneText.text.toString()       //获取手机
            val password = passwordText.text.toString()     //获取密码
            presenter.login(telephone, password)        //登录
        }

        registerButton.setOnClickListener {
            startActivity<RegisterActivity>()            //跳转至注册界面
        }

    }

    /**
     * 初始化权限
     * 获取存储和定位权限
     */
    private fun initPermission() {
        AndPermission.with(application)
                .permission(
                        Permission.Group.STORAGE,
                        Permission.Group.LOCATION
                )
                .start()
    }

    /**
     * 登录成功
     * @param loginResponse
     */
    override fun loginSuccess(loginResponse: LoginResponse) {
        toast("登录成功")
        sharedPreferencesEditor.putString("token", loginResponse.token)             //保存token和user信息
                .putString("user", Gson().toJson(loginResponse.user))
                .commit()
        startActivity<MainActivity>()       //跳转至主界面
    }

    override fun loginFailed() {
        toast("登录失败,未知错误")
    }

    override fun telephoneNotExist() {
        toast("手机号不存在")
    }

    override fun passwordError() {
        toast("密码错误")
    }

    override fun alreadyLogin() {
        toast("切勿重复登录")
    }

    override fun netError() {
        toast("网络异常,请检查")
    }

    override fun telephoneNotNull() {
        toast("手机号不能为空")
    }

    override fun telephoneNotValid() {
        toast("请输入正确的手机号")
    }

    override fun passwordNotNull() {
        toast("密码不能为空")
    }

}
