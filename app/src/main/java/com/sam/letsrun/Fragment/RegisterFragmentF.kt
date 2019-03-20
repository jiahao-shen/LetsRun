package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.qqtheme.framework.picker.NumberPicker
import com.sam.letsrun.Activity.RegisterActivity
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView

import kotlinx.android.synthetic.main.fragment_register_f.*

/**
 * 注册界面F
 * 填写身高,体重
 */
class RegisterFragmentF : Fragment() {

    private lateinit var registerActivity: RegisterActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        registerActivity = context as RegisterActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_f, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        heightText.setOnClickListener {
            val picker = NumberPicker(this.activity)
            picker.setCanceledOnTouchOutside(false)
            picker.setRange(50, 250)
            picker.setSelectedItem(175)
            picker.setLabel("cm")
            picker.setGravity(Gravity.CENTER_VERTICAL)
            picker.setWidth(800)
            picker.show()
            picker.setOnItemPickListener { _, item ->
                heightText.setText("${item}cm")
                registerActivity.initHeight(item.toInt())
            }
        }

        weightText.setOnClickListener {
            val picker = NumberPicker(this.activity)
            picker.setCanceledOnTouchOutside(false)
            picker.setRange(30, 250)
            picker.setSelectedItem(60)
            picker.setLabel("kg")
            picker.setGravity(Gravity.CENTER_VERTICAL)
            picker.setWidth(800)
            picker.show()
            picker.setOnItemPickListener { _, item ->
                weightText.setText("${item}kg")
                registerActivity.initWeight(item.toInt())
            }
        }

        finishButton.setOnClickListener {
            registerActivity.nextFragment()
        }
    }

}// Required empty public constructor
