package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.sam.letsrun.Activity.LoginActivity
import com.sam.letsrun.Adapter.MyFragmentAdapter
import com.sam.letsrun.Common.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.SettingFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.SettingFragmentView
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast

/**
 * 设置fragment
 */
@Suppress("UNREACHABLE_CODE")
class SettingFragment : Fragment(), SettingFragmentView {

    private var settingFragmentMain = SettingFragmentMain()
    private var settingFragmentUserInformation = SettingFragmentUserInformation()
    private var fragmentList = ArrayList<Fragment>()
    private lateinit var fragmentAdapter: MyFragmentAdapter
    var presenter = SettingFragmentPresenter()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    private lateinit var token: String
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.mView = this
        settingFragmentMain.mView = this
        settingFragmentUserInformation.mView = this

        fragmentList.add(settingFragmentMain)
        fragmentList.add(settingFragmentUserInformation)
        fragmentAdapter = MyFragmentAdapter(childFragmentManager, fragmentList)

        mViewPager.adapter = fragmentAdapter
        mViewPager.currentItem = 0
        mViewPager.setScroll(false)

        initUserInfo()

    }

    @SuppressLint("CommitPrefEdits")
    private fun initUserInfo() {
        sharedPreferences = activity!!.getSharedPreferences(activity!!.packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)

        GlideApp.with(this)
                .load(MyUtils.getImageUrl(user.telephone))
                .placeholder(R.drawable.ic_user_image)
                .into(userImageView)

        usernameText.text = user.username
        if (user.signature == null) {
            signatureText.text = "未设置签名"
            signatureText.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
        } else {
            signatureText.typeface = Typeface.DEFAULT
            signatureText.text = user.signature
        }


    }

    override fun logout() {
        presenter.logout(user.telephone, token)
    }

    override fun logoutSuccess() {
        toast("退出成功")
        sharedPreferencesEditor.putString("token", "")
                .putString("user", "")
                .commit()
        startActivity(intentFor<LoginActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

    override fun logoutFailed() {
        toast("网络异常,请稍后再试")
    }

    override fun unKnownError() {
        toast("未知错误,请稍后再试")
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        }
    }
}// Required empty public constructor
