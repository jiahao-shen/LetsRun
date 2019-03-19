package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.RadioButton
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Adapter.MyFragmentAdapter
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.Fragment.*
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.RegisterPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream

/**
 * 注册
 * TODO("给所有Fragment添加背景")
 */
class RegisterActivity : AppCompatActivity(), RegisterView {

    /**
     * 6个fragment
     * 1.手机号,短信验证码
     * 2.密码
     * 3.头像,用户名
     * 4.性别
     * 5.生日,血型
     * 6.身高,体重
     */
    private var fragmentA = RegisterFragmentA()
    private var fragmentB = RegisterFragmentB()
    private var fragmentC = RegisterFragmentC()
    private var fragmentD = RegisterFragmentD()
    private var fragmentE = RegisterFragmentE()
    private var fragmentF = RegisterFragmentF()
    private var fragmentList = ArrayList<Fragment>()
    private lateinit var fragmentAdapter: MyFragmentAdapter

    var presenter = RegisterPresenter()

    /**
     * 用户信息
     */
    private lateinit var telephone: String
    private lateinit var password: String
    private lateinit var userName: String
    private var gender: String = "男"
    private var birthday: String? = null
    private var blood: String? = null
    private var height: Int? = null
    private var weight: Int? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Utils.init(application)

        presenter.mView = this

        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        initFragment()

    }

    /**
     * 初始化fragment
     */
    private fun initFragment() {
        fragmentA.mView = this
        fragmentB.mView = this
        fragmentC.mView = this
        fragmentD.mView = this
        fragmentE.mView = this
        fragmentF.mView = this

        fragmentList.add(fragmentA)
        fragmentList.add(fragmentB)
        fragmentList.add(fragmentC)
        fragmentList.add(fragmentD)
        fragmentList.add(fragmentE)
        fragmentList.add(fragmentF)

        fragmentAdapter = MyFragmentAdapter(supportFragmentManager, fragmentList)
        mViewPager.adapter = fragmentAdapter
        mViewPager.currentItem = 0
        mViewPager.setScroll(false)
    }


    /**
     * fragment的接口
     * 用于跳转到下一个fragment
     */
    override fun nextFragment() {
        if (mViewPager.currentItem == fragmentList.size - 1) {
            presenter.register(telephone, password, userName, gender, birthday, blood, height, weight)  //fragment到达最后一页,直接调用
        } else {
            mViewPager.currentItem++
            (regRadioGroup.getChildAt(mViewPager.currentItem) as RadioButton).isChecked = true  //否则fragment前进1同时更新底部的radiobutton
        }
    }

    override fun initTelephone(data: String) {
        telephone = data
    }

    override fun initPassword(data: String) {
        password = data
    }

    override fun initUserName(data: String) {
        userName = data
    }

    override fun initGender(data: String) {
        gender = data
    }

    override fun initBirthday(data: String) {
        birthday = data
    }

    override fun initBlood(data: String) {
        blood = data
    }

    override fun initHeight(data: Int) {
        height = data
    }

    override fun initWeight(data: Int) {
        weight = data
    }

    override fun initImage(bitmap: Bitmap) {
        val file = File(Const.LOCAL_PATH, "$telephone.jpg")
        if (file.exists()) {
            file.delete()
        }
        ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG)
    }

    override fun unKnownError() {
        toast("未知错误,请稍后再试")
    }

    /**
     * 注册成功
     * @param token
     */
    override fun registerSuccess(token: String) {
        toast("注册成功")
        val user = User(telephone, null, userName, null, null, birthday, gender, blood, height, weight)
        sharedPreferencesEditor.putString("token", token)   //保存token和用户信息
                .putString("user", Gson().toJson(user))
                .commit()
        startActivity<MainActivity>()   //跳转至主界面
    }

    override fun netError() {
        toast("网络异常,请检查")
    }

}
