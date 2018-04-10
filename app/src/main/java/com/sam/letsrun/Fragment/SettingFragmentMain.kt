package com.sam.letsrun.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.orhanobut.logger.Logger

import com.sam.letsrun.R
import com.sam.letsrun.View.SettingFragmentView
import kotlinx.android.synthetic.main.fragment_setting_main.*

/**
 * 设置fragment中的mainFragment
 */
class SettingFragmentMain : Fragment() {

    lateinit var mView: SettingFragmentView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //退出登录按钮
        logoutButton.setOnClickListener {
            MaterialDialog.Builder(this.context!!)
                    .content("您确定要退出登录?")
                    .positiveText("确定")
                    .onPositive { _, _ ->
                        mView.logout()
                    }
                    .negativeText("取消")
                    .onNegative { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        }
    }
}// Required empty public constructor
