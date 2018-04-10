package com.sam.letsrun.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import kotlinx.android.synthetic.main.fragment_register_d.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger



/**
 * 注册界面D
 * 填写性别
 */
class RegisterFragmentD : Fragment() {

    lateinit var mView: RegisterView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_d, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        sexRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.manButton -> {
                    mView.initGender("男")
                }
                R.id.womanButton -> {
                    mView.initGender("女")
                }
            }
        }

        nextButton.setOnClickListener {
            mView.nextFragment()
        }
    }
}

