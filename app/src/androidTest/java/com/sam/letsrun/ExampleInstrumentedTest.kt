package com.sam.letsrun

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.Model.SocketResponse
import com.sam.letsrun.Model.User

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        Logger.addLogAdapter(AndroidLogAdapter())   //Logger初始化

        val user = User("13706212370", "", "shenjiahao")

        print(Gson().toJson(user))
    }

}
