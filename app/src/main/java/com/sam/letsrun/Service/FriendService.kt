package com.sam.letsrun.Service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import com.blankj.utilcode.util.DeviceUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.sam.letsrun.Common.Const
import com.sam.letsrun.Model.*
import com.sam.runapp.Net.RetrofitUtils
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FriendService : Service() {

    private var request = RetrofitUtils.getWebSocketRequest()

    private var okHttpClient = RetrofitUtils.getOkHttpClient()

    private lateinit var myWebSocketListener: MyWebSocketListener
    private lateinit var myWebSocket: WebSocket

    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private var friendRequestList: ArrayList<AddFriendRequest> = ArrayList()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)

        sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        sharedPreferencesEditor = sharedPreferences.edit()

        myWebSocketListener = MyWebSocketListener()

        myWebSocket = okHttpClient.newWebSocket(request, myWebSocketListener)
    }


    inner class MyWebSocketListener : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response?) {
            super.onOpen(webSocket, response)
            val token = sharedPreferences.getString("token", "")    //获取token
            val user = Gson().fromJson(sharedPreferences.getString("user", ""), User::class.java)   //获取user
            val telephone = user.telephone  //获取telephone

            val request = SocketRequest(Const.SOCKET_LOGIN_CODE, telephone, token, DeviceUtils.getManufacturer())
            webSocket.send(Gson().toJson(request))  //发送登录websocket请求

        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            super.onFailure(webSocket, t, response)
            Logger.i(t.toString(), response.toString())
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            super.onMessage(webSocket, text)
            Logger.json(text)

            val response = Gson().fromJson(text, SocketResponse::class.java)
            when (response.msg) {
                Const.SOCKET_PING_CODE -> { }

                Const.SOCKET_ADD_FRIEND -> {
                    friendRequestList.add(Gson().fromJson(Gson().toJson(response.info), AddFriendRequest::class.java))      //往申请列表中添加新的好友申请
                    EventBus.getDefault().postSticky(friendRequestList)       //发送
                }

                Const.UNKNOWN_ERROR -> { }

                Const.SOCKET_LOGIN_CODE -> {
                    friendRequestList.addAll(Gson().fromJson<ArrayList<AddFriendRequest>>(Gson().toJson(response.info), object : TypeToken<ArrayList<AddFriendRequest>>() {}.type))
                    EventBus.getDefault().postSticky(friendRequestList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onMessage(event: SocketRequest) {
        myWebSocket.send(Gson().toJson(event))
    }

}

