package com.sam.letsrun.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.services.route.DistanceResult
import com.amap.api.services.route.DistanceSearch
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Custom.MyUtils
import com.sam.letsrun.R
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_sport.*
import kotlinx.android.synthetic.main.map_view_layout.*
import org.jetbrains.anko.backgroundResource
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

/**
 * 运动
 * TODO("完善测距算法")
 */
class SportActivity : AppCompatActivity(), DistanceSearch.OnDistanceSearchListener, AMap.OnMyLocationChangeListener {

    private var animationX = 0      //动画起始x
    private var animationY = 0      //动画起始y
    private var animationRadius = 0.0    //动画半径r

    private lateinit var aMap: AMap     //控制地图对象
    private var myLocationStyle: MyLocationStyle = MyLocationStyle()    //定位参数
    private var polylineOptions = PolylineOptions()
    private lateinit var currentLocation: LatLng    //当前定位座标
    private var preLocation: LatLng? = null     //前一个定位座标

    private var totalDistance = 0.00f       //运动总距离

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferenceEditor: SharedPreferences.Editor

    /**
     * 格式化参数
     */
    private val timeDecimalFormat = DecimalFormat("00")
    private val distanceDecimalFormat = DecimalFormat("0.00")

    private enum class Status {     //运动状态
        START, PAUSE, STOP
    }

    private var sportStatus = Status.STOP       //运动状态
    private lateinit var sportTimer: Timer      //运动计时器
    private var sportTime = 0       //运动总时间
    private var isPressed = false       //stopButton是否被按压

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)
        Logger.addLogAdapter(AndroidLogAdapter())

        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferenceEditor = sharedPreferences.edit()

        mMapView.onCreate(savedInstanceState)
        Utils.init(this)

        ImmersionBar.with(this)
                .statusBarAlpha(0.3f)
                .supportActionBar(false)
                .navigationBarEnable(true)
                .init()

        initPermission()
        initMapView()

        showMapButton.setOnClickListener { v ->
            //获取button的中心座标
            val location = IntArray(2)
            v.getLocationOnScreen(location)
            animationX = (v.left + v.right) / 2 + location[0]
            animationY = (v.top + v.bottom) / 2 + location[1]

            Logger.i("$animationX, $animationY")
            showMapView()
        }

        hideMapButton.setOnClickListener {
            hideMapView()
        }

        mapModelButtonA.setOnClickListener {
            //正常模式
            aMap.mapType = AMap.MAP_TYPE_NORMAL
            mapModelMenu.close(true)
        }

        mapModelButtonB.setOnClickListener {
            //卫星模式
            aMap.mapType = AMap.MAP_TYPE_SATELLITE
            mapModelMenu.close(true)
        }

        mapModelButtonC.setOnClickListener {
            //夜间模式
            aMap.mapType = AMap.MAP_TYPE_NIGHT
            mapModelMenu.close(true)
        }

        startButton.setOnClickListener {
            when (sportStatus) {
                Status.STOP -> {
                    startCountDown()
                }
                Status.START -> {
                    sportStatus = Status.PAUSE
                    startButton.backgroundResource = R.drawable.ic_sport_start_selector
                    stopButton.visibility = View.VISIBLE
                }
                Status.PAUSE -> {
                    sportStatus = Status.START
                    startButton.backgroundResource = R.drawable.ic_sport_pause_selector
                    stopButton.visibility = View.GONE
                }
            }
        }

        stopButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    Logger.i("up")
                    isPressed = false
                }
                MotionEvent.ACTION_DOWN -> {
                    Logger.i("down")
                    isPressed = true
                    initPressTimer()
                }
            }
            false
        }

        backButton.setOnClickListener {
            MaterialDialog.Builder(this)
                    .title("提示")
                    .titleGravity(GravityEnum.CENTER)
                    .content("您确定要结束运动吗?未完成的记录不会被保存")
                    .positiveText("确定")
                    .onPositive { _, _ ->
                        val temp = sharedPreferences.getInt("${MyUtils.getCurrentDate()}:distance", 0)
                        sharedPreferenceEditor.putFloat("${MyUtils.getCurrentDate()}:distance", temp + totalDistance)
                        sharedPreferenceEditor.apply()

                        this.finish()
                    }
                    .negativeText("取消")
                    .onNeutral { dialog, _ ->
                        dialog.dismiss()
                    }
                    .build()
                    .show()
        }

        cameraButton.setOnClickListener {
//            TODO("拍照我也不知道该写什么,先放着把")
        }

    }

    private fun startCountDown() {      //开始倒计时
        val count = 3
        Observable.interval(1, TimeUnit.SECONDS)
                .take(count)
                .map { aLong -> count - aLong }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Long>() {
                    override fun onNext(t: Long?) {
                        countDownView.visibility = View.VISIBLE
                        countDownView.startAnimation(AnimationUtils.loadAnimation(this@SportActivity, R.anim.count_down_scale_anim))
                        countDownView.text = "$t"
                    }

                    override fun onCompleted() {
                        countDownView.visibility = View.GONE
                        initSportTimer()
                        sportStatus = Status.START
                        startButton.backgroundResource = R.drawable.ic_sport_pause_selector
                    }

                    override fun onError(e: Throwable?) {
                    }
                })
    }

    /**
     * 实现按压特效
     */
    private fun initPressTimer() {
        var progress = 0
        val pressTimer = Timer()
        pressTimer.schedule(object : TimerTask() {
            override fun run() {
                if (isPressed) {
                    progress += 2
                } else {
                    progress = 0
                    pressTimer.cancel()
                }
                runOnUiThread {
                    stopButton.progress = progress
                }
                if (progress >= 100) {
                    sportStatus = Status.STOP
                    isPressed = false
                    runOnUiThread {
                        stopButton.visibility = View.GONE
                        startButton.backgroundResource = R.drawable.ic_sport_start_selector
                    }
                }
            }
        }, 0, 15)
    }

    /**
     * 初始化计时
     */
    private fun initSportTimer() {
        sportTime = 0
        totalDistance = 0.0f
        sportTimer = Timer()
        sportTimer.schedule(object : TimerTask() {
            override fun run() = when (sportStatus) {
                Status.START -> {
                    val hour = sportTime / 3600
                    val minute = (sportTime / 60) % 60
                    val second = sportTime % 60
                    runOnUiThread {
                        timerView1.text = getTimeString(hour, minute, second)
                        timerView2.text = getTimeString(hour, minute, second)
                    }
                    sportTime += 1
                }
                Status.PAUSE -> {
                }
                Status.STOP -> {
                    sportTimer.cancel()
                }
            }
        },  1000, 1000)
    }

    /**
     * 获取格式化时间
     * @param hour
     * @param minute
     * @param second
     */
    private fun getTimeString(hour: Int, minute: Int, second: Int): String {
        return if (hour == 0) {
            "${timeDecimalFormat.format(minute)} : ${timeDecimalFormat.format(second)}"
        } else {
            "${timeDecimalFormat.format(hour)} : ${timeDecimalFormat.format(minute)} : ${timeDecimalFormat.format(second)}"
        }
    }

    /**
     * 初始化地图
     */
    private fun initMapView() {
        myLocationStyle.interval(2000)

        aMap = mMapView.map
        aMap.myLocationStyle = myLocationStyle
        aMap.isMyLocationEnabled = true
        aMap.uiSettings.isScaleControlsEnabled = true
        aMap.uiSettings.zoomPosition = 1
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
        aMap.uiSettings.isMyLocationButtonEnabled = true

        polylineOptions.width(60f)
        polylineOptions.isUseTexture = true
        polylineOptions.customTexture = BitmapDescriptorFactory.fromAsset("tracelinetexture.png")

        aMap.setOnMyLocationChangeListener(this)
    }

    override fun onMyLocationChange(p: Location?) {
        p?.let {
            if (sportStatus == Status.START) {      //运动的时候才记录
                currentLocation = LatLng(p.latitude, p.longitude)       //当前座标
                if (preLocation == null)        //上一次的座标
                    preLocation = LatLng(p.latitude, p.longitude)
                val tempDistance = AMapUtils.calculateLineDistance(preLocation, currentLocation)        //计算距离
                if (tempDistance >= 0.5)    //消除浮动
                    totalDistance += tempDistance / 1000      //增到总距离上
                preLocation = currentLocation       //更新上一次的座标
                polylineOptions.add(currentLocation)       //添加座标点
                aMap.addPolyline(polylineOptions)       //绘制路径
                speedView.text = p.speed.toString()     //更新速度
                distanceView1.text = distanceDecimalFormat.format(totalDistance).toString()     //更新距离1
                distanceView2.text = distanceDecimalFormat.format(totalDistance).toString()     //更新距离2
            }
        }
    }

    override fun onDistanceSearched(distanceResult: DistanceResult?, code: Int) {
        if (code == 1000) {
            distanceResult?.let {
                TODO("用高德的API增加总距离")
            }
        }
    }
    /**
     * 展示地图
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showMapView() {
        val height = ScreenUtils.getScreenHeight()
        val width = ScreenUtils.getScreenWidth()
        val radius = sqrt((height * height + width * width).toDouble())
        animationRadius = radius
        val animation = ViewAnimationUtils.createCircularReveal(mapViewLayout, animationX, animationY, 0f, radius.toFloat())
        animation.interpolator = DecelerateInterpolator()
        animation.duration = 1000
        mapViewLayout.visibility = View.VISIBLE
        animation.start()
        showMapButton.visibility = View.GONE
    }

    /**
     * 隐藏地图
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun hideMapView() {
        val animation = ViewAnimationUtils.createCircularReveal(mapViewLayout, animationX, animationY, animationRadius.toFloat(), 0.0f)
        animation.interpolator = DecelerateInterpolator()
        animation.duration = 1000
        animation.start()
        animation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                mapViewLayout.visibility = View.GONE
                showMapButton.visibility = View.VISIBLE
            }
        })
    }

    /**
     * 初始化定位权限
     */
    private fun initPermission() = AndPermission.with(this)
            .permission(Permission.Group.LOCATION)
            .onGranted { permissions ->
                //权限获得后
                Logger.d(permissions)
            }
            .rationale { _, _, executor ->
                //权限单次被拒绝后
                MaterialDialog.Builder(this)
                        .title("权限相关")
                        .content("需要打开相应的权限以获取您的定位信息")
                        .neutralText("知道了")
                        .onNeutral { _, _ ->
                            executor.execute()
                        }
                        .show()
            }.onDenied { permissions ->
                if (AndPermission.hasAlwaysDeniedPermission(this, permissions)) {   //权限多次被拒绝后
                    val service = AndPermission.permissionSetting(this)
                    MaterialDialog.Builder(this)
                            .title("通知")
                            .content("请前往设置开启相关权限")
                            .positiveText("同意")
                            .onPositive { _, _ ->
                                service.execute()
                            }
                            .negativeText("拒绝")
                            .onNegative { _, _ ->
                                service.cancel()
                            }
                            .show()
                }
            }

    override fun onDestroy() {
        super.onDestroy()
        //防止内存泄漏
        ImmersionBar.with(this).destroy()
        mMapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }
}
