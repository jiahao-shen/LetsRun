package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Chronometer
import android.widget.ScrollView
import com.amap.api.services.weather.LocalWeatherLive
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.google.gson.Gson
import com.mob.wrappers.UMSSDKWrapper
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Activity.MainActivity
import com.sam.letsrun.Activity.SportActivity
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.Model.User
import com.sam.letsrun.R
import kotlinx.android.synthetic.main.fragment_sport.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 运动fragment
 */
class SportFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var user: User
    private lateinit var token: String

    private lateinit var weatherImageList: TypedArray
    private lateinit var weatherList: Array<String>

    private var breathCount = 0
    private var actionDownCount = 0
    private var currentX = 1.0f
    private var currentY = 1.0f
    private var interval: Long = 0
    private lateinit var a: Date
    private lateinit var b: Date
    private var downAnimation = ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
    private var upAnimation = ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        initWeatherImage()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sport, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY - oldScrollY > 5 && scrollY > 400) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            } else if (oldScrollY - scrollY > 5 && scrollY < 600) {
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }

        sportButton.setOnClickListener {
            startActivity<SportActivity>()
        }

        breathButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                actionDownCount++
            } else if (event.action == MotionEvent.ACTION_UP && actionDownCount == breathCount) {
                b = Date()
                interval = b.time - a.time
                if (interval in 2..2000) {
                    currentX = 1.0f + 0.5f / 2000 * interval
                    currentY = 1.0f + 0.5f / 2000 * interval
                    upAnimation = ScaleAnimation(currentX, 1.0f, currentY, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                } else {
                    upAnimation = ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                }
                val animationSet = AnimationSet(true)
                animationSet.duration = 1500
                animationSet.addAnimation(upAnimation)
                breathButton.startAnimation(animationSet)
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onAnimationEnd(animation: Animation?) {
                        breathCountText.text = "本次练习次数: $breathCount"
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
            false
        }

        breathButton.setOnLongClickListener {
            breathCount++
            a = Date()
            actionDownCount = breathCount
            val animationSet = AnimationSet(true)
            animationSet.duration = 2000
            animationSet.addAnimation(downAnimation)
            animationSet.isFillEnabled = true
            animationSet.fillAfter = true
            breathButton.startAnimation(animationSet)
            true
        }

        showSportProgress()
    }

    /**
     * 动态显示今日步数
     */
    @SuppressLint("SetTextI18n")
    private fun showSportProgress() {
        initUser()

        val currentSteps = mainActivity.sharedPreferences.getInt("${MyUtils.getCurrentDate()}:steps", 0)

        val percent = currentSteps * 1.0 / user.goalSteps
        val count = 20
        val interval = percent / count

        Observable.interval(50, TimeUnit.MILLISECONDS)
                .take(count + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    sportProgress.percent = (s * interval).toFloat()
                    currentStepsView.text = (s * interval * user.goalSteps).toInt().toString()
                }

        goalStepsView.text = "目标步数:${user.goalSteps}"
    }


    /**
     * 接受service发来的天气参数
     */
    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: LocalWeatherLive?) {
        event?.let {
            for ((index, value) in weatherList.withIndex()) {
                if (value == event.weather) {
                    weatherImageView.setImageResource(weatherImageList.getResourceId(index, 0))
                    break
                }
            }
            cityTextView.text = event.city
            weatherInfoTextView1.text = "${event.temperature}°C"
            weatherInfoTextView2.text = "${event.windDirection}风 ${event.windPower}级    湿度 ${event.humidity}%"
        }
    }

    @SuppressLint("Recycle")
    private fun initWeatherImage() {
        weatherList = resources.getStringArray(R.array.weather)
        weatherImageList = resources.obtainTypedArray(R.array.weather_image)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            showSportProgress()
        }
    }

    private fun initUser() {
        user = mainActivity.user
        token = mainActivity.token
    }
}// Required empty public constructor
