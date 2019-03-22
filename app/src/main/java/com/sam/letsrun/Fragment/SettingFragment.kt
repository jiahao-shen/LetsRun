package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.TimeUtils
import com.suke.widget.SwitchButton
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.orhanobut.logger.Logger
import com.rengwuxian.materialedittext.MaterialEditText
import com.sam.letsrun.Activity.MainActivity
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.GlideApp
import com.sam.letsrun.Model.User
import com.sam.letsrun.Presenter.SettingFragmentPresenter
import com.sam.letsrun.R
import com.sam.letsrun.View.SettingFragmentView
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.support.v4.toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 设置Fragment
 */

@Suppress("UNREACHABLE_CODE")
class SettingFragment : Fragment(), SettingFragmentView {

    private var presenter = SettingFragmentPresenter()
    private lateinit var mainActivity: MainActivity
    private lateinit var token: String
    private lateinit var user: User

    private lateinit var stepsHistory: ArrayList<Int>
    private lateinit var distanceHistory: ArrayList<Float>

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserInfo()

        presenter.mView = this

        userSignatureText.setOnClickListener {
            val signatureDialog = MaterialDialog.Builder(context!!)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .inputRange(0, 255)
                    .input("设置您的个性签名", user.signature) { _, input ->
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
            val userInfoDialog = MaterialDialog.Builder(context!!)
                    .customView(R.layout.dialog_user_information, true)
                    .build()
            val rootView = userInfoDialog.customView!!

            val userBirthText: TextView = rootView.findViewById(R.id.userBirthText)
            val userBloodText: TextView = rootView.findViewById(R.id.userBloodText)
            val userHeightText: TextView = rootView.findViewById(R.id.userHeightText)
            val userWeightText: TextView = rootView.findViewById(R.id.userWeightText)

            userBirthText.text = user.birthday
            userBloodText.text = user.blood
            userHeightText.text = "${user.height}cm"
            userWeightText.text = "${user.weight}kg"

            userInfoDialog.show()
        }

        userSportHistoryLayout.setOnClickListener {
            val sportsHistoryDialog = MaterialDialog.Builder(context!!)
                    .customView(R.layout.dialog_sports_history, false)
                    .title("运动记录")
                    .titleGravity(GravityEnum.CENTER)
                    .negativeText("关闭")
                    .build()

            val rootView = sportsHistoryDialog.customView!!
            val sportsHistoryChart: CombinedChart = rootView.findViewById(R.id.sportsHistoryChart)
            showSportHistory(sportsHistoryChart)

            sportsHistoryDialog.show()
        }

        userSettingLayout.setOnClickListener {
            //设置
            //获取user数据
            var isCountStep = user.isCountStep
            var goalSteps= user.goalSteps

            val settingDialog = MaterialDialog.Builder(context!!)
                    .title("设置")
                    .titleGravity(GravityEnum.CENTER)
                    .customView(R.layout.dialog_setting, true)
                    .positiveText("保存")
                    .autoDismiss(false)
                    .onPositive { dialog, _ ->
                        if (goalSteps < 5000) {
                            toast("目标步数不能小于5000")
                        } else {
                            val newUser = user.copy()
                            newUser.isCountStep = isCountStep
                            newUser.goalSteps = goalSteps
                            presenter.updateUserInfo(newUser, token)
                            dialog.dismiss()
                        }
                    }
                    .negativeText("取消")
                    .onNegative { dialog, _ ->
                        dialog.dismiss()
                    }
                    .build()

            val rootView: View = settingDialog.customView!!
            val isCountStepButton: SwitchButton = rootView.findViewById(R.id.share_mylocation_button)
            val goalStepText: MaterialEditText = rootView.findViewById(R.id.goal_step_text)

            isCountStepButton.isChecked = (isCountStep == 1)
            goalStepText.setText(goalSteps.toString())

            isCountStepButton.setOnCheckedChangeListener { _, isChecked ->
                isCountStep = if (isChecked) 1 else 0
            }

            goalStepText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    goalSteps = goalStepText.text.toString().toInt()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

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

        getRandomHistory()

        getXAxisValues()

    }

    private fun showSportHistory(sportsHistoryChart: CombinedChart) {
        val sportsHistory = CombinedData()

        val lineData = LineData()
        val distanceData = ArrayList<Entry>()
        for (i in 0..6) {
            distanceData.add(Entry(i.toFloat(), distanceHistory[i]))
        }
        val lineDataSet = LineDataSet(distanceData, "每日距离")
        lineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet.color = Color.RED
        lineDataSet.setCircleColor(Color.RED)
        lineDataSet.lineWidth = 2.0f
        lineDataSet.valueTextColor = Color.RED
        lineData.addDataSet(lineDataSet)
        lineData.setValueFormatter { value, _, _, _ ->
            DecimalFormat("#.0").format(value)
        }

        val barData = BarData()
        val stepsData = ArrayList<BarEntry>()
        for (i in 0..6) {
            stepsData.add(BarEntry(i.toFloat(), stepsHistory[i].toFloat()))
        }
        val barDataSet = BarDataSet(stepsData, "每日步数")
        barDataSet.axisDependency = YAxis.AxisDependency.LEFT
        barDataSet.color = Color.parseColor("#6ccd82")
        barDataSet.valueTextColor = Color.parseColor("#6ccd82")
        barData.addDataSet(barDataSet)
        barData.barWidth = 0.4f

        sportsHistory.setData(lineData)
        sportsHistory.setData(barData)
        sportsHistoryChart.data = sportsHistory

        sportsHistoryChart.setScaleEnabled(false)
        sportsHistoryChart.xAxis.setDrawGridLines(false)
        sportsHistoryChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        sportsHistoryChart.xAxis.valueFormatter = IndexAxisValueFormatter(getXAxisValues())
//        sportsHistoryChart.xAxis.labelRotationAngle = -30.0f
        sportsHistoryChart.xAxis.axisMinimum = -0.5f
        sportsHistoryChart.xAxis.axisMaximum = 6.5f

        sportsHistoryChart.axisRight.setDrawGridLines(false)
        sportsHistoryChart.axisRight.setDrawTopYLabelEntry(true)
        sportsHistoryChart.axisRight.setValueFormatter { value, _ ->
            "${value}km"
        }
        sportsHistoryChart.description.isEnabled = false
        sportsHistoryChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        sportsHistoryChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    }

    private fun getRandomHistory() {
        stepsHistory = ArrayList()
        distanceHistory = ArrayList()
        for (i in 0..6) {
            val steps = MyUtils.getRandomInt(1000, 10000)
            val distance = MyUtils.getRandomFloat(0.8f, 0.9f) * steps / 1000
            stepsHistory.add(steps)
            distanceHistory.add(distance)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getXAxisValues(): ArrayList<String> {
        val dayList = ArrayList<String>()

        val formatter = SimpleDateFormat("M-dd")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -6)
        for (i in 0..6) {
            val date = calendar.time
            dayList.add(formatter.format(date))
            calendar.add(Calendar.DATE, 1)
        }

        return dayList
    }

    @SuppressLint("CommitPrefEdits")
    private fun initUser() {
        token = mainActivity.token
        user = mainActivity.user

    }

    override fun logoutSuccess() {
        mainActivity.logout()
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
        toast("保存失败,请稍后再试")
    }

    override fun updateUserInfoSuccess(newUser: User) {
        mainActivity.updateLocalUserInfo(newUser)
        setUserInfo()
    }

    private fun setUserInfo() {
        initUser()

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
