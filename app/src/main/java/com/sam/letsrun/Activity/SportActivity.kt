package com.sam.letsrun.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import com.afollestad.materialdialogs.MaterialDialog
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
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
import kotlin.math.sqrt

/**
 * 运动界面
 */
class SportActivity : AppCompatActivity() {

    //动画起始座标x,y
    private var animationX = 0
    private var animationY = 0
    //动画半径r
    private var animationRadius = 0.0
    //控制地图对象
    private lateinit var aMap: AMap
    //定位参数
    private var myLocationStyle: MyLocationStyle = MyLocationStyle()
    //当前座标
    private lateinit var currentLocation: LatLng

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)

        ImmersionBar.with(this)
                .statusBarAlpha(0.3f)
                .supportActionBar(false)
                .navigationBarEnable(true)
                .init()

        mMapView.onCreate(savedInstanceState)

        Utils.init(this)
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

    }

    /**
     * 初始化地图
     */
    private fun initMapView() {
        aMap = mMapView.map
        myLocationStyle.interval(2000)
        aMap.myLocationStyle = myLocationStyle
        aMap.isMyLocationEnabled = true
        aMap.uiSettings.isScaleControlsEnabled = true
        aMap.uiSettings.zoomPosition = 1
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18f))
        aMap.uiSettings.isMyLocationButtonEnabled = true
        aMap.setOnMyLocationChangeListener { p ->
            currentLocation = LatLng(p.latitude, p.altitude)
            Logger.json(Gson().toJson(p))
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
            .onGranted { permissions -> //权限获得后
                Logger.d(permissions)
            }
            .rationale { _, _, executor ->  //权限单次被拒绝后
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
