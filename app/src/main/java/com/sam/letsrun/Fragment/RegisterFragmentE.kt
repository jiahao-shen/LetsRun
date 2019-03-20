package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.picker.OptionPicker
import com.sam.letsrun.Activity.RegisterActivity
import com.sam.letsrun.R
import com.sam.letsrun.View.RegisterView
import kotlinx.android.synthetic.main.fragment_register_e.*

/**
 * 注册界面E
 * 填写生日和血型
 */
class RegisterFragmentE : Fragment() {

    private lateinit var registerActivity: RegisterActivity

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        registerActivity = context as RegisterActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_e, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        birthdayText.setOnClickListener {
            val picker = DatePicker(this.activity)
            picker.setCanceledOnTouchOutside(false)
            picker.setWidth(800)
            picker.setGravity(Gravity.CENTER_VERTICAL)
            picker.setResetWhileWheel(false)
            picker.setRangeStart(1900, 1, 1)
            picker.show()
            picker.setOnDatePickListener(DatePicker.OnYearMonthDayPickListener { year, month, day ->
                birthdayText.setText("$year-$month-$day")
                registerActivity.initBirthday("$year-$month-$day")
            })
        }

        bloodText.setOnClickListener {
            val picker = OptionPicker(this.activity, arrayOf("O", "A", "B", "AB"))
            picker.setCanceledOnTouchOutside(false)
            picker.setWidth(800)
            picker.setGravity(Gravity.CENTER_VERTICAL)
            picker.setLabel("型")
            picker.show()
            picker.setOnItemPickListener { _, item ->
                bloodText.setText("${item}型")
                registerActivity.initBlood(item)
            }
        }

        nextButton.setOnClickListener {
            registerActivity.nextFragment()
        }
    }

}// Required empty public constructor
