package com.sam.letsrun.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Activity.RegisterActivity
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import kotlinx.android.synthetic.main.fragment_register_b.*
import org.jetbrains.anko.support.v4.toast


/**
 * 注册界面B
 * 填写密码
 */
class RegisterFragmentB : Fragment() {

    private lateinit var registerActivity: RegisterActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        registerActivity = context as RegisterActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextButton.setOnClickListener {
            if (setPwdText.isCharactersCountValid) {
                val passwordA = setPwdText.text.toString()
                val passwordB = repeatPwdText.text.toString()
                //判断两次输入的密码是否一致
                if (passwordA == passwordB) {
                    registerActivity.initPassword(passwordA)
                    registerActivity.nextFragment()
                } else {
                    toast("密码不一致")
                }
            } else {
                toast("请输入6~15位密码")
            }
        }
    }

}
