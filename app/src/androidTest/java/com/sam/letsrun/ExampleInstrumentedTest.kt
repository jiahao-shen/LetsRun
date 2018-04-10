package com.sam.letsrun

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sam.letsrun.Model.AddFriendRequest
import com.sam.letsrun.Model.SocketResponse

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
        // Context of the app under test.
        Logger.addLogAdapter(AndroidLogAdapter())
        val text = ""
        val list = Gson().fromJson<ArrayList<AddFriendRequest>>(text,
                object : TypeToken<ArrayList<AddFriendRequest>>() {}.type)
        list.add(AddFriendRequest("123", "fsdhfkj", "fdshkjh", "fdshjfsdhkj"))
        Logger.json(list.toString())

//        Logger.json(Gson().toJson(info))

    }

}
