package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.widget.RadioButton
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.sam.letsrun.Adapter.MyFragmentAdapter
import com.sam.letsrun.Common.Const
import com.sam.letsrun.Common.MyUtils
import com.sam.letsrun.Fragment.*
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.RegisterPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register_c.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File

/**
 * 注册界面
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
     * 手机号,密码,用户头像,用户名,缓存图片路径,性别,生日,血型,身高,体重
     */
    private lateinit var telephone: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var tempUri: Uri
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
            //fragment到达最后一页,直接调用
            presenter.register(telephone, password, userName, gender, birthday, blood, height, weight)
        } else {
            //否则fragment前进1同时更新底部的radiobutton
            mViewPager.currentItem++
            (regRadioGroup.getChildAt(mViewPager.currentItem) as RadioButton).isChecked = true
        }
    }

    /**
     * 拍照和相册调用回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //相册选择回调
            Const.SELECT_IMAGE_ALBUM -> {
                if (resultCode == RESULT_OK) {
                    //跳转至剪裁
                    data?.data?.let { presenter.cropImage(this, it) }
                }
            }
            //拍照回调
            Const.SELECT_IMAGE_PHOTO -> {
                if (resultCode == RESULT_OK) {
                    //跳转至剪裁
                    presenter.cropImage(this, tempUri)
                }
            }
            //剪裁回调
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    //设置用户头像
                    UCrop.getOutput(data!!)?.let {
                        fragmentC.setUserImage(it)
                        MyUtils.saveImageView((fragmentC.userImageView.drawable as BitmapDrawable).bitmap, telephone)
                    }
                }
            }
        }
    }


    //拍照
    override fun selectImageCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //拍照图片的临时存储路径
        tempUri = FileProvider.getUriForFile(this, "com.sam.letsrun", File(cacheDir, "${System.currentTimeMillis()}.jpg"))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
        startActivityForResult(intent, Const.SELECT_IMAGE_PHOTO)
    }

    //相册选择图片
    override fun selectImageAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, Const.SELECT_IMAGE_ALBUM)
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

        //保存token和用户信息
        sharedPreferencesEditor.putString("token", token)
                .putString("user", Gson().toJson(user))
                .commit()

        //跳转至主界面
        startActivity<MainActivity>()
    }

    override fun netError() {
        toast("网络异常,请检查")
    }

}
