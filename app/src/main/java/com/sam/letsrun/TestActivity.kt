package com.sam.letsrun

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekViewEvent
import com.baoyachi.stepview.bean.StepBean
import kotlinx.android.synthetic.main.activity_test.*
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.util.*


class TestActivity : AppCompatActivity(), MonthLoader.MonthChangeListener {
    override fun onMonthChange(newYear: Int, newMonth: Int): MutableList<out WeekViewEvent> {
        return ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Logger.addLogAdapter(AndroidLogAdapter())

//        weekView.monthChangeListener = this
//        val stepsBeanList = ArrayList<StepBean>()
//        val stepBean0 = StepBean("接单", 1)
//        val stepBean1 = StepBean("打包", 1)
//        val stepBean2 = StepBean("出发", 1)
//        val stepBean3 = StepBean("送单", 0)
//        val stepBean4 = StepBean("完成", -1)
//        stepsBeanList.add(stepBean0)
//        stepsBeanList.add(stepBean1)
//        stepsBeanList.add(stepBean2)
//        stepsBeanList.add(stepBean3)
//        stepsBeanList.add(stepBean4)
//
//
//        stepView1.setStepViewTexts(stepsBeanList)
//                .setTextSize(12)//set textSize
//                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))//设置StepsViewIndicator完成线的颜色
//                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.cb_color))//设置StepsViewIndicator未完成线的颜色
//                .setStepViewComplectedTextColor(ContextCompat.getColor(this, R.color.black))//设置StepsView text完成线的颜色
//                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.black))//设置StepsView text未完成线的颜色
//                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.complted))//设置StepsViewIndicator CompleteIcon
//                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
//                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.attention))//设置StepsViewIndicator AttentionIcon
//
//
//        val list0 = ArrayList<String>()
//        list0.add("接已提交定案，等待系统确认")
//        list0.add("您的商品需要从外地调拨，我们会尽快处理，请耐心等待")
//        list0.add("您的订单已经进入亚洲第一仓储中心1号库准备出库")
//        list0.add("您的订单预计6月23日送达您的手中，618期间促销火爆，可能影响送货时间，请您谅解，我们会第一时间送到您的手中")
//        list0.add("您的订单已打印完毕")
//        list0.add("您的订单已拣货完成")
//        list0.add("扫描员已经扫描")
//        list0.add("打包成功")
//        list0.add("配送员【包牙齿】已出发，联系电话【130-0000-0000】，感谢您的耐心等待，参加评价还能赢取好多礼物哦")
//        list0.add("感谢你在京东购物，欢迎你下次光临！")
//
//        stepView2.setStepsViewIndicatorComplectingPosition(list0.size - 2)//设置完成的步数
//                .reverseDraw(false)//default is true
//                .setStepViewTexts(list0)//总步骤
//                .setLinePaddingProportion(0.85f)//设置indicator线与线间距的比例系数
//                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, android.R.color.white))//设置StepsViewIndicator完成线的颜色
//                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))//设置StepsViewIndicator未完成线的颜色
//                .setStepViewComplectedTextColor(ContextCompat.getColor(this, android.R.color.black))//设置StepsView text完成线的颜色
//                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))//设置StepsView text未完成线的颜色
//                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.complted))//设置StepsViewIndicator CompleteIcon
//                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
//                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.attention))//设置StepsViewIndicator AttentionIcon
//
//        val list1 = ArrayList<Entry>()
//        list1.add(Entry(0.1f, 1.0f))
//        list1.add(Entry(0.2f, 0.5f))
//        list1.add(Entry(0.4f, 1.6f))
//        val lineDataSet = LineDataSet(list1, "Line")
//        lineChart.data = LineData(lineDataSet)
    }
}
