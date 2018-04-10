package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.AdvertisementPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.AdvertisementView
import kotlinx.android.synthetic.main.activity_advertisement.*
import org.jetbrains.anko.startActivity


/**
 * 广告界面
 * APP每次打开时加载本地token和telephone
 * 上传telephone和token并判断是跳转登录界面还是主界面
 */
class AdvertisementActivity : AppCompatActivity(), AdvertisementView {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private var presenter = AdvertisementPresenter()
    private lateinit var user: User
    private lateinit var token: String

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advertisement)
        Logger.addLogAdapter(AndroidLogAdapter())

        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString("friendRequestList", "").commit()

        presenter.mView = this

        initInformation()

    }

    /**
     * 初始化信息
     */
    private fun initInformation() {
        token = sharedPreferences.getString("token", "")

        if (token == "") {
            loadFailed()
        } else {
            user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)
            presenter.loadInformation(user.telephone, token)
        }

    }

    /**
     * 初始化信息失败
     */
    override fun loadFailed() {
        Logger.e("loadFailed")
        progressBar.visibility = View.GONE
        //跳转到登录界面
        startActivity<LoginActivity>()
    }

    /**
     * 初始化信息成功
     * @param user: User
     */
    override fun loadSuccess(user: User) {
        progressBar.visibility = View.GONE
        Logger.json(Gson().toJson(user))
        //本地保存user信息
        sharedPreferencesEditor.putString("user", Gson().toJson(user)).commit()
        //跳转到主界面
        startActivity<MainActivity>()
    }

}
