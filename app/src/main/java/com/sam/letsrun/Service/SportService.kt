package com.sam.letsrun.Service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearchQuery
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 定位和计步服务
 * TODO("添加判断运动状态功能")
 * 必须强调!!!!!!!这个计步算法不是我写的!!!!不是我写的!!!!
 */
class SportService : Service(), AMapLocationListener, SensorEventListener, WeatherSearch.OnWeatherSearchListener {

    /**
     * 定位相关
     */
    private lateinit var mLocationClient: AMapLocationClient
    private lateinit var mLocationOption: AMapLocationClientOption
    private lateinit var mQuery: WeatherSearchQuery
    private lateinit var mWeatherSearch: WeatherSearch

    /**
     * 计步相关
     */
    private lateinit var sManager: SensorManager
    private val oriValues = FloatArray(3)//存放三轴加速度
    private val valueNum = 4//数组元素个数
    private val tempValue = FloatArray(valueNum)//暂存用于梯度计算阀值的数组
    private var tempCount = 0//暂存元素个数
    private var isDirectionUp = false//是否处于上升状态
    private var continueUpCount = 0//上升次数
    private var continueUpFormerCount = 0//前一点的上升次数
    private var peakOfWave = 0.0f//波峰
    private var valleyOfWave = 0.0f//波谷
    private var timeOfThisPeak: Long = 0//此次波峰时间
    private var gravityOld: Long = 0//过去的总加速度
    private var threadValue = 2.0f //阀值的初始值
    private var lastTime: Long = 0
    private val threadList = FloatArray(1000)
    private val waveList = FloatArray(1000)
    private var countOfThread = 0
    private var countOfWaves = 0

    private var totalSteps = 0  //总步数
    private var speed: Double = 0.0   //当前速度
    private var sportStatus: Int = 0    //运动状态

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initLocation()
        initStep()
    }

    private fun searchWeather(city: String) {
        Logger.i(city)
        mQuery = WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE)
        mWeatherSearch = WeatherSearch(this)
        mWeatherSearch.setOnWeatherSearchListener(this)
        mWeatherSearch.query = mQuery
        mWeatherSearch.searchWeatherAsyn()
    }

    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onWeatherLiveSearched(localWeatherLiveResult: LocalWeatherLiveResult?, code: Int) {
        Logger.json(Gson().toJson(localWeatherLiveResult))
        if (code == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.liveResult != null) {
                EventBus.getDefault().postSticky(localWeatherLiveResult.liveResult)
            }
        }
    }

    private fun initLocation() {
        mLocationClient = AMapLocationClient(this)
        mLocationOption = AMapLocationClientOption()
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        mLocationOption.interval = 60000
        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.setLocationListener(this)
        mLocationClient.startLocation()
    }

    override fun onLocationChanged(location: AMapLocation?) {
        location?.let {
            if (location.errorCode == 0) {
                searchWeather(location.city)
            }
        }
    }

    /**
     * 好了,从下往下的算法跟我一点关系都没有
     */
    private fun initStep() {
        sManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager    //传感器管理器
        val mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI)   //注册传感器，设置刷新时间为60ms
    }

    override fun onSensorChanged(event: SensorEvent) {  //接口的方法，在这里获取三轴加速度，计算出当前的总加速度
        for (i in 0..2) {
            oriValues[i] = event.values[i]
        }
        val gravityNew = Math.sqrt((oriValues[0] * oriValues[0] + oriValues[1] * oriValues[1] + oriValues[2] * oriValues[2]).toDouble()).toLong()
        detectorNewStep(gravityNew)

        if (countOfThread >= 30 && countOfWaves >= 30) { //在运动且组数大于20则判断一次
            judge()
        }
    }

    private fun detectorNewStep(values: Long) {  //核心算法，判断步数：1 满足波峰判定 2 满足时间限制 3 满足阀值 4 将波峰与波谷之差纳入阀值的计算中去
        if (gravityOld == 0L) {
            gravityOld = values
        } else {
            if (detectorPeak(values.toFloat(), gravityOld.toFloat())) {
                val timeOfLastPeak = timeOfThisPeak
                val timeOfNow = System.currentTimeMillis()
                if (timeOfNow - timeOfLastPeak >= 200 && peakOfWave - valleyOfWave >= threadValue) {
                    timeOfThisPeak = timeOfNow
                    countStep()
                }
                val initialValue = 1.3f
                if (timeOfNow - timeOfLastPeak >= 200 && peakOfWave - valleyOfWave >= initialValue) {
                    timeOfThisPeak = timeOfNow
                    threadValue = peakValleyThread(peakOfWave - valleyOfWave)
                }

            }
        }
        gravityOld = values
    }

    private fun countStep() {
        val thisTime = System.currentTimeMillis()

        if (thisTime - lastTime > 3000) {
            tempCount = 0
        } else {
            tempCount++
        }

        if (tempCount >= 10) {
            if (tempCount == 10) {
                totalSteps += 10
            } else {
                totalSteps++
            }
        }
        lastTime = thisTime
    }

    private fun detectorPeak(newValue: Float, oldValue: Float): Boolean {  //判断波峰方法  1 由增到减 2 连续上升次数两次以上 3 波峰值满足一定区间
        //判断波谷方法 1 由减到增
        val lastStatus = isDirectionUp
        if (newValue >= oldValue) {
            isDirectionUp = true
            continueUpCount++
        } else {
            continueUpFormerCount = continueUpCount
            continueUpCount = 0
            isDirectionUp = false
        }
        return if (!isDirectionUp && lastStatus && continueUpFormerCount >= 2 && oldValue >= 20 && oldValue <= 50) {    //判断波峰的条件，当图像一直增长，突然开始增加时，上个点认为是波峰，参数的变化请注意
            peakOfWave = oldValue
            if (countOfWaves <= 500) {   //在运动的时候才记录数据
                waveList[countOfWaves++] = peakOfWave
            }
            true
        } else if (!lastStatus and isDirectionUp) {  //波谷同上
            valleyOfWave = oldValue
            false
        } else {
            false
        }
    }

    private fun peakValleyThread(value: Float): Float {
        var tempThread = threadValue

        if (countOfThread <= 500) {      //在运动的时候才记录数据
            threadList[countOfThread++] = value
        }

        if (tempCount < valueNum) {
            tempValue[tempCount++] = value  //将差值都存入数组中
        } else {
            tempThread = averageValue(tempValue, valueNum) //梯度化差值函数，但是仍然未知为什么要这么做
            for (i in 1 until valueNum) {   //将原有元素前移，腾出位置接收新元素
                tempValue[i - 1] = tempValue[i]
            }
            tempValue[valueNum - 1] = value//更新操作
        }
        return tempThread
    }

    private fun averageValue(value: FloatArray, n: Int): Float { //梯度化阀值 有利于过滤杂波
        var ave = 0f
        for (i in 0 until n) {
            ave += value[i]
        }
        ave /= valueNum
        ave = if (ave >= 8.0)
            4.3f
        else if (ave >= 7.0 && ave < 8.0)
            3.3f
        else if (ave >= 4.0 && ave < 7.0)
            2.3f
        else if (ave >= 3.0 && ave < 4.0)
            2.0f
        else
            1.7f
        return ave
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {} //当传感器的进度发生改变时会回调,这里缺省

    private fun judgeStatus(): Int {
        var maxPeak = 0f
        for (i in 10..19) {
            if (waveList[i] - maxPeak > 0.0001) {
                maxPeak = waveList[i]
            }
        }

        var maxThread = 0f
        var aveThread = 0f
        for (i in 10..29) {
            if (threadList[i] - maxThread > 0.0001) {
                maxThread = threadList[i]
            }
            aveThread += threadList[i]
        }
        aveThread /= 20

        return if (speed > 2.0001 && speed < 10.0001) {
            if (maxPeak > 25.0001 && maxThread > 25.0001 && aveThread > 15.0001) {
                1
            } else {
                2
            }
        } else 0
    }

    private fun judge() {
        sportStatus = judgeStatus()
        Arrays.fill(waveList, 0f)
        Arrays.fill(threadList, 0f)
        countOfThread = 0
        countOfWaves = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        sManager.unregisterListener(this)
        mLocationClient.stopLocation()
        mLocationClient.onDestroy()
    }

}
