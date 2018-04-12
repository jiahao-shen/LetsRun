package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.sam.letsrun.Activity.LoginActivity
import com.sam.letsrun.Common.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.SettingFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.SettingFragmentView
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import kotlin.math.max

/**
 * 设置fragment
 */


@Suppress("UNREACHABLE_CODE")
class SettingFragment : Fragment(), SettingFragmentView {

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

        initUserInfo()
        presenter.mView = this

        userInformationLayout.setOnClickListener {}

        userSportHistoryLayout.setOnClickListener {}

        userSettingLayout.setOnClickListener {}

        appInfoLayout.setOnClickListener{}

        logoutButton.setOnClickListener {
            presenter.logout(user.telephone, token)
        }

        settingRefreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
                val radius: Int = max((10 * (1 - percent)).toInt(), 0)
                if (radius == 0) {
                    settingBackgroundBlur.visibility = View.INVISIBLE
                } else {
                    settingBackgroundBlur.visibility = View.VISIBLE
                    settingBackgroundBlur.setBlurRadius(radius)
                }
            }

        })
    }

    @SuppressLint("CommitPrefEdits")
    private fun initUserInfo() {
        sharedPreferences = activity!!.getSharedPreferences(activity!!.packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)

        GlideApp.with(this)
                .load(MyUtils.getImageUrl(user.telephone))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.ic_user_image)
                .into(userImageView)

        userNameText.text = user.username

        if (user.signature == null) {
            userSignatureText.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
            userSignatureText.text = "未设置签名"
        } else {
            userSignatureText.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            userSignatureText.text = user.signature
        }

        if (user.gender == "男") {
            userGenderView.imageResource = R.drawable.ic_sex_man_checked
        } else {
            userGenderView.imageResource = R.drawable.ic_sex_woman_checked
        }

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
