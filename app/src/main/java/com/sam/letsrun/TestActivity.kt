package com.sam.letsrun

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent
import com.baoyachi.stepview.bean.StepBean
import kotlinx.android.synthetic.main.activity_test.*
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.*
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.util.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Logger.addLogAdapter(AndroidLogAdapter())


    }

}
