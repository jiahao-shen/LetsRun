package com.sam.letsrun.Fragment


import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.weather.LocalWeatherLive
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Activity.SportActivity
import com.sam.letsrun.R
import kotlinx.android.synthetic.main.fragment_sport.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 运动fragment
 */
class SportFragment : Fragment() {

    private var goalSteps: Int = 10000
    private var currentSteps: Int = 7000
    private lateinit var weatherImageList: TypedArray
    private lateinit var weatherList: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        initWeatherImage()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sportButton.setOnClickListener {
            startActivity<SportActivity>()
        }

        showSportProgress()
    }

    /**
     * 动态显示今日步数
     */
    private fun showSportProgress() {
        val percent = (currentSteps * 100.0 / goalSteps).toInt()

        Observable.interval(20, TimeUnit.MILLISECONDS)
                .take(percent + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    sportProgress.percent = s * 0.01f
                    currentStepsView.text = (s * 0.01 * goalSteps).toInt().toString()
                }
    }

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
}// Required empty public constructor
