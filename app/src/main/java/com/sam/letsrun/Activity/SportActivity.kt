package com.sam.letsrun.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.R
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_sport.*
import kotlinx.android.synthetic.main.map_view_layout.*
import org.jetbrains.anko.backgroundResource
import java.text.DecimalFormat
import java.util.*
import kotlin.math.sqrt

/**
 * 运动
 * TODO("还没有把实时的定位信息更新到界面上")
 * TODO("开始,结束和暂停按钮")
 */
class SportActivity : AppCompatActivity() {

    private var animationX = 0      //动画起始x
    private var animationY = 0      //动画起始y
    private var animationRadius = 0.0    //动画半径r

    private lateinit var aMap: AMap     //控制地图对象
    private var myLocationStyle: MyLocationStyle = MyLocationStyle()    //定位参数
    private var polylineOptions = PolylineOptions()
    private lateinit var currentLocation: LatLng    //当前定位座标


    private enum class Status {
        START, PAUSE, STOP
    }

    private var sportStatus = Status.STOP
    private lateinit var sportTimer: Timer
    private var sportTime = 0
    private var isPressed = false

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)
        Logger.addLogAdapter(AndroidLogAdapter())

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
            animationX = (v.left + v.right) / 2
            animationY = (v.top + v.bottom) / 2
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

        speedView.text = "0'00\""

        startButton.setOnClickListener {
            when (sportStatus) {
                Status.STOP -> {
                    initSportTimer()
                    sportStatus = Status.START
                    startButton.backgroundResource = R.drawable.ic_sport_pause_selector
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

    }

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

    private fun initSportTimer() {
        sportTime = 0
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
        },  0, 1000)
    }

    private fun getTimeString(hour: Int, minute: Int, second: Int): String {
        val df = DecimalFormat("00")
        return if (hour == 0) {
            "${df.format(minute)} : ${df.format(second)}"
        } else {
            "${df.format(hour)} : ${df.format(minute)} : ${df.format(second)}"
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
        aMap.setOnMyLocationChangeListener { p ->
            currentLocation = LatLng(p.latitude, p.longitude)
            polylineOptions.add(currentLocation)
            aMap.addPolyline(polylineOptions)
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
