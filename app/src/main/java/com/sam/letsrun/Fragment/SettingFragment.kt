package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.beardedhen.androidbootstrap.font.MaterialIcons
import com.blankj.utilcode.util.KeyboardUtils
import com.suke.widget.SwitchButton
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.rengwuxian.materialedittext.MaterialEditText
import com.sam.letsrun.Activity.LoginActivity
import com.sam.letsrun.Custom.MyUtils
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
 * 设置Fragment
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

        initUser()
        presenter.mView = this

        userSignatureText.setOnClickListener {
            val signatureDialog = MaterialDialog.Builder(context!!)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .inputRange(0, 255)
                    .input("设置您的个性签名", "") { _, input ->
                        val newUser = user.copy()
                        newUser.signature = input.toString()
                        presenter.updateUserInfo(newUser, token)
                    }
                    .positiveText("保存")
                    .negativeText("取消")
                    .build()
            signatureDialog.show()
        }

        userInformationLayout.setOnClickListener {
            //            TODO("修改用户信息")
        }

        userSportHistoryLayout.setOnClickListener {
            //            TODO("展示运动历史记录")
        }

        userSettingLayout.setOnClickListener {
            //设置
//            TODO("添加设置栏目")
            val settingDialog = MaterialDialog.Builder(context!!)
                    .title("设置")
                    .titleGravity(GravityEnum.CENTER)
                    .customView(R.layout.dialog_setting, true)
                    .positiveText("保存")
                    .onPositive { dialog, which ->
                        //                        TODO("保存事件")
                    }
                    .negativeText("取消")
                    .onNegative { dialog, which ->
                        //                        TODO("取消事件")
                    }
                    .build()

            val rootView: View = settingDialog.customView!!
            val shareMyLocationButton: SwitchButton = rootView.findViewById(R.id.share_mylocation_button)
            shareMyLocationButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
//                    TODO("")
                }
            }
            val goalStepText: MaterialEditText = rootView.findViewById(R.id.goal_step_text)
            goalStepText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val goalSteps = goalStepText.text.toString().toInt()
                }
            }
            goalStepText.setOnEditorActionListener { v, _, _ ->
                goalStepText.clearFocus()
                KeyboardUtils.hideSoftInput(v)
                true
            }

            settingDialog.show()
        }

        appInfoLayout.setOnClickListener {
            MaterialDialog.Builder(context!!)
                    .customView(R.layout.app_about, false)
                    .show()
        }

        logoutButton.setOnClickListener {
            //退出登录
            presenter.logout(user.telephone, token)
        }

    }

    @SuppressLint("CommitPrefEdits")
    private fun initUser() {
        sharedPreferences = activity!!.getSharedPreferences(activity!!.packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)

        setUserInfo()
    }

    override fun logoutSuccess() {
        toast("退出成功")
        sharedPreferencesEditor.putString("token", "")
                .putString("user", "")
                .commit()
        startActivity(intentFor<LoginActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        activity?.finish()
    }

    override fun logoutFailed() {
        toast("网络异常,请稍后再试")
    }

    override fun unKnownError() {
        toast("未知错误,请稍后再试")
    }

    override fun netError() {
        toast("网络异常,请检查")
    }

    override fun updateUserInfoFailed() {
        toast("更新失败,请稍后再试")
    }

    override fun updateUserInfoSuccess(newUser: User) {
        user = newUser
        sharedPreferencesEditor.putString("user", Gson().toJson(user))
        sharedPreferencesEditor.commit()

        setUserInfo()
    }

    private fun setUserInfo() {
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


}// Required empty public constructor
