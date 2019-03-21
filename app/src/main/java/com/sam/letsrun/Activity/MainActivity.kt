package com.sam.letsrun.Activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import com.orhanobut.logger.Logger
import com.sam.letsrun.Adapter.MyFragmentAdapter
import com.sam.letsrun.Custom.Const
import com.sam.letsrun.Fragment.*
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.Model.EventFriendRequestList
import com.sam.letsrun.Model.SocketRequest
import com.sam.letsrun.Model.User
import com.sam.letsrun.R
import com.sam.letsrun.Service.FriendService
import com.sam.letsrun.Service.MusicService
import com.sam.letsrun.Service.SportService
import com.sam.letsrun.View.MainView
import devlight.io.library.ntb.NavigationTabBar
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import java.util.*


/**
 * 主界面
 * TODO("完善所有Fragment")
 */
class MainActivity : AppCompatActivity(), MainView {

    /**
     * 5个fragment
     * 新闻,音乐,运动,好友,设置
     */
    private var musicFragment = MusicFragment()
    private var newsFragment = NewsFragment()
    private var sportFragment = SportFragment()
    private var friendFragment = FriendFragment()
    private var settingFragment = SettingFragment()
    private var fragmentList = ArrayList<Fragment>()        //存储Fragment
    private lateinit var fragmentAdapter: MyFragmentAdapter     //Fragment适配器

    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    lateinit var token: String
    lateinit var user: User

    private var friendService: Intent? = null       //监听好友添加等
    private var sportService: Intent? = null        //定位和计步服务
    private var musicService: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.init(application)     //工具类初始化
        EventBus.getDefault().register(this)        //EventBus注册

        initUser()
        initBottomNavigation()

        /*--绑定服务--*/
        friendService = Intent(this, FriendService::class.java)

        if (user.isCountStep == 1) {
            startService(friendService)
        }

        sportService = Intent(this, SportService::class.java)
        startService(sportService)

        musicService = Intent(this, MusicService::class.java)
        startService(musicService)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initUser() {
        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()
        token = sharedPreferences.getString("token", "")
        user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)
    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomNavigation() {
        ImmersionBar.with(this)     //沉浸式导航栏初始化
                .navigationBarEnable(true)
                .supportActionBar(false)
                .statusBarAlpha(0.3f)
                .init()

        fragmentList.add(newsFragment)
        fragmentList.add(musicFragment)
        fragmentList.add(sportFragment)
        fragmentList.add(friendFragment)
        fragmentList.add(settingFragment)

        fragmentAdapter = MyFragmentAdapter(supportFragmentManager, fragmentList)
        mViewPager.adapter = fragmentAdapter
        mViewPager.offscreenPageLimit = 4   //fragment缓存数
        mViewPager.setScroll(false)     //禁止手势滑动

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
        bottomNavigation.setViewPager(mViewPager, 4)        //设置首页

    }

    /**
     * 切忌一定要释放内存
     * 该注销的东西通通注销掉
     */
    override fun onDestroy() {
        super.onDestroy()
        ImmersionBar.with(this).destroy()

        friendService?.let { stopService(it) }
        sportService?.let { stopService(it) }

        EventBus.getDefault().unregister(this)
    }

    /**
     * 添加好友
     * @param friend
     * @param message
     */
    fun addFriendRequest(friend: String, message: String) {
        val socketRequest = SocketRequest(Const.SOCKET_ADD_FRIEND,
                user.telephone,
                token,
                AddFriendRequest(user.telephone, user.username, friend, message))
        //开启
        EventBus.getDefault().postSticky(socketRequest)
    }

    /**
     * 根据好友添加请求动态更新底部导航栏的红点
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessage(event: EventFriendRequestList) {
        val friendRequestList = event.friendRequestList
        if (friendRequestList.size == 0) {
            bottomNavigation.models[3].hideBadge()
        } else {
            bottomNavigation.models[3].badgeTitle = "${friendRequestList.size}"
            bottomNavigation.models[3].updateBadgeTitle("${friendRequestList.size}")
            bottomNavigation.models[3].showBadge()
        }
    }

    fun updateLocalUserInfo(newUser: User) {
        user = newUser
        sharedPreferencesEditor.putString("user", Gson().toJson(user))
        sharedPreferencesEditor.commit()

        if (user.isCountStep == 1) {
            if (!ServiceUtils.isServiceRunning(SportService::class.java)) {
                startService(sportService)
            }
        } else {
            sportService?.let { stopService(it) }
        }

        toast("保存成功")
    }

    fun logout() {
        toast("退出成功")
        sharedPreferencesEditor.putString("token", "")
                .putString("user", "")
                .commit()
        startActivity(intentFor<LoginActivity>().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        friendService?.let { stopService(it) }
        sportService?.let { stopService(it) }
        musicService?.let { stopService(it) }

        this.finish()
    }
}
