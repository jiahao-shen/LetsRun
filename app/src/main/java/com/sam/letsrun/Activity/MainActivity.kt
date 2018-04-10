package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.blankj.utilcode.util.DeviceUtils
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.Logger
import com.sam.letsrun.Adapter.MyFragmentAdapter
import com.sam.letsrun.Common.Const
import com.sam.letsrun.Fragment.*
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.Model.SocketRequest
import com.sam.letsrun.Model.User
import com.sam.letsrun.R
import com.sam.letsrun.Service.FriendService
import com.sam.letsrun.View.MainView
import devlight.io.library.ntb.NavigationTabBar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 主界面
 */
class MainActivity : AppCompatActivity(), MainView {

    /**
     * 5个fragment
     * 运动,音乐,咨询,好友,设置
     */
    private var sportFragment = SportFragment()
    private var musicFragment = MusicFragment()
    private var newsFragment = NewsFragment()
    private var friendFragment = FriendFragment()
    private var settingFragment = SettingFragment()
    private var fragmentList = ArrayList<Fragment>()
    private lateinit var fragmentAdapter: MyFragmentAdapter

    /**
     * sharedPreferences
     */
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var token: String
    private lateinit var user: User

    private lateinit var friendService: Intent


    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)

        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)

        initBottomNavigation()

        friendService = Intent(this, FriendService::class.java)
        startService(friendService)

    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomNavigation() {

        //初始化状态栏,导航栏的状态
        ImmersionBar.with(this)
                .navigationBarEnable(true)
                .supportActionBar(false)
                .statusBarAlpha(0.3f)
                .init()

        friendFragment.mView = this

        fragmentList.add(newsFragment)
        fragmentList.add(musicFragment)
        fragmentList.add(sportFragment)
        fragmentList.add(friendFragment)
        fragmentList.add(settingFragment)

        fragmentAdapter = MyFragmentAdapter(supportFragmentManager, fragmentList)
        mViewPager.adapter = fragmentAdapter
        //fragment缓存数
        mViewPager.offscreenPageLimit = 4
        //fragment禁止手势滑动
        mViewPager.setScroll(false)

        val models = ArrayList<NavigationTabBar.Model>()

        models.add(NavigationTabBar.Model.Builder(
                ContextCompat.getDrawable(this, R.drawable.ic_navigation_news),
                Color.parseColor("#eeeeee")
        ).title("资讯")
                .build())

        models.add(NavigationTabBar.Model.Builder(
                ContextCompat.getDrawable(this, R.drawable.ic_navigation_music),
                Color.parseColor("#eeeeee")
        ).title("音乐")
                .build())


        models.add(NavigationTabBar.Model.Builder(
                ContextCompat.getDrawable(this, R.drawable.ic_navigation_sport),
                Color.parseColor("#eeeeee")
        ).title("运动")
                .build())

        models.add(NavigationTabBar.Model.Builder(
                ContextCompat.getDrawable(this, R.drawable.ic_navigation_friend),
                Color.parseColor("#eeeeee")
        ).title("圈子")
                .build())

        models.add(NavigationTabBar.Model.Builder(
                ContextCompat.getDrawable(this, R.drawable.ic_navigation_setting),
                Color.parseColor("#eeeeee")
        ).title("设置")
                .build()
        )

        bottomNavigation.models = models
        //设置首页
        bottomNavigation.setViewPager(mViewPager, 2)

    }

    override fun onDestroy() {
        super.onDestroy()
        ImmersionBar.with(this).destroy()
        stopService(friendService)
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
    }

    override fun addFriendRequest(friend: String, message: String) {
        val socketRequest = SocketRequest(Const.SOCKET_ADD_FRIEND,
                user.telephone,
                token,
                AddFriendRequest(user.telephone, user.username, friend, message))
        Logger.json(Gson().toJson(socketRequest))
        EventBus.getDefault().postSticky(socketRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: ArrayList<AddFriendRequest>) {
        if (event.size == 0) {
            bottomNavigation.models[3].hideBadge()
        } else {
            bottomNavigation.models[3].badgeTitle = "${event.size}"
            bottomNavigation.models[3].updateBadgeTitle("${event.size}")
            bottomNavigation.models[3].showBadge()
        }
    }
}
