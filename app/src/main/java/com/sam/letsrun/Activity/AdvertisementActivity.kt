package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.AdvertisementPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.AdvertisementView
import kotlinx.android.synthetic.main.activity_advertisement.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


/**
 * 广告
 * APP每次打开时加载本地token和telephone
 * 上传telephone和token并判断是跳转登录界面还是主界面
 * TODO("给广告界面添加加载动画")
 */
class AdvertisementActivity : AppCompatActivity(), AdvertisementView {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var user: User
    private lateinit var token: String
    private lateinit var animationSet: AnimationSet

    private var presenter = AdvertisementPresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement)
        Logger.addLogAdapter(AndroidLogAdapter())   //Logger初始化
        Utils.init(application)
        presenter.mView = this      //view一定不要忘记初始化

        animationSet = AnimationSet(true)
        animationSet.duration = 1000
        animationSet.isFillEnabled = true
        animationSet.fillAfter = true
        animationSet.addAnimation(ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f))

        initUser()
    }


    @SuppressLint("CommitPrefEdits")
    private fun initUser() {
        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")

        if (token == "") {      //token不存在
            loadFailed()
        } else {        //token存在则获取user对象
            user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)
            presenter.loadInformation(user, token)
        }

    }

    /**
     * 初始化信息失败
     */
    override fun loadFailed() {
        Logger.e("loadFailed")
        advertiseView.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startActivity<LoginActivity>()  //跳转到登录界面
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }

    /**
     * 初始化信息成功
     * @param user: User
     */
    override fun loadSuccess(user: User) {
        Logger.e(Gson().toJson(user))
        sharedPreferencesEditor.putString("user", Gson().toJson(user)).commit()     //保存新的user信息到本地
        advertiseView.startAnimation(animationSet)
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                startActivity<MainActivity>()   //跳转到主界面
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }

    /**
     * 网络错误
     */
    override fun netError() {
        toast("网络异常,请检查")
        startActivity<MainActivity>()
    }

}
